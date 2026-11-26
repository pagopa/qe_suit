"""QA-approved lineage -> conservative invariant engine.

The bridge uses only approved links from historical RAE entries.  It does not
learn from today's failures, pending unapproved proposals, or auto-matches.
"""
from __future__ import annotations
from collections import defaultdict
import re
from .qa_confirmation import is_qa_confirmed
from .qa_invariant_engine import Approval, Observation, QALearningMatcher, FRAME


# Cucumber step frames contain spaces and do not match Java FRAME.
_CUCUMBER_FRAME = re.compile(r"^\s*at\s+✽\..+\(classpath:[^()\n]+\)\s*$")


def _key(row):
    return (str(row.get('_source_file', '')),
            str(row.get('id_test', '')).strip().casefold())


def build_learning(history, ignore_rules=None):
    # A historical proposal is QA evidence only after the confirmation policy
    # explicitly approves it. Auto-matches must never train or veto QA groups.
    # Legacy manual analyses without matching metadata remain eligible.
    def eligible(row):
        if not row.get('id_test') or not row.get('error_log'):
            return False
        matching = row.get('matching')
        return (not matching or is_qa_confirmed(row, approved_history=True))

    indexed = {_key(row): row for row in history if eligible(row)}
    parent = {k: k for k in indexed}

    def root(k):
        while parent[k] != k:
            parent[k] = parent[parent[k]]
            k = parent[k]
        return k

    for row in indexed.values():
        if not is_qa_confirmed(row, approved_history=True):
            continue
        matching = row.get('matching') or {}
        source = (str(matching.get('source_rae', '')),
                  str(matching.get('source_id_test', '')).strip().casefold())
        current = _key(row)
        if (source in indexed and current in indexed and source != current
                and source[1] == current[1]
                and (indexed[source].get('matching') or {}).get('status') != 'auto_matched'
                and indexed[source].get('_source_suite') == row.get('_source_suite')):
            parent[root(current)] = root(source)

    # Old manually analyzed RAEs have no ``matching`` lineage.  Multiple
    # independent QA-approved occurrences of the *same normalized full log*
    # and the *same ticket identity* are equivalent evidence, not competing
    # defect groups. Never merge unconfirmed proposals or bot auto-matches.
    identical = {}
    for row in indexed.values():
        key = _key(row)
        ticket = (str(row.get('link_prod_bug') or ''),
                  str(row.get('link_suite_bug') or ''))
        if ticket == ('', ''):
            continue
        log = (ignore_rules.normalize(str(row['error_log']))
               if ignore_rules else str(row['error_log']))
        # Preserve all content except indentation on recognized stack frames.
        # Legacy YAMLs may represent Java and Cucumber frames with spaces or
        # tabs. Never normalize arbitrary diagnostic/trailing text.
        log_identity = '\n'.join(
            line.strip() if FRAME.match(line) or _CUCUMBER_FRAME.match(line)
            else line for line in log.splitlines()
        )
        identity = (str(row.get('_source_suite', '')),
                    str(row['id_test']).strip().casefold(), ticket, log_identity)
        previous = identical.setdefault(identity, key)
        if previous != key:
            parent[root(key)] = root(previous)

    groups = defaultdict(list)
    for k, row in indexed.items():
        groups[root(k)].append(row)

    engine = QALearningMatcher()
    candidates = {}
    for k, rows in groups.items():
        # A singleton historical group is NOT enough evidence for `known`,
        # but it MUST remain visible to veto an incompatible auto-match.
        # In particular, an identical log with a different QA defect identity
        # is a conflict even when it has only one historical occurrence.
        # Historical entries are newest-first. Each group must carry one
        # consistent ticket identity; conflicting tickets require QA review.
        tickets = {(str(r.get('link_prod_bug') or ''), str(r.get('link_suite_bug') or ''))
                   for r in rows}
        tickets.discard(('', ''))
        if len(tickets) != 1:
            continue
        prod, suite_bug = next(iter(tickets))
        ticket = prod or suite_bug
        suite = str(rows[0].get('_source_suite', ''))
        test = str(rows[0]['id_test'])
        group_id = '|'.join(k)
        for row in rows:
            try:
                engine.approve(Approval(
                    Observation(suite, test, ignore_rules.normalize(str(row['error_log'])) if ignore_rules else str(row['error_log'])),
                    ticket, group_id, 'approved-history', '|'.join(_key(row))))
            except ValueError:
                break
        else:
            candidates[(suite, test, group_id)] = next(
                r for r in rows if r.get('link_prod_bug') or r.get('link_suite_bug'))
    return engine, candidates


def evaluate(engine, candidates, test_id, error, suite, ignore_rules=None):
    """Return (status, historical candidate, diagnostics) or None if no QA group.

    A conservative QA result vetoes any more permissive Drain3 auto-match.
    """
    if not any(s == suite and t.casefold() == test_id.strip().casefold()
               for s, t, _ in candidates):
        return None
    decision = engine.classify(Observation(suite, test_id, ignore_rules.normalize(error) if ignore_rules else error))
    historical = candidates.get((suite, test_id, decision.group_id)) if decision.group_id else None
    if historical is None and decision.status == 'proposed' and decision.group_id:
        historical = next((r for (s, t, gid), r in candidates.items()
                           if s == suite and t.casefold() == test_id.strip().casefold()
                           and gid == decision.group_id), None)
    return decision.status, historical, decision.reasons
