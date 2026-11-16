@channel:Given=WEB,When=WEB,Then=WEB
Feature: Disconnessione dai portali SEND degli enti
  In qualità di utente di un ente
  voglio potermi disconnettere dal portale SEND
  così la mia sessione termina qualunque sia il tipo del mio ente

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_1] Un partner tecnologico effettua la disconnessione dal BackOffice PT
    Given il partner tecnologico: PT_1 ha effettuato l'accesso a SelfCare con autenticazione SPID
    And il partner tecnologico ha accesso alle funzionalità del BackOffice PT
    When il partner tecnologico richiede la disconnessione dal portale
    Then la sessione del partner tecnologico risulta terminata

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_2] Un ente PA effettua la disconnessione dal portale ente mittente
    Given la PA Comune_2 ha effettuato l'accesso a SelfCare con autenticazione SPID
    And la PA ha accesso alle funzionalità del portale ente mittente
    When la PA richiede la disconnessione dal portale
    Then la sessione della PA risulta terminata

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_3] Un'impresa effettua la disconnessione dal portale imprese
    Given la PG Impresa_1 ha effettuato l'accesso a SelfCare con autenticazione SPID
    And la PG ha accesso alle funzionalità del portale imprese
    When la PG richiede la disconnessione dal portale
    Then la sessione della PG risulta terminata

  @backofficePt
  Scenario: [BACKOFFICE_PT_LOGOUT_4] Dopo la disconnessione il partner tecnologico ritrova il filtro degli enti vuoto
    Given il partner tecnologico: PT_1 ha effettuato l'accesso a SelfCare con autenticazione SPID
    And il partner tecnologico ha accesso alle funzionalità del BackOffice PT
    And il partner tecnologico cerca gli enti per "ente"
    When il partner tecnologico richiede la disconnessione dal portale
    And la sessione del partner tecnologico risulta terminata
    And il partner tecnologico: PT_1 effettua l'accesso a SelfCare con autenticazione SPID
    Then il partner tecnologico ha accesso alle funzionalità del BackOffice PT
    And il filtro degli enti è vuoto
