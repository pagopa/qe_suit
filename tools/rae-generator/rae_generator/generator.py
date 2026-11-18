"""Build a single YAML per NRT execution using approved history."""
from __future__ import annotations
from datetime import datetime, timezone
from pathlib import Path
import logging
import re
import yaml
from .history import load_history
from .matching import choose_known
from .parsers import collect_reports

FIELDS = ('id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')


def create_rae(report_root: Path, history_dir: Path, *, suite: str, environment: str,
               executor: str, source_branch: str, run_url: str, data_ora: str,
               durata: str) -> dict:
    if not re.fullmatch(r'[A-Za-z0-9_.-]+', suite):
        raise ValueError('Runner name non valido')
    if environment not in ('qa', 'dev'):
        raise ValueError('Ambiente non valido')
    failures = collect_reports(report_root)
    history = load_history(history_dir, suite)
    data = {
        'rae': {'release': '', 'scope': 'NIGHTLY', 'issue_jira': '', 'data_ora': data_ora,
                'durata': durata, 'numero_ko': len(failures), 'run': run_url,
                'suite': suite, 'environment': environment, 'executor': executor,
                'source_branch': source_branch},
        'ko_know': [], 'ko': []
    }
    for failure in failures:
        previous = choose_known(failure.id_test, failure.error_log, history)
        if previous:
            logging.info('KO noto %s: storico=%s ambiente=%s suite=%s', failure.id_test,
                         previous.get('_source_file'), previous.get('_source_environment'),
                         previous.get('_source_suite'))
        entry = {'id_test': failure.id_test, 'error_log': failure.error_log}
        for field in FIELDS[2:]:
            entry[field] = str(previous.get(field, '') or '') if previous else ''
        data['ko_know' if previous else 'ko'].append(entry)
    return data


def write_yaml(data: dict, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(yaml.safe_dump(data, allow_unicode=True, sort_keys=False, width=110), encoding='utf-8')
