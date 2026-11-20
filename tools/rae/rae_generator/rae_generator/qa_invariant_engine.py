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
SEGMENTS = re.compile(r'([._:/-])')
HEAD_TOKENS = re.compile(r'\S+')


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
    historical_context: str = ''
    current_context: str = ''
    location: str = ''


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


def _trailing_log_lines(error: str) -> tuple[str, ...]:
    """Preserve all text after stack frames (including skipped-looking text).

    Intentionally does NOT interpret Cucumber, Java, or any product language.
    The existing head check handles the prefix; this handles the portion that
    split_error() historically discarded after the first stack frame.
    """
    encountered_frame = False
    trailing: list[str] = []
    for line in error.splitlines():
        if FRAME.match(line):
            encountered_frame = True
        elif encountered_frame and line.strip():
            trailing.append(' '.join(line.split()))
    return tuple(trailing)


def _trailing_invariant_reasons(history: list[str], current: str) -> list[Reason]:
    """QA-only full-log invariants, with exact values and stable alignment.

    Invariants in *any* trailing position are checked, including log lines
    which happen to follow a Java stack. Unknown text is never ignored based
    on a status word (such as 'skipped').
    """
    historical = [_trailing_log_lines(error) for error in history]
    incoming = _trailing_log_lines(current)
    if not historical or not any(historical) and not incoming:
        return []
    if incoming in historical:
        return []
    lengths = {len(lines) for lines in historical}
    if len(lengths) != 1 or len(incoming) != next(iter(lengths)):
        # A line insertion/removal is not safely generalizable. Give QA
        # aligned, localized changes rather than silently dropping suffixes.
        best = max(historical, key=lambda lines: SequenceMatcher(None, lines, incoming).ratio())
        changes = []
        for op, i, j, k, l in SequenceMatcher(None, best, incoming, autojunk=False).get_opcodes():
            if op != 'equal':
                changes.append(Reason('FULL_LOG_STRUCTURE_CHANGED',
                                      ' | '.join(best[i:j]) or '<nessuna riga>',
                                      ' | '.join(incoming[k:l]) or '<riga assente>',
                                      'Righe del log inserite, eliminate o modificate rispetto alla struttura approvata'))
        return changes or [Reason('FULL_LOG_STRUCTURE_CHANGED', str(best), str(incoming),
                                  'Struttura delle righe non allineabile')]

    reasons: list[Reason] = []
    for line_no, actual in enumerate(incoming):
        approved_lines = [lines[line_no] for lines in historical]
        if actual in approved_lines:
            continue
        approved_tokens = [HEAD_TOKENS.findall(line) for line in approved_lines]
        current_tokens = HEAD_TOKENS.findall(actual)
        token_lengths = {len(tokens) for tokens in approved_tokens}
        if len(token_lengths) != 1 or len(current_tokens) != next(iter(token_lengths)):
            best = max(approved_lines, key=lambda line: SequenceMatcher(None, line, actual).ratio())
            reasons.append(Reason('FULL_LOG_STRUCTURE_CHANGED', best, actual,
                                  f'Riga {line_no + 1} del suffisso: token non allineabili'))
            continue
        for pos, token in enumerate(current_tokens):
            observed = [tokens[pos] for tokens in approved_tokens]
            if token in observed:
                continue
            structural = _structural_token_reasons(observed, token, pos)
            if structural is not None:
                reasons.extend(structural)
            else:
                place = f'Riga {line_no + 1} dopo lo stack, token {pos + 1}'
                old_ctx, new_ctx, ctx_location = _token_context(
                    approved_lines, actual, pos, location=place)
                reasons.append(Reason(
                    'INVARIANT_CHANGED' if len(set(observed)) == 1 else 'UNAPPROVED_VARIANT',
                    ', '.join(sorted(set(observed))), token,
                    f'{place}: differenza rispetto ai valori approvati dal QA',
                    old_ctx, new_ctx, ctx_location))
    return reasons


def _token_context(approved: list[str], actual: str, token_position: int,
                   *, location: str = '') -> tuple[str, str, str]:
    """Optional, evidence-backed context around an aligned token difference.

    A useful context needs stable neighbors on BOTH sides, a unique occurrence
    of the original token in the compared line, and unambiguous alignment.
    Otherwise return only the exact singular values via the Reason fields.
    No domain keywords or assumed semantics are involved.
    """
    current_tokens = HEAD_TOKENS.findall(actual)
    if token_position >= len(current_tokens) or not approved:
        return '', '', ''
    rows = [HEAD_TOKENS.findall(text) for text in approved]
    if any(len(row) != len(current_tokens) for row in rows):
        return '', '', ''
    if token_position < 2 or len(current_tokens) - token_position - 1 < 2:
        return '', '', ''
    # Require at least two adjacent approved anchors on each side.
    before = current_tokens[token_position - 2:token_position]
    after = current_tokens[token_position + 1:token_position + 3]
    if any(row[token_position - 2:token_position] != before or
           row[token_position + 1:token_position + 3] != after for row in rows):
        return '', '', ''
    # Do not claim a position within repeated equal windows.
    def count_window(tokens):
        return sum(tokens[i - 2:i] == before and tokens[i + 1:i + 3] == after
                   for i in range(2, len(tokens) - 2))
    if count_window(current_tokens) != 1 or any(count_window(row) != 1 for row in rows):
        return '', '', ''
    old = rows[0][token_position]
    # Different historical values at this position: no single original phrase.
    if any(row[token_position] != old for row in rows):
        return '', '', ''
    old_text = ' '.join([*before, old, *after])
    current_text = ' '.join([*before, current_tokens[token_position], *after])
    return old_text, current_text, location


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


def _char_family(observed: list[str]) -> str | None:
    """Agnostic character-family envelope; no domain/product identifiers."""
    if not observed or not all(x and x.isascii() and x.isalnum() for x in observed):
        return None
    if all(x.isdigit() for x in observed):
        return 'DIGIT'
    if all(x.upper() == x for x in observed):
        return 'UPPER_ALNUM'
    if all(x.lower() == x for x in observed):
        return 'LOWER_ALNUM'
    return 'ALNUM'


def _matches_family(value: str, family: str, lengths: set[int]) -> bool:
    if len(value) not in lengths or not value.isascii() or not value.isalnum():
        return False
    return (family == 'ALNUM' or (family == 'DIGIT' and value.isdigit())
            or (family == 'UPPER_ALNUM' and value.upper() == value)
            or (family == 'LOWER_ALNUM' and value.lower() == value))


def _structural_token_reasons(previous: list[str], incoming: str, pos: int) -> list[Reason] | None:
    """Learn variable subsegments without any IUN/UUID/Java-specific schema.

    Require >= 2 independently observed variable segments plus stable anchors
    on both sides. All historical values must share delimiter skeleton.
    Previously invariant segments remain exact until QA approves a change.
    """
    parsed = [SEGMENTS.split(v) for v in previous]
    current = SEGMENTS.split(incoming)
    if len({len(parts) for parts in parsed}) != 1 or len(current) != len(parsed[0]):
        return None
    skeleton = parsed[0]
    if any(any(parts[i] != skeleton[i] for i in range(1, len(skeleton), 2))
           for parts in parsed + [current]):
        return None
    variable = [i for i in range(0, len(skeleton), 2)
                if len({parts[i] for parts in parsed}) >= 2]
    # A single changing substring is too ambiguous to generalize as an ID.
    if len(variable) < 2:
        return None
    # This is a bounded identifier-like region: preserve anchored context.
    left = [i for i in range(0, min(variable), 2)
            if len({parts[i] for parts in parsed}) == 1 and len(skeleton[i]) >= 2]
    right = [i for i in range(max(variable) + 2, len(skeleton), 2)
             if len({parts[i] for parts in parsed}) == 1 and len(skeleton[i]) >= 1]
    if not left or not right:
        return None
    issues: list[Reason] = []
    for i in range(0, len(skeleton), 2):
        observed = [parts[i] for parts in parsed]
        new = current[i]
        if new in observed:
            continue
        if i not in variable:
            issues.append(Reason('STRUCTURAL_INVARIANT_CHANGED',
                                 f'token {pos}, segmento {i // 2}: {observed[0]}',
                                 new, 'Una porzione del valore rimasta invariata nello storico QA è cambiata'))
            continue
        family = _char_family(observed)
        lengths = {len(x) for x in observed}
        if family is None or not _matches_family(new, family, lengths):
            issues.append(Reason('STRUCTURAL_VARIANT_OUT_OF_SHAPE',
                                 f'token {pos}, segmento {i // 2}: {sorted(set(observed))}',
                                 new, 'La variante non rispetta il formato osservato nelle conferme QA'))
    return issues


def _message_reasons(historical: list[str], current: str) -> list[Reason]:
    # An existing approved message is always an admissible value.
    if current in historical:
        return []
    # Keep existing explicit UUID learning, independently of token segmentation.
    historical_ids = set().union(*(set(UUID.findall(h)) for h in historical))
    if len(historical_ids) >= 2:
        normalized = lambda s: UUID.sub('<UUID>', s)
        if any(normalized(h) == normalized(current) for h in historical):
            return []
    rows = [HEAD_TOKENS.findall(h) for h in historical]
    incoming = HEAD_TOKENS.findall(current)
    if not rows or len({len(r) for r in rows}) != 1 or len(incoming) != len(rows[0]):
        return [Reason('MESSAGE_CHANGED', historical[-1], current,
                       'La struttura dei token del messaggio diagnostico è cambiata')]
    reasons: list[Reason] = []
    for pos, token in enumerate(incoming):
        approved = [row[pos] for row in rows]
        if token in approved:
            continue
        # UUID generalization at a single aligned token, including key=value.
        if len(historical_ids) >= 2 and any(UUID.sub('<UUID>', v) == UUID.sub('<UUID>', token)
                                             for v in approved):
            continue
        structural = _structural_token_reasons(approved, token, pos)
        if structural is not None:
            reasons.extend(structural)
        else:
            old_ctx, new_ctx, ctx_location = _token_context(
                historical, current, pos, location=f'Token {pos + 1} del testo iniziale')
            reasons.append(Reason('MESSAGE_CHANGED', ', '.join(sorted(set(approved))), token,
                                  'Valore non approvato o senza struttura sufficientemente distintiva',
                                  old_ctx, new_ctx, ctx_location))
    return reasons


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

    @staticmethod
    def _variant_key(error: str) -> tuple[int, str]:
        """Separate incompatible stack families without knowing any product schema.

        Only used when QA history contains DIFFERENT stack lengths. The
        exception marker is taken as a literal diagnostic token, never as a
        reason to generalize unseen exception classes. A family retains the
        full existing message, method, source and line checks.
        """
        head, frames = split_error(error)
        exception = re.search(r'(?m)(?:^|\s)([A-Za-z_$][\w.$]*):(?:\s|$)', head)
        return len(frames), (exception.group(1) if exception else '')

    def evaluate(self, candidate: Observation) -> tuple[bool, list[Reason], float]:
        """Evaluate approved stack variants independently, never cross-combine.

        A QA group can include different *approved* stack families. A matching
        family is sufficient, but the entire group never gains permission to
        invent a new combination. T7 conflict checking remains in classify().
        """
        lengths = {len(fs) for fs in self.frames()}
        if len(lengths) <= 1:
            return self._evaluate_flat(candidate)

        # A repeat of any exact QA-approved observation remains a match even
        # when there is no second example of that particular stack variant.
        if any(candidate.error == a.observation.error for a in self.approvals):
            return True, [], 1.0

        buckets: dict[tuple[int, str], list[Approval]] = {}
        for approval in self.approvals:
            buckets.setdefault(self._variant_key(approval.observation.error), []).append(approval)
        key = self._variant_key(candidate.error)
        matching = buckets.get(key)
        if matching is not None and len(matching) >= 2:
            # Reuse all existing T8 invariant / variant checks, trained only
            # from QA-approved observations within the compatible family.
            subgroup = Group(self.suite, self.test_id, self.ticket,
                             self.group_id, approvals=matching)
            return subgroup._evaluate_flat(candidate)

        # No compatible family with sufficient QA training: no auto-known.
        # Generate reviewer feedback from the closest approved family, while
        # preserving an explicit signal that the structural variant is unseen.
        reference = [a for a in self.approvals
                     if len(split_error(a.observation.error)[1]) == key[0]]
        if not reference:
            reference = self.approvals
        best = max(reference, key=lambda a: SequenceMatcher(
            None, split_error(a.observation.error)[0],
            split_error(candidate.error)[0]).ratio())
        comparison = Group(self.suite, self.test_id, self.ticket,
                           self.group_id, approvals=[best])
        _, reasons, score = comparison._evaluate_flat(candidate)
        # Even an exact-looking message with a new structural family must be
        # reviewed. Singletons never train generalization.
        if matching:
            evidence = f'{len(matching)} conferma QA per questa famiglia'
            detail = ('La forma dello stack corrente è già osservata e riconosciuta. '
                      'Tuttavia non esistono due conferme in questa famiglia '
                      'che permettano di generalizzare i valori cambiati; '
                      'le differenze puntuali sono elencate separatamente.')
        else:
            evidence = 'nessuna conferma QA per questa famiglia'
            detail = ('La combinazione di numero di frame e classe diagnostica '
                      'non compare fra le famiglie approvate; verifica QA richiesta.')
        reason = Reason('UNAPPROVED_STACK_VARIANT',
                        evidence,
                        f'{key[0]} frame / {key[1] or "diagnostic unknown"}',
                        detail)
        return False, [reason, *reasons], max(score, 0.74 if matching else 0.0)

    def _evaluate_flat(self, candidate: Observation) -> tuple[bool, list[Reason], float]:
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

        # A byte-for-byte repeated QA-approved log is a valid variant even if
        # OTHER members of this group have different stack lengths or shapes.
        # This does not authorize new variants: the minimum QA count and all
        # competing QA groups are checked later by QALearningMatcher.classify.
        if candidate.error in (a.observation.error for a in self.approvals):
            return True, [], 1.0

        # QA-approved structural learning applies to every diagnostic token,
        # not just UUIDs. It does not use current KO as training evidence.
        reasons.extend(_message_reasons(hist_heads, head))
        # The former split_error() only returned the diagnostic prefix and
        # stack; everything after the stack was absent from known/proposed
        # evaluation. Check the entire trailing log against QA-approved
        # observations in this SAME stack family.
        reasons.extend(_trailing_invariant_reasons(
            [a.observation.error for a in self.approvals], candidate.error))

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
        # A diagnostic message with strong unchanged token context is suitable
        # for QA proposal even if the long variable token lowers char-similarity.
        # Never use this floor to authorize `known`.
        head_tokens = head.split()
        if len(head_tokens) >= 8 and any(len(h.split()) == len(head_tokens) for h in hist_heads):
            common = max(sum(a == b for a, b in zip(head_tokens, h.split())) / len(head_tokens)
                         for h in hist_heads if len(h.split()) == len(head_tokens))
            if common >= 0.75:
                score = max(score, 0.74)
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
        # Check ambiguity BEFORE applying the minimum-confirmations rule:
        # one fully learned group plus one identical singleton is still unsafe.
        if len(compatible) > 1:
            tickets = tuple(sorted({g.ticket for g, _ in compatible}))
            detail = ('Il log è compatibile con più identità di difetto storiche, '
                      'inclusi eventuali gruppi con una sola occorrenza. '
                      'Verificare la causa prima di assegnare il ticket.')
            conflicts = ', '.join(f'{g.group_id} (ticket {g.ticket}, {len(g.approvals)} eventi QA)'
                                  for g, _ in compatible)
            code = ('AMBIGUOUS_QA_GROUPS' if all(len(g.approvals) >= 2 for g, _ in compatible)
                    else 'HISTORICAL_DEFECT_CONFLICT')
            return Decision('proposed', None, None,
                            (Reason(code, conflicts, '', detail),), tickets)
        if len(compatible) == 1:
            g, _ = compatible[0]
            if len(g.approvals) >= 2:
                return Decision('known', g.ticket, g.group_id)
            return Decision('proposed', g.ticket, g.group_id,
                            (Reason('INSUFFICIENT_QA_CONFIRMATIONS',
                                    f'{g.group_id}: 1 occorrenza QA',
                                    observation.error,
                                    'Il log coincide con un solo evento storico, ma non esiste '
                                    'una seconda conferma QA che autorizzi il riconoscimento automatico'),),
                            (g.ticket,))
        ranked = sorted(results, key=lambda r: r[3], reverse=True)
        best, _, reasons, score = ranked[0]
        if score >= self.proposal_threshold:
            return Decision('proposed', best.ticket, best.group_id, tuple(reasons),
                            tuple(g.ticket for g, _, _, s in ranked if s >= self.proposal_threshold))
        return Decision('new', None, None,
                        (Reason('NO_SIMILAR_QA_GROUP', '', observation.test_id,
                                'Nessuna firma approvata sufficientemente simile'),))
