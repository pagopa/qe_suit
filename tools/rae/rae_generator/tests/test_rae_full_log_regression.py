"""RAE fail-closed regressions on full Java traces and public multi-product logs.

No product-specific stack-frame allowlists. Public Loghub messages are actual
log excerpts, but QA tickets/labels are SYNTHETIC test annotations.
"""
from __future__ import annotations
import json
from pathlib import Path
import re
import pytest
from rae_generator import progressive as p
from rae_generator.qa_confirmation import analysis_digest

FIXTURE = Path(__file__).with_name('fixtures') / 'loghub_public_samples.json'
PUBLIC = json.loads(FIXTURE.read_text(encoding='utf-8'))['datasets']
TID = 'IntegrationSuite::workflow::case1'
BASE = 'java.lang.IllegalStateException: Polling timed out after 20 seconds'
TRACE = BASE + '\n  at org.acme.payment.PurposePage.createPurpose(PurposePage.java:120)\n  at org.acme.test.PurposeTest.run(PurposeTest.java:50)'


def row(filename, error=TRACE, ticket='QA-100', test=TID, matching=None):
    r = {'_source_file':filename,'id_test':test,'error_log':error,'procedure':'Check service response',
         'result':'Known failure in operation', 'link_prod_bug':'', 'link_suite_bug':ticket}
    if matching is not None:
        r['matching'] = matching
    return r


def confirmed(source, filename='rae_Suite_20261118213605.yaml', ticket=None, error=None):
    r = row(filename, error=source['error_log'] if error is None else error,
            ticket=source['link_suite_bug'] if ticket is None else ticket,
            test=source['id_test'])
    r['matching'] = {'status':'pending_confirmation', 'source_rae':source['_source_file'],
                     'source_id_test':source['id_test'], 'proposed_digest': analysis_digest(r)}
    return r


def classifier(rows):
    return p.ProgressiveMatcher(rows)


def known(rows, error=TRACE, test=TID):
    return classifier(rows).classify(test, error)[0] == 'known'


@pytest.mark.parametrize('log', [
    TRACE,
    TRACE.replace('120', '145'),
    TRACE.replace('PurposePage.createPurpose', 'PurposePage.searchPurpose'),
    TRACE.replace('PurposePage', 'OtherPage'),
    TRACE.replace('PurposeTest.run', 'PurposeTest.retry'),
    TRACE.replace('PurposePage.java:120', 'PurposePage.java:121'),
    TRACE + '\n at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)',
    TRACE.replace('  at org.acme.payment.PurposePage.createPurpose(PurposePage.java:120)\n', ''),
    TRACE.replace('java.lang.IllegalStateException', 'java.lang.AssertionError'),
    TRACE.replace('20 seconds', '30 seconds'),
    TRACE + '\nCaused by: java.net.SocketTimeoutException: timed out',
    TRACE.replace('org.acme.payment.', 'org.acme.billing.'),
    TRACE.replace('PurposeTest.java:50', 'PurposeTest.java:51'),
    TRACE.replace('  at org.acme.test.PurposeTest.run(PurposeTest.java:50)', '  at org.acme.test.PurposeTest.run(PurposeTest.java:50)\n  at org.acme.test.TestRunner.execute(TestRunner.java:91)'),
])
def test_full_log_retained_including_java_frames(log):
    result = p.error_head(log)
    assert result == re.sub(r'\s+', ' ', log).strip()
    assert 'at ' in result


@pytest.mark.parametrize('change', [
    TRACE.replace('PurposePage.createPurpose', 'PurposePage.searchPurpose'),
    TRACE.replace('PurposePage', 'OtherPage'),
    TRACE.replace('PurposeTest.run', 'PurposeTest.retry'),
    TRACE.replace('PurposePage.java:120', 'PurposePage.java:121'),
    TRACE.replace('PurposeTest.java:50', 'PurposeTest.java:51'),
    TRACE + '\n at org.acme.trace.Monitor.probe(Monitor.java:90)',
    TRACE.replace('  at org.acme.payment.PurposePage.createPurpose(PurposePage.java:120)\n', ''),
    TRACE + '\nCaused by: java.io.IOException: connection refused',
    TRACE.replace('IllegalStateException', 'NullPointerException'),
    TRACE.replace('20 seconds', '25 seconds'),
    TRACE.replace('org.acme.payment.', 'org.acme.billing.'),
    TRACE.replace('  at org.acme.test.PurposeTest.run(PurposeTest.java:50)', ''),
])
def test_changed_diagnostics_never_automatically_known_without_qa(change):
    original = row('rae_Suite_20261116213605.yaml')
    approval = confirmed(original)
    assert known([approval, original], TRACE)
    assert not known([approval, original], change)


@pytest.mark.parametrize('changed', [
    TRACE.replace('PurposePage.createPurpose', 'PurposePage.searchPurpose'),
    TRACE.replace('PurposePage.java:120', 'PurposePage.java:121'),
    TRACE + '\n at org.acme.test.Monitor.watch(Monitor.java:16)',
])
def test_change_becomes_known_only_after_subsequent_qa_confirmation(changed):
    old = row('rae_Suite_20261116213605.yaml')
    new = row('rae_Suite_20261117213605.yaml', error=changed, ticket='QA-200')
    assert not known([new, old], changed)
    approval = confirmed(new, filename='rae_Suite_20261118213605.yaml')
    assert known([approval, new, old], changed)
    assert classifier([approval, new, old]).classify(TID, changed)[1]['link_suite_bug'] == 'QA-200'


def test_ticket_override_16_17_18_19_in_confirmed_lineage():
    """QA's newer correction wins, not the first unrelated same-log ticket."""
    old = row('rae_Suite_20261116213605.yaml', ticket='QA-100')
    rejected_proposal = confirmed(old, 'rae_Suite_20261117213605.yaml')
    rejected_proposal['link_suite_bug'] = 'QA-200'  # QA amended ticket: invalidates proposal digest
    new_confirmed = confirmed(rejected_proposal, 'rae_Suite_20261118213605.yaml')
    m = classifier([new_confirmed, rejected_proposal, old])
    decision, picked = m.classify(TID, TRACE)
    assert decision == 'known'
    assert picked['link_suite_bug'] == 'QA-200'


def test_foreign_candidate_with_ticket_cannot_enter_confirmed_group():
    old = row('rae_Suite_20261116213605.yaml', ticket='QA-100')
    foreign = row('rae_Suite_20261119213605.yaml', ticket='QA-999')
    approved = confirmed(old, 'rae_Suite_20261118213605.yaml')
    decision, picked = classifier([foreign, approved, old]).classify(TID, TRACE)
    assert decision == 'known' and picked['link_suite_bug'] == 'QA-100'


def test_only_outsider_has_ticket_must_not_be_known():
    old = row('rae_Suite_20261116213605.yaml', ticket='')
    foreign = row('rae_Suite_20261119213605.yaml', ticket='QA-999')
    approved = confirmed(old, 'rae_Suite_20261118213605.yaml')
    decision, _ = classifier([foreign, approved, old]).classify(TID, TRACE)
    assert decision != 'known'


@pytest.mark.parametrize('product', sorted(PUBLIC))
def test_public_dataset_has_real_non_interop_samples(product):
    assert len(PUBLIC[product]) >= 2
    assert all(isinstance(log, str) and log.strip() for log in PUBLIC[product])
    assert all(p.error_head(log) for log in PUBLIC[product])


@pytest.mark.parametrize('product', sorted(PUBLIC))
def test_public_log_unchanged_can_be_learned(product):
    """These are real logs but fake QA records; tests parser/matcher portability."""
    original = row(f'rae_Public_20261116213605.yaml', error=PUBLIC[product][0], test='Portable::'+product)
    approval = confirmed(original, filename='rae_Public_20261117213605.yaml')
    decision, selected = classifier([approval, original]).classify('Portable::'+product, PUBLIC[product][0])
    assert decision == 'known'
    assert selected is not None


@pytest.mark.parametrize('product', sorted(PUBLIC))
def test_public_log_other_test_never_known(product):
    original = row('rae_Public_20261116213605.yaml', error=PUBLIC[product][0], test='Portable::'+product)
    approval = confirmed(original, filename='rae_Public_20261117213605.yaml')
    assert not known([approval, original], PUBLIC[product][0], 'Portable::OtherTest')


@pytest.mark.parametrize('product', sorted(PUBLIC))
def test_public_log_changed_value_not_approved_no_auto_known(product):
    original = row('rae_Public_20261116213605.yaml', error=PUBLIC[product][0], test='Portable::'+product)
    approval = confirmed(original, filename='rae_Public_20261117213605.yaml')
    # Disjoint log, same test name, no new QA signature.
    assert not known([approval, original], 'DIAGNOSTICALLY_DIFFERENT '+PUBLIC[product][1], 'Portable::'+product)


@pytest.mark.parametrize('bad', ['', ' ', '\r\n', '\x00', 'garbled', 'None', '[]', '{}', 'java.lang.'])
def test_incomplete_logs_never_known(bad):
    original = row('rae_Suite_20261116213605.yaml')
    approval = confirmed(original)
    assert not known([approval, original], bad)


@pytest.mark.parametrize('variant', [
    TRACE+'\nCaused by: java.lang.IllegalArgumentException: invalid amount',
    TRACE+'\nSuppressed: java.io.IOException: transient',
    TRACE.replace('java.lang.IllegalStateException', 'java.lang.IndexOutOfBoundsException'),
    TRACE.replace('createPurpose', 'cancelPurpose'),
    TRACE+'\n  ... 14 more',
    TRACE+'\n\tat org.acme.extra.Foo.bar(Foo.java:200)',
])
def test_nested_causes_suppressed_frames_are_diagnostic(variant):
    src = row('rae_Suite_20261116213605.yaml')
    approval = confirmed(src)
    assert p.error_head(variant) != p.error_head(TRACE)
    assert not known([approval, src], variant)
