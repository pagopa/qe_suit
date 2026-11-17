"""Build a single YAML per NRT execution using approved history."""
from __future__ import annotations

import logging
import re
import yaml
from datetime import datetime, timezone
from pathlib import Path

from .history import load_history
from .matching import choose_known
from .parsers import collect_reports

FIELDS = ('id_test', 'error_log', 'procedure', 'result', 'link_prod_bug', 'link_suite_bug')


class _LiteralString(str):
    """Mark multiline error logs for YAML literal block serialization."""


class _RAEDumper(yaml.SafeDumper):
    """Keep safe YAML serialization and customize only literal strings."""


def _represent_literal(dumper: yaml.SafeDumper, value: _LiteralString):
    return dumper.represent_scalar('tag:yaml.org,2002:str', str(value), style='|')


_RAEDumper.add_representer(_LiteralString, _represent_literal)


def _space_between_rae_items(yaml_text: str) -> str:
    """Add a visual separator between KO mapping items, never within error logs."""
    lines = []
    section = None
    seen_item = False

    for line in yaml_text.splitlines(keepends=True):
        if line in ('ko_know:\n', 'ko:\n'):
            section = line.rstrip('\n')
            seen_item = False
        elif line and not line.startswith((' ', '#', '\n', '\r', '- ')):
            section = None

        # PyYAML usually renders list items at column zero; other dumpers
        # may indent them two spaces. Literal log contents are deeper.
        if section and line.startswith(('- id_test:', '  - id_test:')):
            if seen_item:
                lines.append('\n')
            seen_item = True
        lines.append(line)

    return ''.join(lines)


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
        'rae': {'release': '', 'issue_jira': '', 'data_ora': data_ora,
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

    # Convert only multiline error logs to YAML literal blocks. Keep other
    # fields and the caller's input dictionary unchanged.
    formatted = data.copy()
    for section in ('ko_know', 'ko'):
        formatted[section] = []
        for entry in data.get(section, []):
            item = entry.copy()
            error_log = item.get('error_log')
            if isinstance(error_log, str) and '\n' in error_log:
                # PyYAML escapes tab characters inside literal blocks. Expand
                # stack-trace tabs to spaces so the YAML remains human-readable.
                item['error_log'] = _LiteralString(error_log.replace('\t', '    '))
            formatted[section].append(item)

    serialized = yaml.dump(formatted, Dumper=_RAEDumper, allow_unicode=True,
                           sort_keys=False, width=110)
    output.write_text(_space_between_rae_items(serialized), encoding='utf-8')
