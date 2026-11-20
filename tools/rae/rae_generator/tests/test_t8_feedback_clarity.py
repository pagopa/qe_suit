"""Regressions: optional context must never invent evidence or influence matching."""
from rae_generator.qa_invariant_engine import (
    Approval, Observation, QALearningMatcher, Reason,
    _message_reasons, _trailing_invariant_reasons,
)

SUITE = 'Suite'; TEST = 'Test'; TICKET = 'QA-100'


def _history(*errors):
    m = QALearningMatcher()
    for i, error in enumerate(errors):
        m.approve(Approval(Observation(SUITE, TEST, error), TICKET, 'A', 'reviewer', f'qa-{i}'))
    return m


def _decision(matcher, current):
    return matcher.classify(Observation(SUITE, TEST, current))


def test_40_to_44_reports_exact_values_and_trustworthy_context():
    # Sufficient distinct anchors around the different token.
    before = 'prefix tokens negli ultimi 40 minuti riportante dati confermati'
    after = 'prefix tokens negli ultimi 44 minuti riportante dati confermati'
    reasons = _message_reasons([before, before], after)
    changed = [r for r in reasons if r.current == '44']
    assert len(changed) == 1
    assert changed[0].historical == '40'
    assert changed[0].historical_context == 'negli ultimi 40 minuti riportante'
    assert changed[0].current_context == 'negli ultimi 44 minuti riportante'
    assert changed[0].location


def test_isolated_number_has_no_invented_context():
    r = _message_reasons(['40', '40'], '44')
    assert len(r) == 1 and r[0].historical == '40' and r[0].current == '44'
    assert r[0].historical_context == r[0].current_context == ''


def test_repeated_40_has_no_ambiguous_context():
    before = 'negli ultimi 40 minuti riportante dati negli ultimi 40 minuti riportante dati'
    after = 'negli ultimi 44 minuti riportante dati negli ultimi 40 minuti riportante dati'
    r = [x for x in _message_reasons([before, before], after) if x.current == '44']
    assert len(r) == 1
    assert not r[0].historical_context and not r[0].current_context


def test_changed_suffix_line_carries_trustworthy_context():
    before = ('java.lang.AssertionError: missing\n'
              '    at example.Check.run(Check.java:42)\n'
              'And verifies the the last 40 minutes reported skipped\n')
    after = before.replace('last 40 minutes', 'last 44 minutes')
    reasons = _trailing_invariant_reasons([before, before], after)
    assert any(r.historical == '40' and r.current == '44' for r in reasons)
    r = next(r for r in reasons if r.current == '44')
    assert 'the last 40 minutes reported' in r.historical_context
    assert 'the last 44 minutes reported' in r.current_context
    assert 'Riga' in r.location


def test_no_new_known_from_feedback_context():
    before = 'Alpha beta gamma 40 minutes report diagnostics'
    after = 'Alpha beta gamma 44 minutes report diagnostics'
    m = _history(before, before)
    d = _decision(m, after)
    assert d.status != 'known'
    assert any(r.current == '44' for r in d.reasons)


def test_singleton_known_stack_reports_insufficient_qa_not_shape_missing():
    first = ('java.lang.AssertionError: missing item\n'
             '    at one.Thing.execute(Thing.java:12)\n')
    different = ('java.lang.IllegalStateException: could not find item\n'
                 '    at one.Thing.execute(Thing.java:12)\n'
                 '    at two.Other.call(Other.java:3)\n')
    m = _history(first, first, different)
    current = different.replace('Other.java:3', 'Other.java:4')
    d = _decision(m, current)
    assert d.status == 'proposed'
    r = next(reason for reason in d.reasons if reason.code == 'UNAPPROVED_STACK_VARIANT')
    assert '1 conferma QA' in r.historical
    assert 'già osservata' in r.detail
    assert any(x.code == 'LINE_NUMBER_UNSEEN' for x in d.reasons)


def test_t7_conflicting_group_still_vetoes_exact_match():
    error = 'java.lang.AssertionError: missing\n    at one.Thing.execute(Thing.java:12)\n'
    m = _history(error, error)
    m.approve(Approval(Observation(SUITE, TEST, error), 'QA-104', 'B', 'reviewer', 'different'))
    d = _decision(m, error)
    assert d.status == 'proposed' and set(d.alternatives) == {'QA-100', 'QA-104'}
