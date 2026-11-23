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
    if code in ('STACK_SHAPE_CHANGED', 'STACK_METHOD_CHANGED', 'STACK_SOURCE_CHANGED'):
        return 'Struttura diagnostica differente'
    if code == 'LINE_NUMBER_UNSEEN':
        return 'Posizione dello stack modificata'
    if code in ('UNAPPROVED_VARIANT', 'STRUCTURAL_VARIANT_OUT_OF_SHAPE'):
        return 'Variazione non ancora approvata'
    if code == 'FULL_LOG_STRUCTURE_CHANGED':
        return 'Struttura del log modificata'
    return 'Variazione da verificare'


def render_warning(matching: dict) -> list[str]:
    """One collapsed <details> per proposed KO, before RESULT.

    Older YAML without evidence stays supported (generic short warning).
    """
    evidence = matching.get('review_evidence') or []
    if not isinstance(evidence, list):
        evidence = []
    count = len(evidence)
    summary = ('Corrispondenza storica da validare'
               + (f' · {count} evidenz{'a' if count == 1 else 'e'} da verificare' if count else ''))
    lines = ['<details>', '<summary>⚠️ ' + escape(summary) + '</summary>', '',
             'L’analisi è stata recuperata da una RAE precedente. Verificare le differenze prima di confermarla.', '']
    for index, row in enumerate(evidence, 1):
        code = row['code']
        heading = ('Struttura diagnostica differente'
                   if code == 'UNAPPROVED_STACK_VARIANT' and
                   str(row.get('historical_value', '')).startswith('nessuna conferma')
                   else _text_for(code))
        lines.append(f'**{index}. {heading}**')
        lines.append('')
        if code == 'UNAPPROVED_STACK_VARIANT' and str(row.get('historical_value', '')).startswith('nessuna conferma'):
            lines.append('Questa struttura non compare nelle occorrenze storiche confermate. Verificare la nuova variante.')
        elif code in ('UNAPPROVED_STACK_VARIANT', 'INSUFFICIENT_QA_CONFIRMATIONS'):
            lines.append('Non ci sono sufficienti evidenze per generalizzare le nuove variazioni.')
        elif code in ('HISTORICAL_DEFECT_CONFLICT', 'AMBIGUOUS_DEFECT', 'AMBIGUOUS_QA_GROUPS'):
            lines.append('Il log è compatibile con più difetti storici. La scelta richiede una verifica QA.')
        else:
            old = row.get('historical_value', '')
            new = row.get('current_value', '')
            if old or new:
                lines.append(('Storico QA: ' + _inline(old or '—') + '  ' +
                              'Log corrente: ' + _inline(new or '—')))
            else:
                lines.append('La variazione necessita di verifica QA.')
            old_context = row.get('historical_context')
            new_context = row.get('current_context')
            if old_context and new_context:
                lines.extend(['', 'Contesto: ' + _inline(old_context) + ' → ' + _inline(new_context)])
            if row.get('location'):
                lines.extend(['', 'Posizione: ' + _inline(row['location'])])
        lines.append('')
    lines.extend(['</details>', ''])
    return lines
