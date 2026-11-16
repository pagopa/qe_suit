from copy import deepcopy
from pathlib import Path
import json
import yaml
import pytest

from rae_generator.parsers import collect_reports, ReportError, parse_file
from rae_generator.matching import choose_known, fingerprint, normalized_error
from rae_generator.generator import create_rae, write_yaml
from rae_generator.history import load_history
from rae_generator.renderer import render
from rae_generator.validator import validate, ValidationError, read_yaml
from rae_generator.cli import main


def example_data():
    return {'rae': {'release':'v1', 'issue_jira':'https://jira.local/QE-1', 'data_ora':'2026-11-16T18:00:00Z',
                    'durata':'00:01:13', 'numero_ko':1, 'run':'https://github.com/a/b/actions/runs/1',
                    'suite':'NrtStandardSuiteTest', 'environment':'qa', 'executor':'qa-user',
                    'source_branch':'develop'},
            'ko_know':[], 'ko':[{'id_test':'Test A', 'error_log':'AssertionError: expected A | B',
                                'procedure':'Controllata risposta', 'result':'Bug noto',
                                'link_prod_bug':'', 'link_suite_bug':''}]}


def cucumber_json(tmp_path, name='Test A', error='java.lang.AssertionError: failed'):
    p=tmp_path/'cucumber-reports'/'results.json'
    p.parent.mkdir(exist_ok=True)
    p.write_text(json.dumps([{'name':'Feature', 'elements':[
        {'name':name, 'steps':[{'name':'test step', 'result': {'status':'failed', 'error_message':error}}]}
    ]}]))
    return p


def open_xml(tmp_path, name='Test A', error='java.lang.AssertionError: failed'):
    p=tmp_path/'junit-platform-reports'/'open-test-report.xml'
    p.parent.mkdir(exist_ok=True)
    p.write_text(f'''<e:events xmlns:e="https://schemas.opentest4j.org/reporting/events/0.2.0"
      xmlns:core="https://schemas.opentest4j.org/reporting/core/0.2.0"
      xmlns:java="https://schemas.opentest4j.org/reporting/java/0.2.0">
      <e:started id="1" name="{name}"/>
      <e:finished id="1"><core:result status="FAILED"><java:throwable>{error}</java:throwable></core:result></e:finished>
      </e:events>''')
    return p


def test_cucumber_failure(tmp_path):
    p=cucumber_json(tmp_path)
    rows=parse_file(p)
    assert len(rows)==1 and rows[0].id_test=='Test A'


def test_open_test_xml_failed(tmp_path):
    p=open_xml(tmp_path)
    assert len(parse_file(p))==1
    assert parse_file(p)[0].error_log=='java.lang.AssertionError: failed'


def test_surefire_xml(tmp_path):
    p=tmp_path/'TEST-Suite.xml'
    p.write_text('<testsuite><testcase name="Test B"><failure message="oops">AssertionError: oops</failure></testcase></testsuite>')
    rows=parse_file(p)
    assert len(rows)==1 and rows[0].id_test=='Test B'


def test_deduplicate_cross_format(tmp_path):
    cucumber_json(tmp_path)
    open_xml(tmp_path)
    rows=collect_reports(tmp_path)
    assert len(rows)==1
    assert rows[0].source=='cucumber'


def test_same_error_different_names_not_deduplicated(tmp_path):
    cucumber_json(tmp_path)
    open_xml(tmp_path, name='Test B')
    assert len(collect_reports(tmp_path))==2


def test_no_reports_is_error(tmp_path):
    with pytest.raises(ReportError, match='Nessun report'):
        collect_reports(tmp_path)


def test_bad_json_fails(tmp_path):
    p=cucumber_json(tmp_path)
    p.write_text('{broken')
    with pytest.raises(ReportError):
        collect_reports(tmp_path)


def test_xxe_rejected(tmp_path):
    p=tmp_path/'s.xml'
    p.write_text('<!DOCTYPE x [ <!ENTITY hack SYSTEM "file:///etc/passwd"> ]><testsuite/>')
    with pytest.raises(ReportError, match='DTD'):
        parse_file(p)


def test_zero_ko_is_valid(tmp_path):
    p=cucumber_json(tmp_path)
    p.write_text('[{"elements":[{"name":"OK","steps":[{"result":{"status":"passed"}}]}]}]')
    data=create_rae(tmp_path,tmp_path/'history',suite='NrtStandardSuiteTest',environment='qa',
                    executor='qa', source_branch='develop', run_url='https://example.org',
                    data_ora='2026-11-16T18:00:00Z',durata='00:01:10')
    assert data['rae']['numero_ko']==0 and data['ko']==[] and data['ko_know']==[]


def test_history_last_7_same_suite_and_general(tmp_path):
    hist=tmp_path/'rae';hist.mkdir()
    for i in range(1,11):
        data=example_data();data['rae']['suite']='SuiteA'
        data['ko'][0]['result']=f'A{i}'
        (hist/f'rae_SuiteA_20261116{i:06d}.yaml').write_text(yaml.safe_dump(data))
    for i in range(1,4):
        data=example_data();data['ko'][0]['result']=f'G{i}'
        (hist/f'rae_NrtStandardSuiteTest_20261116{i:06d}.yaml').write_text(yaml.safe_dump(data))
    records=load_history(hist,'SuiteA')
    assert len(records)==10
    assert 'A1' not in [r['result'] for r in records]
    assert 'A10' in [r['result'] for r in records]


def test_matching_requires_id_and_diagnostic_signature():
    previous={'id_test':'A', 'error_log':'java.lang.AssertionError: wrong UUID 00ab'+ '-1234-1234-1234-001122334455',
              'procedure':'checked', 'result':'known'}
    assert choose_known('B',previous['error_log'],[previous]) is None
    assert choose_known('A', 'java.lang.IllegalStateException: wrong UUID 00ab-1234-1234-1234-001122334455',[previous]) is None
    assert choose_known('A', previous['error_log'],[previous])==previous


def test_exact_fingerprint_normalizes_line_numbers():
    assert fingerprint('AssertionError: unexpected at File.java:18') == fingerprint('AssertionError: unexpected at File.java:99')


def test_create_rae_recognizes_historical(tmp_path):
    cucumber_json(tmp_path, error='AssertionError: mismatch')
    hist=tmp_path/'history';hist.mkdir()
    prior=example_data();prior['ko'][0]['error_log']='AssertionError: mismatch'
    prior['ko'][0]['procedure']='historical check';prior['ko'][0]['result']='known'
    (hist/'rae_NrtStandardSuiteTest_20261116000000.yaml').write_text(yaml.safe_dump(prior))
    data=create_rae(tmp_path,hist,suite='NrtStandardSuiteTest',environment='qa',executor='qa',
                    source_branch='develop',run_url='https://example.org',
                    data_ora='2026-11-16T18:00:00Z',durata='00:01:10')
    assert len(data['ko_know'])==1 and data['ko_know'][0]['procedure']=='historical check'
    assert not data['ko']


def test_validator_rejects_blanks_and_duplicates():
    data=example_data();data['ko'][0]['procedure']='  '
    with pytest.raises(ValidationError,match='procedure'):
        validate(data)
    data=example_data();data['ko'].append(deepcopy(data['ko'][0]));data['rae']['numero_ko']=2
    with pytest.raises(ValidationError,match='duplicato'):
        validate(data)


def test_renderer_determinism_and_markdown_escaping():
    data=validate(example_data())
    md=render(data)
    assert md==render(data)
    assert 'KO - Dovuti a bug noti' in md and 'KO - Altri fallimenti' in md
    assert 'expected A &#124; B' in md


def test_cli_finalize_idempotent(tmp_path):
    path=tmp_path/'rae_Suite_20261116180000.yaml'
    write_yaml(example_data(),path)
    assert main(['finalize', str(path), '--write'])==0
    content=path.with_suffix('.md').read_bytes()
    assert main(['finalize', str(path), '--write'])==0
    assert path.with_suffix('.md').read_bytes()==content
    assert main(['check', str(path)])==0
    data=yaml.safe_load(path.read_text());data['ko'][0]['result']='Changed'
    write_yaml(data,path)
    assert main(['check', str(path)])==1


def test_open_test_container_does_not_duplicate_child(tmp_path):
    p=tmp_path/'junit-platform-reports'/'open-test-report.xml';p.parent.mkdir()
    p.write_text('''<e:events xmlns:e="urn:events" xmlns:c="urn:core" xmlns:j="urn:java">
       <e:started id="1" name="Suite"/>
       <e:started id="2" parentId="1" name="Test A"/>
       <e:finished id="2"><c:result status="FAILED"><j:throwable>AssertionError: test</j:throwable></c:result></e:finished>
       <e:finished id="1"><c:result status="FAILED"><j:throwable>AssertionError: aggregate</j:throwable></c:result></e:finished>
       </e:events>''')
    assert [row.id_test for row in parse_file(p)]==['Test A']


def test_metadata_and_zero_ko_validated(tmp_path):
    data=example_data();data['ko']=[];data['rae']['numero_ko']=0
    assert validate(data) == data
    assert 'Nessun fallimento' in render(data)
    data['rae']['release']='   '
    with pytest.raises(ValidationError,match='release'):
        validate(data)
