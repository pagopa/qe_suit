"""T7/T8 hotfix integration tests against production ProgressiveMatcher."""
from rae_generator.progressive import ProgressiveMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'TestCreazionePurpose'


def log(n, header, marker='A'):
    return ('Given crea notifica passed\n'
            'And verifica evento PUBLIC_REGISTRY_RESPONSE failed\n'
            f'{header}: evento mancante {marker}\n'
            + ''.join(f'    at example.Stack{i}.call(Stack{i}.java:{i+10})\n'
                      for i in range(n)))


def row(day, error, ticket='QA-100', parent=None):
    row = {'_source_file': f'rae_{SUITE}_202611{day}000000.yaml',
           '_source_suite': SUITE, 'id_test': TEST, 'error_log': error,
           'procedure': 'Analisi QA', 'result': 'Difetto verificato',
           'link_prod_bug': ticket, 'link_suite_bug': ''}
    if parent is not None:
        row['matching'] = {'status': 'confirmed',
                           'source_rae': parent['_source_file'],
                           'source_id_test': parent['id_test']}
    return row


def test_exact_approved_five_frames_repeated_with_long_stack_in_same_group():
    first = row('16', log(14, 'ConditionTimeoutException', 'FIRST'))
    second = row('17', log(5, 'AssertionFailedError', 'SECOND'), parent=first)
    third = row('18', log(5, 'AssertionFailedError', 'THIRD'), parent=second)
    matcher = ProgressiveMatcher([third, second, first])
    status, historical = matcher.classify(TEST, third['error_log'], suite=SUITE)
    assert status == 'known'
    assert historical is not None and historical['link_prod_bug'] == 'QA-100'
    assert not matcher.last_review_reasons


def test_conflicting_singleton_blocks_exact_match_even_with_mixed_stack():
    first = row('16', log(14, 'ConditionTimeoutException', 'FIRST'))
    second = row('17', log(5, 'AssertionFailedError', 'SECOND'), parent=first)
    third = row('18', log(5, 'AssertionFailedError', 'THIRD'), parent=second)
    competitor = row('19', third['error_log'], ticket='QA-104')
    matcher = ProgressiveMatcher([competitor, third, second, first])
    status, historical = matcher.classify(TEST, third['error_log'], suite=SUITE)
    assert status == 'proposed' and historical is None
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT' for r in matcher.last_review_reasons)
