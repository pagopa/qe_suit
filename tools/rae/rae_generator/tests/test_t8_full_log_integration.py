"""Real ProgressiveMatcher integration; requires project Drain3 installation."""
from rae_generator.progressive import ProgressiveMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'TestCreazionePurpose'
BASE = '/aws/ecs/pn-national-registries'
CHANGED = '/aws/ecs/pn-national-registriez'


def log(path=BASE, *, frame_line=1136):
    return ('Given viene generata una nuova notifica passed\n'
            f'And verifico il log "{BASE}" passed\n'
            'And verifico il log failed\n'
            'org.awaitility.core.ConditionTimeoutException: audit log non trovato\n'
            '    at example.AwsSteps.checkAudit(AwsSteps.java:96)\n'
            f'    at java.base.ThreadPool.runWorker(ThreadPool.java:{frame_line})\n'
            f'And verifico il log "{path}" skipped\n')


def row(day, error, ticket='QA-100', parent=None):
    r = {'_source_file': f'rae_{SUITE}_202611{day}000000.yaml',
         '_source_suite': SUITE, 'id_test': TEST, 'error_log': error,
         'procedure': 'Analisi approvata', 'result': 'Difetto noto',
         'link_prod_bug': ticket, 'link_suite_bug': ''}
    if parent is not None:
        r['matching'] = {'status': 'confirmed', 'source_rae': parent['_source_file'],
                         'source_id_test': parent['id_test']}
    return r


def history():
    old = row('16', log())
    newer = row('17', log(), parent=old)
    return [newer, old]


def test_production_matcher_catches_invariant_after_stack():
    m = ProgressiveMatcher(history())
    status, _ = m.classify(TEST, log(CHANGED), suite=SUITE)
    assert status == 'proposed'
    assert any(r.code == 'INVARIANT_CHANGED'
               and BASE in r.historical and CHANGED in r.current
               for r in m.last_review_reasons)


def test_production_matcher_explains_both_after_stack_and_frame_line():
    m = ProgressiveMatcher(history())
    status, _ = m.classify(TEST, log(CHANGED, frame_line=432), suite=SUITE)
    assert status == 'proposed'
    codes = {r.code for r in m.last_review_reasons}
    assert 'INVARIANT_CHANGED' in codes
    assert 'LINE_NUMBER_UNSEEN' in codes


def test_production_matcher_unchanged_is_known():
    m = ProgressiveMatcher(history())
    status, _ = m.classify(TEST, log(), suite=SUITE)
    assert status == 'known' and not m.last_review_reasons


def test_production_matcher_qa_approved_change_enters_model():
    old = history()
    third = row('18', log(CHANGED), parent=old[0])
    m = ProgressiveMatcher([third, *old])
    status, _ = m.classify(TEST, log(CHANGED), suite=SUITE)
    assert status == 'known'
    status2, _ = m.classify(TEST, log('/aws/ecs/other'), suite=SUITE)
    assert status2 != 'known'


def test_production_t7_conflicting_singleton_vetoes_exact_match():
    conflicting = row('19', log(), ticket='QA-104')
    m = ProgressiveMatcher([conflicting, *history()])
    status, previous = m.classify(TEST, log(), suite=SUITE)
    assert status == 'proposed' and previous is None
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT' for r in m.last_review_reasons)
