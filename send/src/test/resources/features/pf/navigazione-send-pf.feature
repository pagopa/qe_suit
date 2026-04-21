@loadPage
Feature: Navigazione portale SEND persone fisiche
  In qualità di un utente di una PF
  voglio navigare il portare SEND Persona Fisica
  così posso verificare che ogni pagina sia raggiungibile

  Scenario: [PF_LOAD_PAGE] Lato Persona Fisica - Verifica la raggiungibilità delle pagine
    Given l'utente Lucrezia effettua l'accesso a SelfCare con autenticazione SPID
    Then la pagina NotificationPF è caricata con successo
    When naviga alla pagina AddressPF
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina DelegationsPF
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina AppStatusPF
    Then la pagina deve caricarsi correttamente
