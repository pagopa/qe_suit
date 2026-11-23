"""Regression tests for release and scope metadata in the RAE pipeline."""
from copy import deepcopy
import pytest
import yaml
from rae_generator.validator import validate, ValidationError
from rae_generator.renderer import render
from rae_generator.generator import write_yaml


def sample():
    return {
        'rae': {
            'release': '', 'scope': 'NIGHTLY', 'issue_jira': 'https://jira.local/QA-18253',
            'data_ora': '2026-11-16T18:00:00Z', 'durata': '00:01:13',
            'numero_ko': 1, 'run': 'https://github.com/a/b/actions/runs/1',
            'suite': 'NrtStandardSuiteTest', 'environment': 'qa',
            'executor': 'qa-user', 'source_branch': 'develop',
        },
        'ko_know': [],
        'ko': [{'id_test': 'Test A', 'error_log': 'AssertionError: unexpected',
                'procedure': 'Rieseguire il test', 'result': 'Errore confermato',
                'link_prod_bug': '', 'link_suite_bug': ''}],
    }


@pytest.mark.parametrize('scope', ['NO-PROMO', 'NIGHTLY', 'PROMO'])
def test_scope_enum_valid(scope):
    data = sample()
    data['rae']['scope'] = scope
    assert validate(data) is data
    md = render(data)
    assert f'| Scope | {scope} |' in md
    assert md.index('| Release |') < md.index('| Scope |') < md.index('| Issue Jira |')


def test_release_optional_but_must_be_string():
    data = sample()
    data['rae']['release'] = ''
    validate(data)
    data['rae']['release'] = '  '
    validate(data)
    data['rae']['release'] = None
    with pytest.raises(ValidationError, match='release'):
        validate(data)


def test_legacy_without_scope_defaults_to_nightly():
    data = sample()
    del data['rae']['scope']
    validate(data)
    assert '| Scope | NIGHTLY |' in render(data)


@pytest.mark.parametrize('scope', ['PROMO ', 'nightly', 'STAGING', None, 123])
def test_invalid_scope_rejected(scope):
    data = sample()
    data['rae']['scope'] = scope
    with pytest.raises(ValidationError, match='scope'):
        validate(data)


def test_yaml_round_trip_scope_and_release(tmp_path):
    data = sample()
    path = tmp_path / 'rae_NrtStandardSuiteTest_20261116180000.yaml'
    write_yaml(data, path)
    loaded = yaml.safe_load(path.read_text(encoding='utf-8'))
    assert list(loaded['rae'])[:2] == ['release', 'scope']
    assert loaded['rae']['release'] == ''
    assert loaded['rae']['scope'] == 'NIGHTLY'
    validate(loaded, path.name)
