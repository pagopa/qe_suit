@createNotification
Feature: Creazione nuova notifica

  Scenario: [CREATE_NOTIFICATION] Crea e invia una notifica semplice
    Given l'utente è un "admin" di "Comune di Verona"
    And una notifica di tipo "simple_notification"
    When naviga alla pagina CreateNotification
    And compila il form con i dati della notifica
    Then la notifica è stata inviata con successo