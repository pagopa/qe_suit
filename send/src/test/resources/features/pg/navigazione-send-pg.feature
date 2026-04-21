@loadPage
Feature: Navigazione portale SEND persone giuridiche
  In qualità di un utente di una PG
  voglio navigare il portare SEND Persona Giuridica
  così posso verificare che ogni pagina sia raggiungibile

  Scenario: [PG_LOAD_PAGE] Lato Persona Giuridica - Verifica la raggiungibilità delle pagine
    Given la PG FrancescoPetrarca effettua l'accesso a SelfCare con autenticazione SPID
    Then la pagina Notifications è caricata con successo
    When naviga alla pagina DelegatedNotification
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina OrganizationDelegations
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina OrganizationAuthorizedRepresentatives
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina NewDelegation
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina Address
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina ApiIntegration
    Then la pagina deve caricarsi correttamente
    When naviga alla pagina PlatformStatusPagePG
    Then la pagina deve caricarsi correttamente
