"""QA confirmation decisions: never mutate original matching.status."""
from copy import deepcopy
from rae_generator.qa_confirmation import analysis_digest, is_qa_confirmed
from rae_generator.renderer import render


def row():
    r = dict(id_test='Test A', error_log='AssertionError: failed\n at x.java:1',
             procedure='Verify scenario', result='Bug already analyzed',
             link_prod_bug='https://example.test/TICKET-1', link_suite_bug='')
    r['matching'] = dict(status='pending_confirmation', source_rae='rae_NrtStandardSuiteTest_20261117213615.yaml',
                         source_id_test='Test A', proposed_digest=analysis_digest(r))
    return r


def document(r):
    return dict(rae=dict(release='v1', scope='NIGHTLY', issue_jira='https://example.test/QA-1',
                         data_ora='2026-11-18T20:00:00Z', durata='00:00:10', numero_ko=1,
                         run='https://example.test', suite='NrtStandardSuiteTest', environment='qa',
                         executor='human', source_branch='develop'), ko=[r], ko_know=[])


def test_pending_does_not_get_approved_on_generation():
    current = row()
    assert not is_qa_confirmed(current, original=deepcopy(current), qa_reviewed=False)
    assert is_qa_confirmed(current, original=deepcopy(current), qa_reviewed=True)
    assert current['matching']['status'] == 'pending_confirmation'


def test_edited_analysis_never_confirms_even_if_qa_revises():
    orig = row()
    cur = deepcopy(orig)
    cur['procedure'] += ' changed'
    assert not is_qa_confirmed(cur, original=orig, qa_reviewed=True)
    assert not is_qa_confirmed(cur, approved_history=True)
    assert cur['matching']['status'] == 'pending_confirmation'


def test_identity_or_metadata_tampering_cannot_confirm():
    original = row()
    for changed in ('id_test', 'error_log'):
        current = deepcopy(original)
        current[changed] += ' modified'
        assert not is_qa_confirmed(current, original=original, qa_reviewed=True)
    current = deepcopy(original)
    current['matching']['source_rae'] = 'rae_NrtStandardSuiteTest_20261116223758.yaml'
    assert not is_qa_confirmed(current, original=original, qa_reviewed=True)


def test_auto_matches_are_never_training_data():
    current = row()
    current['matching']['status'] = 'auto_matched'
    assert not is_qa_confirmed(current, approved_history=True)
    assert not is_qa_confirmed(current, original=row(), qa_reviewed=True)


def test_renderer_shows_final_message_only_with_review_and_unchanged_analysis():
    old = document(row())
    data = deepcopy(old)
    before = deepcopy(data)
    initial = render(data)
    assert 'Validazione finale prima dell’automazione' not in initial
    assert 'Corrispondenza storica da validare' in initial
    reviewed = render(data, original_data=old, qa_reviewed=True)
    assert 'Validazione finale prima dell’automazione' in reviewed
    assert 'nelle prossime esecuzioni' in reviewed
    assert data == before, 'The report must never be mutated during rendering'
    modified = deepcopy(data)
    modified['ko'][0]['procedure'] = 'Changed procedure'
    assert 'Validazione finale prima dell’automazione' not in render(modified, original_data=old, qa_reviewed=True)


def test_history_learns_only_unchanged_approved_suggestion():
    current = row()
    assert is_qa_confirmed(current, approved_history=True)
    current['result'] = 'QA correction'
    assert not is_qa_confirmed(current, approved_history=True)
