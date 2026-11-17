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
    labels = [('release', 'Release'), ('scope', 'Scope'), ('issue_jira', 'Issue Jira'), ('data_ora', 'Data e ora'),
              ('durata', 'Durata'), ('numero_ko', 'Numero KO'), ('run', 'Run GitHub Actions'),
              ('suite', 'Suite'), ('environment', 'Ambiente'), ('executor', 'Executor'),
              ('source_branch', 'Source branch')]
    for key, label in labels:
        lines.append(f'| {label} | {_cell(m.get(key, 'NIGHTLY') if key == 'scope' else m[key])} |')
    headers = '| ID TEST | ERROR LOG | PROCEDURE | RESULT | LINK PROD BUG | LINK SUITE BUG |'
    ruler = '| --- | --- | --- | --- | --- | --- |'
    for key, title in [('ko_know', 'KO - Dovuti a bug noti'), ('ko', 'KO - Altri fallimenti')]:
        lines.extend(['', f'## {title}', '', headers, ruler])
        for item in data[key]:
            lines.append('| ' + ' | '.join(_cell(item[field]) for field in (
                'id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')) + ' |')
        if not data[key]:
            lines.extend(['', 'Nessun fallimento.'])
    return '\n'.join(lines) + '\n'
