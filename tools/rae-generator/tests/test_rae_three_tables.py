"""Regression cases for fingerprint-only promotion and ticket-backed history."""
import pytest
import yaml
from copy import deepcopy
from pathlib import Path
from rae_generator import generator
from rae_generator.history import load_history
from rae_generator.matching import choose_known_with_level
from rae_generator.renderer import render
from rae_generator.validator import validate, ValidationError

PROD = 'https://pagopa.atlassian.net/browse/QA-PROD'
SUITE = 'https://pagopa.atlassian.net/browse/QA-SUITE'


def record(id_test='Test A', error='java.lang.AssertionError: failed at Test.java:17',
           prod='', suite='', match='none'):
    return {
        'id_test': id_test, 'error_log': error, 'match_type': match,
        'procedure': 'Consultati log e report', 'result': 'Verificato KO',
        'link_prod_bug': prod, 'link_suite_bug': suite,
    }


def report(entries, *, known=()):
    return {
        'rae': {
            'release': 'v1', 'issue_jira': 'https://jira.local/QA-18253',
            'data_ora': '2026-11-17T10:00:00Z', 'durata': '00:01:00',
            'numero_ko': len(entries) + len(known),
            'run': 'https://github.com/pagopa/qe_suit/actions/runs/1',
            'suite': 'NrtStandardSuiteTest', 'environment': 'qa',
            'executor': 'qa', 'source_branch': 'develop',
        },
        'ko_know': list(known), 'ko': list(entries),
    }


def table_rows(md, title):
    section = md.split('## ' + title + '\n', 1)[1].split('\n## ', 1)[0]
    return [line for line in section.splitlines() if line.startswith('| ')][2:]


def test_three_tables_and_product_priority_without_duplicates():
    rows = [
        record('A', prod=PROD, match='exact'),
        record('B', suite=SUITE, match='exact'),
        record('C', prod=PROD, suite=SUITE, match='exact'),
        record('D', prod=PROD, match='similar'),
        record('E', prod=PROD, match='none'),
        record('F', match='exact'),
    ]
    md = render(validate(report(rows)))
    product = table_rows(md, 'KO - Bug di prodotto')
    suite = table_rows(md, 'KO - Bug di suite')
    analysis = table_rows(md, 'KO - Da analizzare')
    assert len(product) == 2 and all(key in '\n'.join(product) for key in ('A', 'C'))
    assert len(suite) == 1 and 'B' in suite[0]
    assert len(analysis) == 3 and all(key in '\n'.join(analysis) for key in ('D', 'E', 'F'))
    assert sum(map(len, (product, suite, analysis))) == 6


def test_legacy_yaml_is_never_silently_promoted():
    old = record(prod=PROD, match='exact')
    del old['match_type']
    md = render(validate(report([old])))
    assert len(table_rows(md, 'KO - Da analizzare')) == 1
    assert not table_rows(md, 'KO - Bug di prodotto')


def test_validator_accepts_old_and_rejects_invalid_provenance():
    doc = report([record()])
    assert validate(doc) is doc
    no_field = deepcopy(doc)
    del no_field['ko'][0]['match_type']
    validate(no_field)
    bad = deepcopy(doc)
    bad['ko'][0]['match_type'] = 'guess'
    with pytest.raises(ValidationError, match='match_type'):
        validate(bad)


def test_history_requires_a_bug_link_and_completed_analysis(tmp_path):
    folder = tmp_path / 'rae'
    folder.mkdir()
    rows = [record('No ticket'), record('Prod', prod=PROD), record('Suite', suite=SUITE)]
    rows.append(record('Empty procedure', prod=PROD))
    rows[-1]['procedure'] = ''
    yaml_path = folder / 'rae_NrtStandardSuiteTest_20261117100000.yaml'
    yaml_path.write_text(yaml.safe_dump(report(rows)), encoding='utf-8')
    history = load_history(folder, 'NrtStandardSuiteTest')
    assert {item['id_test'] for item in history} == {'Prod', 'Suite'}


def test_matcher_exposes_exact_similarity_none():
    old = record(error='java.lang.AssertionError: value expected 401 but was 404 at X.java:17', prod=PROD)
    candidate, kind = choose_known_with_level(
        old['id_test'], old['error_log'].replace(':17', ':99'), [old]
    )
    assert candidate is old and kind == 'exact'
    candidate, kind = choose_known_with_level('Unrelated', old['error_log'], [old])
    assert candidate is None and kind == 'none'
    # TF-IDF layer uses the same exception signature but a changed message.
    fuzzy = old['error_log'].replace('was 404', 'was 405')
    candidate, kind = choose_known_with_level(old['id_test'], fuzzy, [old], threshold=0.6)
    assert candidate is old and kind == 'similar'


def test_generation_provenance_survives_manual_ticket_entry(monkeypatch, tmp_path):
    from rae_generator.parsers import Failure
    error = 'java.lang.AssertionError: wrong state at Test.java:17'
    failures = [Failure(id_test='Test A', error_log=error, source='cucumber')]
    monkeypatch.setattr(generator, 'collect_reports', lambda _: failures)
    monkeypatch.setattr(generator, 'load_history', lambda *_: [])
    opts = dict(suite='NrtStandardSuiteTest', environment='qa', executor='qa',
                source_branch='develop', run_url='https://example.org',
                data_ora='2026-11-17T10:00:00Z', durata='00:01:00')
    first = generator.create_rae(tmp_path, tmp_path / 'rae', **opts)
    assert first['ko'][0]['match_type'] == 'none'
    first['rae'].update(release='v1', issue_jira='https://jira.local/QA-18253')
    first['ko'][0].update(procedure='Verificati log e HTTP status',
                          result='Bug applicativo', link_prod_bug=PROD)
    assert len(table_rows(render(validate(first)), 'KO - Da analizzare')) == 1
    folder = tmp_path / 'rae'
    folder.mkdir()
    path = folder / 'rae_NrtStandardSuiteTest_20261117100000.yaml'
    generator.write_yaml(first, path)
    monkeypatch.setattr(generator, 'load_history', load_history)
    second = generator.create_rae(tmp_path, folder, **opts)
    assert second['ko_know'][0]['match_type'] == 'exact'
    assert second['ko_know'][0]['link_prod_bug'] == PROD
    second['rae'].update(release='v1', issue_jira='https://jira.local/QA-18253')
    assert len(table_rows(render(validate(second)), 'KO - Bug di prodotto')) == 1


def test_similarity_prefills_but_stays_in_analysis(monkeypatch, tmp_path):
    from rae_generator.parsers import Failure
    old = record(
        error='java.lang.AssertionError: expected 200 but got 503 at X.java:17 while processing agreement verification in contract suite',
        prod=PROD)
    new_error = 'java.lang.AssertionError: expected 200 but got 502 at X.java:17 while processing agreement verification in contract suite'
    monkeypatch.setattr(generator, 'collect_reports',
                        lambda _: [Failure(id_test='Test A', error_log=new_error, source='cucumber')])
    monkeypatch.setattr(generator, 'load_history', lambda *_: [old])
    rae = generator.create_rae(
        tmp_path, tmp_path / 'rae', suite='NrtStandardSuiteTest', environment='qa',
        executor='qa', source_branch='develop', run_url='https://example.org',
        data_ora='2026-11-17T10:00:00Z', durata='00:01:00'
    )
    rae['rae'].update(release='v1', issue_jira='https://jira.local/QA-18253')
    assert len(rae['ko_know']) == 1
    assert rae['ko_know'][0]['match_type'] == 'similar'
    assert rae['ko_know'][0]['link_prod_bug'] == PROD
    assert len(table_rows(render(validate(rae)), 'KO - Da analizzare')) == 1
    assert not table_rows(render(rae), 'KO - Bug di prodotto')


def test_markdown_determinism_and_count():
    data = validate(report([record('A'), record('B', suite=SUITE, match='exact')]))
    assert render(data) == render(data)
    assert data['rae']['numero_ko'] == 2
    assert render(data).count('## KO - ') == 3
