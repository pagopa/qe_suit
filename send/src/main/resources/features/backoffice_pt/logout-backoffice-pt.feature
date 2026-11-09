@channel:Given=WEB,When=WEB,Then=WEB
Feature: Logout dal BackOffice dei Partner Tecnologici
  In qualità di utente di un ente
  voglio uscire dal portale SEND con il pulsante "Esci"
  così la mia sessione si chiude qualunque sia il tipo del mio ente

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_1] Un ente PT esce dal BackOffice PT
    Given il PT PT_1 effettua l'accesso a SelfCare con autenticazione SPID
    And la pagina PtInstitutions è caricata con successo
    When l'utente esce dal portale con il pulsante Esci
    Then la pagina LoginPage è caricata con successo

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_2] Un ente PA esce dal portale ente mittente
    Given la PA Comune_2 effettua l'accesso a SelfCare con autenticazione SPID
    And la pagina Dashboard è caricata con successo
    When l'utente esce dal portale con il pulsante Esci
    Then la pagina LoginPage è caricata con successo

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_3] Un'impresa esce dal portale imprese
    Given la PG Impresa_1 effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    And la pagina Notifications è caricata con successo
    When l'utente esce dal portale con il pulsante Esci
    Then la pagina PgLoginPage è caricata con successo

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_4] Dopo il logout il PT ritrova il filtro degli enti vuoto
    Given il PT PT_1 effettua l'accesso a SelfCare con autenticazione SPID
    And la pagina PtInstitutions è caricata con successo
    When il PT cerca gli enti per "ente"
    And l'utente esce dal portale con il pulsante Esci
    And la pagina LoginPage è caricata con successo
    And il PT PT_1 effettua l'accesso a SelfCare con autenticazione SPID
    Then la pagina PtInstitutions è caricata con successo
    And il filtro degli enti è vuoto
