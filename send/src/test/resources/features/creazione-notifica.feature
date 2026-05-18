@createNotification
Feature: Creazione nuova notifica

  Scenario Outline: [CREATE_NOTIFICATION] Crea e invia una notifica semplice
    Given l'utente è un "<role>" di "<mittente>"
    When naviga alla pagina CreateNotification
    And crea e invia una notifica di tipo "simple_notification"
    Then la notifica è stata inviata con successo
    Examples:
      | role  | mittente |
      | admin | Comune di Verona |    