"""Integration regression tests for the QA-backed stage in ProgressiveMatcher."""
from copy import deepcopy
import pytest
from rae_generator.progressive import ProgressiveMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'PurposeContractTest'
A = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa'
B = 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb'
C = 'cccccccc-cccc-4ccc-8ccc-cccccccccccc'


def log(method='createPurpose', line=120, uuid=A, second='execute', secondline=50):
    return (f'TimeoutException: timeout 20s correlationId={uuid}\n'
            f' at demo.PurposePage.{method}(PurposePage.java:{line})\n'
            f' at demo.PurposeTest.{second}(PurposeTest.java:{secondline})')


def row(day, error, ticket='QA-100', approved_source=None, suite=SUITE, test=TEST):
    filename = f'rae_{suite}_202611{day}000000.yaml'
    result = {'_source_file': filename, '_source_suite': suite,
              'id_test': test, 'error_log': error,
              'procedure': 'Verifica timeout', 'result': 'Timeout in Purpose',
              'link_prod_bug': ticket, 'link_suite_bug': ''}
    if approved_source:
        result['matching'] = {
            'status': 'confirmed',
            'source_rae': approved_source['_source_file'],
            'source_id_test': approved_source['id_test'],
        }
    return result


def base():
    first = row('16', log(uuid=A, line=120, secondline=50))
    second = row('17', log(uuid=B, line=145, secondline=55), approved_source=first)
    return first, second


def classified(history, error, suite=SUITE, test=TEST):
    matcher = ProgressiveMatcher(history)
    status, candidate = matcher.classify(test, error, suite=suite)
    return status, candidate, matcher.last_review_reasons


def test_t1_confirmed_variable_line_and_uuid_becomes_known():
    first, second = base()
    status, previous, reasons = classified([second, first], log(uuid=C, line=200, secondline=70))
    assert status == 'known'
    assert previous['link_prod_bug'] == 'QA-100'
    assert not reasons


def test_t2_new_method_is_proposed_with_exact_reason():
    first, second = base()
    status, previous, reasons = classified([second, first], log('deletePurpose', 200, C, secondline=70))
    assert status == 'proposed'
    assert previous['link_prod_bug'] == 'QA-100'
    assert any(r.code == 'STACK_METHOD_CHANGED' and 'deletePurpose' in r.current for r in reasons)


def test_t3_unrelated_error_not_auto_classified_as_existing_ticket():
    first, second = base()
    error = log('deletePurpose', 200, C, second='unrelated', secondline=75)
    status, _, _ = classified([second, first], error)
    assert status != 'known'


def test_t4_t5_qa_confirmation_then_next_run_reuses_method():
    first, second = base()
    new_error = log('deletePurpose', 200, C, secondline=70)
    assert classified([second, first], new_error)[0] == 'proposed'
    approved = row('18', new_error, approved_source=second)
    status, previous, _ = classified([approved, second, first], log('deletePurpose', 230, B, secondline=85))
    assert status == 'known'
    assert previous['link_prod_bug'] == 'QA-100'
    # A fourth, unapproved method is still reviewable.
    status, _, reasons = classified([approved, second, first], log('searchPurpose', 230, B, secondline=85))
    assert status == 'proposed'
    assert any(r.code == 'STACK_METHOD_CHANGED' for r in reasons)


def test_t6_two_compatible_groups_never_pick_a_ticket():
    a, b = base()
    x = row('16', log(uuid=A, line=120, secondline=50), ticket='QA-200', test=TEST)
    y = row('17', log(uuid=B, line=145, secondline=55), ticket='QA-200', approved_source=x, test=TEST)
    # Two distinct source files for two independent QA groups.
    x['_source_file'] = 'rae_NrtStandardSuiteTest_20261016000000.yaml'
    y['_source_file'] = 'rae_NrtStandardSuiteTest_20261017000000.yaml'
    y['matching']['source_rae'] = x['_source_file']
    status, previous, reasons = classified([b, a, y, x], log(uuid=C, line=200, secondline=70))
    assert status == 'proposed' and previous is None
    assert any(r.code == 'AMBIGUOUS_QA_GROUPS' for r in reasons)


def test_unapproved_proposal_does_not_train():
    a, b = base()
    b['matching']['status'] = 'pending_confirmation'
    b['matching']['proposed_digest'] = '0' * 64  # not approved
    status, _, _ = classified([b, a], log(uuid=C, line=200, secondline=70))
    assert status != 'known'


def test_suite_isolation():
    a, b = base()
    status, _, _ = classified([b, a], log(uuid=C, line=200, secondline=70), suite='OtherSuite')
    assert status != 'known'


def test_no_unapproved_recombination():
    a, b = base()
    c = row('18', log('deletePurpose', 190, C, second='finish', secondline=65), approved_source=b)
    status, _, reasons = classified([c, b, a], log('deletePurpose', 230, A, second='execute', secondline=85))
    assert status == 'proposed'
    assert any(r.code == 'UNAPPROVED_METHOD_COMBINATION' for r in reasons)

def test_t7_confirmed_group_conflicts_with_identical_singleton():
    """Regression for the real simulator input: A=3 QA events vs B=1 identical."""
    first = row('16', log(uuid=A, line=120, secondline=50))
    second = row('17', log(uuid=B, line=145, secondline=55), approved_source=first)
    third = row('18', log(uuid=B, line=145, secondline=55), approved_source=second)
    singleton = row('19', third['error_log'], ticket='QA-104')
    status, previous, reasons = classified([singleton, third, second, first], third['error_log'])
    assert status == 'proposed'
    assert previous is None  # Never silently suggest one ticket as authoritative.
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT' for r in reasons)
    info = ' '.join(r.historical for r in reasons)
    assert 'QA-100' in info and 'QA-104' in info


def test_single_historical_group_never_auto_known():
    only = row('16', log())
    status, previous, reasons = classified([only], only['error_log'])
    assert status == 'proposed'
    assert previous is not None and previous['link_prod_bug'] == 'QA-100'
    assert any(r.code == 'INSUFFICIENT_QA_CONFIRMATIONS' for r in reasons)


def test_unrelated_singleton_does_not_veto_valid_group():
    first, second = base()
    third = row('18', 'AnotherException: unrelated\n at demo.Unrelated.run(Unrelated.java:20)', ticket='QA-104')
    status, previous, reasons = classified([third, second, first], log(uuid=C, line=200, secondline=70))
    assert status == 'known'
    assert previous['link_prod_bug'] == 'QA-100'
    assert not reasons


# T8 — integration via the real ProgressiveMatcher and QA-confirmed RAE lineage.
_T8_IUNS = (
    'IUN_ABCD-WMWX-AAA-202611-F-1',
    'IUN_XYQE-MMQW-AAA-202611-G-1',
    'IUN_9K7P-Q2MX-NPYQ-202612-Z-8',
    'IUN_L8MN-R7ST-K4PQ-202701-H-3',
)


def _t8_log(identifier, code='CON080', rec='RECINDEX_0', exception='java.lang.AssertionError'):
    return ('Given viene generata una nuova notifica passed\n'
            'And si controlla che siano presenti tutti gli eventi relativi '
            'alla sequence "OK_RIS" failed\n'
            f'{exception}: [Non è stato trovato nessun elemento con eventId contenente '
            f'SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.{identifier}.{rec} e DeliveryDetailCode {code}')


def test_t8_real_matcher_progressive_qa_approvals():
    one = row('16', _t8_log(_T8_IUNS[0]))
    two = row('17', _t8_log(_T8_IUNS[1]), approved_source=one)
    three_log = _t8_log(_T8_IUNS[2])
    status, candidate, reasons = classified([two, one], three_log)
    assert status == 'proposed', (status, reasons)
    assert candidate is not None and candidate['link_prod_bug'] == 'QA-100'
    assert {r.current for r in reasons if r.code == 'STRUCTURAL_INVARIANT_CHANGED'} >= {'NPYQ', '202612', '8'}

    # Only now does QA confirm the third observation.
    three = row('18', three_log, approved_source=two)
    four_log = _t8_log(_T8_IUNS[3])
    status, candidate, reasons = classified([three, two, one], four_log)
    assert status == 'known', (status, reasons)
    assert candidate is not None and candidate['link_prod_bug'] == 'QA-100'
    assert not reasons


def test_t8_real_matcher_invariant_violation_does_not_auto_match():
    one = row('16', _t8_log(_T8_IUNS[0]))
    two = row('17', _t8_log(_T8_IUNS[1]), approved_source=one)
    three = row('18', _t8_log(_T8_IUNS[2]), approved_source=two)
    for error in (_t8_log(_T8_IUNS[3], code='CON081'),
                  _t8_log(_T8_IUNS[3], rec='RECINDEX_1'),
                  _t8_log(_T8_IUNS[3], exception='java.lang.IllegalArgumentException')):
        status, _, reasons = classified([three, two, one], error)
        assert status != 'known', (status, reasons)
