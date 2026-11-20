"""Adversarial regression tests for conservative Drain3-based RAE matching.

Run on repository checkout:
  PYTHONPATH=tools/rae/rae_generator python -m pytest -q tools/rae/rae_generator/tests/test_rae_signature_edge_cases.py
These tests must not change the source code or any historical RAE.
"""
from __future__ import annotations
import copy
import re
import pytest

from rae_generator import progressive as p
from rae_generator.qa_confirmation import analysis_digest, is_qa_confirmed

TID = 'WebPurposeTemplateContractTest::createPurposeTemplate::disabled'
TICKET = 'https://pagopa.atlassian.net/browse/QA-19652'
BASE = 'java.lang.IllegalStateException: Polling in timeout dopo 20 secondi. Purpose Template Creation Page is not loaded yet. Page title is blank'
UUID_A = 'd147c9e0-86ab-4c13-a24e-53704dcf8f01'
UUID_B = '38d6b3bf-e622-4804-8eda-420ed548e1cc'
UUID_C = '562b4ef8-58ed-4370-a32a-f3fbf3b889a1'


def historical(filename, *, test=TID, error=BASE, ticket=TICKET, matching=None):
    row = dict(id_test=test, error_log=error, procedure='Verificare URL e titolo',
               result='Timeout pagina in carico', link_prod_bug='', link_suite_bug=ticket,
               _source_file=filename, _source_suite='NrtStandardSuiteTest',
               _source_environment='qa')
    if matching is not None:
        row['matching'] = copy.deepcopy(matching)
    return row


def approval(source, filename='rae_NrtStandardSuiteTest_20261118213605.yaml', *, error=None, ticket=TICKET):
    row = historical(filename, error=source['error_log'] if error is None else error, ticket=ticket)
    row['matching'] = {'status': 'pending_confirmation', 'source_rae': source['_source_file'],
                       'source_id_test': source['id_test']}
    row['matching']['proposed_digest'] = analysis_digest(row)
    return row


def signature(template, samples, *, test=TID, members=()):
    regex, kinds = p._pattern_regex(template)
    return p.Signature(test, list(members), template, regex, kinds,
                       [tuple(x) for x in samples])


# -------------- PURE SIGNATURE LOGIC (no Drain3 dependency) --------------
@pytest.mark.parametrize('error, expected', [
    (BASE + '\n  at example.Class.method(Foo.java:12)', BASE),
    (BASE + '\r\n\tat example.Class.method(Foo.java:12)', BASE),
    (BASE + '\n    at pkg.X.call(X.java:44)\nCaused by: other', BASE),
    ('  java.lang.AssertionError:   HTTP 400   instead 200  ', 'java.lang.AssertionError: HTTP 400 instead 200'),
])
def test_error_head_preserves_frames_and_whitespace(error, expected):
    # Contratto aggiornato: non tagliare lo stack trace.
    assert p.error_head(error) == re.sub(r'\s+', ' ', error).strip()


@pytest.mark.parametrize('changed', [
    'java.lang.IllegalStateException: Polling in timeout dopo 60 secondi. Purpose Template Creation Page is not loaded yet. Page title is blank',
    'java.lang.IllegalStateException: Polling in timeout dopo 20 secondi. Purpose Template Edit Page is not loaded yet. Page title is blank',
    'java.lang.IllegalStateException: Polling in timeout dopo 20 secondi. Purpose Template Creation Page is not loaded yet. Page title is visible',
    'java.lang.NullPointerException: Polling in timeout dopo 20 secondi. Purpose Template Creation Page is not loaded yet. Page title is blank',
])
def test_exact_signature_rejects_diagnostic_changes(changed):
    sig = signature(BASE, [()])
    assert sig.matches(BASE)
    assert not sig.matches(changed)


@pytest.mark.parametrize('status', ['400', '401', '403', '404', '500', '503'])
def test_wildcard_rejects_unobserved_http_status(status):
    sig = signature('HTTP response status <*>, expected 200', [('200',), ('201',)])
    assert not sig.matches(f'HTTP response status {status}, expected 200')


def test_wildcard_allows_only_exact_observed_values():
    sig = signature('HTTP response status <*>', [('400',), ('500',)])
    assert sig.matches('HTTP response status 400')
    assert sig.matches('HTTP response status 500')
    assert not sig.matches('HTTP response status 502')


def test_no_generalization_for_new_timeout_even_with_two_samples():
    sig = signature('Polling in timeout dopo <*> secondi', [('20',), ('30',)])
    assert not sig.matches('Polling in timeout dopo 60 secondi')


def test_uuid_single_observed_value_no_generalization():
    sig = signature('Failure correlation <UUID>', [(UUID_A,)])
    assert sig.matches('Failure correlation ' + UUID_A)
    assert not sig.matches('Failure correlation ' + UUID_C)


def test_uuid_two_distinct_values_enable_generalization():
    sig = signature('Failure correlation <UUID>', [(UUID_A,), (UUID_B,)])
    assert sig.matches('Failure correlation ' + UUID_C)
    assert not sig.matches('Failure correlation malformed-uuid')


def test_timestamp_one_value_no_generalization():
    sig = signature('Failure at <TIMESTAMP>', [('2026-11-16T20:00:00Z',)])
    assert not sig.matches('Failure at 2026-11-19T20:00:00Z')


def test_timestamp_two_values_enable_generalization():
    sig = signature('Failure at <TIMESTAMP>', [('2026-11-16T20:00:00Z',), ('2026-11-17T21:00:00Z',)])
    assert sig.matches('Failure at 2026-11-19T20:00:00Z')
    assert sig.matches('Failure at 2026-11-19T20:00:00+01:00')
    assert not sig.matches('Failure at yesterday')


def test_two_masks_require_each_to_have_variation():
    sig = signature('Failure <UUID> at <TIMESTAMP>', [
        (UUID_A, '2026-11-16T20:00:00Z'), (UUID_B, '2026-11-16T20:00:00Z')])
    assert sig.matches(f'Failure {UUID_C} at 2026-11-16T20:00:00Z')
    assert not sig.matches(f'Failure {UUID_C} at 2026-11-19T20:00:00Z')


def test_pattern_metacharacters_treated_literally():
    sig = signature('HTTP [POST] /api/x?a=1 (error) <*>', [('42',)])
    assert sig.matches('HTTP [POST] /api/x?a=1 (error) 42')
    assert not sig.matches('HTTP POST /api/xa=1 error 42')


def test_signature_requires_same_whole_head_not_substring():
    sig = signature(BASE, [()])
    assert not sig.matches('prefix ' + BASE)
    assert not sig.matches(BASE + ' suffix')


def test_identity_casefold_trims_test_id_only():
    row = historical('run18.yaml', test='  MyTest::case  ')
    assert p._identity(row) == ('run18.yaml', 'mytest::case')


def test_signature_does_not_consider_test_id_on_its_own():
    sig = signature(BASE, [()], test='test-A')
    assert sig.matches(BASE)  # Matcher.classify, not Signature.matches, enforces id_test


# -------------- MATCHER POLICY under controlled miner/signatures --------------
class Cluster:
    cluster_id = 7

class ControlledMiner:
    def match(self, error, full_search_strategy=None):
        return Cluster()


def controlled_matcher(rows, signatures):
    matcher = p.ProgressiveMatcher.__new__(p.ProgressiveMatcher)
    matcher.history = rows
    matcher.miner = ControlledMiner()
    matcher.cluster_members = {7: rows}
    matcher.signatures = signatures
    return matcher


def test_matcher_rejects_other_test_even_with_same_signature():
    row = historical('run18.yaml')
    m = controlled_matcher([row], [signature(BASE, [()], members=[row])])
    decision, item = m.classify('OtherTest', BASE)
    assert (decision, item) == ('new', None)


def test_matcher_never_auto_knows_without_compatible_signature(monkeypatch):
    row = historical('run18.yaml')
    monkeypatch.setattr(p, 'find_match', lambda *args, **kwargs: None)
    m = controlled_matcher([row], [])
    assert m.classify(TID, BASE) == ('new', None)


def test_matcher_rejects_ambiguous_signatures(monkeypatch):
    row = historical('run18.yaml')
    monkeypatch.setattr(p, 'find_match', lambda *args, **kwargs: None)
    s = signature(BASE, [()], members=[row])
    m = controlled_matcher([row], [s, s])
    assert m.classify(TID, BASE) == ('new', None)


def test_matcher_rejects_pending_with_changed_analysis(monkeypatch):
    src = historical('run16.yaml')
    pending = approval(src)
    pending['result'] = 'Diagnosi modificata dopo approvazione'
    monkeypatch.setattr(p, 'find_match', lambda *args, **kwargs: None)
    m = controlled_matcher([pending], [signature(BASE, [()], members=[src, pending])])
    assert m.classify(TID, BASE) == ('new', None)


def test_matcher_allows_approved_pending_with_ticket():
    src = historical('run16.yaml')
    pending = approval(src)
    m = controlled_matcher([pending], [signature(BASE, [()], members=[src, pending])])
    decision, selected = m.classify(TID, BASE)
    assert decision == 'known' and selected is pending


def test_matcher_does_not_auto_know_ticketless_candidates(monkeypatch):
    src = historical('run16.yaml', ticket='')
    pending = approval(src, ticket='')
    monkeypatch.setattr(p, 'find_match', lambda *args, **kwargs: None)
    m = controlled_matcher([pending], [signature(BASE, [()], members=[src, pending])])
    assert m.classify(TID, BASE) == ('new', None)


def test_matcher_same_signature_different_exception_not_known(monkeypatch):
    src = historical('run16.yaml')
    monkeypatch.setattr(p, 'find_match', lambda *args, **kwargs: None)
    m = controlled_matcher([src], [signature(BASE, [()], members=[src])])
    assert m.classify(TID, BASE.replace('IllegalStateException', 'NullPointerException')) == ('new', None)


def test_new_error_does_not_expand_signature_by_itself():
    s = signature('HTTP status <*>', [('400',)])
    row = historical('run16.yaml', error='HTTP status 400')
    m = controlled_matcher([row], [s])
    assert m.classify(TID, 'HTTP status 500')[0] != 'known'
    assert s.samples == [('400',)]


def test_known_ticket_must_come_from_confirmed_signature_members():
    """An unrelated newer compatible entry must not override the approved ticket.

    The observed code selects the first compatible candidate, without checking
    whether it belongs to the approved equivalence class that formed the signature.
    """
    src = historical('run16.yaml', ticket='https://jira.example/QA-100')
    approved = approval(src, filename='run18.yaml', ticket='https://jira.example/QA-100')
    unrelated = historical('run19.yaml', ticket='https://jira.example/QA-999')
    sig = signature(BASE, [()], members=[src, approved])
    m = controlled_matcher([unrelated, approved, src], [sig])
    decision, chosen = m.classify(TID, BASE)
    assert decision == 'known'
    assert chosen['link_suite_bug'] == 'https://jira.example/QA-100'


# -------------- REAL DRAIN3 INTEGRATION (never emulated) --------------
@pytest.mark.drain3_real
def test_real_two_confirmed_occurrences_create_a_known_signature():
    src = historical('run16.yaml')
    confirmed = approval(src)
    m = p.ProgressiveMatcher([confirmed, src])
    decision, row = m.classify(TID, BASE)
    assert decision == 'known' and row is not None and row['link_suite_bug'] == TICKET


@pytest.mark.drain3_real
@pytest.mark.parametrize('changed', [
    BASE.replace('20 secondi', '60 secondi'),
    BASE.replace('IllegalStateException', 'NullPointerException'),
    BASE.replace('Page title is blank', 'Page title is missing'),
])
def test_real_diagnostic_changes_must_not_be_auto_known(changed):
    src = historical('run16.yaml')
    confirmed = approval(src)
    m = p.ProgressiveMatcher([confirmed, src])
    assert m.classify(TID, changed)[0] != 'known'


@pytest.mark.drain3_real
def test_real_same_error_different_test_must_not_be_known():
    src = historical('run16.yaml')
    confirmed = approval(src)
    m = p.ProgressiveMatcher([confirmed, src])
    assert m.classify('DifferentTest', BASE)[0] != 'known'


@pytest.mark.drain3_real
def test_real_pending_without_qa_does_not_train():
    src = historical('run16.yaml')
    pending = approval(src)
    pending['result'] = 'QA changed the result'
    m = p.ProgressiveMatcher([pending, src])
    assert m.classify(TID, BASE)[0] != 'known'


@pytest.mark.drain3_real
def test_real_auto_matched_record_does_not_train():
    src = historical('run16.yaml')
    auto = historical('run18.yaml', matching={'status': 'auto_matched',
        'source_rae': 'run16.yaml', 'source_id_test': TID})
    m = p.ProgressiveMatcher([auto, src])
    assert m.classify(TID, BASE)[0] != 'known'


@pytest.mark.drain3_real
def test_real_order_independence_of_known_classification():
    src = historical('run16.yaml')
    confirmed = approval(src)
    outcomes = [p.ProgressiveMatcher(rows).classify(TID, BASE)[0]
                for rows in ([confirmed, src], [src, confirmed])]
    assert outcomes == ['known', 'known']


@pytest.mark.drain3_real
def test_real_stack_frame_change_does_not_create_false_positive():
    """Threat model: identical exception head but different application call site.

    This is an intentionally stringent safety assertion, likely to expose a
    current blind spot because error_head strips all Java frames.
    """
    first = BASE + '\n    at it.pagopa.web.PageA.assertLoaded(PageA.java:34)'
    second = BASE + '\n    at it.pagopa.web.PageB.assertLoaded(PageB.java:90)'
    src = historical('run16.yaml', error=first)
    confirmed = approval(src, error=first)
    m = p.ProgressiveMatcher([confirmed, src])
    assert m.classify(TID, second)[0] != 'known'


@pytest.mark.drain3_real
def test_real_empty_and_malformed_logs_never_auto_known():
    src = historical('run16.yaml')
    confirmed = approval(src)
    m = p.ProgressiveMatcher([confirmed, src])
    for error in ('', '  ', '\n\n', 'garbled', 'java.lang.IllegalStateException:'):
        assert m.classify(TID, error)[0] != 'known'
