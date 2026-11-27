"""Regressions for approved dynamic fields and same-ticket QA ambiguity."""
from rae_generator.qa_matching_extensions import json_normalized_heads, normalize_stamps
from rae_generator.qa_invariant_engine import (
    _char_family, _matches_family, _message_reasons, _structural_token_reasons,
    QALearningMatcher, Approval, Observation,
)
from rae_generator.qa_invariant_bridge import evaluate


def test_single_dynamic_suffix_learned_from_two_approved_values():
    assert _structural_token_reasons(
        ['description-AbC123', 'description-XyZ789'], 'description-QwE456', 0) == []


def test_single_dynamic_suffix_requires_two_distinct_approvals():
    assert _structural_token_reasons(
        ['description-AbC123', 'description-AbC123'], 'description-QwE456', 0) is None


def test_single_dynamic_suffix_does_not_change_prefix():
    assert _structural_token_reasons(
        ['description-AbC123', 'description-XyZ789'], 'different-QwE456', 0) is None


def test_json_value_only_with_equal_shape():
    old = [
        'AssertionError: - payload: {"name":"client-AbC123","description":null}',
        'AssertionError: - payload: {"name":"client-XyZ789","description":null}',
    ]
    actual = 'AssertionError: - payload: {"description":null,"name":"client-QwE456"}'
    assert _message_reasons(old, actual) == []
    bad = 'AssertionError: - payload: {"name":"client-QwE456","description":"changed"}'
    assert _message_reasons(old, bad)
    missing = 'AssertionError: - payload: {"name":"client-QwE456"}'
    assert _message_reasons(old, missing)


def test_json_numeric_business_value_remains_invariant():
    old = [
        'AssertionError: - payload: {"name":"client-AbC123","count":10}',
        'AssertionError: - payload: {"name":"client-XyZ789","count":10}',
    ]
    changed = 'AssertionError: - payload: {"name":"client-QwE456","count":12}'
    assert _message_reasons(old, changed)


def test_timestamp_generalization_needs_two_distinct_valid_approved_values():
    history = [
        'x "creationTime":"2026-11-17T21:22:06.357Z"',
        'x "creationTime":"2026-11-18T21:22:06.983Z"',
    ]
    incoming = 'x "creationTime":"2026-11-26T21:22:06.457Z"'
    old, new, accepted = normalize_stamps(history, incoming)
    assert len(accepted) == 1
    assert old[0] == old[1] == new
    same, unchanged, accepted = normalize_stamps([history[0], history[0]], incoming)
    assert accepted == []
    assert unchanged == incoming


def _engine_and_candidates(*, second_result='Wrong order', include_second=True):
    suite, test, ticket = 'NrtStandardSuiteTest', 'ORDERING_EXAMPLE', 'https://jira.example/PIN-1'
    log = 'java.lang.AssertionError: wrong order\n\tat pkg.Order.verify(Order.java:12)'
    engine = QALearningMatcher()
    candidates = {}
    for group_id, result in [('newer', 'Wrong order'), ('older', second_result)]:
        if group_id == 'older' and not include_second:
            continue
        engine.approve(Approval(Observation(suite, test, log), ticket, group_id,
                                'qa', 'approval-' + group_id))
        candidates[(suite, test, group_id)] = {
            'id_test': test, 'error_log': log,
            'link_prod_bug': ticket, 'link_suite_bug': '',
            'procedure': 'Verify the order of the e-services', 'result': result,
            '_source_file': 'rae_' + group_id + '.yaml',
        }
    return engine, candidates, suite, test, log


def test_same_ticket_same_analysis_collapses_with_two_independent_approvals():
    engine, candidates, suite, test, log = _engine_and_candidates()
    status, historical, reasons = evaluate(engine, candidates, test, log, suite)
    assert status == 'known'
    assert historical['_source_file'] == 'rae_newer.yaml'
    assert reasons == ()


def test_same_ticket_different_results_stays_proposed():
    engine, candidates, suite, test, log = _engine_and_candidates(second_result='Different cause')
    status, _, reasons = evaluate(engine, candidates, test, log, suite)
    assert status == 'proposed'
    assert any(r.code == 'MULTIPLE_QA_GROUPS_SAME_TICKET' for r in reasons)


def test_one_qa_confirmation_stays_proposed():
    engine, candidates, suite, test, log = _engine_and_candidates(include_second=False)
    status, _, reasons = evaluate(engine, candidates, test, log, suite)
    assert status == 'proposed'
    assert any(r.code == 'INSUFFICIENT_QA_CONFIRMATIONS' for r in reasons)



# Copilot #1339 review regressions: preserve QA fail-closed behavior.
from rae_generator.qa_matching_extensions import extract_json


def test_qa_equivalence_rejects_whitespace_only_analyses():
    engine, candidates, suite, test, log = _engine_and_candidates()
    for row in candidates.values():
        row['procedure'] = '   \n  '
        row['result'] = '\t  '
    status, _, reasons = evaluate(engine, candidates, test, log, suite)
    assert status == 'proposed'
    assert any(reason.code == 'MULTIPLE_QA_GROUPS_SAME_TICKET' for reason in reasons)


def test_duplicate_json_keys_are_rejected_even_when_nested():
    for payload in [
        '{"count":10,"count":20,"name":"client-AbC123"}',
        '{"meta":{"count":10,"count":20},"name":"client-AbC123"}',
    ]:
        assert extract_json('AssertionError: - payload: ' + payload) == {}
    historical = [
        'AssertionError: - payload: {"count":10,"count":20,"name":"client-AbC123"}',
        'AssertionError: - payload: {"count":10,"count":20,"name":"client-XyZ789"}',
    ]
    changed = 'AssertionError: - payload: {"count":11,"count":20,"name":"client-QwE456"}'
    assert _message_reasons(historical, changed)


def test_escaped_unicode_whitespace_preserves_invariant_differences():
    historical = [
        r'AssertionError: - payload: {"name":"client-AbC123","label":"service\u00a0disabled"}',
        r'AssertionError: - payload: {"name":"client-XyZ789","label":"service\u00a0disabled"}',
    ]
    incoming = 'AssertionError: - payload: {"name":"client-QwE456","label":"service disabled"}'
    assert _message_reasons(historical, incoming)


def test_timestamp_only_json_normalizes_reordered_keys():
    historical = [
        'AssertionError: - responseBody: {"creationTime":"2026-11-17T21:22:06.357Z","status":"FAILED"}',
        'AssertionError: - responseBody: {"creationTime":"2026-11-18T21:22:06.983Z","status":"FAILED"}',
    ]
    incoming = 'AssertionError: - responseBody: {"status":"FAILED","creationTime":"2026-11-26T21:22:06.457Z"}'
    assert _message_reasons(historical, incoming) == []


def test_timestamps_do_not_train_across_json_paths():
    historical = [
        'AssertionError: - responseBody: {"expected":{"creationTime":"2026-11-17T21:22:06.357Z"},"actual":{"creationTime":"not-set"}}',
        'AssertionError: - responseBody: {"expected":{"creationTime":"not-set"},"actual":{"creationTime":"2026-11-18T21:22:06.983Z"}}',
    ]
    incoming = ('AssertionError: - responseBody: '
                '{"expected":{"creationTime":"2026-11-26T21:22:06.457Z"},'
                '"actual":{"creationTime":"not-set"}}')
    assert _message_reasons(historical, incoming)


def test_unstructured_timestamp_uses_full_context_not_leaf_name():
    historical = [
        'AssertionError: expected {"creationTime":"2026-11-17T21:22:06.357Z"}',
        'AssertionError: actual {"creationTime":"2026-11-18T21:22:06.983Z"}',
    ]
    incoming = 'AssertionError: expected {"creationTime":"2026-11-26T21:22:06.457Z"}'
    _old, unchanged, accepted = normalize_stamps(historical, incoming)
    assert not accepted
    assert unchanged == incoming


def test_same_json_path_requires_two_distinct_timestamps():
    historical = [
        'AssertionError: - responseBody: {"creationTime":"2026-11-17T21:22:06.357Z"}',
        'AssertionError: - responseBody: {"creationTime":"2026-11-17T21:22:06.357Z"}',
    ]
    incoming = 'AssertionError: - responseBody: {"creationTime":"2026-11-26T21:22:06.457Z"}'
    assert _message_reasons(historical, incoming)
