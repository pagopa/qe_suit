"""Cucumber JSON, JUnit/Surefire XML and JUnit Platform Open Test Reporting parsers."""
from __future__ import annotations

from dataclasses import dataclass
import json
from pathlib import Path
import re
import xml.etree.ElementTree as ET


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
        for scenario in feature.get('elements', []):
            if not isinstance(scenario, dict):
                raise ReportError(f'Scenario Cucumber non valido: {path}')
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
                failures.append(Failure(str(name), '\n\n'.join(failed_steps), 'cucumber'))
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
        failures.append(Failure(name, log, 'junit-open'))
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
    """Parse all known files; fail closed when all inputs are absent or any is invalid.

    Cucumber has precedence on overlapping JUnit records. Cross-format duplicate
    detection requires both matching ID and comparable failure text; unrelated
    tests with identical stack traces remain separate.
    """
    files: list[Path] = []
    for name in ('cucumber-reports', 'surefire-reports', 'junit-platform-reports'):
        folder = root / name
        if folder.is_dir():
            for path in sorted(folder.rglob('*')):
                if not path.is_file():
                    continue
                if name == 'cucumber-reports' and path.suffix == '.json':
                    files.append(path)
                elif name == 'surefire-reports' and path.name.startswith('TEST-') and path.suffix == '.xml':
                    files.append(path)
                elif name == 'junit-platform-reports' and path.name == 'open-test-report.xml':
                    files.append(path)
    if not files:
        raise ReportError(f'Nessun report Cucumber/JUnit trovato in {root}')
    parsed = []
    for path in files:
        parsed.extend(parse_file(path))
    # Prefer cucumber diagnostics when exact duplicates exist across report formats.
    parsed.sort(key=lambda f: (f.source != 'cucumber', f.id_test.casefold()))
    unique: list[Failure] = []
    seen = set()
    for f in parsed:
        key = (_idkey(f.id_test), _error_key(f.error_log))
        if key not in seen:
            seen.add(key)
            unique.append(f)
    return unique


def _error_key(s: str) -> str:
    # File paths and line numbers may differ between reporting engines;
    # compare the primary diagnostic line, retaining full original logs in YAML.
    return re.sub(r'\s+', ' ', s.strip().splitlines()[0] if s.strip() else '').casefold()
