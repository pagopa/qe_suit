"""Regression T8: all log content, including text after stack traces, is QA evidence."""
from rae_generator.qa_invariant_engine import Approval, Observation, QALearningMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'TestCreazionePurpose'
BASE = '/aws/ecs/pn-national-registries'
CHANGED = '/aws/ecs/pn-national-registriez'


def log(*, location=BASE, line=1136, iun='IUN_AAA-BBB-202611-A-1', prefix=BASE,
        ending=True, middle=None):
    head = (f'Given una notifica {iun} passed\n'
            f'And verifica "{prefix}" passed\n'
            'And cerca il log failed\n'
            'java.lang.AssertionError: log non trovato per test\n')
    frames = ('    at example.Checks.test(Checks.java:42)\n'
              f'    at example.ThreadPool.runWorker(ThreadPool.java:{line})\n')
    suffix = f'And verifica "{location}" skipped\n' if ending else ''
    return head + frames + (middle or '') + suffix


def matcher():
    m = QALearningMatcher()
    for i, text in enumerate((log(iun='IUN_AAA-BBB-202611-A-1'),
                               log(iun='IUN_ZZZ-QQQ-202611-B-1'))):
        m.approve(Approval(Observation(SUITE, TEST, text), 'QA-100', 'defect-A', 'qa', f'approved-{i}'))
    return m


def decision(m, text):
    return m.classify(Observation(SUITE, TEST, text))


def test_after_stack_invariant_path_changed_blocks_and_explains_exact_values():
    d = decision(matcher(), log(location=CHANGED))
    assert d.status == 'proposed'
    assert any(r.code == 'INVARIANT_CHANGED'
               and BASE in r.historical and CHANGED in r.current for r in d.reasons)


def test_all_simultaneous_changes_are_reported():
    d = decision(matcher(), log(location=CHANGED, line=432, prefix='/aws/ecs/other'))
    assert d.status == 'proposed'
    assert any(r.code == 'LINE_NUMBER_UNSEEN' and '432' in r.current for r in d.reasons)
    assert any(r.code == 'INVARIANT_CHANGED' and CHANGED in r.current for r in d.reasons)
    assert any(r.code == 'MESSAGE_CHANGED' and '/aws/ecs/other' in r.current for r in d.reasons)


def test_same_full_log_approved_is_known():
    assert decision(matcher(), log()).status == 'known'


def test_invariant_changed_at_end_of_log():
    d = decision(matcher(), log(location='/aws/ecs/another-location'))
    assert d.status != 'known'
    assert any('another-location' in r.current for r in d.reasons)


def test_missing_tail_line_is_reported():
    d = decision(matcher(), log(ending=False))
    assert d.status != 'known'
    assert any(r.code == 'FULL_LOG_STRUCTURE_CHANGED' for r in d.reasons)


def test_inserted_tail_line_is_reported():
    d = decision(matcher(), log(middle='An extra context line ignored-before-change\n'))
    assert d.status != 'known'
    assert any('extra context' in r.current for r in d.reasons)


def test_approval_enables_new_exact_value_without_training_from_proposals():
    m = matcher()
    changed = log(location=CHANGED)
    assert decision(m, changed).status == 'proposed'
    m.approve(Approval(Observation(SUITE, TEST, changed), 'QA-100', 'defect-A', 'qa', 'third'))
    assert decision(m, changed).status == 'known'
    assert decision(m, log(location='/aws/ecs/completely-new')).status != 'known'


def test_t7_other_group_identical_singleton_vetoes_known():
    m = matcher()
    current = log()
    m.approve(Approval(Observation(SUITE, TEST, current), 'QA-104', 'defect-B', 'qa', 'other'))
    d = decision(m, current)
    assert d.status == 'proposed' and d.ticket is None
    assert set(d.alternatives) == {'QA-100', 'QA-104'}


def test_unrelated_tail_change_stays_visible_with_different_exception():
    d = decision(matcher(), log(location=CHANGED).replace('java.lang.AssertionError:', 'java.lang.IllegalStateException:'))
    assert d.status != 'known'
    assert any(CHANGED in r.current for r in d.reasons)
