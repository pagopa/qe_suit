@purpose-management
@channel:Given=BFF,When=BFF,Then=BFF
@channel:Given=BFF,When=M2M,Then=M2M
Feature: Gestione delle finalità inoltrate

  Scenario: [APPROVE_PURPOSE_1] Approvazione di una finalità in stato di attesa da parte di un erogatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato WAITING_FOR_APPROVAL associate da PagoPA
    Then Comune di Milano approva la finalità che va in stato ACTIVE

  Scenario: [APPROVE_PURPOSE_2] Approvazione di una finalità in stato di attesa da parte di un erogatore delegato
    Given una delega attiva dal Comune di Pozzallo al Comune di Milano per la gestione delle finalità
    And una finalità in stato di attesa creata dal Comune di Pozzallo e associata a un suo e-service
    When un admin appartenente al delegato Comune di Milano approva la finalità per conto del Comune di Pozzallo
    Then la finalità risulta approvata dall'erogatore e in stato ACTIVE

  Scenario: [APPROVE_PURPOSE_3] Approvazione di una seconda versione di una finalità in stato di attesa da parte di un erogatore
    Given una finalità con una prima versione attiva e una seconda versione in stato di attesa creata dal Comune di Pozzallo per un e-service del Comune di Milano
    When un admin appartenente all'erogatore Comune di Milano approva la seconda versione della finalità
    Then la seconda versione della finalità risulta approvata e in stato ACTIVE

  Scenario: [APPROVE_PURPOSE_4] Approvazione di una finalità in stato di attesa creata da template da parte di un erogatore
    Given una finalità in stato di attesa creata dal Comune di Pozzallo da un template e associata a un e-service del Comune di Milano
    When un admin appartenente all'erogatore Comune di Milano approva la finalità in attesa
    Then la finalità risulta approvata dall'erogatore e in stato ACTIVE

  Scenario: [REJECT_PURPOSE_1] Rifiuto di una finalità in stato di attesa da parte di un erogatore
    Given una finalità in stato di attesa creata dal Comune di Pozzallo e associata a un e-service del Comune di Milano
    When un admin appartenente all'erogatore Comune di Milano rifiuta la finalità in attesa
    Then la finalità risulta rifiutata dall'erogatore e in stato REJECTED

  Scenario: [REJECT_PURPOSE_2] Rifiuto di una finalità in stato di attesa da parte di un erogatore delegato
    Given una delega attiva dal Comune di Pozzallo al Comune di Milano per la gestione delle finalità
    And una finalità in stato di attesa creata dal Comune di Pozzallo e associata a un suo e-service
    When un admin appartenente al delegato Comune di Milano rifiuta la finalità per conto del Comune di Pozzallo
    Then la finalità risulta rifiutata dall'erogatore e in stato REJECTED

  Scenario: [REJECT_PURPOSE_3] Rifiuto di una seconda versione di una finalità in stato di attesa da parte di un erogatore
    Given una finalità con una prima versione attiva e una seconda versione in stato di attesa creata dal Comune di Pozzallo per un e-service del Comune di Milano
    When un admin appartenente all'erogatore Comune di Milano rifiuta la seconda versione della finalità
    Then la seconda versione della finalità risulta rifiutata e in stato REJECTED

  Scenario: [REJECT_PURPOSE_4] Rifiuto di una finalità in stato di attesa creata da template da parte di un erogatore
    Given una finalità in stato di attesa creata dal Comune di Pozzallo da un template e associata a un e-service del Comune di Milano
    When un admin appartenente all'erogatore Comune di Milano rifiuta la finalità in attesa
    Then la finalità risulta rifiutata dall'erogatore e in stato REJECTED
