@createNotification @channel:Given=WEB,When=WEB,Then=WEB
Feature: Creazione nuova notifica

  Scenario: [CREATE_NOTIFICATION] Crea e invia una notifica dal portale
    Given la PA Comune_1 predispone una nuova notifica con i seguenti dati:
      | subject | Notifica inviata dal portale |
    And è stato aggiunto User_1 come destinatario:
      | digitalDomicile | test@pec.it |
      | pagoPA_number   | 0           |
      | F24_number      | 0           |
    When la notifica viene sottomessa
    Then la notifica raggiunge lo stato "ACCEPTED"
