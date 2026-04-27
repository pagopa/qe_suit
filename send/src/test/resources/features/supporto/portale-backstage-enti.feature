@backstage
Feature: Navigazione portale SEND Backstage

  Scenario: [BACKSTAGE_ENTI_1] Verifica la navigazione del portale Backstage per un utente di supporto
    Given naviga alla pagina BackstageProfile
    When l'utente accede alla BackstageProfile selezionando "Comune di Palermo"
    Then la pagina Dashboard è caricata con successo
    And viene aperto il dettaglio di una notifica
    Then la pagina NotificationDetails è caricata con successo
    When si passa alla sezione "API Key" tramite la sidebar
    Then come utente di supporto non posso visualizzare le API Keys
    When si passa alla sezione "Statistics" tramite la sidebar
    Then la pagina Statistics è caricata con successo
    When si passa alla sezione "Platform status" tramite la sidebar
    Then la pagina PlatformStatus è caricata con successo
    And la lingua della pagina viene impostata su "italiano"
    Then viene effettutato il logout
    And la pagina LogoutPage è caricata con successo
