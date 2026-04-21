@loadPage
Feature: Navigazione portale SEND persone giuridiche
  In qualità di un utente di una PA
  voglio navigare il portare SEND Mittenti
  così posso verificare che ogni pagina sia raggiungibile

  Scenario Outline: [LOAD_PAGE] Verifica la raggiungibilità delle pagine
    Given la PG FrancescoPetrarca effettua l'accesso a SelfCare con autenticazione SPID
    When l'utente accede alla dashboard selezionando "Comune di Palermo"

    Examples:
#      | Pagina              |
#      | Dashboard           |
#      | NotificationDetails |
#      | CreateNotification  |
#      | APIKey              |
#      | NewAPIKey           |
#      | Statistics          |
#      | PlatformStatus      |
