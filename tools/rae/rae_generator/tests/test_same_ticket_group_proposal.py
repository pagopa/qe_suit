"""Legacy QA groups with the same ticket must remain reviewable, not disappear."""
from rae_generator.qa_invariant_engine import Approval, Observation, QALearningMatcher
from rae_generator.qa_invariant_bridge import evaluate

SUITE = 'NrtStandardSuiteTest'
TEST = 'PRIVATE_CATALOG_ORDERING_PAGINATION_2 Example #4'
TICKET = 'https://pagopa.atlassian.net/browse/PIN-11313'


def log(uuid):
    return (f'org.opentest4j.AssertionFailedError: Relative e-service order {uuid}\n'
            '    at sample.Pagination.scan(Pagination.java:162)')


def build(ticket_two=TICKET):
    matcher = QALearningMatcher()
    candidates = {}
    for n, (ticket, error) in enumerate(((TICKET, log('a')), (ticket_two, log('a')))):
        group_id = f'legacy-{n}'
        matcher.approve(Approval(Observation(SUITE, TEST, error),
                                ticket, group_id, 'qa', f'approval-{n}'))
        candidates[(SUITE, TEST, group_id)] = {
            'id_test': TEST, 'procedure': f'analysis-{n}',
            'result': 'Known historical problem', 'link_prod_bug': ticket,
        }
    return matcher, candidates


def test_same_ticket_competing_groups_propose_with_historical_analysis():
    matcher, candidates = build()
    status, previous, reasons = evaluate(matcher, candidates, TEST, log('a'), SUITE)
    assert status == 'proposed'
    assert previous is not None and previous['link_prod_bug'] == TICKET
    assert any(reason.code == 'MULTIPLE_QA_GROUPS_SAME_TICKET' for reason in reasons)


def test_different_ticket_competing_groups_still_have_no_candidate():
    matcher, candidates = build('https://pagopa.atlassian.net/browse/PIN-OTHER')
    status, previous, reasons = evaluate(matcher, candidates, TEST, log('a'), SUITE)
    assert status == 'proposed' and previous is None
    assert any(reason.code == 'HISTORICAL_DEFECT_CONFLICT' for reason in reasons)
