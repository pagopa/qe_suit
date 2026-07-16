@loadPage
Feature: Navigazione portale SEND persone fisiche
  In qualità di un utente di una PF
  voglio navigare il portale SEND Persona Fisica
  così posso verificare che ogni pagina sia raggiungibile

  Scenario: [PF_LOAD_PAGE] Lato Persona Fisica - Verifica la raggiungibilità delle pagine
    Given l'utente Lucrezia effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    Then la pagina NotificationPF è caricata con successo
    When naviga alla pagina AddressPF
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina DelegationsPF
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina AppStatusPF
    Then la pagina deve caricarsi correttamente

  Scenario: [PF_DETTAGLIO_NOTIFICA_1] Viene controllato la pagina di dettaglio di una notifica legale
#    When l'ente "Comune di palermo" crea una notifica di tipo singolo destinatario con bollettino pagoPA per il destinatario "Mario Gherkin"
#    Then la richiesta di notifica è stata accettata
    Given l'utente Lucrezia effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    Then la pagina NotificationPF è caricata con successo
    And viene aperto il dettaglio di una notifica
    And la pagina di dettaglio della notifica contiene la sezione relativa al sommario della notifica
    And la pagina di dettaglio della notifica contiene la sezione relativa al dettagli di pagamento
    And la pagina di dettaglio della notifica contiene la sezione relativa al documenti allegati
    And la pagina di dettaglio della notifica contiene la sezione relativa al stato della notifica
    And la pagina di dettaglio della notifica non contiene la sezione relativa al facsimile della notifica
    And visualizza il dettaglio dello stato della notifica
    And torna alla pagina precedente
    And la lingua della pagina viene impostata su "inglese"
    And la pagina di dettaglio della notifica contiene la sezione relativa al facsimile della notifica
