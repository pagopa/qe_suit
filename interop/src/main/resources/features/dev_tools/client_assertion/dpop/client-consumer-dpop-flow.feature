Feature: Debugger Client Assertion Sync Bearer And DPoP (Frontend)

  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion standard e la mia DPoP proof
  Al fine di identificare errori strutturali, temporali o crittografici nelle cinque fasi di validazione (Formale, Recupero Chiave, Firma, Stato Piattaforma, DPoP)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente (caricamento dei componenti, input, invio) sia funzionale e fluido.
  2. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  3. Validare il mapping tra le risposte API (BE) e i componenti grafici della pagina (Step di validazione).
  4. Verificare, per induzione, la corretta renderizzazione dei messaggi di errore:
  non si mira alla copertura esaustiva di ogni casistica di business (demandata ai test BE),
  ma alla conferma che il componente di UI reagisca correttamente ai diversi stati (PASSED, FAILED, SKIPPED).
  ***

  Scenario: [DPOP_CONSUMER_CLIENT_ASSERTION_VALIDATION_SUCCESS]
  Dato un client CONSUMER valido ed una DPoP proof valida, quando viene inviata una client assertion corretta
  allora tutte le fasi di validazione risultano PASSED.

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


  Scenario Outline: [DPOP_CONSUMER_CLIENT_ASSERTION_VALIDATION_MISSING_REQUIRED_CLAIMS]
  Dato un client CONSUMER valido ed una client assertion valida, quando la DPoP Proof presenta un claim obbligatorio mancante
  allora la validazione formale fallisce con il rispettivo messaggio di errore

    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    And una dpop proof generata con una chiave RSA e:
      | claim    | value           |
      | __remove | <claimToRemove> |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion e della dpop proof associate al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors            |
      | clientAssertionValidation            | PASSED | []                |
      | publicKeyRetrieve                    | PASSED | []                |
      | clientAssertionSignatureVerification | PASSED | []                |
      | platformStatesVerification           | PASSED | []                |
      | dpopProofValidation                  | FAILED | [<expectedError>] |

    Examples:
      | claimToRemove | expectedError                       |
      | jti           | JTI not found in DPoP proof         |
      | iat           | IAT not found in DPoP proof         |
      | htu           | HTU not found in DPoP proof payload |
      | htm           | HTM not found in DPoP proof payload |
