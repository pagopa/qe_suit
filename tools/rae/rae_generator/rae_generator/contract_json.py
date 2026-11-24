"""Read the versioned Java ContractReport export; never reclassify JUnit XML here."""
from __future__ import annotations

import json
from pathlib import Path

from .parsers import Failure, ReportError

STATUSES = {'PASSED', 'FAILED', 'SKIPPED', 'ABORTED'}


def collect_contract_failures(path: Path, *, all_ids: list[str] | None = None) -> list[Failure]:
    try:
        document = json.loads(path.read_text(encoding='utf-8'))
    except (OSError, UnicodeError, json.JSONDecodeError) as exc:
        raise ReportError(f'Contract JSON non valido: {path}: {exc}') from exc
    if not isinstance(document, dict) or document.get('schemaVersion') != 1:
        raise ReportError('Contract JSON: schemaVersion non supportata (attesa 1)')
    summary = document.get('summary')
    groups = document.get('groups')
    if not isinstance(summary, dict) or not isinstance(groups, list):
        raise ReportError('Contract JSON: summary/groups mancanti')
    counts = {status: 0 for status in STATUSES}
    seen: set[str] = set()
    failures: list[Failure] = []
    preconditions = 0
    for group in groups:
        if not isinstance(group, dict) or not isinstance(group.get('scenarios'), list):
            raise ReportError('Contract JSON: gruppo/scenari non validi')
        scenarios = group['scenarios']
        if group.get('scenarioCount') != len(scenarios):
            raise ReportError('Contract JSON: scenarioCount non coerente')
        for scenario in scenarios:
            if not isinstance(scenario, dict):
                raise ReportError('Contract JSON: scenario non valido')
            status = scenario.get('status')
            if status not in STATUSES:
                raise ReportError(f'Contract JSON: stato sconosciuto {status!r}')
            identity = scenario.get('id_test')
            if not isinstance(identity, str) or not identity.strip():
                raise ReportError('Contract JSON: id_test assente')
            if identity in seen:
                raise ReportError(f'Contract JSON: id_test duplicato: {identity}')
            seen.add(identity)
            if all_ids is not None:
                all_ids.append(identity)
            counts[status] += 1
            if scenario.get('preconditionFailure') is True:
                preconditions += 1
            if status == 'FAILED':
                diagnostic = (scenario.get('stackTrace') or scenario.get('failureMessage')
                              or scenario.get('failureType') or 'Contract failure without diagnostic')
                if not isinstance(diagnostic, str):
                    raise ReportError(f'Contract JSON: diagnostica non testuale: {identity}')
                failures.append(Failure(identity, diagnostic, 'contract-json'))
    if summary.get('total') != len(seen):
        raise ReportError(f"Contract JSON: total={summary.get('total')} != {len(seen)}")
    for status, key in [('PASSED', 'passed'), ('FAILED', 'failed'),
                        ('SKIPPED', 'skipped'), ('ABORTED', 'aborted')]:
        if summary.get(key) != counts[status]:
            raise ReportError(f'Contract JSON: {key}={summary.get(key)} != {counts[status]}')
    if summary.get('preconditionFailed') != preconditions:
        raise ReportError('Contract JSON: preconditionFailed non coerente')
    return failures
