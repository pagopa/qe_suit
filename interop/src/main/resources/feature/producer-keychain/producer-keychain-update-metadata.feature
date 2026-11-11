Feature: Modifica dei metadati dei portachiavi erogatore
  Come amministratore dell’ente erogatore
  Voglio aggiornare nome e descrizione di un portachiavi
  Per conservarne chiavi membri e associazioni agli e-service.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PRODUCER_KEYCHAIN_METADATA_1] - Modifica di un metadato senza alterare le relazioni del portachiavi
  Dato un portachiavi erogatore appartenente all'ente amministrato e contenente chiavi,
  quando l’amministratore modifica un metadato del portachiavi,
  allora il sistema aggiorna il solo campo richiesto preservando chiavi membri e associazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva i metadati non modificati
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

    Examples:
      | campo       | valore                               |
      | nome        | Portachiavi erogatore aggiornato     |
      | descrizione | Descrizione aggiornata portachiavi   |

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [PRODUCER_KEYCHAIN_METADATA_2] - Modifica di un metadato senza alterare le relazioni del portachiavi
  Dato un portachiavi erogatore appartenente all'ente amministrato e contenente chiavi,
  quando l’amministratore modifica un metadato del portachiavi,
  allora il sistema aggiorna il solo campo richiesto preservando chiavi membri e associazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva i metadati non modificati
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

    Examples:
      | campo       | valore                               |
      | nome        | Portachiavi erogatore aggiornato     |
      | descrizione | Descrizione aggiornata portachiavi   |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_3] - Modifica congiunta di nome e descrizione del portachiavi
  Dato un portachiavi erogatore con chiavi e associazioni,
  quando l’amministratore modifica nome e descrizione,
  allora entrambi i metadati sono aggiornati senza alterare configurazione e relazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo       | valore                                |
      | nome        | Portachiavi erogatore aggiornato      |
      | descrizione | Descrizione aggiornata portachiavi    |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_4] - Modifica congiunta di nome e descrizione del portachiavi
  Dato un portachiavi erogatore con chiavi e associazioni,
  quando l’amministratore modifica nome e descrizione,
  allora entrambi i metadati sono aggiornati senza alterare configurazione e relazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo       | valore                                |
      | nome        | Portachiavi erogatore aggiornato      |
      | descrizione | Descrizione aggiornata portachiavi    |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PRODUCER_KEYCHAIN_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato una portachiavi erogatore modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "R-1"
    Then il form della risorsa "R-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "R-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "R-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PRODUCER_KEYCHAIN_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    And il form di modifica dei metadati di una risorsa "K-1" del Comune di Milano aperto
    When un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "K-1" e richiede il salvataggio
    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PRODUCER_KEYCHAIN_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When il form di modifica dei metadati della risorsa "K-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "K-1"
    Then il form della risorsa "K-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "K-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "K-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "K-1" viene rifiutata
    And la risorsa "K-1" conserva metadati, stati, versioni e relazioni iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano aggiorna i metadati della risorsa "R-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "R-1"
    Then la ripetizione della modifica della risorsa "R-1" viene accettata
    And la risorsa "R-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "K-1" confermando gli altri valori correnti
    Then la risorsa "K-1" presenta il metadato aggiornato
    And la risorsa "K-1" conserva stati, versioni, relazioni e metadati invariati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_UNIQUENESS_1] - I portachiavi possono avere lo stesso nome
  Dato due portachiavi del medesimo ente,
  quando l’amministratore assegna a un portachiavi il nome dell’altro,
  allora la rinomina riesce senza introdurre un vincolo di unicità.

    Given due portachiavi erogatore "K-1" e "K-2" del Comune di Milano
    When un admin del Comune di Milano richiede di assegnare al portachiavi "K-1" il nome di "K-2"
    Then la rinomina del portachiavi "K-1" viene accettata
    And il portachiavi "K-2" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PRODUCER_KEYCHAIN_METADATA_VALIDATION_1] - Rispetto dei limiti di lunghezza dei metadati
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi e rifiuta gli altri senza modifiche parziali.

    Given un portachiavi erogatore "K-1" del Comune di Milano
    When un admin del Comune di Milano richiede un valore di <lunghezza> caratteri per il campo <campo> della risorsa "K-1"
    Then la modifica della risorsa "K-1" ha esito <esito>
    And in caso di rifiuto la risorsa "K-1" conserva tutti i dati iniziali

    Examples:
      | campo       | lunghezza | esito     |
      | nome        | 60        | accettata |
      | descrizione | 251       | rifiutata |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_VALIDATION_2] - Rifiuto di valori non ammessi nei metadati di un portachiavi
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given una portachiavi erogatore "K-1" del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica del campo descrizione della risorsa "K-1" con valore vuoto
    Then la modifica della risorsa "K-1" viene rifiutata
    And la risorsa "K-1" conserva tutti i dati iniziali
