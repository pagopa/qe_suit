@PIN-11317
Feature: Modifica dei metadati degli e-service
  Come erogatore proprietario
  Voglio modificare nome e descrizione di un e-service indipendentemente dallo stato dei descrittori
  Per mantenere aggiornate le informazioni senza modificare le versioni operative.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_1] - Modifica di un singolo metadato indipendente dallo stato del descrittore
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <stato>
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then l'e-service "ES-1" presenta i metadati richiesti
    And l'e-service "ES-1" conserva i metadati non modificati
    And l'e-service "ES-1" conserva versioni, stati e configurazioni dei descrittori

    Examples:
      | stato                     | campo       | valore                              |
      | in bozza                  | nome        | Servizio anagrafico aggiornato      |
      | pubblicata                | descrizione | Descrizione aggiornata del servizio |
      | sospesa                   | nome        | Servizio anagrafico aggiornato      |
      | deprecata                 | descrizione | Descrizione aggiornata del servizio |
      #| archiviata             | nome        | Servizio anagrafico aggiornato      |
      | in attesa di approvazione | descrizione | Descrizione aggiornata del servizio |
      | in archiviazione          | nome        | Servizio anagrafico aggiornato      |
      | in archiviazione sospesa  | descrizione | Descrizione aggiornata del servizio |


  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_2] - Modifica congiunta dei metadati indipendente dallo stato del descrittore
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <stato>
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti
    And l'e-service "ES-1" conserva versioni, stati e configurazioni dei descrittori

    Examples:
      | stato                     |
      | in bozza                  |
      | pubblicata                |
      | sospesa                   |
      | deprecata                 |
      #| archiviata                |
      | in attesa di approvazione |
      | in archiviazione          |
      | in archiviazione sospesa  |


