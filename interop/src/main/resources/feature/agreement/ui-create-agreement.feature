Feature: Creazione di una richiesta di fruizione verso un EService (Frontend) - Test di Flusso Funzionale

  Come Aderente interessato alla fruizione di un EService in catalogo
  Voglio inoltrare una richiesta di fruizione verso l'erogatore del servizio
  Al fine di instaurare un Agreement che abiliti alle fasi successive necessarie a raggiungere la fruizione dell'EService.

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare che il flusso utente sia completabile con l'esito atteso e con fluidità.
  2. Validare il mapping tra le risposte API (BE) e i componenti grafici della pagina.
  ***

  Scenario: [AGREEMENT_DEPRECATED_DESCRIPTOR_REQ_1] - Impossibilità di richiedere la fruizione di una versione obsoleta dell'EService
  Dato un EService con una versione in stato DEPRECATED,
  quando un Fruitore tenta di inoltrare una richiesta di fruizione per tale versione,
  allora il sistema impedisce l'inoltro della richiesta

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    Then il sistema impedisce al Comune di Pozzallo di inoltrare una richiesta di fruizione per la versione deprecata dell'EService