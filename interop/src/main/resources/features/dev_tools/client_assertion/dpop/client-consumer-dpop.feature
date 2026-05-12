Feature: Debugger Client Assertion Sync Bearer And DPoP
  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion standard e la mia DPoP proof
  Al fine di identificare errori strutturali, temporali o crittografici nelle cinque fasi di validazione (Formale, Recupero Chiave, Firma, Stato Piattaforma, DPoP)

  Scenario: [DPOP_CONSUMER_CLIENT_ASSERTION_VALIDATION_SUCCESS]
  Dato un client CONSUMER valido ed una DPoP proof valida, quando viene inviata una client assertion corretta
  allora tutte le fasi di validazione risultano PASSED

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    And una dpop proof valida generata con una chiave RSA
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion e della dpop proof associate al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |
      | platformStatesVerification           | PASSED | []     |
      | dpopProofValidation                  | PASSED | []     |


  # Bug: https://pagopa.atlassian.net/browse/PIN-10056
  Scenario: [DPOP_CONSUMER_CLIENT_ASSERTION_VALIDATION_INVALID_AUDIENCE]
  Dato un client CONSUMER valido ed una DPoP Proof valida, quando la client assertion ha audience invalida
  allora la validazione formale fallisce con invalidAudience e la DPoP Proof viene validata correttamente

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim | value            |
      | aud   | invalid_audience |
    And una dpop proof valida generata con una chiave RSA
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion e della dpop proof associate al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | FAILED  | [Unexpected client assertion audience: invalid_audience] |
      | publicKeyRetrieve                    | SKIPPED | []                                                       |
      | clientAssertionSignatureVerification | SKIPPED | []                                                       |
      | platformStatesVerification           | SKIPPED | []                                                       |
      | dpopProofValidation                  | PASSED  | []                                                       |


