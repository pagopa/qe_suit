import json
from pathlib import Path
import pytest
import yaml
from rae_generator.namespace import namespace_dir, validate_namespace, validate_output_path
from rae_generator.history import load_history
from rae_generator.cli import main


def _report(suite, result):
    return {'rae': {'suite': suite, 'environment': 'qa'}, 'ko': [
        {'id_test': 'Test A', 'error_log': 'AssertionError: mismatch',
         'procedure': 'check', 'result': result, 'link_suite_bug': 'https://jira.local/QA-1'}],
         'ko_know': []}


def test_history_never_crosses_namespace(tmp_path):
    for namespace, result in (('interop', 'interop-result'), ('send', 'send-result')):
        folder = namespace_dir(tmp_path / 'rae', namespace)
        folder.mkdir(parents=True)
        (folder / 'rae_NrtStandardSuiteTest_20261119000000.yaml').write_text(
            yaml.safe_dump(_report('NrtStandardSuiteTest', result)))
    (tmp_path/'rae'/'rae_NrtStandardSuiteTest_20261119000000.yaml').write_text(
        yaml.safe_dump(_report('NrtStandardSuiteTest', 'legacy-result')))
    interop = load_history(namespace_dir(tmp_path/'rae', 'interop'), 'NrtStandardSuiteTest')
    send = load_history(namespace_dir(tmp_path/'rae', 'send'), 'NrtStandardSuiteTest')
    assert [x['result'] for x in interop] == ['interop-result']
    assert [x['result'] for x in send] == ['send-result']
    assert load_history(namespace_dir(tmp_path/'rae', 'other'), 'NrtStandardSuiteTest') == []


@pytest.mark.parametrize('namespace', ['', '../send', 'interop/send', '.', '..', 'foo\\bar', 'a b'])
def test_reject_invalid_namespace(namespace):
    with pytest.raises(ValueError):
        validate_namespace(namespace)


def test_reject_output_outside_namespace(tmp_path):
    root = tmp_path/'rae'
    good = root/'interop'/'rae_Suite_20261119000000.yaml'
    assert validate_output_path(root, 'interop', good) == good
    for bad in (root/'send'/'rae_Suite_20261119000000.yaml',
                root/'rae_Suite_20261119000000.yaml',
                root/'interop'/'wrong.yaml'):
        with pytest.raises(ValueError):
            validate_output_path(root, 'interop', bad)


def test_cli_generate_requires_namespace(tmp_path):
    args = ['generate','--reports',str(tmp_path), '--history',str(tmp_path/'rae'),
            '--suite','NrtStandardSuiteTest','--environment','qa','--executor','qa',
            '--source-branch','develop','--run-url','https://example.org',
            '--metadata',str(tmp_path/'meta.json'),
            '--output',str(tmp_path/'rae'/'interop'/'rae_NrtStandardSuiteTest_20261119000000.yaml')]
    with pytest.raises(SystemExit) as ex:
        main(args)
    assert ex.value.code == 2
