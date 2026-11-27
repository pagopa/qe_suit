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


def test_auto_match_replacing_legacy_approval_does_not_resurrect_it():
    legacy = row('23', 'X')
    replacement = row('23', 'Y', matching={'status': 'auto_matched'})
    older = row('18', 'X')
    status, candidate, reasons = classify([legacy, replacement, older], 'X')
    assert status == 'proposed', reasons
    assert candidate is not None
    assert candidate['_source_file'] == older['_source_file']


def test_unconfirmed_proposal_replacing_legacy_approval_does_not_resurrect_it():
    legacy = row('23', 'X')
    replacement = row('23', 'Y', matching={
        'status': 'pending_confirmation', 'proposed_digest': '0' * 64})
    older = row('18', 'X')
    status, candidate, reasons = classify([legacy, replacement, older], 'X')
    assert status == 'proposed', reasons
    assert candidate is not None
    assert candidate['_source_file'] == older['_source_file']


def test_conflicting_confirmed_lineage_cannot_fall_back_to_known():
    # Two approved occurrences linked by source_rae have identical logs,
    # but different tickets. The QA bridge must veto Drain3 auto-known.
    from rae_generator.progressive import ProgressiveMatcher

    older = row('18', 'X')
    newer = row('23', 'X', matching={
        'status': 'confirmed',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
    })
    newer['link_prod_bug'] = 'https://example.org/QA-456'

    matcher = ProgressiveMatcher([newer, older])
    status, candidate = matcher.classify(TEST, log('X'), suite=SUITE)
    assert status == 'proposed'
    assert candidate is None
    assert any(reason.code in ('HISTORICAL_DEFECT_CONFLICT',
                               'AMBIGUOUS_QA_GROUPS')
               for reason in matcher.last_review_reasons)


def test_same_ticket_confirmed_lineage_still_known():
    from rae_generator.progressive import ProgressiveMatcher

    older = row('18', 'X')
    newer = row('23', 'X', matching={
        'status': 'confirmed',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
    })
    matcher = ProgressiveMatcher([newer, older])
    status, candidate = matcher.classify(TEST, log('X'), suite=SUITE)
    assert status == 'known'
    assert candidate is not None
    assert candidate['link_prod_bug'] == TICKET


def test_whitespace_ticket_in_conflicting_lineage_is_not_approved():
    older = row('18', 'X')
    newer = row('23', 'X', matching={
        'status': 'confirmed',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
    })
    newer['link_prod_bug'] = '   '
    newer['link_suite_bug'] = 'https://example.org/QA-456'
    status, candidate, reasons = classify([newer, older], 'X')
    assert status == 'proposed', reasons
    assert candidate is None
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT' for r in reasons)


def test_whitespace_only_ticket_cannot_count_as_second_qa_approval():
    older = row('18', 'X')
    newer = row('23', 'X', matching={
        'status': 'confirmed',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
    })
    newer['link_prod_bug'] = '   '
    status, candidate, reasons = classify([newer, older], 'X')
    assert status == 'proposed', reasons
    assert candidate is not None
    assert candidate['_source_file'] == older['_source_file']


def test_blank_product_ticket_does_not_hide_valid_suite_ticket():
    only = row('23', 'X')
    only['link_prod_bug'] = '   '
    only['link_suite_bug'] = TICKET
    status, candidate, reasons = classify([only], 'X')
    assert status == 'proposed', reasons
    assert candidate is not None
    assert candidate['link_prod_bug'] == ''
    assert candidate['link_suite_bug'] == TICKET


def test_split_lineage_keeps_global_newest_first_group_order():
    older = row('18', 'X')
    independent = row('23', 'Z')
    linked_newer = row('25', 'Y', matching={
        'status': 'confirmed',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
    })
    linked_newer['link_prod_bug'] = 'https://example.org/QA-456'

    engine, candidates = build_learning([linked_newer, independent, older])
    expected = [linked_newer['_source_file'], independent['_source_file'],
                older['_source_file']]
    actual = [group_id.split('|')[0]
              for _, _, group_id in engine.groups]
    assert actual == expected
    assert len(candidates) == 3


def test_approved_pending_with_padded_ticket_keeps_confirmed_lineage():
    # A QA-approved pending entry has a digest over its *original* analysis.
    # Trimming its ticket for grouping must not invalidate that approval.
    from rae_generator.qa_confirmation import analysis_digest
    from rae_generator.progressive import ProgressiveMatcher

    older = row('18', 'X')
    newer = row('23', 'X', matching={
        'status': 'pending_confirmation',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
    })
    newer['error_log'] = log('X').replace('Example.java:21', 'Example.java:22')
    newer['link_prod_bug'] = TICKET + '  '
    newer['matching']['proposed_digest'] = analysis_digest(newer)

    matcher = ProgressiveMatcher([newer, older])
    status, candidate = matcher.classify(TEST, log('X'), suite=SUITE)
    assert status == 'known', matcher.last_review_reasons
    assert candidate is not None
    assert candidate['link_prod_bug'] == TICKET


def test_unconfirmed_pending_with_padded_ticket_still_cannot_train():
    from rae_generator.progressive import ProgressiveMatcher

    older = row('18', 'X')
    newer = row('23', 'X', matching={
        'status': 'pending_confirmation',
        'source_rae': older['_source_file'],
        'source_id_test': TEST,
        'proposed_digest': '0' * 64,
    })
    newer['error_log'] = log('X').replace('Example.java:21', 'Example.java:22')
    newer['link_prod_bug'] = TICKET + '  '

    matcher = ProgressiveMatcher([newer, older])
    status, _ = matcher.classify(TEST, log('X'), suite=SUITE)
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
