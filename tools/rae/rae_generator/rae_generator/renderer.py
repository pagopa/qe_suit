"""Deterministic Markdown renderer; YAML is the only mutable source of truth."""
from __future__ import annotations
import re
from .qa_confirmation import is_qa_confirmed, original_entries_by_id
from .review_evidence import render_warning  # RAE_REVIEW_PANEL_V1


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


def render(data: dict, *, original_data: dict | None = None, qa_reviewed: bool = False) -> str:
    originals = original_entries_by_id(original_data) if original_data is not None else {}
    m = data['rae']
    date = str(m['data_ora'])[:10].replace('-', '')
    lines = [
        f'# {date} Report Analisi Esiti (RAE) {m["suite"]} {m["environment"].upper()}', '',
        '| Campo | Valore |', '| --- | --- |',
    ]
    labels = [('release', 'Release'), ('scope', 'Scope'), ('issue_jira', 'Issue Jira'), ('data_ora', 'Data e ora'),
              ('durata', 'Durata'), ('numero_ko', 'Numero KO'), ('run', 'Run GitHub Actions'),
              ('suite', 'Suite'), ('environment', 'Ambiente'), ('executor', 'Executor'),
              ('source_branch', 'Source branch')]
    for key, label in labels:
        lines.append(f'| {label} | {_cell(m.get(key, "NIGHTLY") if key == "scope" else m[key])} |')
    lines.extend([
        '', '### Guida alla compilazione dei campi', '',
        '- **Result** *(obbligatorio)*: descrivere il motivo del KO e l’esito dell’analisi in modo chiaro e sintetico, senza ripercorrere i singoli passaggi dell’indagine.',
        '- **Procedure** *(obbligatorio)*: documentare passo per passo le verifiche svolte per arrivare al risultato. Se è già stato effettuato un intervento, includere il link alla PR o al ticket relativo.',
        '- **BUG SUITE**: indicare il ticket Jira relativo a un difetto della suite di test. Obbligatorio se non è presente un BUG PRODOTTO, salvo i casi motivati nella nota.',
        '- **BUG PRODOTTO**: indicare il ticket Jira relativo a un difetto del prodotto. Se il problema riguarda componenti esterni (ad esempio SelfCare) e non esiste un ticket, spiegare il motivo nel campo Result. Obbligatorio se non è presente un BUG SUITE, salvo i casi motivati nella nota.',
        '',
        '> **Nota:** in assenza di ticket specifici, l’analisi si basa esclusivamente sulle informazioni riportate in **Result** e **Procedure**.',
    ])
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
        lines.extend([
            '', f'## {len(items)} - {title}', '',
            '<details>',
            f'<summary>Mostra/nascondi {len(items)} KO</summary>', '',
        ])
        if not items:
            lines.extend(['Nessun fallimento.', '', '</details>'])
            continue
        for index, item in enumerate(items, 1):
            if index > 1:
                lines.extend(['', '---', ''])
            lines.extend([f'### {index}. {_prose(item["id_test"])}', ''])
            matching = item.get('matching')
            if (isinstance(matching, dict) and matching.get('status') == 'pending_confirmation'
                    and not is_qa_confirmed(item, original=originals.get(str(item.get('id_test', ''))),
                                            qa_reviewed=qa_reviewed)):
                lines.extend(render_warning(matching))
            lines.extend([
                '**RESULT**', '', _prose(item['result']), '',
                '**PROCEDURE**', '', _prose(item['procedure']), '',
            ])
            initial = originals.get(str(item.get('id_test', '')))
            if is_qa_confirmed(item, original=initial, qa_reviewed=qa_reviewed):
                lines.extend([
                    '> **ℹ️ Validazione finale prima dell’automazione**',
                    '>',
                    '> L’analisi di questo KO è stata precompilata automaticamente sulla base di un '
                    'fallimento precedentemente analizzato e confermata dall’analista QA '
                    'nell’ambito della presente RAE.',
                    '>',
                    '> Questa validazione rappresenta il passaggio conclusivo prima dell’automazione: '
                    '**nelle prossime esecuzioni, il KO potrà essere riconosciuto e classificato '
                    'automaticamente**, qualora si ripresenti con le medesime caratteristiche.', '',
                ])
            elif isinstance(matching, dict) and matching.get('status') == 'auto_matched':
                lines.extend(['**MATCH AUTOMATICO CON PRECEDENTE CONFERMATO**', ''])
            for field, label in [('link_prod_bug', 'LINK PROD BUG'), ('link_suite_bug', 'LINK SUITE BUG')]:
                if item[field]:
                    lines.extend([f'**{label}:** {item[field]}', ''])
            lines.extend(['**ERROR LOG**', '', *_log_block(item['error_log']), ''])
        lines.append('</details>')
    return '\n'.join(lines).rstrip('\n') + '\n'
