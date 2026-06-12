@debug-client-assertion-page-ui-flow
Feature: Debugger della request di tipo DPoP per un voucher spendibile presso le API della piattaforma Interop (Frontend) - Test di Flusso Funzionale

  Come Aderente in possesso di un client di tipo API
  Voglio validare la mia request di tipo DPoP per un voucher spendibile presso le API della piattaforma Interop
  Al fine di identificare errori strutturali, temporali o crittografici nelle cinque fasi di validazione (Client assertion, Recupero Chiave, Firma, Stato Piattaforma, DPoP)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente sia completabile con l'esito atteso e con fluidità.
  2. Validare il mapping tra le risposte API (BE) e i componenti grafici della pagina.
  ***

  Scenario: [DEBUG_INTEROP_VOUCHER_DPOP_REQ_1]
  Dato un client API, una DPoP Proof ed una Client assertion valida,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora tutte le fasi di validazione risultano in stato PASSED

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato
    And una dpop proof valida generata con una chiave RSA
    When l'utente admin di PagoPA si trova alla pagina DebugClientAssertion del portale Interop
    And l'utente richiede la validazione della client assertion e della dpop proof associate al client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED | []     |
      | publicKeyRetrieve                    | PASSED | []     |
      | clientAssertionSignatureVerification | PASSED | []     |
      | dpopProofValidation                  | PASSED | []     |

  Scenario: [DEBUG_INTEROP_VOUCHER_DPOP_REQ_2]
  Dato un client API, una DPoP Proof valida ed una Client assertion avente claim audiance invalido,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client e:
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
      | dpopProofValidation                  | PASSED  | []                                                       |

  Scenario Outline: [DEBUG_INTEROP_VOUCHER_DPOP_REQ_3]
  Dato un client API, una Client Assertion valida ed una DPoP Proof con un claim obbligatorio mancante,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora tutte le fasi di validazione di Client assertion, Recupero chiave, Firma e Stato della piattaforma risultano in stato PASSED
  e la fase di validazione della DPoP Proof risulta in stato FAILED con il messaggio di errore specifico al claim mancante

    Given un eservice creato da Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un client API creato da PagoPA in cui è presente l'admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato
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
      | dpopProofValidation                  | FAILED | [<expectedError>] |

    Examples:
      | claimToRemove | expectedError                       |
      | jti           | JTI not found in DPoP proof         |
      | iat           | IAT not found in DPoP proof         |
      | htu           | HTU not found in DPoP proof payload |
      | htm           | HTM not found in DPoP proof payload |
