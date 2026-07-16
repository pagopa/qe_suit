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


  Scenario: [BFF_NOTIFICA_1] Lato mittente viene controllato la pagina di dettaglio di una notifica legale
#    When l'ente "Comune di palermo" crea una notifica di tipo singolo destinatario con bollettino pagoPA per il destinatario "Mario Gherkin"
#    Then la richiesta di notifica è stata accettata
    Given l'utente è un "admin" di "Comune di Verona"
    And naviga alla pagina Dashboard
    And viene aperto il dettaglio di una notifica
    And la pagina di dettaglio della notifica contiene la sezione relativa al sommario della notifica
    And la pagina di dettaglio della notifica contiene la sezione relativa al dettagli di pagamento
    And la pagina di dettaglio della notifica contiene la sezione relativa al documenti allegati
    And la pagina di dettaglio della notifica contiene la sezione relativa al stato della notifica
    And viene aperta la Sidebar contenente i dettagli aggiuntivi della notifica
    And il pannello dei dettagli aggiuntivi della notifica contiene tutti i campi popolati
    And chiude il pannello dei dettagli aggiuntivi della notifica
    And visualizza il dettaglio dello stato della notifica
    And torna alla pagina precedente
