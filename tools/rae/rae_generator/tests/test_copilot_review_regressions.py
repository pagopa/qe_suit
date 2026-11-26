"""Regressions for Copilot review of QA historical learning and review rendering."""

from rae_generator.qa_invariant_bridge import build_learning, evaluate
from rae_generator.review_evidence import render_warning, snapshot
from rae_generator.qa_invariant_engine import Reason

SUITE = 'NrtStandardSuiteTest'
TEST = '[CASE_1] Scenario [Given=BFF,When=BFF,Then=BFF]'
TICKET = 'https://example.org/QA-123'


def log(message='old'):
    return (f'java.lang.AssertionError: {message}\n'
            '    at demo.Example.check(Example.java:21)\n')


def row(day, message='old', *, matching=None):
    item = {
        '_source_file': f'rae_{SUITE}_202611{day}120000.yaml',
        '_source_suite': SUITE,
        'id_test': TEST,
        'error_log': log(message),
        'procedure': 'Inspect the failure.',
        'result': 'Failure reproduced.',
        'link_prod_bug': TICKET,
        'link_suite_bug': '',
    }
    if matching is not None:
        item['matching'] = matching
    return item


def classify(history, message='old'):
    engine, candidates = build_learning(history)
    return evaluate(engine, candidates, TEST, log(message), SUITE)


def test_auto_match_does_not_veto_two_legacy_approvals():
    status, candidate, reasons = classify([
        row('24', matching={'status': 'auto_matched'}),
        row('23'), row('18')])
    assert status == 'known', reasons
    assert candidate['_source_file'].endswith('20261123120000.yaml')


def test_unconfirmed_proposal_does_not_veto_two_legacy_approvals():
    status, candidate, reasons = classify([
        row('24', matching={'status': 'pending_confirmation',
                            'proposed_digest': '0' * 64}),
        row('23'), row('18')])
    assert status == 'known', reasons
    assert candidate['_source_file'].endswith('20261123120000.yaml')


def test_only_auto_matched_rows_cannot_train():
    engine, candidates = build_learning([
        row('24', matching={'status': 'auto_matched'}),
        row('23', matching={'status': 'auto_matched'})])
    assert candidates == {}
    assert evaluate(engine, candidates, TEST, log(), SUITE) is None


def test_only_unconfirmed_rows_cannot_train():
    engine, candidates = build_learning([
        row('24', matching={'status': 'pending_confirmation',
                            'proposed_digest': '0' * 64})])
    assert candidates == {}
    assert evaluate(engine, candidates, TEST, log(), SUITE) is None


def test_discarded_same_id_different_log_cannot_merge_retained_row():
    # Same source and ID, but different logs: the earlier X is overwritten
    # by Y in the index, so only Y can participate in reconciliation.
    first = row('23', 'X')
    retained = row('23', 'Y')
    older = row('18', 'X')
    status, _, _ = classify([first, retained, older], 'Y')
    assert status != 'known'


def test_same_ticket_reason_has_dedicated_qa_rendering():
    evidence = snapshot([Reason('MULTIPLE_QA_GROUPS_SAME_TICKET',
                                'group A, group B', TICKET,
                                'Multiple compatible QA groups share a ticket')])
    html = '\n'.join(render_warning({'review_evidence': evidence}))
    assert 'Più gruppi QA compatibili con lo stesso ticket' in html
    assert 'Ticket comune:' in html
    assert TICKET in html
    assert 'Log corrente:' not in html
    assert 'Storico QA:' not in html
