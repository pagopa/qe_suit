@loadPage
Feature: Navigazione portale SEND Mittenti
  In qualità di un utente di una PA
  voglio navigare il portare SEND Mittenti
  così posso verificare che ogni pagina sia raggiungibile

  Scenario Outline: [LOAD_PAGE_1] Verifica la raggiungibilità delle pagine
    Given l'utente è un "admin" di "Comune di Verona"
    When naviga alla pagina <Pagina>
    Then la pagina deve caricarsi correttamente

    Examples:
      | Pagina              |
      | Dashboard           |
      | CreateNotification  |
      | APIKey              |
      | NewAPIKey           |
      | Statistics          |
      | PlatformStatus      |

  Scenario: [LOAD_PAGE_2] Verifica la navigazione alla pagina NotificationDetails
    Given l'utente è un "admin" di "Comune di Verona"
    When naviga alla pagina Dashboard
    Then viene aperto il dettaglio di una notifica
    And la pagina NotificationDetails è caricata con successo
