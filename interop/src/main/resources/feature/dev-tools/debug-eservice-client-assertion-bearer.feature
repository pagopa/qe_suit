@debug-client-assertion-page-ui-flow
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Debugger della request di tipo bearer per un voucher spendibile presso un erogatore di un e-service sincrono

  Come Aderente in possesso di un client di tipo CONSUMER
  Voglio validare la mia Client Assertion facente parte di una request di tipo bearer utile al recupero di un Voucher spendibile presso un erogatore di un EService sincrono
  Al fine di identificare errori strutturali, temporali o crittografici nelle quattro fasi di validazione (Client assertion, Recupero Chiave, Firma, Stato Piattaforma)
  
  Scenario: DEBUG_ESERVICE_VOUCHER_BEARER_REQ_1
  Dato un client CONSUMER ed una Client assertion valida,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora tutte le fasi di validazione risultano in stato PASSED
    
    Given un EService creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    When l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors |
      | clientAssertionValidation            | PASSED |      |
      | publicKeyRetrieve                    | PASSED |      |
      | clientAssertionSignatureVerification | PASSED |      |
      | platformStatesVerification           | PASSED |      |

  Scenario: DEBUG_ESERVICE_VOUCHER_BEARER_REQ_2
  Dato un client CONSUMER ed una Client assertion avente claim audiance invalido,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED
    
    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim | value            |
      | aud   | invalid_audience |
    When l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | FAILED  | Unexpected client assertion audience: invalid_audience |
      | publicKeyRetrieve                    | SKIPPED |                                                        |
      | clientAssertionSignatureVerification | SKIPPED |                                                        |

  Scenario: DEBUG_ESERVICE_VOUCHER_BEARER_REQ_3
  Dato un client CONSUMER ed una Client assertion non contenete claim obbligatori,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED
    
    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim    | value |
      | __remove | jti   |
      | __remove | iat   |
      | __remove | aud   |
      | __remove | exp   |
      | __remove | iss   |
      | __remove | sub   |
    When l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion creata sono:
      | step                                 | result  | errors                                                                                                                                                                                                                         |
      | clientAssertionValidation            | FAILED  | JTI not found in client assertion, IAT not found in client assertion, EXP not found in client assertion, Issuer not found in client assertion, Subject not found in client assertion, Audience not found in client assertion |
      | publicKeyRetrieve                    | SKIPPED |                                                                                                                                                                                                                              |
      | clientAssertionSignatureVerification | SKIPPED |                                                                                                                                                                                                                              |

  Scenario: DEBUG_ESERVICE_VOUCHER_BEARER_REQ_4
  Dato un client CONSUMER ed una Client assertion avente claim kid invalido,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fase di validazione della Client Assertion risulta in stato FAILED
  e la fase di Stato Piattaforma non viene visualizzata (rif. /PIN-10056?focusedCommentId=317150)
  e le restanti risultano in stato SKIPPED
    
    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim      | value                  |
      | header.kid | not-a-valid-kid-format |
    When l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                      |
      | clientAssertionValidation            | FAILED  | Unexpected format for kid |
      | publicKeyRetrieve                    | SKIPPED |                           |
      | clientAssertionSignatureVerification | SKIPPED |                           |

  Scenario: DEBUG_ESERVICE_VOUCHER_BEARER_REQ_5
  Dato un client CONSUMER ed una Client assertion scaduta,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fasi di validazione Client Assertion e Recupero Chiave risultano in stato PASSED
  e la fase di validazione della Firma risulta in stato FAILED con errore di token scaduto
  e le restanti risultano in stato SKIPPED
    
    Given un eservice creato da Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion generata usando il client, la finalità e:
      | claim | value     |
      | exp   | now-10800 |
    When l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result  | errors                                                   |
      | clientAssertionValidation            | PASSED  |                                                        |
      | publicKeyRetrieve                    | PASSED  |                                                        |
      | clientAssertionSignatureVerification | FAILED  | Token expired in client assertion signature validation |
      | platformStatesVerification           | SKIPPED |                                                        |

  Scenario: DEBUG_ESERVICE_VOUCHER_BEARER_REQ_6
  Dato un client CONSUMER valido, una PURPOSE associata in stato SUSPENDED ed una Client assertion valida,
  quando l'utente sottomette le informazioni nella form di debugging,
  allora la fasi di validazione Client Assertion, Recupero Chiave e Firma risultano in stato PASSED
  e la fase di validazione dello Stato Piattaforma risulta in stato FAILED con errore di finalità in stato inattivo
    
    Given un eservice creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato SUSPENDED associate da PagoPA
    And un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client e la finalità
    When l'utente inoltra la richiesta di validazione specificando client assertion e client
    Then i risultati della validazione della client assertion sono:
      | step                                 | result | errors                       |
      | clientAssertionValidation            | PASSED |                            |
      | publicKeyRetrieve                    | PASSED |                            |
      | clientAssertionSignatureVerification | PASSED |                            |
      | platformStatesVerification           | FAILED | Purpose state is: INACTIVE |