@loadPage
Feature: Navigazione portale SEND Mittenti
  In qualità di un utente di una PA
  voglio navigare il portare SEND Mittenti
  così posso verificare che ogni pagina sia raggiungibile

  Scenario Outline: [LOAD_PAGE] Verifica la raggiungibilità delle pagine
    Given l'utente è un "admin" di "Comune di Verona"
    When naviga alla pagina <Pagina>
    Then la pagina deve caricarsi correttamente

    Examples:
      | Pagina              |
      | Dashboard           |
      | NotificationDetails |
      | CreateNotification  |
      | APIKey              |
      | NewAPIKey           |
      | Statistics          |
      | PlatformStatus      |
