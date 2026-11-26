"""Presentation-only, QA-readable snapshot of matcher review reasons.

No history lookup or classification happens here; this module cannot authorize known.
"""
from __future__ import annotations

from html import escape
import re

MAX_EVIDENCES = 40
MAX_TEXT = 1200


def snapshot(reasons) -> list[dict]:
    """Convert already-calculated T7/T8 reasons into constrained YAML scalars."""
    output = []
    seen = set()
    for reason in reasons:
        code = str(getattr(reason, 'code', '') or '')
        if not re.fullmatch(r'[A-Z][A-Z0-9_]{0,79}', code):
            continue
        row = {'code': code}
        for attr, field in [('historical', 'historical_value'),
                            ('current', 'current_value'),
                            ('historical_context', 'historical_context'),
                            ('current_context', 'current_context'),
                            ('location', 'location')]:
            value = str(getattr(reason, attr, '') or '').strip()
            if value:
                row[field] = value[:MAX_TEXT]
        key = tuple(sorted(row.items()))
        if key not in seen:
            seen.add(key)
            output.append(row)
        if len(output) >= MAX_EVIDENCES:
            break
    return output


def _inline(value: str) -> str:
    """Untrusted log fragments must not inject HTML or Markdown controls."""
    return '<code>' + escape(str(value), quote=True) + '</code>'


def _text_for(code: str) -> str:
    if code in ('INVARIANT_CHANGED', 'STRUCTURAL_INVARIANT_CHANGED'):
        return 'Invariante modificata'
    if code == 'UNAPPROVED_STACK_VARIANT':
        return 'Stack già osservato'
    if code == 'INSUFFICIENT_QA_CONFIRMATIONS':
        return 'Evidenze QA insufficienti'
    if code in ('HISTORICAL_DEFECT_CONFLICT', 'AMBIGUOUS_DEFECT', 'AMBIGUOUS_QA_GROUPS'):
        return 'Più analisi storiche compatibili'
    if code == 'MULTIPLE_QA_GROUPS_SAME_TICKET':
        return 'Più gruppi QA compatibili con lo stesso ticket'
    if code in ('STACK_SHAPE_CHANGED', 'STACK_METHOD_CHANGED', 'STACK_SOURCE_CHANGED'):
        return 'Struttura diagnostica differente'
    if code == 'LINE_NUMBER_UNSEEN':
        return 'Posizione dello stack modificata'
    if code in ('UNAPPROVED_VARIANT', 'STRUCTURAL_VARIANT_OUT_OF_SHAPE'):
        return 'Variazione non ancora approvata'
    if code == 'FULL_LOG_STRUCTURE_CHANGED':
        return 'Struttura del log modificata'
    return 'Variazione da verificare'


def visible_evidence(evidence: list[dict]) -> list[dict]:
    """Presentation-only selection; never changes matcher decisions or YAML."""
    generic_codes = {
        'UNAPPROVED_STACK_VARIANT', 'INSUFFICIENT_QA_CONFIRMATIONS',
        'NO_APPROVAL',
    }
    entries = [row for row in evidence if isinstance(row, dict)]
    shown = [row for row in entries if str(row.get('code', '')) not in generic_codes]
    if not shown and entries:
        # A technical gate with no reportable diff gets one neutral explanation.
        return [{'code': 'QA_REVIEW_REQUIRED'}]
    return shown


def render_warning(matching: dict) -> list[str]:
    """Render one compact, GitHub-compatible review panel before RESULT.

    Presentation-only. Redundant gate diagnostics are suppressed when specific
    differences exist, without altering the evidence persisted in the YAML.
    """
    evidence = matching.get('review_evidence') or []
    if not isinstance(evidence, list):
        evidence = []

    visible = visible_evidence(evidence)

    count = len(visible)
    count_text = (f' · {count} evidenza da verificare' if count == 1
                  else f' · {count} evidenze da verificare' if count else '')
    summary = '⚠️ Corrispondenza storica da validare' + count_text
    lines = [
        '<table>', '<tbody><tr><td width="100%">', '<details>',
        '<summary><strong>' + escape(summary, quote=True) + '</strong></summary>',
        '<p>L’analisi è stata recuperata da una RAE precedente. '
        'Verificare le differenze prima di confermarla.</p>',
    ]
    for index, row in enumerate(visible, 1):
        code = str(row.get('code', ''))
        if code == 'QA_REVIEW_REQUIRED':
            lines.append('<p>Le informazioni storiche disponibili non consentono '
                         'di confermare automaticamente l’equivalenza del KO.</p>')
            continue
        heading = _text_for(code)
        lines.append('<p><strong>' + str(index) + '. ' + escape(heading) + '</strong></p>')
        if code in ('HISTORICAL_DEFECT_CONFLICT', 'AMBIGUOUS_DEFECT', 'AMBIGUOUS_QA_GROUPS'):
            lines.append('<p>Il log è compatibile con più difetti storici. '
                         'La scelta richiede una verifica QA.</p>')
        elif code == 'MULTIPLE_QA_GROUPS_SAME_TICKET':
            lines.append('<p>Più gruppi QA storici compatibili condividono lo stesso ticket. '
                         'L’analisi più recente è proposta, non confermata automaticamente. '
                         'Verificare la causa e confermare l’equivalenza prima dell’apprendimento.</p>')
            if row.get('current_value'):
                lines.append('<p>Ticket comune: ' + _inline(row['current_value']) + '</p>')
        else:
            old = row.get('historical_value', '')
            current = row.get('current_value', '')
            if old or current:
                lines.append('<p>Storico QA: ' + _inline(old or '—') +
                             ' &nbsp; Log corrente: ' + _inline(current or '—') + '</p>')
            else:
                lines.append('<p>La variazione necessita di verifica QA.</p>')
            if row.get('historical_context') and row.get('current_context'):
                lines.append('<p>Contesto: ' + _inline(row['historical_context']) +
                             ' → ' + _inline(row['current_context']) + '</p>')
            if row.get('location'):
                lines.append('<p>Posizione: ' + _inline(row['location']) + '</p>')
    lines.extend(['</details>', '</td></tr></tbody>', '</table>', ''])
    return lines
