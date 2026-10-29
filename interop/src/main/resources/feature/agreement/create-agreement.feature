@agreement
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Creazione di una richiesta di fruizione verso un EService

  Come Aderente interessato alla fruizione di un EService in catalogo
  Voglio inoltrare una richiesta di fruizione verso l'erogatore del servizio
  Al fine di instaurare un Agreement che abiliti alle fasi successive necessarie a raggiungere la fruizione di un EService.

  Scenario: [AGREEMENT_DEPRECATED_DESCRIPTOR_1] - Impossibilità di richiedere la fruizione di una versione obsoleta dell'EService
  Dato un EService con una versione deprecata
  quando un Fruitore tenta di inoltrare una richiesta di fruizione per tale versione
  allora il sistema impedisce l'inoltro della richiesta

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    Then il sistema impedisce al Comune di Pozzallo di inoltrare una richiesta di fruizione per la versione deprecata dell'EService

  Scenario: [AGREEMENT_DEPRECATED_DESCRIPTOR_2] - Impossibilità di richiedere la fruizione di una versione obsoleta di un EService in fase di archiviazione
  Dato un EService in fase di archiviazione con la prima versione deprecata e l'ultima versione attiva
  quando un Fruitore tenta di inoltrare una richiesta di fruizione per la versione deprecata
  allora il sistema impedisce l'inoltro della richiesta

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA ed EService in archiviazione
    Then il sistema impedisce al Comune di Pozzallo di inoltrare una richiesta di fruizione per la versione deprecata dell'EService