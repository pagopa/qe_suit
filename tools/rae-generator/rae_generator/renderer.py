"""Deterministic Markdown renderer; YAML is the only mutable source of truth."""
from __future__ import annotations
import re


def _cell(value: object) -> str:
    # Escape Markdown pipe and HTML in metadata fields.
    text = str(value or '').replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')
    return text.replace('|', '&#124;').replace('\n', '<br>')


def _prose(value: str) -> str:
    # Keep paragraphs, but unwrap source line breaks for full-width reading.
    paragraphs = re.split(r'\n\s*\n', value.strip())
    return '\n\n'.join(
        re.sub(r'\s*\n\s*', ' ', paragraph).replace('&', '&amp;')
        .replace('<', '&lt;').replace('>', '&gt;')
        for paragraph in paragraphs
    )


def _log_block(value: str) -> list[str]:
    # A sufficiently long fence cannot be closed by backticks in the log.
    fence = '`' * max(4, max((len(match.group()) for match in re.finditer(r'`+', value)), default=0) + 1)
    return [f'{fence}text', value, fence]


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
        lines.append(f'| {label} | {_cell(m.get(key, "NIGHTLY") if key == "scope" else m[key])} |')
    known = data['ko_know']
    # Product bugs take precedence when both links are present.
    product = [item for item in known if item['link_prod_bug']]
    suite = [item for item in known if not item['link_prod_bug'] and item['link_suite_bug']]
    # Defensive handling of legacy YAML: do not silently lose unlinked entries.
    other = [item for item in known if not item['link_prod_bug'] and not item['link_suite_bug']] + data['ko']
    groups = [
        (product, 'KO DOVUTI A BUG DI PRODOTTO NOTI'),
        (suite, 'KO DOVUTI A BUG DI SUITE NOTI'),
        (other, 'KO DA ANALIZZARE'),
    ]
    for items, title in groups:
        lines.extend(['', f'## {len(items)} - {title}', ''])
        if not items:
            lines.append('Nessun fallimento.')
            continue
        for index, item in enumerate(items, 1):
            if index > 1:
                lines.extend(['', '---', ''])
            lines.extend([
                f'### {index}. {_prose(item["id_test"])}', '',
                '**RESULT**', '', _prose(item['result']), '',
                '**PROCEDURE**', '', _prose(item['procedure']), '',
            ])
            for field, label in [('link_prod_bug', 'LINK PROD BUG'), ('link_suite_bug', 'LINK SUITE BUG')]:
                if item[field]:
                    lines.extend([f'**{label}:** {item[field]}', ''])
            lines.extend(['**ERROR LOG**', '', *_log_block(item['error_log']), ''])
    return '\n'.join(lines).rstrip('\n') + '\n'
