@channel:Given=WEB,When=WEB,Then=WEB
Feature: Accesso al BackOffice dei Partner Tecnologici
  In qualità di utente di un ente
  voglio accedere a SEND da Area Riservata
  così atterro sul portale dedicato al tipo del mio ente

  @backofficePt
  Scenario: [BACKOFFICE_PT_ACCESSO_1] Un ente PT accede a SEND e atterra sul BackOffice PT
    Given il PT PT_1 effettua l'accesso a SelfCare con autenticazione SPID
    Then la pagina PtInstitutions è caricata con successo

  @backofficePt
  Scenario: [BACKOFFICE_PT_ACCESSO_2] Un ente PA accede a SEND e atterra sul portale ente mittente
    Given la PA Comune_2 effettua l'accesso a SelfCare con autenticazione SPID
    Then la pagina Dashboard è caricata con successo

  @backofficePt
  Scenario: [BACKOFFICE_PT_ACCESSO_3] Un'impresa accede a SEND e atterra sul portale imprese
    Given la PG Impresa_1 effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    Then la pagina Notifications è caricata con successo
