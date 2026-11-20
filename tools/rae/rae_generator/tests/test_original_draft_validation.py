"""The bot-produced original is a draft; the QA-edited RAE must be complete."""
from copy import deepcopy
from pathlib import Path
import yaml
import pytest
from rae_generator.cli import main
from rae_generator.validator import validate, ValidationError
from rae_generator.qa_confirmation import analysis_digest


def original():
    row={
        'id_test':'Test A', 'error_log':'AssertionError: historical timeout',
        'procedure':'', 'result':'', 'link_prod_bug':'', 'link_suite_bug':'',
    }
    return {
        'rae': {'release':'', 'scope':'NIGHTLY', 'issue_jira':'',
                'data_ora':'2026-11-20T00:00:00Z', 'durata':'00:00:10',
                'numero_ko':1, 'run':'https://example.org/run',
                'suite':'NrtStandardSuiteTest', 'environment':'qa',
                'executor':'qa', 'source_branch':'develop'},
        'ko':[row], 'ko_know':[],
    }


def test_original_draft_relaxes_only_human_fields():
    doc=original()
    assert validate(doc, draft=True) is doc
    with pytest.raises(ValidationError, match='issue_jira|procedure|result'):
        validate(doc)
    edited=deepcopy(doc)
    edited['rae']['issue_jira']='https://pagopa.atlassian.net/browse/QA-19638'
    edited['ko'][0]['result']='Diagnosi QA'
    edited['ko'][0]['procedure']='Riprodotto e verificato'
    assert validate(edited) is edited


@pytest.mark.parametrize('modify, message', [
    (lambda d: d['rae'].update(numero_ko=2), 'numero_ko'),
    (lambda d: d['rae'].update(environment='prod'), 'environment'),
    (lambda d: d['ko'][0].update(id_test=''), 'id_test'),
    (lambda d: d['ko'][0].update(error_log=''), 'error_log'),
    (lambda d: d['ko'][0].update(result=None), 'result'),
    (lambda d: d['rae'].update(issue_jira='http://evil.test/browse/QA-1'), 'issue_jira'),
    (lambda d: d['ko'][0].update(link_suite_bug='not-a-ticket'), 'link_suite_bug'),
    (lambda d: d['ko'][0].update(unknown='x'), 'campi obbligatori'),
])
def test_draft_still_rejects_bad_input(modify, message):
    doc=original(); modify(doc)
    with pytest.raises(ValidationError,match=message):
        validate(doc,draft=True)


def test_original_pending_with_evidence_and_digest_is_valid_draft():
    doc=original()
    row=doc['ko'][0]
    row.update(result='Diagnosi proposta', procedure='Verificare il timeout')
    row['matching']={
        'status':'pending_confirmation',
        'source_rae':'rae_NrtStandardSuiteTest_20261117000000.yaml',
        'source_id_test':'Test A',
        'proposed_digest':analysis_digest(row),
        'review_evidence':[{'code':'INVARIANT_CHANGED', 'historical_value':'40','current_value':'44'}],
    }
    assert validate(doc,draft=True) is doc
    doc['ko'][0]['matching']['review_evidence'][0]['code']='invalid'
    with pytest.raises(ValidationError,match='code'):
        validate(doc,draft=True)


def test_cli_finalizes_reviewed_rae_using_incomplete_original(tmp_path):
    draft=original()
    current=deepcopy(draft)
    current['rae']['issue_jira']='https://pagopa.atlassian.net/browse/QA-19638'
    current['ko'][0]['result']='Difetto esaminato'
    current['ko'][0]['procedure']='Verifica effettuata'
    orig=tmp_path/'original.yaml'
    target=tmp_path/'rae_NrtStandardSuiteTest_20261120000000.yaml'
    orig.write_text(yaml.safe_dump(draft),encoding='utf-8')
    target.write_text(yaml.safe_dump(current),encoding='utf-8')
    assert main(['finalize',str(target),'--write','--original-yaml',str(orig),'--qa-reviewed'])==0
    assert target.with_suffix('.md').is_file()
    assert main(['check',str(target),'--original-yaml',str(orig),'--qa-reviewed'])==0


def test_cli_still_rejects_unfinished_reviewed_rae(tmp_path):
    draft=original()
    orig=tmp_path/'original.yaml'
    target=tmp_path/'rae_NrtStandardSuiteTest_20261120000000.yaml'
    orig.write_text(yaml.safe_dump(draft),encoding='utf-8')
    target.write_text(yaml.safe_dump(draft),encoding='utf-8')
    assert main(['finalize',str(target),'--write','--original-yaml',str(orig),'--qa-reviewed'])==1
    assert not target.with_suffix('.md').exists()
