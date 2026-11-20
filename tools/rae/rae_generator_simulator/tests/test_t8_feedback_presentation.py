"""Simulator only displays trusted evidence returned by the production learner."""
from feedback import explanations
from rae_generator.qa_invariant_engine import Reason


def test_context_when_available_and_exact_values_retained():
    source = Reason('INVARIANT_CHANGED', '40', '44', 'token 4',
                    'negli ultimi 40 minuti riportante',
                    'negli ultimi 44 minuti riportante', 'Riga 9')
    e, = explanations([source])
    assert e.historical == '40' and e.current == '44'
    assert e.historical_context.endswith('40 minuti riportante')
    assert e.current_context.endswith('44 minuti riportante')
    assert e.location == 'Riga 9' and e.detail == 'token 4'


def test_isolated_values_stay_isolated():
    e, = explanations([Reason('INVARIANT_CHANGED', '40', '44', 'solo numero')])
    assert not e.historical_context and not e.current_context
    assert e.historical == '40' and e.current == '44'


def test_singleton_family_explanation_is_not_misleading():
    e, = explanations([Reason('UNAPPROVED_STACK_VARIANT', '1 conferma QA',
                              '14 frame / org.awaitility.core.ConditionTimeoutException',
                              'Stack già osservato; generalizzazione non autorizzata')])
    assert 'insufficienti' in e.title.lower()
    assert 'già osservato' in e.detail
