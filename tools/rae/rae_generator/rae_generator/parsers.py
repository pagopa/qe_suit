"""Cucumber JSON, JUnit/Surefire XML and JUnit Platform Open Test Reporting parsers."""
from __future__ import annotations

import json
import re
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class Failure:
    id_test: str
    error_log: str
    source: str


class ReportError(ValueError):
    pass


def _local(tag: str) -> str:
    return tag.rsplit('}', 1)[-1]


def _first_descendant(el: ET.Element, name: str) -> ET.Element | None:
    return next((x for x in el.iter() if x is not el and _local(x.tag) == name), None)


def _text(el: ET.Element | None) -> str:
    return ''.join(el.itertext()).strip() if el is not None else ''


def _example_suffix(scenario: dict, is_outline: bool) -> str:
    """Identify the expanded Examples row using Cucumber's own JSON ID.

    The Cucumber formatter emits IDs such as
    `feature;scenario;examples-group;2` for the FIRST data row. Its 1-based
    index also counts the table header, hence Example # = final index - 1.
    Use the group to avoid collisions across different Examples tables.
    When the format is absent/ambiguous, fall back to the feature source row.
    """
    if not is_outline:
        return ''
    cucumber_id = scenario.get('id')
    if isinstance(cucumber_id, str):
        parts = cucumber_id.rsplit(';', 2)
        if len(parts) == 3 and parts[0] and parts[-1].isdigit():
            group, raw_index = parts[1], int(parts[2])
            if raw_index >= 2:
                if group:
                    return f' [Examples: {group} | Example #{raw_index - 1}]'
                # Unnamed Examples tables may occur more than once. Keep the
                # source position to avoid merging identically numbered rows.
                line = scenario.get('line')
                if isinstance(line, int) and line > 0:
                    return f' [Examples: (senza nome) | Example #{raw_index - 1} | line: {line}]'
    line = scenario.get('line')
    if isinstance(line, int) and line > 0:
        return f' [example line: {line}]'
    return ''


def _parse_cucumber(path: Path) -> list[Failure]:
    try:
        content = json.loads(path.read_text(encoding='utf-8'))
    except (json.JSONDecodeError, UnicodeError) as exc:
        raise ReportError(f'Cucumber JSON non valido: {path}: {exc}') from exc
    if not isinstance(content, list):
        raise ReportError(f'Cucumber JSON deve essere una lista di feature: {path}')
    failures = []
    for feature in content:
        if not isinstance(feature, dict):
            raise ReportError(f'Feature Cucumber non valida: {path}')
        scenarios = feature.get('elements', [])
        if not isinstance(scenarios, list):
            raise ReportError(f'Elenco scenari Cucumber non valido: {path}')
        # Cucumber expands Scenario Outlines into one element per Examples row.
        # Count all rows (including passed ones) to recognize expanded outlines,
        # but derive the number from the formatter ID, not from KO ordering.
        name_counts: dict[str, int] = {}
        for scenario in scenarios:
            if not isinstance(scenario, dict):
                raise ReportError(f'Scenario Cucumber non valido: {path}')
            name = scenario.get('name')
            if name:
                name_counts[str(name)] = name_counts.get(str(name), 0) + 1
        for scenario in scenarios:
            failed_steps = []
            for step in list(scenario.get('before', [])) + list(scenario.get('steps', [])) + list(scenario.get('after', [])):
                result = step.get('result') or {}
                if (result.get('status') or '').lower() in {'failed', 'ambiguous', 'undefined'}:
                    msg = result.get('error_message') or result.get('message') or f"Step {step.get('name', '<unknown>')}: {result.get('status')}"
                    failed_steps.append(str(msg))
            if failed_steps:
                name = scenario.get('name') or scenario.get('id') or scenario.get('line')
                if not name:
                    raise ReportError(f'Scenario KO senza identificativo: {path}')
                test_id = str(name)
                # On expanded outlines, distinguish repeated scenario names by
                # their Examples source row. Some reporters explicitly label the
                # item as scenario_outline; support that even for one example.
                is_outline = (
                        name_counts.get(str(scenario.get('name')), 0) > 1
                        or str(scenario.get('type', '')).lower() == 'scenario_outline'
                        or str(scenario.get('keyword', '')).strip().lower()
                        in {'scenario outline', 'scenario template'}
                )
                test_id += _example_suffix(scenario, is_outline)
                failures.append(Failure(test_id, '\n\n'.join(failed_steps), 'cucumber'))
    return failures


def _parse_junit(path: Path, root: ET.Element) -> list[Failure]:
    failures = []
    if _local(root.tag) not in {'testsuite', 'testsuites'}:
        raise ReportError(f'JUnit XML senza root testsuite(s): {path}')
    for case in root.iter():
        if _local(case.tag) != 'testcase':
            continue
        failure_nodes = [x for x in case if _local(x.tag) in {'failure', 'error'}]
        for failure in failure_nodes:
            ident = case.get('name') or case.get('classname')
            if not ident:
                raise ReportError(f'Testcase KO senza nome: {path}')
            msg = _text(failure) or failure.get('message') or failure.get('type') or 'JUnit failure'
            failures.append(Failure(ident, msg, 'junit'))
    return failures



def _has_ancestor(event_id: str, ancestor_id: str, started: dict[str, ET.Element]) -> bool:
    visited = set()
    while event_id in started and event_id not in visited:
        visited.add(event_id)
        parent = started[event_id].get('parentId', '')
        if parent == ancestor_id:
            return True
        event_id = parent
    return False


def _junit_source(start: ET.Element | None) -> tuple[str, str]:
    """Read Java source metadata rather than guessing from display names."""
    if start is None:
        return '', ''
    for element in start.iter():
        if _local(element.tag) == 'methodSource':
            return element.get('className', ''), element.get('methodName', '')
    return '', ''


def _junit_unique_id(start: ET.Element | None) -> str:
    if start is None:
        return ''
    for element in start.iter():
        if _local(element.tag) == 'uniqueId':
            return _text(element)
    return ''


def _contract_test_id(event_id: str, started: dict[str, ET.Element]) -> str:
    """For JUnit dynamic tests, use Class::FactoryMethod::DynamicTestName.

    Only classify as a dynamic test when its parent is a method/factory
    container or its JUnit uniqueId explicitly denotes a dynamic test.
    Other JUnit tests preserve their existing display-name IDs.
    """
    leaf = started[event_id]
    display_name = leaf.get('name', '').strip()
    parent = started.get(leaf.get('parentId', ''))
    class_name, method_name = _junit_source(parent)
    is_dynamic = bool(method_name)

    uid = _junit_unique_id(leaf)
    if '[dynamic-test:' in uid or '[dynamic-container:' in uid:
        is_dynamic = True
    if not is_dynamic:
        return display_name

    # In real Open Test XML methodSource may live on the child as well.
    if not method_name:
        class_name, method_name = _junit_source(leaf)
    if not class_name or not method_name:
        # Source metadata is missing: do not invent a method name.
        return display_name
    return f'{class_name.rsplit(".", 1)[-1]}::{method_name}::{display_name}'


def _parse_open_test(path: Path, root: ET.Element) -> list[Failure]:
    if _local(root.tag) != 'events':
        raise ReportError(f'JUnit Open Test XML senza root events: {path}')
    started: dict[str, ET.Element] = {}
    finished = []
    for ev in root:
        name = _local(ev.tag)
        if name == 'started':
            started[ev.get('id', '')] = ev
        elif name == 'finished':
            finished.append(ev)
    failed = {}
    for event in finished:
        result = next((x for x in event if _local(x.tag) == 'result'), None)
        if result is not None and (result.get('status') or '').lower() in {'failed', 'aborted'}:
            failed[event.get('id', '')] = result
    failures = []
    for event_id, result in failed.items():
        start = started.get(event_id)
        # A failed container often aggregates the failure of a child test.
        # Emit the child only; keep failed containers that have no failed child.
        if any(_has_ancestor(other, event_id, started) for other in failed if other != event_id):
            continue
        throwable = _first_descendant(result, 'throwable')
        if throwable is None:
            raise ReportError(f'JUnit Open Test KO senza diagnostica per id={event_id}: {path}')
        name = start.get('name', '') if start is not None else ''
        if not name:
            raise ReportError(f'JUnit Open Test KO senza nome: {path}')
        log = _text(throwable) or throwable.get('type') or 'JUnit failure'
        failures.append(Failure(_contract_test_id(event_id, started), log, 'junit-open'))
    return failures


def parse_file(path: Path) -> list[Failure]:
    if path.suffix.lower() == '.json':
        return _parse_cucumber(path)
    if path.suffix.lower() != '.xml':
        raise ReportError(f'Formato report non supportato: {path}')
    try:
        raw = path.read_text(encoding='utf-8-sig')
        if re.search(r'<!DOCTYPE|<!ENTITY', raw, flags=re.I):
            raise ReportError(f'DTD/ENTITY non consentite: {path}')
        root = ET.fromstring(raw)
    except (ET.ParseError, UnicodeError) as exc:
        raise ReportError(f'XML non valido: {path}: {exc}') from exc
    return _parse_open_test(path, root) if _local(root.tag) == 'events' else _parse_junit(path, root)


def _idkey(s: str) -> str:
    return re.sub(r'\W+', '', s.casefold())


def collect_reports(root: Path) -> list[Failure]:
    """Use Cucumber JSON and the Java ContractReport JSON; JUnit XML is not an input.

    Both sources are required: missing contract export must fail closed rather
    than silently undercounting contract failures.
    """
    from .contract_json import collect_contract_failures
    cucumber_dir = root / 'cucumber-reports'
    cucumber_files = sorted(cucumber_dir.glob('*.json'))
    contract = root / 'contract-test' / 'report.json'
    if not cucumber_files:
        raise ReportError(f'Nessun report Cucumber JSON in: {cucumber_dir}')
    if not contract.is_file():
        raise ReportError(f'Contract JSON mancante: {contract}')
    cucumber_failures = []
    for cucumber in cucumber_files:
        cucumber_failures.extend(_parse_cucumber(cucumber))
    contract_failures = collect_contract_failures(contract)
    result = cucumber_failures + contract_failures
    ids: set[str] = set()
    for failure in result:
        if failure.id_test in ids:
            raise ReportError(f'ID KO duplicato tra report: {failure.id_test}')
        ids.add(failure.id_test)
    return result
