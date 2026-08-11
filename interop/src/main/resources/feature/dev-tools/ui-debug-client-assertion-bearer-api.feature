@debug-client-assertion-page-ui-flow
Feature: Debugger della request di tipo bearer per un voucher spendibile presso le API della piattaforma Interop (Frontend) - Test di Flusso Funzionale

  Come Aderente in possesso di un client di tipo API
  Voglio validare la mia Client Assertion facente parte di una request di tipo bearer utile al recupero di un Voucher spendibile presso le API della piattaforma Interop
  Al fine di identificare errori strutturali, temporali o crittografici nelle tre fasi di validazione (Client assertion, Recupero Chiave, Firma)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente sia completabile con l'esito atteso e con fluidità.
  2. Validare il mapping tra le risposte API (BE) e i componenti grafici della pagina.
  ***

  Scenario: DEBUG_INTEROP_VOUCHER_BEARER_REQ_1
  Dato un client API ed una Client assertion valida,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora tutte le fasi di validazione risultano in stato PASSED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED |      |
      | publicKeyRetrieve                    | PASSED |      |
      | clientAssertionSignatureVerification | PASSED |      |

  Scenario: DEBUG_INTEROP_VOUCHER_BEARER_REQ_2
  Dato un client API ed una Client assertion avente claim audiance invalido,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim | value            |
      | aud   | invalid_audience |
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | FAILED  | Unexpected client assertion audience: invalid_audience |
      | publicKeyRetrieve                    | SKIPPED |                                                        |
      | clientAssertionSignatureVerification | SKIPPED |                                                        |

  Scenario: DEBUG_INTEROP_VOUCHER_BEARER_REQ_3
  Dato un client API ed una Client assertion non contenete claim obbligatori,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim    | value |
      | __remove | jti   |
      | __remove | iat   |
      | __remove | exp   |
      | __remove | iss   |
      | __remove | sub   |
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion creata sono:
      | step                                 | result  | errors                                                                                                                                                                                 |
      | clientAssertionValidation            | FAILED  | JTI not found in client assertion, IAT not found in client assertion, EXP not found in client assertion, Issuer not found in client assertion, Subject not found in client assertion |
      | publicKeyRetrieve                    | SKIPPED |                                                                                                                                                                                      |
      | clientAssertionSignatureVerification | SKIPPED |                                                                                                                                                                                      |

  Scenario: DEBUG_INTEROP_VOUCHER_BEARER_REQ_4
  Dato un client API ed una Client assertion non contenete il claim aud obbligatorio,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim    | value |
      | __remove | aud   |
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion creata sono:
      | step                                 | result  | errors                                   |
      | clientAssertionValidation            | FAILED  | Audience not found in client assertion |
      | publicKeyRetrieve                    | SKIPPED |                                        |
      | clientAssertionSignatureVerification | SKIPPED |                                        |

  Scenario: DEBUG_INTEROP_VOUCHER_BEARER_REQ_5
  Dato un client API ed una Client assertion avente claim kid invalido,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim      | value                  |
      | header.kid | not-a-valid-kid-format |
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                      |
      | clientAssertionValidation            | FAILED  | Unexpected format for kid |
      | publicKeyRetrieve                    | SKIPPED |                           |
      | clientAssertionSignatureVerification | SKIPPED |                           |

  Scenario: DEBUG_INTEROP_VOUCHER_BEARER_REQ_6
  Dato un client API ed una Client assertion scaduta,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fasi di validazione Client Assertion e Recupero Chiave risultano in stato PASSED
  e la fase di validazione della Firma risulta in stato FAILED con errore di token scaduto
  e le restanti risultano in stato SKIPPED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client creato e:
      | claim | value     |
      | exp   | now-10800 |
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors                                                   |
      | clientAssertionValidation            | PASSED |                                                        |
      | publicKeyRetrieve                    | PASSED |                                                        |
      | clientAssertionSignatureVerification | FAILED | Token expired in client assertion signature validation |
      | platformStatesVerification           | SKIPPED |                                                        |

