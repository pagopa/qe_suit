"""Regression tests for overlapping Cucumber reports in an NRT artifact."""
import json

import pytest

from rae_generator.parsers import ReportError, collect_reports


def _report(root, name, rows):
    folder = root / "cucumber-reports"
    folder.mkdir(parents=True, exist_ok=True)
    scenarios = [
        {
            "name": test_id,
            "type": "scenario",
            "steps": [{"result": {"status": "failed", "error_message": error}}],
        }
        for test_id, error in rows
    ]
    (folder / name).write_text(json.dumps([{"elements": scenarios}]), encoding="utf-8")


def test_duplicate_ids_from_different_reports_keep_both(tmp_path):
    _report(tmp_path, "cucumber-report-nrt-bff-m2mv2.json", [("SAME", "same stack")])
    _report(tmp_path, "cucumber-report-m2mv3.json", [("SAME", "same stack")])
    failures = collect_reports(tmp_path)
    assert len(failures) == 2
    assert {x.id_test for x in failures} == {
        "SAME [cucumber-report-nrt-bff-m2mv2.json]",
        "SAME [cucumber-report-m2mv3.json]",
    }
    assert {x.error_log for x in failures} == {"same stack"}


def test_unique_id_is_not_modified(tmp_path):
    _report(tmp_path, "v2.json", [("UNIQUE", "error")])
    assert [x.id_test for x in collect_reports(tmp_path)] == ["UNIQUE"]


def test_different_errors_are_both_preserved(tmp_path):
    _report(tmp_path, "v2.json", [("SAME", "error V2")])
    _report(tmp_path, "v3.json", [("SAME", "error V3")])
    assert {(x.id_test, x.error_log) for x in collect_reports(tmp_path)} == {
        ("SAME [v2.json]", "error V2"),
        ("SAME [v3.json]", "error V3"),
    }


def test_duplicate_in_same_report_remains_an_error(tmp_path):
    _report(tmp_path, "v3.json", [("SAME", "first"), ("SAME", "second")])
    with pytest.raises(ReportError, match="duplicato anche dopo il suffisso"):
        collect_reports(tmp_path)
