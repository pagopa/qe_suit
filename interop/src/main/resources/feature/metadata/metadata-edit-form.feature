@PIN-11380
Feature: Modifica dei metadati tramite il form
  Come utente autorizzato
  Voglio modificare e consultare i metadati nel dettaglio della risorsa
  Per ricevere conferma del salvataggio e preservare gli input in caso di errore.

  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario Outline: [METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato una risorsa modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "R-1"
    Then il form della risorsa "R-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "R-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "R-1" presentano i metadati aggiornati

    Examples:
      | risorsa            |
      | e-service          |
      | finalità           |
      | template e-service |
      | template finalità  |
      | client Consumer    |
      | client API         |
      | portachiavi        |

  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given il form di modifica dei metadati di una risorsa "R-1" del Comune di Milano aperto
    When un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "R-1" e richiede il salvataggio
    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given il form di modifica dei metadati della risorsa "R-1" del Comune di Milano aperto con una modifica concorrente
    When un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "R-1"
    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "R-1"

