Feature: Modifica dei metadati delle finalità
  Come fruitore proprietario
  Voglio aggiornare titolo e descrizione di una finalità ordinaria o inversa
  Per conservarne stato e configurazione funzionale.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_1] - Modifica di un singolo metadato di una finalità ordinaria o inversa
  Dato una finalità non derivata da template nella modalità e nello stato previsti,
  quando il fruitore ne aggiorna soltanto titolo o descrizione,
  allora la modifica viene applicata senza alterare i dati operativi e il metadato omesso.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato <stato>
    When un admin di PagoPA richiede la modifica dei metadati della finalità "P-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la finalità "P-1" presenta i metadati richiesti
    And la finalità "P-1" conserva i metadati non modificati
    And la finalità "P-1" conserva stato, versioni e configurazione funzionale

    Examples:
      | modalità | stato                | campo       | valore                                |
      | ordinaria| DRAFT                | titolo      | Finalità aggiornata                   |
      | ordinaria| ACTIVE               | descrizione | Descrizione aggiornata della finalità |
      | ordinaria| SUSPENDED            | titolo      | Finalità aggiornata                   |
      | ordinaria| ARCHIVED             | descrizione | Descrizione aggiornata della finalità |
      | ordinaria| WAITING_FOR_APPROVAL | titolo      | Finalità aggiornata                   |
      | ordinaria| REJECTED             | descrizione | Descrizione aggiornata della finalità |
      | inversa  | DRAFT                | titolo      | Finalità aggiornata                   |
      | inversa  | ACTIVE               | descrizione | Descrizione aggiornata della finalità |
      | inversa  | SUSPENDED            | titolo      | Finalità aggiornata                   |
      | inversa  | ARCHIVED             | descrizione | Descrizione aggiornata della finalità |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_2] - Aggiornamento congiunto della finalità senza nuove versioni
  Dato una finalità ordinaria o inversa non derivata da template,
  quando il fruitore ne aggiorna titolo e descrizione,
  allora la finalità conserva la propria configurazione e le versioni preesistenti.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When un admin di PagoPA richiede la modifica dei metadati della finalità "P-1":
      | campo       | valore                                |
      | titolo      | Finalità aggiornata                   |
      | descrizione | Descrizione aggiornata della finalità |
    Then la finalità "P-1" presenta i metadati richiesti
    And la finalità "P-1" conserva stato, versioni e configurazione funzionale

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PURPOSE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato una finalità modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "R-1"
    Then il form della risorsa "R-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "R-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "R-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PURPOSE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When il form di modifica dei metadati di una risorsa "P-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "R-1" e richiede il salvataggio
    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PURPOSE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When il form di modifica dei metadati della risorsa "P-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "P-1"
    Then il form della risorsa "P-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "P-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given una finalità "P-1" <modalità> non derivata da template del Comune di Milano in stato ACTIVE
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "P-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "P-1" viene rifiutata
    And la risorsa "P-1" conserva metadati, stati, versioni e relazioni iniziali
