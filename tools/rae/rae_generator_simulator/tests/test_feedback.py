from dataclasses import dataclass
from feedback import explanations, summary
from simulator_core import make_manual_history, snapshot_json
import json

@dataclass
class Reason:
    code: str
    historical: str
    current: str
    detail: str = ''

def test_specific_method_feedback():
    d = explanations([Reason('STACK_METHOD_CHANGED', 'createPurpose', 'deletePurpose')])[0]
    assert 'Metodo' in d.title
    assert d.historical == 'createPurpose'
    assert d.current == 'deletePurpose'
    assert 'punto di fallimento' in d.qa_action

def test_ambiguous_ticket_message():
    d = explanations([Reason('AMBIGUOUS_QA_GROUPS', 'QA-100, QA-200', '')])[0]
    assert 'Più difetti' in d.title
    assert 'ticket' in d.explanation

def test_proposed_without_reason_is_transparent():
    text = summary('proposed', has_candidate=True, reasons=[])
    assert 'manca una firma QA' in text

def test_new_does_not_assert_new_defect():
    text = summary('new', has_candidate=False, reasons=[])
    assert 'non significa necessariamente' in text

def test_events_link_only_same_group():
    h = make_manual_history([
        {'group':'A', 'ticket':'QA-100', 'error':'ERROR 1'},
        {'group':'B', 'ticket':'QA-200', 'error':'ERROR 2'},
        {'group':'A', 'ticket':'QA-100', 'error':'ERROR 3'},
    ], 'NrtStandardSuiteTest', 'TestA')
    assert h[0]['matching']['source_rae'] == h[2]['_source_file']
    assert 'matching' not in h[1]

def test_ticket_mutation_rejected():
    try:
        make_manual_history([
            {'group':'A','ticket':'QA-100','error':'x'},
            {'group':'A','ticket':'QA-200','error':'y'},
        ], 'NrtStandardSuiteTest','TestA')
    except ValueError as ex:
        assert 'ticket' in str(ex)
    else:
        raise AssertionError('Expected ValueError')

def test_export_json():
    s = snapshot_json([], 'X', 'T', 'Log')
    assert json.loads(s)['current_error'] == 'Log'
