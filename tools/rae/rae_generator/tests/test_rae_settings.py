"""Regression tests for RAE identity settings."""
import pytest
from rae_generator.rae_settings import load_append_report_name


def test_missing_settings_defaults_to_false(tmp_path):
    assert load_append_report_name(tmp_path / 'missing.yaml') is False


@pytest.mark.parametrize('flag', ['true', 'false'])
def test_valid_boolean_settings(tmp_path, flag):
    path = tmp_path / 'settings.yaml'
    path.write_text('version: 1\ntest_ids:\n  append_report_name: ' + flag + '\n')
    assert load_append_report_name(path) is (flag == 'true')


@pytest.mark.parametrize('content', [
    'version: 1\ntest_ids:\n  append_report_name: yes\n',
    'version: 1\ntest_ids:\n  append_report_name: "true"\n',
    'version: 2\ntest_ids:\n  append_report_name: true\n',
    'version: 1\ntest_ids:\n  append_report_name: true\n  extra: 3\n',
    'version: 1\n',
    'not: [valid',
])
def test_invalid_settings_rejected(tmp_path, content):
    path = tmp_path / 'settings.yaml'
    path.write_text(content)
    with pytest.raises(ValueError, match='RAE settings'):
        load_append_report_name(path)
