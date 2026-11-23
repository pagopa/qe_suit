"""Regression tests: no dependence between Cucumber and Contract report inputs."""
import json

import pytest

from rae_generator.parsers import ReportError, collect_reports
from rae_generator.generator import create_rae


def _cucumber(root, *, failed=True):
    directory = root / 'cucumber-reports'
    directory.mkdir(exist_ok=True)
    data = [{'name': 'Example feature', 'elements': [{
        'name': 'Scenario Cucumber',
        'steps': [{'name': 'step', 'result': {
            'status': 'failed' if failed else 'passed',
            'error_message': 'AssertionError: cucumber',
        }}],
    }]}]
    path = directory / 'results.json'
    path.write_text(json.dumps(data), encoding='utf-8')
    return path


def _contract(root, *, failed=True):
    directory = root / 'contract-test'
    directory.mkdir(exist_ok=True)
    status = 'FAILED' if failed else 'PASSED'
    data = {
        'schemaVersion': 1,
        'summary': {'total': 1, 'passed': 0 if failed else 1,
                    'failed': 1 if failed else 0, 'skipped': 0,
                    'aborted': 0, 'preconditionFailed': 0},
        'groups': [{'scenarioCount': 1, 'scenarios': [{
            'id_test': 'ContractTest::testCase::scenario', 'status': status,
            'stackTrace': 'AssertionError: contract',
            'preconditionFailure': False,
        }]}],
    }
    path = directory / 'report.json'
    path.write_text(json.dumps(data), encoding='utf-8')
    return path


def test_only_cucumber(tmp_path):
    _cucumber(tmp_path)
    failures = collect_reports(tmp_path)
    assert [(f.id_test, f.source) for f in failures] == [
        ('Scenario Cucumber', 'cucumber')]


def test_only_contract(tmp_path):
    _contract(tmp_path)
    failures = collect_reports(tmp_path)
    assert [(f.id_test, f.source) for f in failures] == [
        ('ContractTest::testCase::scenario', 'contract-json')]


def test_both_sources_are_aggregated(tmp_path):
    _cucumber(tmp_path)
    _contract(tmp_path)
    assert [f.source for f in collect_reports(tmp_path)] == ['cucumber', 'contract-json']


def test_only_cucumber_zero_failures(tmp_path):
    _cucumber(tmp_path, failed=False)
    assert collect_reports(tmp_path) == []


def test_only_contract_zero_failures(tmp_path):
    _contract(tmp_path, failed=False)
    assert collect_reports(tmp_path) == []


def test_no_reports_fails_not_silent_zero_ko(tmp_path):
    with pytest.raises(ReportError, match='Nessun report'):
        collect_reports(tmp_path)


def test_unrelated_html_or_xml_not_counted_as_reports(tmp_path):
    d = tmp_path / 'contract-test'
    d.mkdir()
    (d / 'report.html').write_text('<html></html>', encoding='utf-8')
    (tmp_path / 'TEST-test.xml').write_text('<testsuite/>', encoding='utf-8')
    with pytest.raises(ReportError, match='Nessun report'):
        collect_reports(tmp_path)


def test_invalid_present_cucumber_fails_even_with_valid_contract(tmp_path):
    _cucumber(tmp_path).write_text('{not-json', encoding='utf-8')
    _contract(tmp_path)
    with pytest.raises(ReportError, match='Cucumber JSON non valido'):
        collect_reports(tmp_path)


def test_invalid_present_contract_fails_even_with_valid_cucumber(tmp_path):
    _cucumber(tmp_path)
    _contract(tmp_path).write_text('{not-json', encoding='utf-8')
    with pytest.raises(ReportError, match='Contract JSON non valido'):
        collect_reports(tmp_path)


def test_contract_wrong_summary_fails_alone(tmp_path):
    path = _contract(tmp_path)
    data = json.loads(path.read_text(encoding='utf-8'))
    data['summary']['failed'] = 5
    path.write_text(json.dumps(data), encoding='utf-8')
    with pytest.raises(ReportError, match='failed'):
        collect_reports(tmp_path)


def test_duplicate_ids_across_sources_get_report_suffix(tmp_path):
    _cucumber(tmp_path)
    path = _contract(tmp_path)
    data = json.loads(path.read_text(encoding='utf-8'))
    data['groups'][0]['scenarios'][0]['id_test'] = 'Scenario Cucumber'
    path.write_text(json.dumps(data), encoding='utf-8')
    failures = collect_reports(tmp_path)

    assert [(f.id_test, f.source) for f in failures] == [
        ('Scenario Cucumber [results.json]', 'cucumber'),
        ('Scenario Cucumber [report.json]', 'contract-json'),
    ]


@pytest.mark.parametrize('source', ['cucumber', 'contract'])
def test_create_rae_with_single_source(tmp_path, source):
    if source == 'cucumber':
        _cucumber(tmp_path)
    else:
        _contract(tmp_path)
    result = create_rae(
        tmp_path, tmp_path / 'history', suite='NrtStandardSuiteTest',
        environment='qa', executor='qa', source_branch='develop',
        run_url='https://example.com', data_ora='2026-11-20T00:00:00Z',
        durata='00:01:00',
    )
    assert result['rae']['numero_ko'] == 1
    assert len(result['ko']) + len(result['ko_know']) == 1
