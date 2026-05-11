Feature: : Debugger Client Assertion Sync Bearer
  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion standard
  Al fine di identificare errori strutturali, temporali o crittografici nelle quattro fasi di validazione (Formale, Recupero Chiave, Firma, Stato Piattaforma)

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


  # Bug: https://pagopa.atlassian.net/browse/PIN-10056
  Scenario: [CONSUMER_CLIENT_ASSERTION_VALIDATION_INVALID_AUDIENCE]
  Dato un client CONSUMER valido, quando la client assertion ha audience invalida
  allora la validazione formale fallisce con invalidAudience

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim | value            |
      | aud   | invalid_audience |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | FAILED  | [Unexpected client assertion audience: invalid_audience] |
      | publicKeyRetrieve                    | SKIPPED | []                                                       |
      | clientAssertionSignatureVerification | SKIPPED | []                                                       |
      | platformStatesVerification           | SKIPPED | []                                                       |

  # Bug: https://pagopa.atlassian.net/browse/PIN-10056
  Scenario: [CONSUMER_CLIENT_ASSERTION_VALIDATION_MISSING_REQUIRED_CLAIMS]
  Dato un client CONSUMER valido, quando la client assertion non contiene claim obbligatori
  allora la validazione formale fallisce con i rispettivi errori

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim    | value |
      | __remove | jti   |
      | __remove | iat   |
      | __remove | aud   |
      | __remove | exp   |
      | __remove | iss   |
      | __remove | sub   |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion creata sono:
      | step                                 | result  | errors                                                                                                                                                                                                                         |
      | clientAssertionValidation            | FAILED  | [JTI not found in client assertion, IAT not found in client assertion, EXP not found in client assertion, Issuer not found in client assertion, Subject not found in client assertion, Audience not found in client assertion] |
      | publicKeyRetrieve                    | SKIPPED | []                                                                                                                                                                                                                             |
      | clientAssertionSignatureVerification | SKIPPED | []                                                                                                                                                                                                                             |
      | platformStatesVerification           | SKIPPED | []                                                                                                                                                                                                                             |


  Scenario: [CONSUMER_CLIENT_ASSERTION_PUBLIC_KEY_RETRIEVE_INVALID_KID_FORMAT]
  Dato un client CONSUMER valido, quando il claim kid non è in formato valido
  allora il recupero della chiave pubblica fallisce con invalidKidFormat

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim      | value                  |
      | header.kid | not-a-valid-kid-format |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                                                |
      | clientAssertionValidation            | PASSED  | []                                                                                    |
      | publicKeyRetrieve                    | FAILED  | [Public key with kid not-a-valid-kid-format not found for client $retrieve(clientId)] |
      | clientAssertionSignatureVerification | SKIPPED | []                                                                                    |
      | platformStatesVerification           | SKIPPED | []                                                                                    |


  Scenario: [CONSUMER_CLIENT_ASSERTION_VALIDATION_EXPIRED_TOKEN]
  Dato un client CONSUMER valido, quando la client assertion è scaduta
  allora la verifica della firma fallisce con tokenExpiredError

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim | value     |
      | exp   | now-10800 |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | PASSED  | []                                                       |
      | publicKeyRetrieve                    | PASSED  | []                                                       |
      | clientAssertionSignatureVerification | FAILED  | [Token expired in client assertion signature validation] |
      | platformStatesVerification           | SKIPPED | []                                                       |


  Scenario: [CONSUMER_CLIENT_ASSERTION_PLATFORM_STATES_INVALID_PURPOSE_STATE]
  Dato un client CONSUMER valido, quando la finalità associata è in stato non valido
  allora la verifica degli stati fallisce con invalidPurposeState

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità in stato SUSPENDED provenienti da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors                       |
      | clientAssertionValidation            | PASSED | []                           |
      | publicKeyRetrieve                    | PASSED | []                           |
      | clientAssertionSignatureVerification | PASSED | []                           |
      | platformStatesVerification           | FAILED | [Purpose state is: INACTIVE] |