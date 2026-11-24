"""Regression tests for overlapping Cucumber reports in an NRT artifact."""
import json

import pytest

from rae_generator.parsers import ReportError, collect_reports


def _report(root, name, rows, *, passed=()):
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
    scenarios.extend(
        {"name": test_id, "type": "scenario", "steps": [{"result": {"status": "passed"}}]}
        for test_id in passed
    )
    (folder / name).write_text(json.dumps([{"elements": scenarios}]), encoding="utf-8")


def test_duplicate_ids_from_different_reports_keep_both(tmp_path):
    _report(tmp_path, "cucumber-report-nrt-bff-m2mv2.json", [("SAME", "same stack")])
    _report(tmp_path, "cucumber-report-m2mv3.json", [("SAME", "same stack")])
    failures = collect_reports(tmp_path, append_report_name=True)
    assert len(failures) == 2
    assert {x.id_test for x in failures} == {
        "SAME [cucumber-report-nrt-bff-m2mv2.json]",
        "SAME [cucumber-report-m2mv3.json]",
    }
    assert {x.error_log for x in failures} == {"same stack"}


def test_unique_id_is_not_modified(tmp_path):
    _report(tmp_path, "v2.json", [("UNIQUE", "error")])
    assert [x.id_test for x in collect_reports(tmp_path, append_report_name=True)] == ["UNIQUE [v2.json]"]


def test_different_errors_are_both_preserved(tmp_path):
    _report(tmp_path, "v2.json", [("SAME", "error V2")])
    _report(tmp_path, "v3.json", [("SAME", "error V3")])
    assert {(x.id_test, x.error_log) for x in collect_reports(tmp_path, append_report_name=True)} == {
        ("SAME [v2.json]", "error V2"),
        ("SAME [v3.json]", "error V3"),
    }


def test_duplicate_in_same_report_remains_an_error(tmp_path):
    _report(tmp_path, "v3.json", [("SAME", "first"), ("SAME", "second")])
    with pytest.raises(ReportError, match="ID KO duplicato"):
        collect_reports(tmp_path, append_report_name=True)


def test_id_stays_stable_when_other_report_passes(tmp_path):
    _report(tmp_path, "v2.json", [("SAME", "error V2")])
    _report(tmp_path, "v3.json", [("SAME", "error V3")])
    initial = {f.id_test for f in collect_reports(tmp_path, append_report_name=True)}
    assert initial == {"SAME [v2.json]", "SAME [v3.json]"}

    _report(tmp_path, "v3.json", [], passed=["SAME"])
    failures = collect_reports(tmp_path, append_report_name=True)
    assert [(f.id_test, f.error_log) for f in failures] == [
        ("SAME [v2.json]", "error V2")
    ]


def test_id_is_suffixed_when_other_report_has_only_passed_scenario(tmp_path):
    _report(tmp_path, "v2.json", [], passed=["SAME"])
    _report(tmp_path, "v3.json", [("SAME", "error V3")])
    assert [f.id_test for f in collect_reports(tmp_path, append_report_name=True)] == ["SAME [v3.json]"]


def test_unique_failure_stays_unsuffixed_with_unrelated_passed_scenario(tmp_path):
    _report(tmp_path, "v2.json", [("UNIQUE", "error")])
    _report(tmp_path, "v3.json", [], passed=["OTHER"])
    assert [f.id_test for f in collect_reports(tmp_path, append_report_name=True)] == ["UNIQUE [v2.json]"]


def test_cross_source_id_stays_stable_when_contract_passes(tmp_path):
    _report(tmp_path, "v2.json", [("SAME", "cucumber error")])
    contract_dir = tmp_path / "contract-test"
    contract_dir.mkdir()
    contract_path = contract_dir / "report.json"

    def write_contract(status):
        contract_path.write_text(json.dumps({
            "schemaVersion": 1,
            "summary": {
                "total": 1, "passed": int(status == "PASSED"),
                "failed": int(status == "FAILED"), "skipped": 0,
                "aborted": 0, "preconditionFailed": 0,
            },
            "groups": [{"scenarioCount": 1, "scenarios": [{
                "id_test": "SAME", "status": status,
                "stackTrace": "contract error", "preconditionFailure": False,
            }]}],
        }), encoding="utf-8")

    write_contract("FAILED")
    assert {f.id_test for f in collect_reports(tmp_path, append_report_name=True)} == {
        "SAME [v2.json]", "SAME [report.json]"
    }
    write_contract("PASSED")
    assert [f.id_test for f in collect_reports(tmp_path, append_report_name=True)] == ["SAME [v2.json]"]


def test_default_mode_rejects_duplicates_across_reports(tmp_path):
    _report(tmp_path, "v2.json", [("SAME", "error V2")])
    _report(tmp_path, "v3.json", [("SAME", "error V3")])
    with pytest.raises(ReportError, match="ID KO duplicato"):
        collect_reports(tmp_path)


def test_report_suffix_remains_when_other_report_disappears(tmp_path):
    _report(tmp_path, "v2.json", [("SAME", "error V2")])
    _report(tmp_path, "v3.json", [("SAME", "error V3")])
    assert {f.id_test for f in collect_reports(tmp_path, append_report_name=True)} == {
        "SAME [v2.json]", "SAME [v3.json]"
    }
    (tmp_path / "cucumber-reports" / "v3.json").unlink()
    assert [f.id_test for f in collect_reports(tmp_path, append_report_name=True)] == ["SAME [v2.json]"]


def test_unique_id_gets_suffix_when_enabled(tmp_path):
    _report(tmp_path, "v2.json", [("UNIQUE", "error")])
    assert [f.id_test for f in collect_reports(tmp_path, append_report_name=True)] == ["UNIQUE [v2.json]"]
    assert [f.id_test for f in collect_reports(tmp_path)] == ["UNIQUE"]
