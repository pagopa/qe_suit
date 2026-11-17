"""Deterministic Markdown renderer; YAML is the only mutable source of truth."""
from __future__ import annotations

import re


def _cell(value: object) -> str:
    # Escape Markdown pipe and HTML; line breaks stay readable in tables.
    text = str(value or '').replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')
    return text.replace('|', '&#124;').replace('\n', '<br>')


def render(data: dict) -> str:
    m = data['rae']
    lines = [
        '# Report di Analisi Esecuzione (RAE)', '',
        '| Campo | Valore |', '| --- | --- |',
    ]
    labels = [('release', 'Release'), ('issue_jira', 'Issue Jira'), ('data_ora', 'Data e ora'),
              ('durata', 'Durata'), ('numero_ko', 'Numero KO'), ('run', 'Run GitHub Actions'),
              ('suite', 'Suite'), ('environment', 'Ambiente'), ('executor', 'Executor'),
              ('source_branch', 'Source branch')]
    for key, label in labels:
        lines.append(f'| {label} | {_cell(m[key])} |')
    headers = '| ID TEST | ERROR LOG | PROCEDURE | RESULT | LINK PROD BUG | LINK SUITE BUG |'
    ruler = '| --- | --- | --- | --- | --- | --- |'
    # YAML collection membership is not evidence of a confirmed bug.
    # Only a historical exact fingerprint plus an appropriate ticket can
    # promote a KO. A QA-added ticket in the current RAE cannot promote it.
    grouped = {'product': [], 'suite': [], 'analysis': []}
    for section in ('ko_know', 'ko'):
        for item in data[section]:
            match_type = item.get('match_type', 'none')
            if match_type == 'exact' and str(item.get('link_prod_bug') or '').strip():
                category = 'product'  # Product wins when both links exist.
            elif match_type == 'exact' and str(item.get('link_suite_bug') or '').strip():
                category = 'suite'
            else:
                category = 'analysis'
            grouped[category].append(item)

    for category, title in (
            ('product', 'KO - Bug di prodotto'),
            ('suite', 'KO - Bug di suite'),
            ('analysis', 'KO - Da analizzare'),
    ):
        lines.extend(['', f'## {title}', '', headers, ruler])
        for item in grouped[category]:
            lines.append('| ' + ' | '.join(_cell(item[field]) for field in (
                'id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')) + ' |')
        if not grouped[category]:
            lines.extend(['', 'Nessun fallimento.'])
    return '\n'.join(lines) + '\n'
