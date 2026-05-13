@dev-tools-page-ui-behavior
Feature: Dev Tools Page

  Come Aderente iscritto alla piattaforma Interop
  Voglio accedere alla pagina Tool per lo sviluppo del portale Interop
  Al fine di accedere ai tools (Export analisi del rischio, Debug Client Assertion, Simulatore ottenimento di un voucher)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente (caricamento dei componenti, input, invio) sia funzionale e fluido.
  2. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  ***

  #Bug: https://pagopa.atlassian.net/browse/PIN-10061
  Scenario: [DEV_TOOLS_PAGE_ACCESS_SUCCESS]
  Dato un utente abilitato alla piattaforma, quando si accede alla sezione Tool per lo sviluppo del portale Interop,
  allora è possibile accedere alle pagine Export analisi del rischio, Debug Client Assertion e Simulatore ottenimento di un voucher

    Then l'utente admin di PagoPA si trova alla pagina Tool per lo sviluppo del portale Interop e verifica che tutti gli elementi siano visibili

