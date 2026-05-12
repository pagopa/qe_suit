Feature: Debugger Client Assertion Page

  Come Aderente iscritto alla piattaforma Interop
  Voglio accedere alla pagina di Debugger Client Assertion del portale
  Al fine di accedere ai tools (Export analisi del rischio, Debug Client Assertion, Simulatore ottenimento di un voucher)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente (caricamento dei componenti, input, invio) sia funzionale e fluido.
  2. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  ***

  Scenario: [DEBUG_CLIENT_ASSERTION_REQUIRED_INPUTS_VALIDATION]
  Dato un utente abilitato alla piattaforma, quando si accede alla sezione Tool per lo sviluppo del portale Interop e si accede alla pagina Debug Client Assertion,
  quando si tenta di validare una client assertion senza compilare il campo 'Client assertion'
  allora l'input viene evidenziato come errore e viene mostrato un messaggio di validazione 'Campo richiesto'

    Given l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    When l'utente invia la form della debug client assertion inserendo:
      | clientAssertion | $blank() |
    Then il text field Client assertion viene evidenziato come errore e viene mostrato il messaggio di validazione "Campo richiesto"