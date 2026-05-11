Feature: : Debugger Client Assertion Page
  Come Aderente iscritto alla piattaforma Interop
  Voglio accedere alla pagina di Debugger Client Assertion del portale
  Al fine di accedere ai tools (Export analisi del rischio, Debug Client Assertion, Simulatore ottenimento di un voucher)

  Scenario: [DEBUGGER_CLIENT_ASSERTION_PAGE_ACCESS_SUCCESS]
  Dato un utente abilitato alla piattaforma, quando si accede alla sezione Tool per lo sviluppo del portale Interop,
  allora è possibile accedere alle pagine Export analisi del rischio, Debug Client Assertion e Simulatore ottenimento di un voucher

    Then l'utente admin di PagoPA si trova alla pagina Tool per lo sviluppo del portale Interop e verifica che tutti gli elementi siano visibili

