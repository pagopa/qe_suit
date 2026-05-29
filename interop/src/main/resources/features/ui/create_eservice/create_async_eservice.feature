Feature:
  Come utente

  Scenario: [CREATE_ESERVICE_SUCCESS]
    Given l'utente admin del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When l'utente compila il form di creazione dell'eService con dati validi e invia la richiesta

  Scenario: [CREATE_ASYNC_ESERVICE_REQUIRED_FIELDS_VALIDATION]
    Given l'utente admin del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When l'utente invia la form dello step Dati Generali senza compilare i campi obbligatori
    Then la creazione non prosegue ed il campo Nome dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Descrizione dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"