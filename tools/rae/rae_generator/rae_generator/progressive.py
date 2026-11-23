"""Progressive, fail-closed RAE recognition learned from QA-approved history.

Only approved historical RAE entries are inputs. A pending suggestion becomes an
implicit QA confirmation *only when read back from approved develop history*
and its original prefilled analysis remains unchanged.
"""
from __future__ import annotations

from collections import defaultdict
from dataclasses import dataclass
import hashlib
import json
import re
from typing import Iterable

from drain3 import TemplateMiner
from drain3.masking import MaskingInstruction
from drain3.template_miner_config import TemplateMinerConfig

from .matching import find_match
from .qa_confirmation import is_qa_confirmed, analysis_digest
from .qa_invariant_bridge import build_learning, evaluate

_UUID = r'\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\b'
_TIMESTAMP = r'\b\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?(?:Z|[+-]\d{2}:?\d{2})?\b'
_PLACEHOLDER = re.compile(r'(<\*>|<UUID>|<TIMESTAMP>)')
_ANALYSIS_FIELDS = ('procedure', 'result', 'link_prod_bug', 'link_suite_bug')


def error_head(error: str) -> str:
    """Normalize whitespace but retain the full diagnostic log, including stack frames."""
    return re.sub(r'\s+', ' ', error).strip()



def _miner() -> TemplateMiner:
    cfg = TemplateMinerConfig()
    cfg.drain_sim_th = 0.5
    cfg.drain_depth = 4
    cfg.parametrize_numeric_tokens = False
    cfg.masking_instructions = [MaskingInstruction(_UUID, 'UUID'),
                                MaskingInstruction(_TIMESTAMP, 'TIMESTAMP')]
    return TemplateMiner(config=cfg)


def _identity(item: dict) -> tuple[str, str]:
    return (str(item.get('_source_file', '')), str(item.get('id_test', '')).strip().casefold())



def _pattern_regex(template: str) -> tuple[re.Pattern[str], list[str]]:
    names: list[str] = []
    fragments = ['^']
    for token in _PLACEHOLDER.split(template):
        if token == '<UUID>':
            names.append('UUID')
            fragments.append('(' + _UUID.replace('\\b', '') + ')')
        elif token == '<TIMESTAMP>':
            names.append('TIMESTAMP')
            fragments.append('(' + _TIMESTAMP.replace('\\b', '') + ')')
        elif token == '<*>':
            names.append('STAR')
            fragments.append('(.*?)')
        else:
            fragments.append(re.escape(token))
    fragments.append('$')
    return re.compile(''.join(fragments)), names


@dataclass
class Signature:
    """Structural template and per-parameter variation proven in QA-approved logs."""
    test_id: str
    members: list[dict]
    template: str
    regex: re.Pattern[str]
    kinds: list[str]
    samples: list[tuple[str, ...]]

    def matches(self, error: str) -> bool:
        result = self.regex.fullmatch(error_head(error))
        if not result:
            return False
        values = result.groups()
        for i, (kind, value) in enumerate(zip(self.kinds, values)):
            observed = {s[i] for s in self.samples}
            if value in observed:
                continue
            # Only known, typed execution identifiers actually observed to
            # vary within QA-confirmed occurrences generalize to new values.
            if kind in ('UUID', 'TIMESTAMP') and len(observed) >= 2:
                continue
            # Drain3's <*> means variable token, NOT arbitrary acceptable value.
            return False
        return True


def _signatures(history: list[dict]) -> list[Signature]:
    indexed = {_identity(row): row for row in history}
    parent: dict[tuple[str, str], tuple[str, str]] = {key: key for key in indexed}

    def root(key: tuple[str, str]) -> tuple[str, str]:
        while parent[key] != key:
            parent[key] = parent[parent[key]]
            key = parent[key]
        return key

    def union(a: tuple[str, str], b: tuple[str, str]) -> None:
        pa, pb = root(a), root(b)
        if pa != pb:
            parent[pa] = pb

    for item in history:
        if not is_qa_confirmed(item, approved_history=True):
            continue
        m = item['matching']
        source = (str(m.get('source_rae', '')), str(m.get('source_id_test', '')).strip().casefold())
        me = _identity(item)
        if source in indexed and source != me and source[1] == me[1]:
            # Do not let an unreviewed auto-match become new learning evidence.
            if indexed[source].get('matching', {}).get('status') != 'auto_matched':
                union(me, source)

    groups: dict[tuple[str, str], list[dict]] = defaultdict(list)
    for key, row in indexed.items():
        groups[root(key)].append(row)

    output = []
    for members in groups.values():
        if len(members) < 2:
            continue
        miner = _miner()  # isolated miner for this QA-validated group
        for item in members:
            miner.add_log_message(error_head(item['error_log']))
        clusters = [miner.match(error_head(row['error_log']), full_search_strategy='always')
                    for row in members]
        if any(c is None for c in clusters) or len({c.cluster_id for c in clusters}) != 1:
            # Different structural clusters: no automatic classifier.
            continue
        template = clusters[0].get_template()
        regex, kinds = _pattern_regex(template)
        captured = [regex.fullmatch(error_head(row['error_log'])) for row in members]
        if any(x is None for x in captured):
            continue
        output.append(Signature(members[0]['id_test'], members, template,
                                regex, kinds, [x.groups() for x in captured]))
    return output


class ProgressiveMatcher:
    """Learn from approved history and classify without training on current KO."""

    def __init__(self, history: list[dict], *, ignore_rules=None):
        self.history = [r for r in history if r.get('id_test') and r.get('error_log')]
        self.miner = _miner()
        self.cluster_members: dict[int, list[dict]] = defaultdict(list)
        for row in self.history:
            self.miner.add_log_message(error_head(row['error_log']))
        for row in self.history:
            cluster = self.miner.match(error_head(row['error_log']), full_search_strategy='always')
            if cluster is not None:
                self.cluster_members[cluster.cluster_id].append(row)
        self.signatures = _signatures(self.history)
        self.ignore_rules = ignore_rules
        self.qa_engine, self.qa_candidates = build_learning(self.history, ignore_rules=ignore_rules)
        self.last_review_reasons = ()

    def classify(self, test_id: str, error: str, *, suite: str | None = None) -> tuple[str, dict | None]:
        """Return ('known' | 'proposed' | 'new', historical entry or None)."""
        self.last_review_reasons = ()
        if suite is not None:
            qa = evaluate(self.qa_engine, self.qa_candidates, test_id, error, suite, ignore_rules=self.ignore_rules)
            if qa is not None:
                status, previous, reasons = qa
                self.last_review_reasons = tuple(reasons)
                # The explainable, QA-backed gate is authoritative: never
                # fall through to an old, more permissive auto-known decision.
                return status, previous
        matched = self.miner.match(error_head(error), full_search_strategy='always')
        if matched is None:
            return 'new', None
        candidates = [row for row in self.cluster_members.get(matched.cluster_id, ())
                      if row['id_test'].strip().casefold() == test_id.strip().casefold()
                      and row.get('result') and row.get('procedure')]
        if not candidates:
            return 'new', None

        compatible = [sig for sig in self.signatures
                      if sig.test_id.strip().casefold() == test_id.strip().casefold()
                      and sig.matches(error)]
        if len(compatible) == 1:
            sig = compatible[0]
            # Prefer the newest finalized *compatible* historical analysis;
            # ticket values themselves never define defect identity.
            member_ids = {id(member) for member in sig.members}
            eligible = [r for r in candidates if id(r) in member_ids
                        and sig.matches(r['error_log'])
                        and not (isinstance(r.get('matching'), dict)
                                 and r['matching'].get('status') == 'pending_confirmation'
                                 and not is_qa_confirmed(r, approved_history=True))]
            known = next((r for r in eligible if r.get('link_prod_bug') or r.get('link_suite_bug')), None)
            if known is not None:
                return 'known', known
        # A zero or ambiguous signature result cannot authorize auto-known.
        candidate_match = find_match(test_id, error, candidates)
        return ('proposed', candidate_match.candidate) if candidate_match else ('new', None)
