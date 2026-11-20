import json
import re
import pytest
import yaml
from copy import deepcopy
from pathlib import Path
from rae_generator.cli import main
from rae_generator.generator import create_rae, write_yaml
from rae_generator.history import load_history
from rae_generator.matching import choose_known, find_match, fingerprint, normalized_error
from rae_generator.parsers import collect_reports, ReportError, parse_file
from rae_generator.renderer import render
from rae_generator.validator import validate, ValidationError, read_yaml
from rae_generator.qa_confirmation import analysis_digest


def test_yaml_writer_uses_literal_blocks_and_preserves_values(tmp_path):
    data = example_data()
    row = data['ko'][0]
    row['error_log'] = "java.lang.AssertionError: l'atteso\n\tat Example.run(Example.java:12)\n"
    row['procedure'] = "Prima riga\nSeconda riga\n"
    row['result'] = "L'atteso non coincide\nConfermato dal QA\n"
    row['link_suite_bug'] = ''
    path = tmp_path / 'rae_NrtStandardSuiteTest_20261118213605.yaml'
    write_yaml(data, path)
    contents = path.read_text(encoding='utf-8')
    assert re.search(r'\berror_log: \|\n', contents)
    assert re.search(r'\bprocedure: \|\n', contents)
    assert re.search(r'\bresult: \|\n', contents)
    assert "link_suite_bug: ''" in contents
    assert yaml.safe_load(contents) == data


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
    create_empty_contract_json(tmp_path)
    return p


def create_empty_contract_json(tmp_path):
    path = tmp_path / 'contract-test' / 'report.json'
    path.parent.mkdir(exist_ok=True)
    path.write_text(json.dumps({
        'schemaVersion': 1,
        'summary': {'total': 0, 'passed': 0, 'failed': 0,
                    'skipped': 0, 'aborted': 0, 'preconditionFailed': 0},
        'groups': []
    }))
    return path


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


def test_junit_xml_never_influences_rae_when_contract_json_present(tmp_path):
    cucumber_json(tmp_path)
    open_xml(tmp_path, name='Unrelated JUnit failure')
    assert len(collect_reports(tmp_path)) == 1


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
    prior['ko'][0]['link_prod_bug'] = 'https://jira.local/PROD-1'
    (hist/'rae_NrtStandardSuiteTest_20261116000000.yaml').write_text(yaml.safe_dump(prior))
    data=create_rae(tmp_path,hist,suite='NrtStandardSuiteTest',environment='qa',executor='qa',
                    source_branch='develop',run_url='https://example.org',
                    data_ora='2026-11-16T18:00:00Z',durata='00:01:10')
    assert not data['ko_know']
    assert len(data['ko']) == 1
    assert data['ko'][0]['procedure'] == 'historical check'
    assert data['ko'][0]['matching']['status'] == 'pending_confirmation'


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
    assert '0 - KO DOVUTI A BUG DI PRODOTTO NOTI' in md
    assert '0 - KO DOVUTI A BUG DI SUITE NOTI' in md
    assert '1 - KO DA ANALIZZARE' in md
    assert 'AssertionError: expected A | B' in md
    assert md.index('**RESULT**') < md.index('**PROCEDURE**') < md.index('**ERROR LOG**')
    assert '| ID TEST | ERROR LOG |' not in md
    assert '| Campo | Valore |' in md
    assert '| --- | --- |' in md
    assert '| Suite | NrtStandardSuiteTest |' in md
    assert md.index('| Release |') < md.index('| Scope |') < md.index('| Issue Jira |')


def test_renderer_full_width_logs_and_fences():
    data = example_data()
    data['ko'][0].update({
        'error_log': 'AssertionError: expected A | B\n    at Example.java:1\n````\nlast line',
        'result': 'Prima riga\nseconda riga',
        'procedure': 'Riga uno\nRiga due',
        'link_prod_bug': 'https://example.org/PROD-1',
        'link_suite_bug': 'https://example.org/SUITE-1',
    })
    md = render(data)
    assert '**RESULT**\n\nPrima riga seconda riga' in md
    assert '**PROCEDURE**\n\nRiga uno Riga due' in md
    assert '`````text\n' in md
    assert 'AssertionError: expected A | B\n    at Example.java:1\n````\nlast line' in md
    assert md.count('`````') == 2
    assert '**LINK PROD BUG:** https://example.org/PROD-1' in md
    assert '**LINK SUITE BUG:** https://example.org/SUITE-1' in md


def test_renderer_both_ko_sections_and_empty_message():
    data = example_data()
    data['ko_know'] = data.pop('ko')
    data['ko_know'][0]['link_prod_bug'] = 'https://jira.local/PROD-1'
    data['ko'] = []
    md = render(data)
    assert '### 1. Test A' in md
    assert '## 0 - KO DA ANALIZZARE\n\n<details>\n<summary>Mostra/nascondi 0 KO</summary>\n\nNessun fallimento.' in md


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
    assert validate(data) == data


def test_cucumber_outline_same_name_different_example_rows(tmp_path):
    folder = tmp_path / 'cucumber-reports'
    folder.mkdir()
    path = folder / 'report.json'
    scenario_name = '[PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_1] Solo e-service attivi'
    path.write_text(json.dumps([{'elements': [
        {'name': scenario_name, 'line': 101, 'type': 'scenario',
         'steps': [{'result': {'status': 'failed', 'error_message': 'same failure'}}]},
        {'name': scenario_name, 'line': 102, 'type': 'scenario',
         'steps': [{'result': {'status': 'failed', 'error_message': 'same failure'}}]},
    ]}]))
    rows = parse_file(path)
    assert len(rows) == 2  # Even identical failure messages are different examples.
    assert {row.id_test for row in rows} == {
        f'{scenario_name} [example line: 101]',
        f'{scenario_name} [example line: 102]',
    }


def test_cucumber_outline_single_failed_example_has_source_line(tmp_path):
    folder = tmp_path / 'cucumber-reports'
    folder.mkdir()
    path = folder / 'report.json'
    path.write_text(json.dumps([{'elements': [
        {'name': 'outline', 'line': 42, 'type': 'scenario_outline',
         'steps': [{'result': {'status': 'failed', 'error_message': 'boom'}}]},
    ]}]))
    assert parse_file(path)[0].id_test == 'outline [example line: 42]'


def test_cucumber_without_example_metadata_keeps_existing_id(tmp_path):
    path = cucumber_json(tmp_path, name='Plain scenario')
    assert parse_file(path)[0].id_test == 'Plain scenario'


def test_cucumber_examples_id_named_group_and_stable_number(tmp_path):
    folder = tmp_path / 'cucumber-reports'
    folder.mkdir()
    path = folder / 'report.json'
    name = '[PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_1] Solo e-service attivi'
    group = 'versione-sospesa-superata-da-una-versione-successiva'

    def scenario(group, idx, line, status, message='same failure'):
        return {'name': name, 'keyword': 'Scenario Outline', 'type': 'scenario',
                'id': f'feature;scenario;{group};{idx}', 'line': line,
                'steps': [{'result': {'status': status, 'error_message': message}}]}

    path.write_text(json.dumps([{'elements': [
        scenario('versione-sospesa-non-superata', 2, 34, 'passed'),
        scenario(group, 2, 39, 'failed'),
        scenario(group, 3, 40, 'failed'),
        scenario(group, 4, 42, 'passed'),
        scenario('versione-pubblicata', 2, 46, 'failed'),
    ]}]))
    rows = parse_file(path)
    assert len(rows) == 3  # The two identical diagnostics are separate KO.
    assert {row.id_test for row in rows} == {
        f'{name} [Examples: {group} | Example #1]',
        f'{name} [Examples: {group} | Example #2]',
        f'{name} [Examples: versione-pubblicata | Example #1]',
    }


def test_cucumber_examples_unnamed_group_preserves_line_for_uniqueness(tmp_path):
    folder = tmp_path / 'cucumber-reports';
    folder.mkdir()
    path = folder / 'report.json'
    path.write_text(json.dumps([{'elements': [
        {'name': 'outline', 'keyword': 'Scenario Outline', 'id': 'f;s;;2',
         'line': 282, 'steps': [{'result': {'status': 'failed', 'error_message': 'same failure'}}]},
        {'name': 'outline', 'keyword': 'Scenario Outline', 'id': 'f;s;;2',
         'line': 310, 'steps': [{'result': {'status': 'failed', 'error_message': 'same failure'}}]},
    ]}]))
    rows = parse_file(path)
    assert len(rows) == 2
    assert rows[0].id_test != rows[1].id_test
    assert all('Example #1' in row.id_test for row in rows)


def test_cucumber_examples_invalid_id_uses_source_line(tmp_path):
    folder = tmp_path / 'cucumber-reports';
    folder.mkdir()
    path = folder / 'report.json'
    path.write_text(json.dumps([{'elements': [
        {'name': 'outline', 'keyword': 'Scenario Outline', 'id': 'nonstandard',
         'line': 42, 'steps': [{'result': {'status': 'failed', 'error_message': 'boom'}}]},
    ]}]))
    assert parse_file(path)[0].id_test == 'outline [example line: 42]'


def test_cucumber_plain_scenario_does_not_rewrite_trailing_number(tmp_path):
    folder = tmp_path / 'cucumber-reports';
    folder.mkdir()
    path = folder / 'report.json'
    path.write_text(json.dumps([{'elements': [
        {'name': 'normal test', 'keyword': 'Scenario', 'id': 'feature;test;group;2',
         'line': 20, 'steps': [{'result': {'status': 'failed', 'error_message': 'boom'}}]},
    ]}]))
    assert parse_file(path)[0].id_test == 'normal test'


def test_open_test_contract_dynamic_id_three_components(tmp_path):
    p = tmp_path / 'junit-platform-reports' / 'open-test-report.xml'
    p.parent.mkdir()
    p.write_text('''<e:events xmlns:e="urn:events" xmlns:core="urn:core"
      xmlns:java="urn:java" xmlns:junit="urn:junit">
      <e:started id="1" name="JUnit Jupiter"/>
      <e:started id="2" parentId="1" name="WebTOSContractTest"/>
      <e:started id="3" parentId="2" name="shouldDisplayTermsOfService()">
        <core:sources><java:methodSource
          className="it.pagopa.interop.suite.contract.WebTOSContractTest"
          methodName="shouldDisplayTermsOfService"/></core:sources>
      </e:started>
      <e:started id="4" parentId="3" name="Visuale TOS: copy esatto del titolo pagina">
        <core:metadata><junit:uniqueId>[engine:junit-jupiter]/[class:it.pagopa.interop.suite.contract.WebTOSContractTest]/[test-factory:shouldDisplayTermsOfService()]/[dynamic-test:#1]</junit:uniqueId></core:metadata>
      </e:started>
      <e:started id="5" parentId="3" name="Visuale TOS: 11 sezioni ordinate">
        <core:metadata><junit:uniqueId>[engine:junit-jupiter]/[class:it.pagopa.interop.suite.contract.WebTOSContractTest]/[test-factory:shouldDisplayTermsOfService()]/[dynamic-test:#2]</junit:uniqueId></core:metadata>
      </e:started>
      <e:finished id="4"><core:result status="FAILED"><java:throwable>AssertionError: title</java:throwable></core:result></e:finished>
      <e:finished id="5"><core:result status="FAILED"><java:throwable>AssertionError: sections</java:throwable></core:result></e:finished>
      <e:finished id="3"><core:result status="FAILED"><java:throwable>AssertionError: container</java:throwable></core:result></e:finished>
    </e:events>''')
    rows = parse_file(p)
    assert {r.id_test for r in rows} == {
        'WebTOSContractTest::shouldDisplayTermsOfService::Visuale TOS: copy esatto del titolo pagina',
        'WebTOSContractTest::shouldDisplayTermsOfService::Visuale TOS: 11 sezioni ordinate',
    }
    assert len(rows) == 2


def test_open_test_non_contract_keeps_display_name(tmp_path):
    p = open_xml(tmp_path, name='Test A')
    assert parse_file(p)[0].id_test == 'Test A'


def test_open_test_dynamic_without_method_source_falls_back(tmp_path):
    p = tmp_path / 'junit-platform-reports' / 'open-test-report.xml'
    p.parent.mkdir()
    p.write_text('''<e:events xmlns:e="urn:e" xmlns:core="urn:core"
      xmlns:junit="urn:junit" xmlns:java="urn:java">
      <e:started id="1" name="factory()"/>
      <e:started id="2" parentId="1" name="dynamic example">
        <core:metadata><junit:uniqueId>[engine:junit-jupiter]/[dynamic-test:#1]</junit:uniqueId></core:metadata>
      </e:started>
      <e:finished id="2"><core:result status="FAILED"><java:throwable>boom</java:throwable></core:result></e:finished>
    </e:events>''')
    assert parse_file(p)[0].id_test == 'dynamic example'


def test_contract_json_matches_java_summary(tmp_path):
    cucumber_json(tmp_path)
    contract = tmp_path / 'contract-test' / 'report.json'
    payload = json.loads(contract.read_text())
    payload['summary'].update(total=2, passed=1, failed=1)
    payload['groups'] = [{
        'channel': 'BFF', 'channelKey': 'bff', 'targetType': 'OPENAPI',
        'target': 'POST /agreements', 'className': 'x.BffAgreementContractTest',
        'methodName': 'createAgreement', 'scenarioCount': 2,
        'scenarios': [
            {'id_test': 'BffAgreementContractTest::createAgreement::case 1',
             'status': 'FAILED', 'stackTrace': 'AssertionError: failed', 'preconditionFailure': False},
            {'id_test': 'BffAgreementContractTest::createAgreement::case 2',
             'status': 'PASSED', 'preconditionFailure': False},
        ]}]
    contract.write_text(json.dumps(payload))
    results = collect_reports(tmp_path)
    assert len(results) == 2  # One Cucumber + one Contract
    assert results[1].id_test == 'BffAgreementContractTest::createAgreement::case 1'
    assert results[1].source == 'contract-json'


def test_contract_json_bad_total_fails_closed(tmp_path):
    cucumber_json(tmp_path)
    contract = tmp_path / 'contract-test' / 'report.json'
    payload = json.loads(contract.read_text())
    payload['summary']['failed'] = 184
    contract.write_text(json.dumps(payload))
    with pytest.raises(ReportError, match='failed'):
        collect_reports(tmp_path)


def test_contract_json_missing_accepts_cucumber(tmp_path):
    cucumber_json(tmp_path)
    (tmp_path / 'contract-test' / 'report.json').unlink()
    failures = collect_reports(tmp_path)
    assert len(failures) == 1
    assert failures[0].source == 'cucumber'


def test_contract_json_duplicate_id_fails_closed(tmp_path):
    cucumber_json(tmp_path)
    contract = tmp_path / 'contract-test' / 'report.json'
    payload = json.loads(contract.read_text())
    payload['summary'].update(total=2, passed=0, failed=2)
    payload['groups'] = [{'scenarioCount': 2, 'scenarios': [
        {'id_test': 'duplicate', 'status': 'FAILED', 'preconditionFailure': False},
        {'id_test': 'duplicate', 'status': 'FAILED', 'preconditionFailure': False},
    ]}]
    contract.write_text(json.dumps(payload))
    with pytest.raises(ReportError, match='duplicato'):
        collect_reports(tmp_path)


def test_renderer_three_sections_product_precedence_and_counts():
    data = example_data()
    base = data['ko'].pop()
    product = {**base, 'id_test': 'Product only', 'link_prod_bug': 'https://jira.local/P-1'}
    both = {**base, 'id_test': 'Both', 'link_prod_bug': 'https://jira.local/P-2',
            'link_suite_bug': 'https://jira.local/S-2'}
    suite = {**base, 'id_test': 'Suite only', 'link_suite_bug': 'https://jira.local/S-1'}
    data['ko_know'] = [product, both, suite]
    data['ko'] = [{**base, 'id_test': 'Fuzzy suggestion', 'procedure': 'suggested procedure'}]
    data['rae']['numero_ko'] = 4
    md = render(data)
    assert '## 2 - KO DOVUTI A BUG DI PRODOTTO NOTI' in md
    assert '## 1 - KO DOVUTI A BUG DI SUITE NOTI' in md
    assert '## 1 - KO DA ANALIZZARE' in md
    product_block = md.split('## 2 - KO DOVUTI A BUG DI PRODOTTO NOTI')[1].split('## 1 - KO DOVUTI A BUG DI SUITE NOTI')[0]
    suite_block = md.split('## 1 - KO DOVUTI A BUG DI SUITE NOTI')[1].split('## 1 - KO DA ANALIZZARE')[0]
    assert 'Both' in product_block and 'Both' not in suite_block
    assert 'suggested procedure' in md


def test_fingerprint_without_bug_remains_to_analyze(tmp_path):
    cucumber_json(tmp_path, error='AssertionError: mismatch')
    hist = tmp_path / 'history'; hist.mkdir()
    prior = example_data()
    prior['ko'][0]['error_log'] = 'AssertionError: mismatch'
    prior['ko'][0]['procedure'] = 'historical check'
    (hist / 'rae_NrtStandardSuiteTest_20261116000000.yaml').write_text(yaml.safe_dump(prior))
    data = create_rae(tmp_path, hist, suite='NrtStandardSuiteTest', environment='qa',
                      executor='qa', source_branch='develop', run_url='https://example.org',
                      data_ora='2026-11-16T18:00:00Z', durata='00:01:10')
    assert not data['ko_know']
    assert data['ko'][0]['procedure'] == 'historical check'


def test_similarity_with_bug_is_proposed_pending_qa(tmp_path):
    actual = 'AssertionError: the response status was 400 instead of 200 at endpoint /api/clients'
    previous_error = 'AssertionError: the response status was 401 instead of 200 at endpoint /api/clients'
    assert fingerprint(actual) != fingerprint(previous_error)
    candidate = {'id_test': 'Test A', 'error_log': previous_error, 'procedure': 'investigate API',
                 'result': 'known mismatch', 'link_prod_bug': 'https://jira.local/PROD-1',
                 'link_suite_bug': 'https://jira.local/SUITE-1'}
    match = find_match('Test A', actual, [candidate])
    assert match is not None and match.method == 'similarity' and match.score > 0.90
    cucumber_json(tmp_path, error=actual)
    hist = tmp_path / 'history'; hist.mkdir()
    prior = example_data(); prior['ko'] = [candidate]
    (hist / 'rae_NrtStandardSuiteTest_20261116000000.yaml').write_text(yaml.safe_dump(prior))
    data = create_rae(tmp_path, hist, suite='NrtStandardSuiteTest', environment='qa',
                      executor='qa', source_branch='develop', run_url='https://example.org',
                      data_ora='2026-11-16T18:00:00Z', durata='00:01:10')
    assert data['ko_know'] == []
    assert len(data['ko']) == 1
    matching = data['ko'][0].get('matching')

    assert matching is not None
    assert matching['status'] == 'pending_confirmation'
    assert matching['source_rae'] == (
        'rae_NrtStandardSuiteTest_20261116000000.yaml'
    )
    assert matching['source_id_test'] == 'Test A'
    assert matching['proposed_digest'] == analysis_digest(data['ko'][0])
    for field in ('procedure', 'result', 'link_prod_bug', 'link_suite_bug'):
        assert data['ko'][0][field] == candidate[field]


def test_fingerprint_suite_bug_is_known(tmp_path):
    cucumber_json(tmp_path, error='AssertionError: mismatch')
    hist = tmp_path / 'history'; hist.mkdir()
    prior = example_data(); prior['ko'][0].update(
        error_log='AssertionError: mismatch', link_suite_bug='https://jira.local/SUITE-1')
    (hist / 'rae_NrtStandardSuiteTest_20261116000000.yaml').write_text(yaml.safe_dump(prior))
    data = create_rae(tmp_path, hist, suite='NrtStandardSuiteTest', environment='qa',
                      executor='qa', source_branch='develop', run_url='https://example.org',
                      data_ora='2026-11-16T18:00:00Z', durata='00:01:10')
    assert len(data['ko']) == 1
    assert data['ko'][0]['matching']['status'] == 'pending_confirmation'
    assert '## 1 - KO DA ANALIZZARE' in render(data)


def test_renderer_dynamic_title_guide_and_collapsible_groups():
    data = example_data()
    data['rae']['data_ora'] = '2026-11-16T22:13:00Z'
    data['rae']['environment'] = 'qa'
    md = render(data)
    assert md.startswith('# 20261116 Report Analisi Esiti (RAE) NrtStandardSuiteTest QA\n')
    assert md.index('| Source branch |') < md.index('### Guida alla compilazione dei campi')
    assert md.index('### Guida alla compilazione dei campi') < md.index('## 0 - KO DOVUTI A BUG DI PRODOTTO NOTI')
    for label in ['**Result**', '**Procedure**', '**BUG SUITE**', '**BUG PRODOTTO**']:
        assert label in md
    assert md.count('<details>') == 3
    assert md.count('</details>') == 3
    assert md.count('<summary>Mostra/nascondi') == 3
    assert '<summary>Mostra/nascondi 1 KO</summary>' in md
    assert md.index('### 1. Test A') < md.index('</details>', md.index('## 1 - KO DA ANALIZZARE'))
    assert 'Nessun fallimento.' in md


def test_similarity_without_bug_stays_to_analyze(tmp_path):
    actual = 'AssertionError: the response status was 400 instead of 200 at endpoint /api/clients'
    previous_error = 'AssertionError: the response status was 401 instead of 200 at endpoint /api/clients'
    candidate = {'id_test': 'Test A', 'error_log': previous_error, 'procedure': 'investigate API',
                 'result': 'known mismatch', 'link_prod_bug': '', 'link_suite_bug': ''}
    cucumber_json(tmp_path, error=actual)
    hist = tmp_path / 'history'; hist.mkdir()
    prior = example_data(); prior['ko'] = [candidate]
    (hist / 'rae_NrtStandardSuiteTest_20261116000000.yaml').write_text(yaml.safe_dump(prior))
    data = create_rae(tmp_path, hist, suite='NrtStandardSuiteTest', environment='qa',
                      executor='qa', source_branch='develop', run_url='https://example.org',
                      data_ora='2026-11-16T18:00:00Z', durata='00:01:10')
    assert data['ko_know'] == []
    assert len(data['ko']) == 1
    assert data['ko'][0]['procedure'] == ''  # status 401 to 400 is structurally rejected


def test_renderer_unlinked_known_entry_moves_to_analysis():
    data = example_data()
    entry = data['ko'].pop()
    data['ko_know'] = [entry]
    md = render(data)
    assert '## 0 - KO DOVUTI A BUG DI PRODOTTO NOTI' in md
    assert '## 0 - KO DOVUTI A BUG DI SUITE NOTI' in md
    assert '## 1 - KO DA ANALIZZARE' in md
