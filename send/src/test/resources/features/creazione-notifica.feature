@createNotification
Feature: Creazione nuova notifica

  #Scenario Outline: [CREATE_NOTIFICATION] Crea e invia una notifica semplice
  #  Given l'utente è un "<role>" di "<mittente>"
  #  When naviga alla pagina CreateNotification
  #  And crea e invia una notifica di tipo "<tipoNotifica>"
  #  Then la notifica è stata inviata con successo
  #  Examples:
  #    | role             | mittente                 | tipoNotifica             |
  #    | admin            | Comune di Verona         | simple_notification      |
  #    | admin            | Comune di Verona         | notification_with_pec    |
  #    | admin            | Comune di Milano         | notification_no_payment  |

  Scenario: [CREATE_NOTIFICATION-PEC] Crea e invia una notifica semplice with custom PEC
    #Given l'utente è un "admin" di "Comune di Verona"
    Given l'utente Grossini effettua l'accesso a SelfCare con autenticazione SPID
    When l'utente accede alla Dashboard selezionando "Comune di Palermo"
    And l'utente accede alla area riservata e seleziona il prodotto SEND
    And l'utente clicca su Crea Notifica
    #And crea e invia una notifica di tipo "simple_notification" con i seguenti override:
    #  | campo   | valore                                |
    #  | pec     | testpagopa2@pnpagopa.postecert.local  |
    #  | subject | [SUIT] [CREATE_NOTIFICATION-PEC]      |
    #Then la notifica è stata inviata con successo