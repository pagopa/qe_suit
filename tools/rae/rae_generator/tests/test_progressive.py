"""End-to-end safety regression tests for progressive QA-driven recognition."""
from copy import deepcopy
import re

from rae_generator.progressive import (
    ProgressiveMatcher, Signature, _signatures, analysis_digest, error_head,
    _pattern_regex,
)

UUID_A = 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa'
UUID_B = 'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb'
UUID_C = 'cccccccc-cccc-4ccc-8ccc-cccccccccccc'


def log(status='400', uuid=UUID_A, exception='java.lang.AssertionError'):
    return (f'{exception}: HTTP contract expectation failed\n'
            'scenario: REPLACED_WITH_EMPTY_STRING\n'
            'targetPath: /rejectionReason\n'
            f'status: {status}\n'
            f'delegationId: {uuid}\n'
            '    at com.example.MyTest.run(MyTest.java:12)')


def row(rae, *, status='400', uuid=UUID_A, ticket='QA-100', matching=None):
    result = {'_source_file': rae, 'id_test': 'TestA', 'error_log': log(status, uuid),
              'procedure': 'diagnosi QA', 'result': 'Bug prodotto',
              'link_prod_bug': ticket, 'link_suite_bug': ''}
    if matching:
        result['matching'] = matching
    return result


def historical_pair():
    first = row('rae_NrtStandardSuiteTest_20261116000000.yaml', uuid=UUID_A)
    second = row('rae_NrtStandardSuiteTest_20261117000000.yaml', uuid=UUID_B)
    second['matching'] = {
        'status': 'pending_confirmation',
        'source_rae': first['_source_file'],
        'source_id_test': first['id_test'],
        'proposed_digest': analysis_digest(second),
    }
    return first, second


def test_stack_frames_preserved_with_exception():
    result = error_head(log())

    assert result.startswith('java.lang.AssertionError: HTTP contract')
    assert 'com.example.MyTest.run' in result
    assert 'MyTest.java:12' in result


def test_match_signature_typed_mask_requires_variation():
    expr, kinds = _pattern_regex('java.lang.AssertionError: problem id: <UUID>')
    sig = Signature('TestA', [], '...', expr, kinds, [(UUID_A,), (UUID_B,)])
    assert sig.matches('java.lang.AssertionError: problem id: ' + UUID_C)
    assert not sig.matches('java.lang.IllegalStateException: problem id: ' + UUID_C)
    assert not sig.matches('java.lang.AssertionError: problem id: garbage')
    sig_single = Signature('TestA', [], '...', expr, kinds, [(UUID_A,), (UUID_A,)])
    assert not sig_single.matches('java.lang.AssertionError: problem id: ' + UUID_C)


def test_generic_wildcards_accept_only_confirmed_values():
    expr, kinds = _pattern_regex('status: <*>; scenario: KEEP')
    sig = Signature('TestA', [], '', expr, kinds, [('400',), ('500',)])
    assert sig.matches('status: 400; scenario: KEEP')
    assert sig.matches('status: 500; scenario: KEEP')
    assert not sig.matches('status: 404; scenario: KEEP')
    assert not sig.matches('status: 400; scenario: CHANGE')


def test_one_history_observation_cannot_auto_classify():
    first, _ = historical_pair()
    decision, candidate = ProgressiveMatcher([first]).classify('TestA', log(uuid=UUID_B))
    assert decision != 'known'


def test_unapproved_or_modified_proposal_is_not_training_evidence():
    first, second = historical_pair()
    changed = deepcopy(second)
    changed['result'] = 'QA changed diagnosis'
    assert not _signatures([first, changed])
    assert len(_signatures([first, second])) == 1


def test_confirmed_pair_allows_new_uuid_but_not_new_status():
    first, second = historical_pair()
    matcher = ProgressiveMatcher([second, first])  # newest first
    assert matcher.classify('TestA', log(uuid=UUID_C))[0] == 'known'
    assert matcher.classify('TestA', log(status='500', uuid=UUID_C))[0] != 'known'
    assert matcher.classify('TestA', log(uuid=UUID_C, exception='java.lang.IllegalStateException'))[0] != 'known'


def test_history_window_expiry_disables_automatic_recognition():
    _, second = historical_pair()
    assert not _signatures([second])
    assert ProgressiveMatcher([second]).classify('TestA', log(uuid=UUID_C))[0] != 'known'


def test_ticket_changes_do_not_change_signature_identity():
    first, second = historical_pair()
    second['link_prod_bug'] = 'QA-200'
    second['matching']['status'] = 'confirmed'  # QA explicitly confirms after edit
    decision, candidate = ProgressiveMatcher([second, first]).classify('TestA', log(uuid=UUID_C))
    assert decision == 'known'
    assert candidate['link_prod_bug'] == 'QA-200'


def test_auto_matched_history_does_not_create_new_training_edges():
    first, second = historical_pair()
    second['matching']['status'] = 'auto_matched'
    assert not _signatures([first, second])
