"""T8 evolution: QA-approved stack families are learned independently."""
from rae_generator.qa_invariant_engine import Approval, Observation, QALearningMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'TestCreazionePurpose'


def log(*, kind='timeline', iun='KJPW-WEYP-JAVX-202611-H-1',
        feature_line=387, first_line=1259, code='PUBLIC_REGISTRY_RESPONSE',
        frame_method='throwAssertionErrorWithIUN'):
    prefix = ('Given viene generata una nuova notifica passed\n'
              'And destinatario passed\n'
              'And destinatario passed\n'
              'And viene letta la timeline PUBLIC_REGISTRY_CALL passed\n')
    if kind == 'audit':
        return (prefix + 'And verifico un audit log failed\n'
                'org.awaitility.core.ConditionTimeoutException: audit non trovato\n'
                + ''.join(f'    at example.Await{i}.call(Await{i}.java:{i + 10})\n'
                          for i in range(14)))
    return (prefix + f'And viene letta la timeline {code} failed\n'
            f'org.opentest4j.AssertionFailedError: [Non è stato trovato {code} '
            f'EventId IUN_{iun}]\n'
            f'    at example.SharedSteps.{frame_method}(SharedSteps.java:{first_line})\n'
            '    at example.B2bSteps.checkIfTimelineElementExists(B2bSteps.java:471)\n'
            '    at example.B2bSteps.readEventsUpToTimelineElement(B2bSteps.java:167)\n'
            '    at example.AvanzamentoSteps.readEventsUpToTimelineElement(AvanzamentoSteps.java:219)\n'
            f'    at ✽.vengono_letti_eventi(AvanzamentoNotifiche.feature:{feature_line})\n')


def matcher_with_approvals():
    m = QALearningMatcher()
    samples = [
        log(kind='audit'),
        log(iun='KVDG-HQUE-WLKV-202611-H-1', feature_line=350, first_line=1176),
        log(feature_line=387, first_line=1259),
    ]
    for i, sample in enumerate(samples):
        m.approve(Approval(Observation(SUITE, TEST, sample), 'QA-100',
                           'defect-A', 'qa', f'approved-{i}'))
    return m


def classify(m, current):
    return m.classify(Observation(SUITE, TEST, current))


def test_same_stack_family_with_previously_approved_line_is_known():
    # Only difference from historical 3 is the final feature line 387->350.
    m = matcher_with_approvals()
    d = classify(m, log(feature_line=350, first_line=1259))
    assert d.status == 'known' and d.ticket == 'QA-100' and not d.reasons


def test_new_line_allowed_after_two_approved_line_values():
    d = classify(matcher_with_approvals(), log(feature_line=999, first_line=1259))
    # Two different QA-approved source lines at this position authorize
    # positional line-number variability in the *same* family. This is known.
    assert d.status == 'known'


def test_unseen_method_cannot_cross_family():
    d = classify(matcher_with_approvals(), log(feature_line=350, frame_method='anotherMethod'))
    assert d.status != 'known'


def test_changed_diagnostic_cannot_cross_family():
    d = classify(matcher_with_approvals(), log(feature_line=350, code='PUBLIC_REGISTRY_CALL'))
    assert d.status != 'known'


def test_new_exception_with_same_frame_count_is_not_known():
    m = matcher_with_approvals()
    current = log(feature_line=350).replace('org.opentest4j.AssertionFailedError:',
                                             'java.lang.IllegalArgumentException:')
    assert classify(m, current).status != 'known'


def test_other_group_singleton_vetoes_approved_family():
    m = matcher_with_approvals()
    current = log(feature_line=350, first_line=1259)
    m.approve(Approval(Observation(SUITE, TEST, current), 'QA-104',
                       'defect-B', 'qa', 'other-singleton'))
    d = classify(m, current)
    assert d.status == 'proposed' and d.ticket is None
    assert set(d.alternatives) == {'QA-100', 'QA-104'}


def test_singleton_approved_variant_not_generalized():
    m = QALearningMatcher()
    m.approve(Approval(Observation(SUITE, TEST, log(kind='audit')),
                       'QA-100', 'defect-A', 'qa', 'one'))
    m.approve(Approval(Observation(SUITE, TEST, log()),
                       'QA-100', 'defect-A', 'qa', 'two'))
    d = classify(m, log(feature_line=350))
    assert d.status != 'known'


def test_same_count_different_methods_not_combined():
    m = matcher_with_approvals()
    current = log(feature_line=350).replace('example.B2bSteps.checkIfTimelineElementExists',
                                             'example.Await1.call')
    assert classify(m, current).status != 'known'
