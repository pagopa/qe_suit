@purpose-management
@channel:Given=BFF,When=BFF,Then=BFF
@channel:Given=BFF,When=M2M,Then=M2M
Feature: Gestione delle finalità inoltrate

  Scenario: [APPROVE_PURPOSE_1] Approvazione di una finalità in stato di attesa da parte di un erogatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato WAITING_FOR_APPROVAL associate da PagoPA
    Then Comune di Milano approva la finalità che va in stato ACTIVE

  @producer-delegation-settings
  Scenario: [APPROVE_PURPOSE_2] Approvazione di una finalità in stato di attesa da parte di un erogatore delegato
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       |
      | ES-1  | Comune di Milano | E-service Mandato Delegato |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And l'e-service "ES-1" del Comune di Pozzallo sottoscritto dal Comune di Comun Nuovo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato WAITING_FOR_APPROVAL associata dal Comune di Comun Nuovo
    Then Comune di Pozzallo delegato all'erogazione approva la finalità che va in stato ACTIVE

  Scenario: [APPROVE_PURPOSE_3] Approvazione di una seconda versione di una finalità in stato di attesa da parte di un erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service test |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato ACTIVE associata dal Comune di Pozzallo
    And il fruitore aggiorna la stima di chiamate giornaliere oltre la sua soglia per la finalità
    Then Comune di Milano approva la finalità che va in stato ACTIVE

  Scenario: [APPROVE_PURPOSE_4] Approvazione di una finalità in stato di attesa creata da template da parte di un erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service test |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And un template finalità creato dal Comune di Milano e pubblicato
    And l'e-service con una finalità creata dal template in stato WAITING_FOR_APPROVAL associata dal Comune di Pozzallo
    Then Comune di Milano approva la finalità che va in stato ACTIVE

  Scenario: [REJECT_PURPOSE_1] Rifiuto di una finalità in stato di attesa da parte di un erogatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato WAITING_FOR_APPROVAL associate da PagoPA
    Then Comune di Milano rifiuta la finalità che va in stato REJECTED

  @producer-delegation-settings
  Scenario: [REJECT_PURPOSE_2] Rifiuto di una finalità in stato di attesa da parte di un erogatore delegato
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       |
      | ES-1  | Comune di Milano | E-service Mandato Delegato |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And l'e-service "ES-1" del Comune di Pozzallo sottoscritto dal Comune di Comun Nuovo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato WAITING_FOR_APPROVAL associata dal Comune di Comun Nuovo
    Then Comune di Pozzallo rifiuta la finalità che va in stato REJECTED

  Scenario: [REJECT_PURPOSE_3] Rifiuto di una seconda versione di una finalità in stato di attesa da parte di un erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service test |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato ACTIVE associata dal Comune di Pozzallo
    And il fruitore aggiorna la stima di chiamate giornaliere oltre la sua soglia per la finalità
    Then Comune di Milano rifiuta la finalità che va in stato REJECTED

  Scenario: [REJECT_PURPOSE_4] Rifiuto di una finalità in stato di attesa creata da template da parte di un erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service test |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And un template finalità creato dal Comune di Milano e pubblicato
    And l'e-service con una finalità creata dal template in stato WAITING_FOR_APPROVAL associata dal Comune di Pozzallo
    Then Comune di Milano rifiuta la finalità che va in stato REJECTED
