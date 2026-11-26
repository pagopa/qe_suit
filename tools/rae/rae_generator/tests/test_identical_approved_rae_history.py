"""Regression: approved legacy RAEs can repeat without explicit matching lineage."""

from rae_generator.ignore_rules import IgnoreRules
from rae_generator.qa_invariant_bridge import build_learning, evaluate

SUITE = 'NrtStandardSuiteTest'
TEST = ('[CONSUMER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento '
        'della richiesta di fruizione si abilita quando pronto per il fruitore '
        '[Given=BFF,When=WEB,Then=WEB]')
BUG = 'https://pagopa.atlassian.net/browse/PIN-11528'


def log(first, second, message='Impossibile verificare lo stato del button'):
    return (f'java.util.NoSuchElementException: {message}\n'
            '    at it.pagopa.infrastructure.suit.component.Button.isDisabled(Button.java:17)\n'
            f'    at jdk.proxy2/jdk.proxy2.$Proxy{first}.isDisabled(Unknown Source)\n'
            '    at it.pagopa.interop.web.agreement.infrastructure.WebAgreementGateway.verifyAgreementDownloadAvailability(WebAgreementGateway.java:132)\n'
            f'    at jdk.proxy2/jdk.proxy2.$Proxy{second}.verifyConsumerAgreementDownloadAvailability(Unknown Source)\n')


def row(day, error, ticket=BUG, matching=None):
    item = {
        '_source_file': f'rae_{SUITE}_202611{day}213000.yaml',
        '_source_suite': SUITE,
        'id_test': TEST,
        'error_log': error,
        'procedure': 'Controllare la presenza del pulsante.',
        'result': 'Pulsante assente durante il controllo.',
        'link_prod_bug': ticket,
        'link_suite_bug': '',
    }
    if matching is not None:
        item['matching'] = matching
    return item


def rules(tmp_path):
    config = tmp_path / 'ignore_rules.yaml'
    config.write_text('''version: 1
rules:
  - id: java-generated-proxy-ordinal
    scope: stack_method
    pattern: '(?<=\\$Proxy)[0-9]+(?=\\.)'
    replacement: '<PROXY_ORDINAL>'
''', encoding='utf-8')
    return IgnoreRules.load(config)


def classify(history, error, ignore_rules):
    engine, candidates = build_learning(history, ignore_rules=ignore_rules)
    return evaluate(engine, candidates, TEST, error, SUITE, ignore_rules=ignore_rules)


def test_legacy_recurrences_become_known_after_proxy_normalization(tmp_path):
    """Real pattern from the 17/18/23/25 Nov CONSUMER_AGREEMENT_DOWNLOAD_1 KO."""
    evidence = [row('23', log(274, 152)),
                row('18', log(272, 151)),
                row('17', log(272, 150))]
    status, historical, reasons = classify(evidence, log(275, 154), rules(tmp_path))
    assert status == 'known', reasons
    assert historical is not None and historical['link_prod_bug'] == BUG
    assert not reasons


def test_same_test_and_log_but_different_ticket_is_not_known(tmp_path):
    evidence = [row('23', log(274, 152)),
                row('18', log(272, 151), ticket='https://pagopa.atlassian.net/browse/PIN-OTHER')]
    status, historical, reasons = classify(evidence, log(275, 154), rules(tmp_path))
    assert status == 'proposed'
    assert historical is None
    assert any(r.code == 'HISTORICAL_DEFECT_CONFLICT' for r in reasons)


def test_different_diagnostic_is_not_merged_into_known(tmp_path):
    evidence = [row('23', log(274, 152)),
                row('18', log(272, 151, message='Errore diverso'))]
    status, _, _ = classify(evidence, log(275, 154), rules(tmp_path))
    assert status != 'known'


def test_auto_matched_history_does_not_count_as_independent_qa(tmp_path):
    evidence = [row('23', log(274, 152)),
                row('18', log(272, 151), matching={'status': 'auto_matched'})]
    status, _, _ = classify(evidence, log(275, 154), rules(tmp_path))
    assert status != 'known'


def test_unconfirmed_proposal_does_not_count_as_independent_qa(tmp_path):
    evidence = [row('23', log(274, 152)),
                row('18', log(272, 151), matching={
                    'status': 'pending_confirmation', 'proposed_digest': '0' * 64})]
    status, _, _ = classify(evidence, log(275, 154), rules(tmp_path))
    assert status != 'known'
