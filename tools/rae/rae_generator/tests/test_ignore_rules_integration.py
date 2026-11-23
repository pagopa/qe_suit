"""Safety regression: proxy-only ordinal changes do not bypass QA confirmations."""
from pathlib import Path
from rae_generator.ignore_rules import IgnoreRules
from rae_generator.qa_invariant_engine import Approval, Observation, QALearningMatcher


CONFIG = Path(__file__).resolve().parents[1] / 'config' / 'ignore_rules.yaml'


def error(ordinal='150', method='isDisabled'):
    return ('java.util.NoSuchElementException: Button non presente\n'
            '  at it.example.Button.isDisabled(Button.java:17)\n'
            f'  at jdk.proxy2/jdk.proxy2.$Proxy{ordinal}.{method}(Unknown Source)\n')


def test_two_independent_approvals_authorize_proxy_variation():
    norm = IgnoreRules.load(CONFIG).normalize
    engine = QALearningMatcher()
    for i, n in enumerate(('150', '272'), 1):
        engine.approve(Approval(Observation('NrtStandardSuiteTest', 'Test A', norm(error(n))),
                               'QA-1', 'same-defect', 'qa', f'approval-{i}'))
    current = engine.classify(Observation('NrtStandardSuiteTest', 'Test A', norm(error('130'))))
    assert current.status == 'known'
    assert engine.classify(Observation('NrtStandardSuiteTest', 'Test A', norm(error('130', method='isEnabled')))).status != 'known'


def test_single_approval_never_becomes_known_by_ignoring_proxy():
    norm = IgnoreRules.load(CONFIG).normalize
    engine = QALearningMatcher()
    engine.approve(Approval(Observation('NrtStandardSuiteTest', 'Test A', norm(error('150'))),
                           'QA-1', 'same-defect', 'qa', 'approval-1'))
    assert engine.classify(Observation('NrtStandardSuiteTest', 'Test A', norm(error('130')))).status == 'proposed'


def test_t7_conflicting_groups_are_not_bypassed():
    norm = IgnoreRules.load(CONFIG).normalize
    engine = QALearningMatcher()
    for defect in ('A', 'B'):
        for i, n in enumerate(('150', '272'), 1):
            engine.approve(Approval(Observation('NrtStandardSuiteTest', 'Test A', norm(error(n))),
                                   f'QA-{defect}', f'defect-{defect}', 'qa', f'{defect}-{i}'))
    assert engine.classify(Observation('NrtStandardSuiteTest', 'Test A', norm(error('130')))).status == 'proposed'
