@PIN-11317
Feature: Clonazione degli e-service dopo la modifica dei metadati
  Come erogatore proprietario
  Voglio clonare un e-service aggiornato
  Per ottenere una nuova risorsa con i metadati correnti senza alterare il sorgente.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_CLONE_1] - Il clone usa i metadati aggiornati del sorgente
  Dato un e-service non derivato da template che può essere clonato,
  quando l’erogatore aggiorna nome e descrizione e lo clona,
  allora il clone nasce distinto con una versione in bozza e conserva i metadati previsti dalle regole di clonazione.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                       |
      | ES-1  | Comune di Milano | Servizio Anagrafe Sorgente |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    And un admin del Comune di Milano clona la versione scelta dell'e-service "ES-1" creando "ES-2"
    Then l'e-service "ES-2" è distinto da "ES-1" e ha una versione 1 in bozza
    And l'e-service "ES-2" presenta i metadati derivati dai valori aggiornati di "ES-1" secondo le regole di clonazione
    And l'e-service "ES-1" conserva stato e configurazione

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_CLONE_2] - Clonazione con nome del sorgente vicino al limite
  Dato un e-service clonabile con nome vicino alla lunghezza massima consentita,
  quando l’erogatore aggiorna i metadati e lo clona,
  allora il nome del clone rispetta i limiti e l’unicità previsti.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                      |
      | ES-1  | Comune di Milano | Servizio Anagrafe Esteso |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                                                       |
      | nome  | Servizio consultazione pratiche anagrafiche comunali 2026    |
    And un admin del Comune di Milano clona la versione scelta dell'e-service "ES-1" creando "ES-2"
    Then il nome dell'e-service "ES-2" rispetta limiti di lunghezza, composizione e unicità
    And l'e-service "ES-1" conserva stato e configurazione

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_CLONE_3] - Indipendenza dei metadati tra sorgente e clone
  Dato un e-service non derivato da template,
  quando l’erogatore lo clona e aggiorna separatamente nome e descrizione delle due risorse,
  allora le modifiche interessano soltanto la risorsa destinataria.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                       |
      | ES-1  | Comune di Milano | Servizio Anagrafe Origine  |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un admin del Comune di Milano clona la versione scelta dell'e-service "ES-1" creando "ES-2"
    And un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                         |
      | nome  | Sorgente rinominato            |
    Then l'e-service "ES-2" conserva i metadati iniziali
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-2":
      | campo       | valore                             |
      | descrizione | Nuova descrizione del clone        |
    Then l'e-service "ES-1" conserva i metadati aggiornati precedentemente
    And gli e-service "ES-1" e "ES-2" conservano identificativi, versioni e configurazioni distinti

