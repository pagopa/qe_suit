"""Regression: a historical QA-approved stack variant must remain matchable."""
from rae_generator.qa_invariant_engine import Approval, Observation, QALearningMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'TestCreazionePurpose'


def log(kind='short', identifier='IUN_KJPW-WEYP-JAVX-202611-H-1'):
    if kind == 'long':
        return (f'Given viene generata una nuova notifica passed\n'
                f'And verifico la presenza di un audit log failed\n'
                f'org.awaitility.core.ConditionTimeoutException: non è stato trovato {identifier}\n'
                + ''.join(f'    at example.await.Frame{i}.run(Frame{i}.java:{i+10})\n'
                          for i in range(14)))
    return (f'Given viene generata una nuova notifica passed\n'
            f'And vengono letti gli eventi fino a PUBLIC_REGISTRY_RESPONSE failed\n'
            f'org.opentest4j.AssertionFailedError: category PUBLIC_REGISTRY_RESPONSE {identifier}\n'
            + ''.join(f'    at example.pn.Frame{i}.run(Frame{i}.java:{i+40})\n'
                      for i in range(5)))


def approved(*, conflict=False):
    matcher = QALearningMatcher()
    for i, error in enumerate([log('long', 'IUN_FIRST'), log('short', 'IUN_SECOND'), log('short')]):
        matcher.approve(Approval(Observation(SUITE, TEST, error),
                                'QA-100', 'defect-A', 'qa', f'QA-A-{i}'))
    if conflict:
        matcher.approve(Approval(Observation(SUITE, TEST, log('short')),
                                'QA-104', 'defect-B', 'qa', 'QA-B-1'))
    return matcher


def test_t8_exact_approved_five_frames_with_other_fourteen_frame_variant_is_known():
    decision = approved().classify(Observation(SUITE, TEST, log('short')))
    assert decision.status == 'known'
    assert decision.ticket == 'QA-100'
    assert not decision.reasons


def test_t7_exact_approved_variant_also_present_in_singleton_is_ambiguous():
    decision = approved(conflict=True).classify(Observation(SUITE, TEST, log('short')))
    assert decision.status == 'proposed'
    assert decision.ticket is None
    assert set(decision.alternatives) == {'QA-100', 'QA-104'}
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT' for r in decision.reasons)


def test_single_approved_exact_occurrence_never_known():
    matcher = QALearningMatcher()
    matcher.approve(Approval(Observation(SUITE, TEST, log('short')),
                            'QA-100', 'defect-A', 'qa', 'QA-A-1'))
    assert matcher.classify(Observation(SUITE, TEST, log('short'))).status == 'proposed'


def test_near_match_with_changed_stack_method_is_not_known():
    original = log('short')
    changed = original.replace('Frame3.run', 'Frame3.other')
    d = approved().classify(Observation(SUITE, TEST, changed))
    assert d.status != 'known'
    # The public decision can be `new` if similarity falls below the proposal threshold.
    # Irrespective of proposal ranking, this must never become `known`.


def test_near_match_with_changed_diagnostic_is_not_known():
    changed = log('short').replace('PUBLIC_REGISTRY_RESPONSE IUN', 'PUBLIC_REGISTRY_CALL IUN')
    assert approved().classify(Observation(SUITE, TEST, changed)).status != 'known'
