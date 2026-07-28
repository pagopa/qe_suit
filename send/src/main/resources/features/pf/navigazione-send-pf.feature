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
#    When l'ente "Comune di palermo" crea una notifica di tipo singolo destinatario con bollettino pagoPA per il destinatario "Lucrezia"
#    When l'ente "Comune di palermo" crea una notifica di tipo semplice per il destinatario "Lucrezia"
#    Then la richiesta di notifica è stata accettata
    Given l'utente Lucrezia effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    Then la pagina NotificationPF è caricata con successo
    Then viene effettuata una ricerca notifica tramite i seguenti parametri:
      | iun       | LPJT-LPZT-GYXQ-202604-Z-1  |
#      | startDate | $currentDate |
#      | endDate   | $currentDate |
    And la pagina di dettaglio della notifica contiene la sezione relativa al sommario della notifica
    And la pagina di dettaglio della notifica contiene la sezione relativa al dettagli di pagamento
    And la pagina di dettaglio della notifica contiene la sezione relativa al documenti allegati
    And la pagina di dettaglio della notifica contiene la sezione relativa al stato della notifica
    And la pagina di dettaglio della notifica non contiene la sezione relativa al facsimile della notifica
    And visualizza il dettaglio dello stato della notifica
    And torna alla pagina precedente
    And la lingua della pagina viene impostata su "inglese"
    And la pagina di dettaglio della notifica contiene la sezione relativa al facsimile della notifica















#  Scenario: [PF_DETTAGLIO_NOTIFICA_3] Crea una notifica con due avvisi pagoPA e override di alcuni campi
#    When l'ente "Comune di palermo" crea una notifica di tipo singolo destinatario con bollettino pagoPA per il destinatario "Lucrezia" con i seguenti valori
#      | subject       | Notifica di test con oggetto personalizzato |
#      | taxonomyCode  | 010203N                                     |
#      | pagoPA_number | 2                                            |
#    Then la richiesta di notifica è stata accettata
#
#  Scenario: [PF_DETTAGLIO_NOTIFICA_4] Crea una notifica senza indirizzo fisico del destinatario
#    When l'ente "Comune di palermo" crea una notifica di tipo semplice per il destinatario "Lucrezia" con i seguenti valori
#      | physicalAddress | $NULL |
#    Then la richiesta di notifica è stata accettata
