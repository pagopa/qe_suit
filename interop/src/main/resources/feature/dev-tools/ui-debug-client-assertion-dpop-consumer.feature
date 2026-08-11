@debug-client-assertion-page-ui-flow
Feature: Debugger della request di tipo DPoP per un voucher spendibile presso un erogatore di un e-service sincrono (Frontend) - Test di Flusso Funzionale

  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion facente parte di una request di tipo DPoP utile al recupero di un Voucher spendibile presso un erogatore di un EService sincrono
  Al fine di identificare errori strutturali, temporali o crittografici nelle cinque fasi di validazione (Client assertion, Recupero Chiave, Firma, Stato Piattaforma, DPoP)

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente sia completabile con l'esito atteso e con fluidità.
  2. Validare il mapping tra le risposte API (BE) e i componenti grafici della pagina.
  ***

  Scenario: DEBUG_ESERVICE_VOUCHER_DPOP_REQ_1
  Dato un client CONSUMER, una DPoP Proof valida ed una Client assertion valida,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora tutte le fasi di validazione risultano in stato PASSED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    And una dpop proof valida generata con una chiave RSA
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion, dpop proof e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED |      |
      | publicKeyRetrieve                    | PASSED |      |
      | clientAssertionSignatureVerification | PASSED |      |
      | platformStatesVerification           | PASSED |      |
      | dpopProofValidation                  | PASSED |      |

  Scenario: DEBUG_ESERVICE_VOUCHER_DPOP_REQ_2
  Dato un client CONSUMER, DPoP Proof valida ed una Client assertion avente claim audiance invalido,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED

    Given una sessione HTTP programmatica su BFF
    And un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim | value            |
      | aud   | invalid_audience |
    And una dpop proof valida generata con una chiave RSA
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion, dpop proof e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | FAILED  | Unexpected client assertion audience: invalid_audience |
      | publicKeyRetrieve                    | SKIPPED |                                                        |
      | clientAssertionSignatureVerification | SKIPPED |                                                        |
      | dpopProofValidation                  | PASSED  |                                                        |

  Scenario Outline: DEBUG_ESERVICE_VOUCHER_DPOP_REQ_3
  Dato un client CONSUMER, una Client assertion valida ed una DPoP Proof invalida,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora tutte le fasi di validazione di Client assertion, Recupero chiave, Firma e Stato della piattaforma risultano in stato PASSED
  e la fase di validazione della DPoP Proof risulta in stato FAILED con il messaggio di errore specifico al claim mancante

    Given una sessione HTTP programmatica su BFF
    And un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    And una dpop proof generata con una chiave RSA e:
      | claim    | value           |
      | __remove | <claimToRemove> |
    And un admin di PagoPA collegato al portale Interop dal Browser
    When l'utente naviga alla pagina Debug Client Assertion
    And l'utente inoltra la richiesta di validazione specificando client assertion, dpop proof e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors            |
      | clientAssertionValidation            | PASSED |                 |
      | publicKeyRetrieve                    | PASSED |                 |
      | clientAssertionSignatureVerification | PASSED |                 |
      | platformStatesVerification           | PASSED |                 |
      | dpopProofValidation                  | FAILED | <expectedError> |

    Examples:
      | claimToRemove | expectedError                       |
      | jti           | JTI not found in DPoP proof         |
      | iat           | IAT not found in DPoP proof         |
      | htu           | HTU not found in DPoP proof payload |
      | htm           | HTM not found in DPoP proof payload |