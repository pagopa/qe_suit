Feature: : Debugger Client Assertion Sync Bearer
  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion standard
  Al fine di identificare errori strutturali, temporali o crittografici nelle tre fasi di validazione (Formale, Recupero Chiave, Firma)

  Scenario: [CONSUMER_CLIENT_ASSERTION_VALIDATION_SUCCESS]
  Dato un client CONSUMER valido, quando viene inviata una client assertion corretta allora tutte le fasi di validazione risultano PASSED
    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |
      | platformStatesVerification           | PASSED | []     |