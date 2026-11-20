"""Persistent regression tests: run with PYTHONPATH=tools/rae/rae_generator pytest ...

All synthetic data is created in tmp_path; no network, Jira, GitHub or live files.
The historical replay uses separately bundled real YAML and is NOT part of this
small, fast unit suite.
"""
from __future__ import annotations
import copy
from pathlib import Path
from types import SimpleNamespace
import pytest
import yaml

from rae_generator.generator import create_rae, write_yaml
from rae_generator.history import load_history
from rae_generator.namespace import namespace_dir, validate_namespace, validate_output_path
from rae_generator.progressive import ProgressiveMatcher
from rae_generator.qa_confirmation import analysis_digest, is_qa_confirmed
from rae_generator.renderer import render

SUITE = 'NrtStandardSuiteTest'
ID = 'PurposeTemplateContractTest::createPurposeTemplate::disabled'
ERROR = ('java.lang.IllegalStateException: Polling in timeout dopo 20 secondi. '
         'Purpose Template Creation Page is not loaded yet. Page title is blank\n'
         '    at sample.Page.assertLoaded(Page.java:34)')
TICKET = 'https://pagopa.atlassian.net/browse/QA-19652'
FILE_16 = f'rae_{SUITE}_20261116010101.yaml'
FILE_18 = f'rae_{SUITE}_20261118010101.yaml'


def history_row(*, ticket=TICKET, id_test=ID, error=ERROR):
    return {'id_test': id_test, 'error_log': error,
            'procedure': 'Controllare la pagina e i tempi del polling',
            'result': 'Timeout della pagina in condizioni di carico',
            'link_prod_bug': '', 'link_suite_bug': ticket}


def save_report(root: Path, namespace: str, name: str, rows: list[dict]):
    folder = namespace_dir(root, namespace)
    folder.mkdir(parents=True, exist_ok=True)
    doc = {'rae': {'suite': SUITE, 'environment': 'qa', 'numero_ko': len(rows)},
           'ko': rows, 'ko_know': []}
    write_yaml(doc, folder/name)
    return folder/name


def failures(monkeypatch, *, id_test=ID, error=ERROR):
    import rae_generator.generator as generator
    monkeypatch.setattr(generator, 'collect_reports',
                        lambda _root: [SimpleNamespace(id_test=id_test, error_log=error)])


def generate(monkeypatch, root: Path, namespace: str, *, id_test=ID, error=ERROR):
    failures(monkeypatch, id_test=id_test, error=error)
    return create_rae(root, namespace_dir(root, namespace), suite=SUITE,
                      environment='qa', executor='qa', source_branch='develop',
                      run_url='https://example.org/run',
                      data_ora='2026-11-18T20:00:00Z', durata='00:00:20')


def setup_proposal(monkeypatch, tmp_path, *, ticket=TICKET, namespace='interop'):
    root = tmp_path/'rae'
    save_report(root, namespace, FILE_16, [history_row(ticket=ticket)])
    proposal = generate(monkeypatch, root, namespace)
    assert len(proposal['ko']) == 1 and not proposal['ko_know']
    row = proposal['ko'][0]
    assert row['matching']['status'] == 'pending_confirmation'
    assert row['matching']['source_rae'] == FILE_16
    return root, proposal


def test_01_first_seen_history_proposes_not_known(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    row = proposal['ko'][0]
    assert row['link_suite_bug'] == TICKET
    assert row['procedure'] and row['result']
    assert row['matching']['proposed_digest'] == analysis_digest(row)
    assert len(load_history(namespace_dir(root, 'interop'), SUITE)) == 1
    assert not is_qa_confirmed(row, approved_history=False)


def test_02_no_confirmation_before_human_review(monkeypatch, tmp_path):
    _, proposal = setup_proposal(monkeypatch, tmp_path)
    before = render(proposal)
    assert 'Corrispondenza storica da validare' in before
    assert 'Validazione finale prima dell’automazione' not in before
    assert not is_qa_confirmed(proposal['ko'][0], original=proposal['ko'][0], qa_reviewed=False)


def test_03_qa_confirmation_renders_without_changing_yaml(monkeypatch, tmp_path):
    _, proposal = setup_proposal(monkeypatch, tmp_path)
    original = copy.deepcopy(proposal)
    after = render(proposal, original_data=original, qa_reviewed=True)
    assert 'Validazione finale prima dell’automazione' in after
    assert 'MATCH PROPOSTO' not in after
    assert proposal == original
    assert proposal['ko'][0]['matching']['status'] == 'pending_confirmation'


@pytest.mark.parametrize('field,value', [
    ('procedure', 'modifica QA'), ('result', 'nuova diagnosi'),
    ('link_suite_bug', ''), ('link_prod_bug', 'https://example.org/bug'),
    ('id_test', 'other-test'), ('error_log', 'other-error'),
])
def test_04_changed_analysis_or_identity_revokes_qa(monkeypatch, tmp_path, field, value):
    _, proposal = setup_proposal(monkeypatch, tmp_path)
    original = copy.deepcopy(proposal['ko'][0])
    modified = copy.deepcopy(original)
    modified[field] = value
    # PR review has the original generated YAML and protects test identity.
    assert not is_qa_confirmed(modified, original=original, qa_reviewed=True)
    # Approved history is checked using only the stored analysis digest.
    # id_test/error_log are not part of that digest (documented limitation).
    if field in ('id_test', 'error_log'):
        assert is_qa_confirmed(modified, approved_history=True)
    else:
        assert not is_qa_confirmed(modified, approved_history=True)
    modified[field] = original[field]
    assert is_qa_confirmed(modified, original=original, qa_reviewed=True)


def test_05_source_tamper_or_digest_tamper_never_confirms(monkeypatch, tmp_path):
    _, proposal = setup_proposal(monkeypatch, tmp_path)
    initial = proposal['ko'][0]
    for key in ('source_rae', 'source_id_test', 'proposed_digest'):
        candidate = copy.deepcopy(initial)
        candidate['matching'][key] += 'x'
        assert not is_qa_confirmed(candidate, original=initial, qa_reviewed=True)
    corrupt = copy.deepcopy(initial)
    corrupt['matching']['proposed_digest'] = '0'*64
    assert not is_qa_confirmed(corrupt, approved_history=True)


def test_06_approved_history_promotes_to_known_with_ticket(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    save_report(root, 'interop', FILE_18, proposal['ko'])
    next_rae = generate(monkeypatch, root, 'interop')
    assert len(next_rae['ko_know']) == 1 and not next_rae['ko']
    known = next_rae['ko_know'][0]
    assert known['matching']['status'] == 'auto_matched'
    assert known['link_suite_bug'] == TICKET
    assert known['matching']['source_rae'] == FILE_18
    assert 'MATCH AUTOMATICO' in render(next_rae)
    assert 'Validazione finale prima dell’automazione' not in render(next_rae)


def test_07_unapproved_or_changed_proposal_cannot_train(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    proposal['ko'][0]['procedure'] += ' - editata'
    save_report(root, 'interop', FILE_18, proposal['ko'])
    next_rae = generate(monkeypatch, root, 'interop')
    assert not next_rae['ko_know']


def test_08_approved_without_ticket_stays_to_analyze(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path, ticket='')
    save_report(root, 'interop', FILE_18, proposal['ko'])
    next_rae = generate(monkeypatch, root, 'interop')
    assert next_rae['ko_know'] == []
    assert len(next_rae['ko']) == 1
    assert next_rae['ko'][0].get('matching', {}).get('status') != 'auto_matched'


def test_09_different_id_does_not_inherit_historical_ticket(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    save_report(root, 'interop', FILE_18, proposal['ko'])
    next_rae = generate(monkeypatch, root, 'interop', id_test='OtherTest::sameError')
    assert not next_rae['ko_know']
    assert next_rae['ko'][0]['link_suite_bug'] == ''


def test_10_no_cross_namespace_even_same_suite_and_error(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    save_report(root, 'interop', FILE_18, proposal['ko'])
    send = generate(monkeypatch, root, 'send')
    assert not send['ko_know']
    assert send['ko'][0].get('matching') is None
    assert send['ko'][0]['link_suite_bug'] == ''
    assert load_history(namespace_dir(root, 'send'), SUITE) == []
    assert len(load_history(namespace_dir(root, 'interop'), SUITE)) == 2


def test_11_no_legacy_root_history_fallback(monkeypatch, tmp_path):
    root = tmp_path/'rae'
    root.mkdir()
    legacy = {'rae': {'suite': SUITE, 'environment': 'qa'},
              'ko': [history_row()], 'ko_know': []}
    write_yaml(legacy, root/FILE_16)
    result = generate(monkeypatch, root, 'interop')
    assert 'matching' not in result['ko'][0]
    assert not result['ko_know']


def test_12_automatched_cannot_become_training_evidence(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    auto = copy.deepcopy(proposal['ko'][0]); auto['matching']['status'] = 'auto_matched'
    auto['matching'].pop('proposed_digest', None)
    save_report(root, 'interop', FILE_18, [auto])
    next_rae = generate(monkeypatch, root, 'interop')
    assert not next_rae['ko_know']


def test_13_namespace_input_and_output_validation(tmp_path):
    root = tmp_path/'rae'
    for val in ('', '.', '..', '../send', 'interop/send', 'interop\\send', 'interop send', '/tmp'):
        with pytest.raises(ValueError): validate_namespace(val)
    for val in ('interop', 'send', 'qa_2026', 'x.y-z'):
        assert validate_namespace(val) == val
    good = root/'interop'/FILE_16
    assert validate_output_path(root, 'interop', good) == good
    for bad in (root/'send'/FILE_16, root/FILE_16, root/'interop'/'evil.yaml'):
        with pytest.raises(ValueError): validate_output_path(root, 'interop', bad)


def test_14_symlink_namespace_must_be_rejected(tmp_path):
    """Security regression: namespace must not alias another namespace via symlink."""
    root = tmp_path/'rae'
    (root/'send').mkdir(parents=True)
    (root/'interop').symlink_to(root/'send', target_is_directory=True)
    with pytest.raises(ValueError, match='(?i)namespace|symlink|symbolic|collegamento'):
        validate_output_path(root, 'interop', root/'interop'/FILE_16)


def test_15_history_namespace_symlink_must_be_rejected(tmp_path):
    """Fail closed for history reads as well as output paths."""
    root = tmp_path/'rae'
    save_report(root, 'send', FILE_16, [history_row()])
    (root/'interop').symlink_to(root/'send', target_is_directory=True)
    with pytest.raises(ValueError, match='(?i)namespace|symlink|symbolic|collegamento'):
        namespace_dir(root, 'interop')


def test_16_metadata_and_markdown_are_deterministic(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    md = render(proposal)
    assert md == render(copy.deepcopy(proposal))
    assert proposal['rae']['numero_ko'] == 1
    path = root/'interop'/FILE_18
    write_yaml(proposal, path)
    assert yaml.safe_load(path.read_text()) == proposal
    assert path.parent == namespace_dir(root, 'interop')


def test_17_same_test_different_exception_must_not_auto_match(monkeypatch, tmp_path):
    root, proposal = setup_proposal(monkeypatch, tmp_path)
    save_report(root, 'interop', FILE_18, proposal['ko'])
    different = ('java.lang.AssertionError: HTTP returned 500 instead of 200\n'
                 '    at sample.Other.verify(Other.java:100)')
    next_rae = generate(monkeypatch, root, 'interop', error=different)
    assert not next_rae['ko_know']


def test_18_workflow_routes_to_namespace_and_validates_pr(tmp_path):
    # Check the exact production workflows, not a simulated YAML snippet.
    repo = Path(__file__).resolve().parents[4]
    interop = (repo/'.github/workflows/interop-pipeline.yaml').read_text(encoding='utf-8')
    validation = (repo/'.github/workflows/rae-validation.yaml').read_text(encoding='utf-8')
    for required in ('RAE_NAMESPACE: interop', 'rae/${RAE_NAMESPACE}/rae_',
                     '--namespace "$RAE_NAMESPACE"'):
        assert required in interop, f'Interop pipeline missing {required}'
    assert 'rae/${NAMESPACE}/${NAME}.yaml' in validation
    assert 'git log --diff-filter=A' in validation
    assert '--qa-reviewed --original-yaml' in validation
    assert 'rae/rae_*' in validation, 'Legacy open PR compatibility changed'
