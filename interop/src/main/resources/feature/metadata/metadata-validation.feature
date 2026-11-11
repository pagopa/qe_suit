@PIN-11002
Feature: Validazione dei valori e delle richieste di modifica dei metadati
  Come utente autorizzato
  Voglio aggiornare metadati conformi ai vincoli di ogni risorsa
  Per impedire salvataggi parziali o modifiche con dati non ammessi.

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
