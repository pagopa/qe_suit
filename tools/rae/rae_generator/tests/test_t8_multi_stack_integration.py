"""Regression against real production ProgressiveMatcher (requires project Drain3)."""
from rae_generator.progressive import ProgressiveMatcher
from test_t8_multi_stack_variants import SUITE, TEST, log


def row(day, error, ticket='QA-100', parent=None):
    result = {'_source_file': f'rae_{SUITE}_202611{day}000000.yaml',
              '_source_suite': SUITE, 'id_test': TEST, 'error_log': error,
              'procedure': 'Analisi QA confermata', 'result': 'Difetto verificato',
              'link_prod_bug': ticket, 'link_suite_bug': ''}
    if parent is not None:
        result['matching'] = {'status': 'confirmed',
                              'source_rae': parent['_source_file'],
                              'source_id_test': parent['id_test']}
    return result


def history():
    first = row('16', log(kind='audit'))
    second = row('17', log(iun='KVDG-HQUE-WLKV-202611-H-1',
                           feature_line=350, first_line=1176), parent=first)
    third = row('18', log(feature_line=387, first_line=1259), parent=second)
    return [third, second, first]


def test_multi_stack_approved_line_variant_known_in_production_matcher():
    m = ProgressiveMatcher(history())
    status, previous = m.classify(TEST, log(feature_line=350, first_line=1259), suite=SUITE)
    assert status == 'known'
    assert previous is not None and previous['link_prod_bug'] == 'QA-100'
    assert not m.last_review_reasons


def test_multi_stack_singleton_competitor_blocks_known_in_production_matcher():
    current = log(feature_line=350, first_line=1259)
    competing = row('19', current, ticket='QA-104')
    m = ProgressiveMatcher([competing, *history()])
    status, previous = m.classify(TEST, current, suite=SUITE)
    assert status == 'proposed' and previous is None
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT'
               for r in m.last_review_reasons)


def test_multi_stack_changed_method_not_known_in_production_matcher():
    m = ProgressiveMatcher(history())
    current = log(feature_line=350).replace('example.B2bSteps.checkIfTimelineElementExists',
                                             'example.B2bSteps.unapprovedMethod')
    status, _ = m.classify(TEST, current, suite=SUITE)
    assert status != 'known'
