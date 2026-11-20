"""End-to-end real production matcher, requires project Drain3."""
from rae_generator.progressive import ProgressiveMatcher

SUITE = 'NrtStandardSuiteTest'
TEST = 'TestCreazionePurpose'


def log(minutes=40, line=1136):
    return ('Given si verifica il log negli ultimi ' + str(minutes) + ' minuti passed\n'
            'org.awaitility.core.ConditionTimeoutException: audit missing\n'
            '    at example.Aws.check(Aws.java:96)\n'
            f'    at example.Thread.runWorker(Thread.java:{line})\n'
            'Then il test termina failed\n')


def row(i, error, parent=None):
    r = {'_source_file': f'rae_{SUITE}_202611{i}120000.yaml',
         '_source_suite': SUITE, 'id_test': TEST, 'error_log': error,
         'procedure': 'Confermato', 'result': 'Noto',
         'link_prod_bug': 'QA-100', 'link_suite_bug': ''}
    if parent:
        r['matching'] = {'status': 'confirmed', 'source_rae': parent['_source_file'],
                         'source_id_test': TEST}
    return r


def test_real_matcher_emits_context_when_trusted():
    a = row(16, log()); b = row(17, log(), a)
    m = ProgressiveMatcher([b, a])
    status, _ = m.classify(TEST, log(minutes=44), suite=SUITE)
    assert status == 'proposed'
    reasons = [r for r in m.last_review_reasons if r.current == '44']
    assert reasons and reasons[0].historical == '40'
    assert reasons[0].current_context and reasons[0].historical_context


def test_real_matcher_still_reports_multiple_differences():
    a = row(16, log()); b = row(17, log(), a)
    m = ProgressiveMatcher([b, a])
    status, _ = m.classify(TEST, log(minutes=44, line=432), suite=SUITE)
    assert status == 'proposed'
    assert any(r.current == '44' for r in m.last_review_reasons)
    assert any(r.code == 'LINE_NUMBER_UNSEEN' for r in m.last_review_reasons)
