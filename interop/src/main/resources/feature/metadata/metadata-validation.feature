@PIN-11002
Feature: Validazione dei valori e delle richieste di modifica dei metadati
  Come utente autorizzato
  Voglio aggiornare metadati conformi ai vincoli di ogni risorsa
  Per impedire salvataggi parziali o modifiche con dati non ammessi.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_VALIDATION_1] - Rispetto dei limiti di lunghezza dei metadati
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi e rifiuta gli altri senza modifiche parziali.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede un valore di <lunghezza> caratteri per il campo <campo> della risorsa "R-1"
    Then la modifica della risorsa "R-1" ha esito <esito>
    And in caso di rifiuto la risorsa "R-1" conserva tutti i dati iniziali

    Examples:
      | risorsa            | campo       | lunghezza | esito     |
      | e-service          | nome        | 4         | rifiutata |
      | e-service          | nome        | 5         | accettata |
      | e-service          | nome        | 60        | accettata |
      | e-service          | nome        | 61        | rifiutata |
      | e-service          | descrizione | 9         | rifiutata |
      | e-service          | descrizione | 10        | accettata |
      | e-service          | descrizione | 400       | accettata |
      | e-service          | descrizione | 401       | rifiutata |
      | finalità           | titolo      | 4         | rifiutata |
      | finalità           | titolo      | 60        | accettata |
      | finalità           | descrizione | 9         | rifiutata |
      | finalità           | descrizione | 250       | accettata |
      | template e-service | nome        | 45        | accettata |
      | template e-service | nome        | 46        | rifiutata |
      | template e-service | destinatari | 250       | accettata |
      | template e-service | destinatari | 251       | rifiutata |
      | template finalità  | titolo      | 60        | accettata |
      | template finalità  | descrizione | 251       | rifiutata |
      | client             | nome        | 61        | rifiutata |
      | client             | descrizione | 250       | accettata |
      | portachiavi        | nome        | 60        | accettata |
      | portachiavi        | descrizione | 251       | rifiutata |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_VALIDATION_2] - Rifiuto di valori non ammessi nei metadati
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica del campo <campo> della risorsa "R-1" con valore <valore>
    Then la modifica della risorsa "R-1" viene rifiutata
    And la risorsa "R-1" conserva tutti i dati iniziali

    Examples:
      | risorsa           | campo       | valore             |
      | e-service         | nome        | vuoto              |
      | finalità          | descrizione | non impostato      |
      | template e-service| destinatari | collegamento vietato |
      | client            | nome        | vuoto              |
      | portachiavi       | descrizione | vuoto              |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_VALIDATION_3] - I campi omessi non vengono sovrascritti
  Dato una risorsa con nome e descrizione valorizzati,
  quando l’attore invia la modifica del solo nome,
  allora la descrizione rimane identica a quella iniziale.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica del solo nome della risorsa "R-1"
    Then la risorsa "R-1" presenta il nome aggiornato
    And la risorsa "R-1" conserva la descrizione iniziale

    Examples:
      | risorsa     |
      | e-service   |
      | client      |
      | portachiavi |

