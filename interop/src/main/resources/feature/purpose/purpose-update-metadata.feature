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
  Scenario Outline: [PURPOSE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato una finalità modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "R-1"
    Then il form della risorsa "R-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "R-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "R-1" presentano i metadati aggiornati

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario Outline: [PURPOSE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When il form di modifica dei metadati di una finalità "P-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "R-1" e richiede il salvataggio
    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario Outline: [PURPOSE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When il form di modifica dei metadati della risorsa "P-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "P-1"
    Then il form della risorsa "P-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "P-1"

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given una finalità "P-1" <modalità> non derivata da template del Comune di Milano in stato ACTIVE
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "P-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "P-1" viene rifiutata
    And la risorsa "P-1" conserva metadati, stati, versioni e relazioni iniziali

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_2] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given una finalità "P-1" <modalità> non derivata da template del Comune di Milano in stato ACTIVE
    When un admin del Comune di Milano aggiorna i metadati della risorsa "P-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "P-1"
    Then la ripetizione della modifica della risorsa "P-1" viene accettata
    And la risorsa "P-1" conserva stati, versioni e relazioni

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_3] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given una finalità "P-1" <modalità> non derivata da template del Comune di Milano in stato ACTIVE
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "P-1" confermando gli altri valori correnti
    Then la risorsa "P-1" presenta il metadato aggiornato
    And la risorsa "P-1" conserva stati, versioni, relazioni e metadati invariati

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_UNIQUENESS_1] - Rifiuto della rinomina in conflitto per una finalità
  Dato due risorse nello stesso ambito di unicità con nomi distinti,
  quando l’attore rinomina una risorsa usando il nome dell’altra e una descrizione valida,
  allora la modifica è respinta integralmente lasciando intatte entrambe le risorse.

    Given due finalità "P-1" e "P-2" nel medesimo ambito di unicità
    When un admin del Comune di Milano richiede di assegnare alla risorsa "P-1" il nome di "P-2" e una nuova descrizione
    Then la modifica della risorsa "P-1" viene rifiutata
    And le risorse "P-1" e "P-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_UNIQUENESS_2] - Unicità non aggirabile con differenze di maiuscole e minuscole per una finalità
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata senza applicare anche la descrizione valida.

    Given due finalità "P-1" e "P-2" nel medesimo ambito di unicità
    When un admin del Comune di Milano richiede per "P-1" il nome di "P-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica della risorsa "P-1" viene rifiutata
    And le risorse "P-1" e "P-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_UNIQUENESS_3] - Omonimia consentita tra finalità in ambiti distinti
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given due risorse "P-1" e "P-2" di tipo finalità in ambiti di unicità distinti
    When un admin del Comune di Milano richiede di assegnare alla risorsa "P-1" il nome di "P-2"
    Then la rinomina della risorsa "P-1" viene accettata
    And la risorsa "P-2" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_VALIDATION_1] - Rispetto dei limiti di lunghezza dei metadati
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi e rifiuta gli altri senza modifiche parziali.

    Given una finalità "P-1" del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede un valore di <lunghezza> caratteri per il campo <campo> della risorsa "P-1"
    Then la modifica della risorsa "P-1" ha esito <esito>
    And in caso di rifiuto la risorsa "P-1" conserva tutti i dati iniziali

    Examples:
      | campo       | lunghezza | esito     |
      | titolo      | 4         | rifiutata |
      | titolo      | 60        | accettata |
      | descrizione | 9         | rifiutata |
      | descrizione | 250       | accettata |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_VALIDATION_2] - Rifiuto di valori non ammessi nei metadati di una finalità
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given una finalità "P-1" del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica del campo descrizione della risorsa "P-1" con valore non impostato
    Then la modifica della risorsa "P-1" viene rifiutata
    And la risorsa "P-1" conserva tutti i dati iniziali
