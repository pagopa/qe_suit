"""Regression: historical Java stack indentation must not split QA defect groups."""
from pathlib import Path

from rae_generator.ignore_rules import IgnoreRules
from rae_generator.progressive import ProgressiveMatcher


SUITE = "NrtStandardSuiteTest"
TEST = ("[CONSUMER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento "
        "della richiesta di fruizione si abilita quando pronto per il fruitore "
        "[Given=BFF,When=WEB,Then=WEB]")
TICKET = "https://pagopa.atlassian.net/browse/PIN-11528"
RULES = Path(__file__).resolve().parents[1] / "config" / "ignore_rules.yaml"


def log(first_proxy, second_proxy, *, indent="\t", message="button non presente"):
    return (f"java.util.NoSuchElementException: {message}\n"
            f"{indent}at demo.Button.isDisabled(Button.java:17)\n"
            f"{indent}at jdk.proxy2/jdk.proxy2.$Proxy{first_proxy}.isDisabled(Unknown Source)\n"
            f"{indent}at demo.Gateway.verify(Gateway.java:132)\n"
            f"{indent}at jdk.proxy2/jdk.proxy2.$Proxy{second_proxy}.verify(Unknown Source)\n"
            f"{indent}at ✽.il fruitore può scaricare il documento della richiesta "
            "di fruizione solo quanto è pronto"
            "(classpath:feature-expanded/agreement/download-agreement.feature:10)\n")


def historical(day, raw, *, ticket=TICKET, status=None):
    row = {
        "_source_file": f"rae_{SUITE}_202611{day}213600.yaml",
        "_source_suite": SUITE,
        "id_test": TEST,
        "error_log": raw,
        "procedure": "Verificare la presenza del pulsante nella UI.",
        "result": "Pulsante non presente nella UI.",
        "link_prod_bug": ticket,
        "link_suite_bug": "",
    }
    if status:
        row["matching"] = {"status": status}
    return row


def history():
    # 16 Nov: legacy YAML uses spaces for stack frames. Later runs use tabs.
    return [
        historical("23", log(274, 152)),
        historical("18", log(272, 151)),
        historical("17", log(272, 150)),
        historical("16", log(271, 150, indent="    ")),
    ]


def classify(rows, error):
    matcher = ProgressiveMatcher(rows, ignore_rules=IgnoreRules.load(RULES))
    status, candidate = matcher.classify(TEST, error, suite=SUITE)
    return status, candidate, matcher.last_review_reasons


def test_four_legacy_occurrences_with_spaced_and_tabbed_frames_are_known():
    rows = history()
    original_logs = [r["error_log"] for r in rows]
    status, candidate, reasons = classify(rows, log(275, 154))
    assert status == "known", reasons
    assert candidate is not None and candidate["link_prod_bug"] == TICKET
    assert not reasons
    assert [r["error_log"] for r in rows] == original_logs


def test_different_ticket_still_vetoes_auto_known():
    competitor = historical("24", log(275, 154), ticket="https://example.org/QA-OTHER")
    status, candidate, reasons = classify([competitor, *history()], log(275, 154))
    assert status == "proposed" and candidate is None
    assert any(r.code in ("HISTORICAL_DEFECT_CONFLICT", "AMBIGUOUS_QA_GROUPS")
               for r in reasons)


def test_changed_diagnostic_message_cannot_become_known():
    status, _, _ = classify(history(), log(275, 154, message="other failure"))
    assert status != "known"


def test_unconfirmed_proposal_is_not_an_independent_approval():
    first = historical("16", log(271, 150, indent="    "))
    unapproved = historical("17", log(272, 151), status="pending_confirmation")
    status, _, _ = classify([unapproved, first], log(275, 154))
    assert status != "known"


def test_cucumber_step_content_is_not_ignored():
    rows = history()
    changed = log(275, 154).replace(
        'il fruitore può scaricare il documento',
        'il fruitore non può scaricare il documento',
    )
    status, _, _ = classify(rows, changed)
    assert status != 'known'
