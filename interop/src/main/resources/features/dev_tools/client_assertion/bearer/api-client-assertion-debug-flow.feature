@debug-client-assertion-page-ui-flow
Feature: Debugger Client Assertion Sync Bearer (Frontend)

  Come Aderente in possesso di un client di tipo API
  Voglio validare la mia Client Assertion standard
  Al fine di identificare errori strutturali, temporali o crittografici nelle tre fasi di validazione (Formale, Recupero Chiave, Firma)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente (caricamento dei componenti, input, invio) sia funzionale e fluido.
  2. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  3. Validare il mapping tra le risposte API (BE) e i componenti grafici della pagina (Step di validazione).
  4. Verificare, per induzione, la corretta renderizzazione dei messaggi di errore:
  non si mira alla copertura esaustiva di ogni casistica di business (demandata ai test BE),
  ma alla conferma che il componente di UI reagisca correttamente ai diversi stati (PASSED, FAILED, SKIPPED).
  ***

  Scenario: [API_CLIENT_ASSERTION_VALIDATION_SUCCESS]
  Dato un client API valido, quando viene inviata una client assertion corretta allora tutte le fasi di validazione risultano PASSED

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |


  Scenario: [API_CLIENT_ASSERTION_VALIDATION_INVALID_AUDIENCE]
  Dato un client API valido, quando la client assertion ha audience invalida
  allora la validazione formale fallisce con invalidAudience

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim | value            |
      | aud   | invalid_audience |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | FAILED  | [Unexpected client assertion audience: invalid_audience] |
      | publicKeyRetrieve                    | SKIPPED | []                                                       |
      | clientAssertionSignatureVerification | SKIPPED | []                                                       |


  # Elimino tutti i claim tranne uno (aud) altrimenti il payload sarebbe una stringa vuota, out-of-scope del test
  Scenario: [API_CLIENT_ASSERTION_VALIDATION_MISSING_REQUIRED_CLAIMS]
  Dato un client API valido, quando la client assertion non contiene claim obbligatori
  allora la validazione formale fallisce con i rispettivi errori

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim    | value |
      | __remove | jti   |
      | __remove | iat   |
      | __remove | exp   |
      | __remove | iss   |
      | __remove | sub   |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion creata sono:
      | step                                 | result  | errors                                                                                                                                                                                 |
      | clientAssertionValidation            | FAILED  | [JTI not found in client assertion, IAT not found in client assertion, EXP not found in client assertion, Issuer not found in client assertion, Subject not found in client assertion] |
      | publicKeyRetrieve                    | SKIPPED | []                                                                                                                                                                                     |
      | clientAssertionSignatureVerification | SKIPPED | []                                                                                                                                                                                     |


  Scenario: [API_CLIENT_ASSERTION_VALIDATION_MISSING_REQUIRED_AUD_CLAIMS]
  Dato un client API valido, quando la client assertion non contiene il claim obbligatorio aud
  allora la validazione formale fallisce con l'errore audience not found

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim    | value |
      | __remove | aud   |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion creata sono:
      | step                                 | result  | errors                                   |
      | clientAssertionValidation            | FAILED  | [Audience not found in client assertion] |
      | publicKeyRetrieve                    | SKIPPED | []                                       |
      | clientAssertionSignatureVerification | SKIPPED | []                                       |


  Scenario: [API_CLIENT_ASSERTION_PUBLIC_KEY_RETRIEVE_INVALID_KID_FORMAT]
  Dato un client API valido, quando il claim kid non è in formato valido
  allora il recupero della chiave pubblica fallisce con invalidKidFormat

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim      | value                  |
      | header.kid | not-a-valid-kid-format |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                                                |
      | clientAssertionValidation            | PASSED  | []                                                                                    |
      | publicKeyRetrieve                    | FAILED  | [Public key with kid not-a-valid-kid-format not found for client $retrieve(clientId)] |
      | clientAssertionSignatureVerification | SKIPPED | []                                                                                    |


  Scenario: [API_CLIENT_ASSERTION_VALIDATION_EXPIRED_TOKEN]
  Dato un client API valido, quando la client assertion è scaduta
  allora la verifica della firma fallisce con tokenExpiredError

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim | value     |
      | exp   | now-10800 |
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion associata al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors                                                   |
      | clientAssertionValidation            | PASSED | []                                                       |
      | publicKeyRetrieve                    | PASSED | []                                                       |
      | clientAssertionSignatureVerification | FAILED | [Token expired in client assertion signature validation] |
