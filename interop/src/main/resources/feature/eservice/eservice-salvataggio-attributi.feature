@agreement
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Gestione del salvataggio degli attributi nella pagina di configurazione del EService

  Scenario: [ESERVICE_SALVATAGGIO_ATTRIBUTI_1] - Verificare che alla richiesta di salvataggio di un attributo vuoto non venga restituito un messaggio di errore
  Dato un EService attivo
  quando l'Erogatore tenta di aggiungere un attributo vuoto (solo campi non inizializzati)
  allora il sistema non deve mostrare un messaggio di errore a video ma terminare piuttosto in maniera "soft" la richiesta

    Given un EService creato dal Comune di Milano
    When il Comune di Milano tenta di salvare attributi vuoti per l'EService
    Then il sistema non ritorna un messaggio di errore