"""T8: iterative, product-agnostic QA structural learning (no Drain3 mock)."""
from rae_generator.qa_invariant_engine import Approval, Observation, QALearningMatcher

SUITE, TEST, GROUP = 'NrtStandardSuiteTest', 'SEND_Sequence_Contract', 'defect-A'
TICKET = 'QA-100'
IDS = [
    'IUN_ABCD-WMWX-AAA-202611-F-1',
    'IUN_XYQE-MMQW-AAA-202611-G-1',
    'IUN_9K7P-Q2MX-NPYQ-202612-Z-8',
    'IUN_L8MN-R7ST-K4PQ-202701-H-3',
]

def log(identifier, exc='java.lang.AssertionError', rec='RECINDEX_0', code='CON080'):
    return (f'Given viene generata una nuova notifica passed\n'
            f'And si controlla che siano presenti tutti gli eventi relativi alla sequence "OK_RIS" failed\n'
            f'{exc}: [Non è stato trovato nessun elemento con eventId contenente '
            f'SEND_SIMPLE_REGISTERED_LETTER_PROGRESS.{identifier}.{rec} e DeliveryDetailCode {code}')


def approved_engine(count=2):
    engine = QALearningMatcher()
    for i in range(count):
        engine.approve(Approval(Observation(SUITE, TEST, log(IDS[i])), TICKET, GROUP,
                                'reviewer', f'confirmed-{i + 1}'))
    return engine


def decision(engine, error):
    return engine.classify(Observation(SUITE, TEST, error))


def test_t8_third_violation_proposed_and_explains_all_changed_invariants():
    d = decision(approved_engine(), log(IDS[2]))
    assert d.status == 'proposed' and d.ticket == TICKET
    assert any(r.code == 'STRUCTURAL_INVARIANT_CHANGED' and 'NPYQ' in r.current for r in d.reasons)
    assert any(r.code == 'STRUCTURAL_INVARIANT_CHANGED' and '202612' in r.current for r in d.reasons)
    assert any(r.code == 'STRUCTURAL_INVARIANT_CHANGED' and r.current == '8' for r in d.reasons)


def test_t8_after_third_qa_confirmation_fourth_new_id_is_known():
    e = approved_engine(3)
    d = decision(e, log(IDS[3]))
    assert d.status == 'known' and d.ticket == TICKET and not d.reasons


def test_t8_without_third_qa_confirmation_still_proposed():
    d = decision(approved_engine(), log(IDS[3]))
    assert d.status != 'known'


def test_t8_new_diagnostic_code_never_auto_known():
    d = decision(approved_engine(3), log(IDS[3], code='CON081'))
    assert d.status != 'known'
    assert any(r.code == 'MESSAGE_CHANGED' for r in d.reasons)


def test_t8_new_recindex_never_auto_known():
    d = decision(approved_engine(3), log(IDS[3], rec='RECINDEX_1'))
    assert d.status != 'known'
    assert any(r.code == 'STRUCTURAL_INVARIANT_CHANGED' for r in d.reasons)


def test_t8_unseen_exception_never_auto_known():
    e = QALearningMatcher()
    for i, exception in enumerate(['java.util.concurrent.TimeoutException',
                                   'java.lang.NullPointerException',
                                   'java.lang.NullPointerException']):
        e.approve(Approval(Observation(SUITE, TEST, log(IDS[i], exc=exception)),
                           TICKET, GROUP, 'reviewer', f'ref-{i}'))
    assert decision(e, log(IDS[3], exc='java.lang.IllegalArgumentException')).status != 'known'
    assert decision(e, log(IDS[3], exc='java.lang.NullPointerException')).status == 'known'


def test_t8_arbitrary_value_rejected_despite_qa_confirmations():
    d = decision(approved_engine(3), log('anything', rec='RECINDEX_0'))
    assert d.status != 'known'


def test_t8_one_qa_approval_is_not_enough():
    d = decision(approved_engine(1), log(IDS[0]))
    assert d.status == 'proposed'


def test_t8_second_identity_conflict_vetoes_auto_known():
    e = approved_engine(3)
    e.approve(Approval(Observation(SUITE, TEST, log(IDS[3])), 'QA-104', 'defect-B',
                       'reviewer', 'ref-conflict'))
    d = decision(e, log(IDS[3]))
    assert d.status == 'proposed' and d.ticket is None
    assert 'QA-104' in d.alternatives and 'QA-100' in d.alternatives


def test_t8_no_cross_suite_contamination():
    e = approved_engine(3)
    assert e.classify(Observation('AnotherSuite', TEST, log(IDS[3]))).status == 'new'
