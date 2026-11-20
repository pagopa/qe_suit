"""Local GUI to exercise the SAME ProgressiveMatcher used by qe_suit."""
from __future__ import annotations
import json
from pathlib import Path
import streamlit as st

from feedback import explanations, summary
from rae_generator.review_evidence import snapshot, visible_evidence
from simulator_core import make_manual_history, run_match, load_uploaded_raes, snapshot_json

st.set_page_config(page_title='RAE · QA Matcher Debugger', page_icon='🔎', layout='wide')
st.title('RAE · QA Matcher Debugger')
st.caption('Simulazione locale: classificazione reale di `rae_generator.progressive.ProgressiveMatcher`. Nessun commit, push o scrittura nello storico.')

EX1 = ('java.util.concurrent.TimeoutException: Timeout 20s\n'
       '    at com.demo.PurposePage.createPurpose(PurposePage.java:120)\n'
       '    at com.demo.PurposeTest.execute(PurposeTest.java:50)')
EX2 = EX1.replace('java:120', 'java:145').replace('java:50', 'java:55')
EX3 = EX1.replace('java:120', 'java:180').replace('java:50', 'java:65')
EX_NEW_METHOD = EX3.replace('createPurpose', 'deletePurpose')

st.sidebar.header('Ambiente')
mode = st.sidebar.radio('Sorgente storico', ['Scenario QA manuale', 'Carica RAE YAML'], help='Entrambe le modalità usano il matcher di produzione.')
with st.sidebar.expander('Verifiche consigliate', expanded=False):
    st.write('1. Esecuzione 1: primo KO e ticket assegnato dal QA.\n\n'
             '2. Esecuzione 2: conferma QA e apprendimento.\n\n'
             '3. Esecuzione 3: prova log compatibile oppure metodo diverso.\n\n'
             '4. Se è `proposed`, aggiungi un evento di conferma e prova una quarta esecuzione.')

col_a, col_b = st.columns([1, 1])
with col_a:
    suite = st.text_input('Suite', value='NrtStandardSuiteTest')
with col_b:
    test_id = st.text_input('id_test', value='TestCreazionePurpose')

if 'events' not in st.session_state:
    st.session_state.events = [
        {'group': 'defect-A', 'ticket': 'QA-100', 'error': EX1},
        {'group': 'defect-A', 'ticket': 'QA-100', 'error': EX2},
    ]
if 'current_error' not in st.session_state:
    st.session_state.current_error = EX3

if mode == 'Scenario QA manuale':
    st.subheader('1 · Storico QA simulato')
    st.caption('I dati qui inseriti sono trattati come conferme QA **simulate**; non vengono scritti su develop e non costituiscono approvazioni effettive.')
    events = st.session_state.events
    with st.expander(f'Eventi QA: {len(events)}', expanded=True):
        for i, event in enumerate(events):
            c1, c2 = st.columns([1, 1])
            with c1:
                event['group'] = st.text_input(f'Identità difetto · evento {i+1}', value=event['group'], key=f'g{i}')
            with c2:
                event['ticket'] = st.text_input(f'Ticket · evento {i+1}', value=event['ticket'], key=f't{i}')
            event['error'] = st.text_area(f'error_log storico · evento {i+1}', value=event['error'], key=f'e{i}', height=110)
        add_col, clear_col = st.columns(2)
        with add_col:
            if st.button('Aggiungi conferma QA simulata', use_container_width=True):
                st.session_state.events.append({'group': events[-1]['group'], 'ticket': events[-1]['ticket'],
                                                'error': st.session_state.current_error})
                st.rerun()
        with clear_col:
            if st.button('Ripristina esempio iniziale', use_container_width=True):
                st.session_state.events = [{'group': 'defect-A', 'ticket': 'QA-100', 'error': EX1},
                                           {'group': 'defect-A', 'ticket': 'QA-100', 'error': EX2}]
                st.session_state.current_error = EX3
                st.rerun()
    try:
        history = make_manual_history(events, suite, test_id)
        history_error = None
    except Exception as exc:
        history, history_error = [], str(exc)
else:
    st.subheader('1 · RAE storiche caricate')
    st.caption('Carica solo YAML che vuoi **considerare approvati nella simulazione**. Il simulatore non può verificare la provenienza da develop.')
    files = st.file_uploader('File rae_*.yaml', type=['yaml'], accept_multiple_files=True)
    try:
        history = load_uploaded_raes([(f.name, f.getvalue()) for f in files], suite) if files else []
        history_error = None
    except Exception as exc:
        history, history_error = [], str(exc)
    st.write(f'Analisi storiche complete selezionate: **{len(history)}**')

if history_error:
    st.error(f'Storico non valido: {history_error}')

st.subheader('2 · Nuovo KO da classificare')
with st.form('classifier'):
    new_error = st.text_area('error_log corrente', value=st.session_state.current_error, height=190)
    submitted = st.form_submit_button('Classifica con il ProgressiveMatcher di produzione', type='primary')

if submitted:
    st.session_state.current_error = new_error
    if history_error or not suite.strip() or not test_id.strip() or not new_error.strip():
        st.error('Compila suite, id_test e log, e correggi eventuali errori nello storico.')
    else:
        try:
            result = run_match(history, suite, test_id, new_error)
            st.session_state.last_result = result
        except Exception as exc:
            st.exception(exc)
            st.session_state.pop('last_result', None)

result = st.session_state.get('last_result')
if result:
    st.subheader('3 · Decisione e feedback QA')
    labels = {'known': 'KNOWN · analisi automatica', 'proposed': 'PROPOSED · verifica QA', 'new': 'NEW · analisi QA completa'}
    status = result['status']
    st.markdown(f'### {labels.get(status, status)}')
    st.write(summary(status, has_candidate=bool(result['source_rae']), reasons=explanations(result['reasons'])))
    c1, c2, c3 = st.columns(3)
    c1.metric('Ticket suggerito', result['ticket'] or 'Nessuno')
    c2.metric('Firme QA apprese', result['signature_count'])
    c3.metric('Record storici letti', result['history_count'])
    if result['source_rae']:
        st.caption(f"Fonte analisi: {result['source_rae']} · test {result['source_test_id']}")
    formatted_all = explanations(result['reasons'])
    # Keep the QA-visible view concise and consistent with the Markdown RAE.
    visible_rows = visible_evidence(snapshot(result['reasons']))
    allowed = {(r.get('code'), r.get('historical_value', ''), r.get('current_value', ''))
               for r in visible_rows}
    formatted = [item for item in formatted_all
                 if (item.code, item.historical, item.current) in allowed]
    if formatted:
        st.markdown('#### Corrispondenza storica da validare')
        with st.expander(f'{len(formatted)} evidenze da verificare', expanded=False):
            for index, item in enumerate(formatted, 1):
                st.markdown(f'**{index}. {item.title}**')
                if item.historical or item.current:
                    st.code(f'Storico QA: {item.historical or "—"}\n'
                            f'Log corrente: {item.current or "—"}', language=None)
                elif item.explanation:
                    st.write(item.explanation)
                if item.historical_context and item.current_context:
                    st.caption(f'Contesto: {item.historical_context} → {item.current_context}')
                if item.location:
                    st.caption(f'Posizione: {item.location}')
                if index < len(formatted):
                    st.divider()
    elif visible_rows == [{'code': 'QA_REVIEW_REQUIRED'}]:
        st.info('Le informazioni storiche disponibili non consentono di confermare automaticamente l’equivalenza del KO.')
    elif status == 'proposed':
        st.info('Il confronto storico ha individuato una possibile corrispondenza, ma non dispone di differenze puntuali verificabili.')
    with st.expander('Debug tecnico · motivazioni complete, anche quelle non mostrate nel report'):
        st.code(json.dumps([{'code': x.code, 'historical': x.historical, 'current': x.current} for x in formatted_all], ensure_ascii=False, indent=2), language='json')
        st.code(json.dumps({k:v for k,v in result.items() if k != 'reasons'}, ensure_ascii=False, indent=2), language='json')
        st.caption('I feedback sono una presentazione delle Reason prodotte dal matcher: non sono usati per prendere la decisione.')

st.subheader('4 · Esporta scenario riproducibile')
if mode == 'Scenario QA manuale':
    st.download_button('Scarica scenario JSON', snapshot_json(st.session_state.events, suite, test_id,
                          st.session_state.current_error), file_name='rae_debug_scenario.json', mime='application/json')
    with st.expander('Importa uno scenario JSON'):
        imported = st.file_uploader('Scenario esportato', type='json', key='json_import')
        if imported and st.button('Carica scenario'):
            try:
                payload = json.loads(imported.getvalue())
                st.session_state.events = payload['events']
                st.session_state.current_error = payload['current_error']
                st.rerun()
            except Exception as exc:
                st.error(f'Scenario non valido: {exc}')
