@newUIDetailPage
Feature: Tramite nuova interfaccia di notifica
  verifico che tutti gli elementi siano correttamente posti
  per mittente e per destinatario

  Scenario: [NUI_DETTAGLIO_NOTIFICA_ANNULLO_NOTIFICA] Viene controllato che, a seguito di annullamento, la pagina
    riporti correttamente l'informazione tramite apposita chip
    Given una sessione HTTP programmatica su B2B
    And una notifica di tipo semplice creata dalla PA Grossini per il destinatario "Lucrezia" in stato "ACCEPTED"
    And la PA Grossini ha creato ed annullato una notifica di tipo semplice per il destinatario "Lucrezia"
    And la PA Grossini è loggata al portale SEND tramite Browser
    And naviga alla pagina Dashboard
    And viene ricercata e aperta una notifica che rispetta i seguenti criteri:
      | iun   | $currentNotificationIUN |
    And la pagina di dettaglio della notifica contiene la sezione relativa al sommario della notifica
    And la pagina di dettaglio della notifica contiene la sezione relativa al documenti allegati
    And la pagina di dettaglio della notifica contiene la sezione relativa al stato della notifica
    Then la notifica è in stato annullata





    And la PA Grossini è loggata a SEND
    And l'ente crea una notifica di tipo semplice per il destinatario "Lucrezia"
    And la richiesta di notifica è in stato "ACCEPTED"
    When l'ente annulla la notifica precedentemente creata
    #cambiare con una formulazione più specifica e lineare
    And viene aperto il dettaglio della notifica che soddisfa i seguenti criteri:
      | iun   | $currentNotificationIUN |
    Then la notifica è in stato annullata


  Scenario: [NUI_DETTAGLIO_NOTIFICA_AFTER_120_GIORNI] Viene controllato che, dopo 120 giorni giorni, gli allegati
    non siano presenti allegati e che compaia il relativo banner
    Given la PA Grossini è loggata a SEND
    When viene effettuata una ricerca notifica tramite i seguenti parametri:
      | endDate   | $120DaysAgo |
    #Then


  Scenario: [NUI_DETTAGLIO_NOTIFICA_5_PAGOPA] Viene controllato che la paginazione sia mostrata con
  5 bollettini PagoPA
    Given la PA Grossini è loggata a SEND
    When l'ente "Comune di Palermo" crea una notifica di tipo singolo destinatario con bollettino pagoPA per il destinatario "Lucrezia" con i seguenti valori:
      | pagoPA_number   | 5 |
    And viene effettuata una ricerca notifica tramite i seguenti parametri:
      | iun   | IUN da notifica corrente |
    #Then

  Scenario: [NUI_DETTAGLIO_NOTIFICA_5_F24] Viene controllato che la tabella specifica sia mostrata con
  5 bollettini F24
    Given la PA Grossini effettua l'accesso a SelfCare con autenticazione SPID
    When l'ente "Comune di Palermo" crea una notifica di tipo singolo destinatario con bollettino F24 per il destinatario "Lucrezia" con i seguenti valori:
      | F24_number   | 5 |
    And viene effettuata una ricerca notifica tramite i seguenti parametri:
      | iun   | IUN da notifica corrente |
    #Then
