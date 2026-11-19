"""Prototype of QA-approved progressive invariant matching; NOT production integration.

No Drain3 requirement: isolates the third-level semantic confirmation gate.
Only QA approvals train signatures. Unsupported values never turn into `known`.
"""
from __future__ import annotations

from dataclasses import dataclass, field
from difflib import SequenceMatcher
import re
from typing import Literal

UUID = re.compile(r'(?i)\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\b')
FRAME = re.compile(r'^\s*at\s+(?P<method>\S+)\((?P<source>[^():\n]*)(?::(?P<line>\d+))?\)\s*$')
PROXY = re.compile(r'\$Proxy\d+(?=\.)')


@dataclass(frozen=True)
class Frame:
    method: str
    source: str
    line: str | None


@dataclass(frozen=True)
class Observation:
    suite: str
    test_id: str
    error: str


@dataclass(frozen=True)
class Approval:
    observation: Observation
    ticket: str
    group_id: str
    qa_reviewer: str
    qa_ref: str


@dataclass(frozen=True)
class Reason:
    code: str
    historical: str
    current: str
    detail: str


@dataclass(frozen=True)
class Decision:
    status: Literal['known', 'proposed', 'new']
    ticket: str | None
    group_id: str | None
    reasons: tuple[Reason, ...] = ()
    alternatives: tuple[str, ...] = ()


def split_error(error: str) -> tuple[str, tuple[Frame, ...]]:
    heads: list[str] = []
    frames: list[Frame] = []
    for line in error.splitlines():
        match = FRAME.match(line)
        if match:
            frames.append(Frame(match['method'], match['source'], match['line']))
        elif not frames:
            heads.append(line.strip())
    return ' '.join(' '.join(heads).split()), tuple(frames)


def structural_method(method: str) -> str:
    # Proxy number is a generated ordinal. This is for alignment only;
    # permission to GENERALIZE it is still learned separately from QA.
    return PROXY.sub('$Proxy#', method)


def group_signature(approvals: list[Approval]) -> dict:
    """Report stable/variant positions for reviewer visibility."""
    if not approvals:
        return {}
    parsed = [split_error(a.observation.error) for a in approvals]
    length = {len(frames) for _, frames in parsed}
    aligned = len(length) == 1
    stable_methods: list[str] = []
    variable_methods: list[dict] = []
    variable_lines: list[dict] = []
    if aligned:
        for index in range(next(iter(length))):
            methods = {structural_method(frames[index].method) for _, frames in parsed}
            lines = {frames[index].line for _, frames in parsed}
            if len(methods) == 1:
                stable_methods.append(next(iter(methods)))
            else:
                variable_methods.append({'position': index, 'observed': sorted(methods)})
            if len(lines) > 1:
                variable_lines.append({'position': index, 'observed': sorted(str(v) for v in lines)})
    return {'approvals': len(approvals), 'aligned_by_position': aligned,
            'stable_methods': stable_methods, 'variable_methods': variable_methods,
            'variable_lines': variable_lines}


@dataclass
class Group:
    suite: str
    test_id: str
    ticket: str
    group_id: str
    approvals: list[Approval] = field(default_factory=list)

    def heads(self) -> list[str]:
        return [split_error(a.observation.error)[0] for a in self.approvals]

    def frames(self) -> list[tuple[Frame, ...]]:
        return [split_error(a.observation.error)[1] for a in self.approvals]

    def evaluate(self, candidate: Observation) -> tuple[bool, list[Reason], float]:
        """Known is permitted only for a QA-approved, unambiguous diagnostic shape.

        A new line number is accepted at position i only after >=2 distinct
        line values at i were approved. A new proxy number similarly requires
        >=2 approved ordinals of a matching proxy method. New method names
        are NEVER wildcarded: the whole observed method sequence must match
        one QA-approved example (with explicit approved method alternatives).
        """
        head, incoming = split_error(candidate.error)
        hist_heads = self.heads()
        hist_frames = self.frames()
        reasons: list[Reason] = []
        if not hist_heads:
            return False, [Reason('NO_APPROVAL', '', '', 'Firma priva di conferme QA')], 0

        # UUIDs can generalize only after two distinct QA-confirmed UUIDs.
        historical_ids = set().union(*(set(UUID.findall(h)) for h in hist_heads))
        allow_new_uuid = len(historical_ids) >= 2
        normalized = (lambda s: UUID.sub('<UUID>', s)) if allow_new_uuid else (lambda s: s)
        compatible_head = any(normalized(h) == normalized(head) for h in hist_heads)
        if not compatible_head:
            reasons.append(Reason('MESSAGE_CHANGED', hist_heads[-1], head,
                                  'Messaggio diagnostico differente dai valori QA approvati'))

        # Determine frame position semantics only when all approved shapes align.
        frame_lengths = {len(fs) for fs in hist_frames}
        positional = len(frame_lengths) == 1 and len(incoming) == next(iter(frame_lengths))
        if not positional:
            reasons.append(Reason('STACK_SHAPE_CHANGED',
                                  ', '.join(map(str, sorted(frame_lengths))), str(len(incoming)),
                                  'Numero di frame differente; allineamento non dimostrato'))
        else:
            for i, current in enumerate(incoming):
                old = [frames[i] for frames in hist_frames]
                observed_methods = {f.method for f in old}
                observed_structural = {structural_method(f.method) for f in old}
                if current.method not in observed_methods:
                    if structural_method(current.method) in observed_structural:
                        # New ordinal is generalized ONLY when observed multiple
                        # distinct proxy method identities in this same position.
                        proxy_variants = {f.method for f in old if structural_method(f.method) == structural_method(current.method)}
                        if len(proxy_variants) < 2:
                            reasons.append(Reason('PROXY_ORDINAL_UNSEEN',
                                                  ', '.join(sorted(proxy_variants)), current.method,
                                                  'Nuovo ordinale proxy: non ancora generalizzato dal QA'))
                    else:
                        reasons.append(Reason('STACK_METHOD_CHANGED',
                                              ', '.join(sorted(observed_methods)), current.method,
                                              'Metodo non compreso nei metodi approvati per questa posizione'))
                if current.source not in {f.source for f in old}:
                    reasons.append(Reason('STACK_SOURCE_CHANGED',
                                          ', '.join(sorted({f.source for f in old})), current.source,
                                          'File sorgente non osservato nei log approvati'))
                observed_lines = {f.line for f in old}
                if current.line not in observed_lines and len(observed_lines) < 2:
                    reasons.append(Reason('LINE_NUMBER_UNSEEN',
                                          ', '.join(sorted(str(v) for v in observed_lines)), str(current.line),
                                          'Numero di riga nuovo; variabilità non ancora provata dal QA'))

        # Observed values at different positions must not be freely recombined:
        # two approved traces A/X and B/Y do NOT approve A/Y.
        if positional and not reasons:
            def method_compatible(actual: Frame, expected: Frame, position: int) -> bool:
                if actual.method == expected.method:
                    return True
                pool = [frames[position].method for frames in hist_frames]
                allowed_proxy = (structural_method(actual.method) == structural_method(expected.method)
                                 and len({p for p in pool if structural_method(p) == structural_method(actual.method)}) >= 2)
                return allowed_proxy
            approved_shape = any(
                all(method_compatible(incoming[i], frames[i], i) for i in range(len(incoming)))
                for frames in hist_frames)
            if not approved_shape:
                reasons.append(Reason('UNAPPROVED_METHOD_COMBINATION',
                                      ' | '.join(' → '.join(f.method for f in fs) for fs in hist_frames),
                                      ' → '.join(f.method for f in incoming),
                                      'Combinazione di metodi mai approvata dal QA'))

        if not reasons:
            return True, [], 1.0
        # Selection of a proposed candidate is a diagnostic aid, not permission for known.
        reference = hist_heads[-1]
        head_ratio = SequenceMatcher(None, UUID.sub('<UUID>', head), UUID.sub('<UUID>', reference)).ratio()
        if incoming and hist_frames:
            current_set = {structural_method(f.method) for f in incoming}
            previous_set = {structural_method(f.method) for f in hist_frames[-1]}
            overlap = len(current_set & previous_set) / max(1, len(current_set | previous_set))
        else:
            overlap = 0
        score = 0.75 * head_ratio + 0.25 * overlap
        return False, reasons, score


class QALearningMatcher:
    def __init__(self, proposal_threshold: float = 0.72) -> None:
        self.groups: dict[tuple[str, str, str], Group] = {}
        self.proposal_threshold = proposal_threshold

    def approve(self, approval: Approval) -> None:
        if not approval.qa_reviewer.strip() or not approval.qa_ref.strip():
            raise ValueError('Occorre una conferma QA con revisore e riferimento')
        if not approval.ticket.strip() or not approval.group_id.strip():
            raise ValueError('Ticket e identità del gruppo QA obbligatori')
        key = (approval.observation.suite, approval.observation.test_id, approval.group_id)
        if key not in self.groups:
            self.groups[key] = Group(approval.observation.suite, approval.observation.test_id,
                                     approval.ticket, approval.group_id)
        group = self.groups[key]
        if group.ticket != approval.ticket:
            raise ValueError('Il ticket del gruppo QA non può cambiare implicitamente')
        if approval.qa_ref in {a.qa_ref for a in group.approvals}:
            raise ValueError('Riferimento QA duplicato')
        group.approvals.append(approval)

    def classify(self, observation: Observation) -> Decision:
        groups = [g for g in self.groups.values()
                  if (g.suite, g.test_id) == (observation.suite, observation.test_id)]
        if not groups:
            return Decision('new', None, None,
                            (Reason('NO_HISTORY', '', observation.test_id,
                                    'Nessuna conferma QA disponibile per questa suite e test'),))
        results = [(g, *g.evaluate(observation)) for g in groups]
        compatible = [(g, r) for g, ok, r, _ in results if ok]
        if len(compatible) == 1:
            g, _ = compatible[0]
            return Decision('known', g.ticket, g.group_id)
        if len(compatible) > 1:
            tickets = tuple(sorted({g.ticket for g, _ in compatible}))
            return Decision('proposed', None, None,
                            (Reason('AMBIGUOUS_QA_GROUPS', ', '.join(tickets), '',
                                    'Più gruppi QA approvati corrispondono al nuovo log'),), tickets)
        ranked = sorted(results, key=lambda r: r[3], reverse=True)
        best, _, reasons, score = ranked[0]
        if score >= self.proposal_threshold:
            return Decision('proposed', best.ticket, best.group_id, tuple(reasons),
                            tuple(g.ticket for g, _, _, s in ranked if s >= self.proposal_threshold))
        return Decision('new', None, None,
                        (Reason('NO_SIMILAR_QA_GROUP', '', observation.test_id,
                                'Nessuna firma approvata sufficientemente simile'),))
