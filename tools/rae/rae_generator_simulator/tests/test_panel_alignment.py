"""QA-facing filtering is shared between Streamlit and the Markdown renderer."""
from dataclasses import dataclass
from rae_generator.review_evidence import snapshot, visible_evidence, render_warning


@dataclass
class Reason:
    code: str
    historical: str = ''
    current: str = ''
    historical_context: str = ''
    current_context: str = ''


def render(*reasons):
    raw = snapshot(reasons)
    return raw, visible_evidence(raw), '\n'.join(render_warning({'review_evidence': raw}))


def test_specific_differences_suppress_generic_stack():
    raw, visible, html = render(
        Reason('INVARIANT_CHANGED', '40', '44', 'negli ultimi 40 minuti', 'negli ultimi 44 minuti'),
        Reason('UNAPPROVED_STACK_VARIANT', '14 frame', '14 frame'),
        Reason('LINE_NUMBER_UNSEEN', '1136', '432'),
    )
    assert len(raw) == 3
    assert len(visible) == 2
    assert '2 evidenze da verificare' in html
    assert 'Stack già osservato' not in html
    assert 'Non ci sono sufficienti evidenze per generalizzare' not in html
    assert '<code>40</code>' in html and '<code>44</code>' in html
    assert '1136' in html and '432' in html
    assert html.count('<details>') == 1


def test_only_generic_reason_gets_one_plain_explanation():
    _, visible, html = render(Reason('UNAPPROVED_STACK_VARIANT', '14 frame', '14 frame'))
    assert visible == [{'code':'QA_REVIEW_REQUIRED'}]
    assert '1 evidenza da verificare' in html
    assert 'non consentono di confermare automaticamente' in html
    assert 'Stack già osservato' not in html


def test_concurrent_conflict_is_retained():
    _, visible, html = render(Reason('UNAPPROVED_STACK_VARIANT'),
                              Reason('AMBIGUOUS_QA_GROUPS', 'QA-100', 'QA-200'))
    assert len(visible) == 1
    assert 'Più analisi storiche compatibili' in html
    assert 'generalizzare' not in html


def test_isolated_value_and_html_escaping():
    _, _, html = render(Reason('INVARIANT_CHANGED', '<script>40</script>', '44'))
    assert '&lt;script&gt;' in html and '<script>' not in html
    assert 'Contesto:' not in html


def test_no_reasons_does_not_invent_evidence():
    assert visible_evidence([]) == []
    html = '\n'.join(render_warning({}))
    assert 'evidenze da verificare' not in html
