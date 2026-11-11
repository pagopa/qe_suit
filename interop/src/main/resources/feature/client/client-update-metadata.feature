Feature: Modifica dei metadati dei client
  Come amministratore dell'ente proprietario
  Voglio aggiornare il nome e la descrizione dei client
  Per mantenerne aggiornate le informazioni senza alterare identità, chiavi e associazioni.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_METADATA_1] - Modifica di un singolo metadato di un client Consumer
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore ne modifica soltanto il nome o la descrizione,
  allora il sistema salva il nuovo valore, conserva quello omesso
  e mantiene invariati identità, chiavi e associazioni del client.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva i metadati non modificati
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

    Examples:
      | campo       | valore                            |
      | nome        | Client aggiornato                 |
      | descrizione | Descrizione aggiornata del client |

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [CLIENT_METADATA_2] - Modifica di un singolo metadato di un client Consumer da parte dell'amministratore M2M
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore M2M ne modifica soltanto il nome o la descrizione,
  allora il sistema salva il nuovo valore, conserva quello omesso
  e mantiene invariati identità, chiavi e associazioni del client.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva i metadati non modificati
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

    Examples:
      | campo       | valore                            |
      | nome        | Client aggiornato                 |
      | descrizione | Descrizione aggiornata del client |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_3] - Modifica congiunta di nome e descrizione di un client Consumer
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore ne modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i valori senza alterare identità, chiavi e associazioni.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client Consumer aggiornato   |
      | descrizione | Nuova descrizione del client |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_METADATA_4] - Modifica congiunta di nome e descrizione di un client Consumer da parte dell'amministratore M2M
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore M2M ne modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i valori senza alterare identità, chiavi e associazioni.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client Consumer aggiornato   |
      | descrizione | Nuova descrizione del client |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_METADATA_5] - Modifica dei metadati di un client API
  Dato un client API appartenente all'ente amministrato,
  quando l'amministratore ne modifica il nome o la descrizione,
  allora il sistema salva il metadato richiesto senza alterare identità, chiavi e associazioni.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva i metadati non modificati
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

    Examples:
      | campo       | valore                            |
      | nome        | Client API aggiornato             |
      | descrizione | Descrizione aggiornata del client |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_6] - Modifica congiunta di nome e descrizione di un client API
  Dato un client API appartenente all'ente amministrato,
  quando l'amministratore ne modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati lasciando invariate le proprietà operative del client.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client API aggiornato        |
      | descrizione | Nuova descrizione del client |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [CLIENT_METADATA_7] - Impossibilità per un amministratore M2M di modificare un client API
  Dato un client API appartenente all'ente dell'amministratore,
  quando l'amministratore M2M ne richiede la modifica dei metadati,
  allora il sistema rifiuta l'operazione e conserva integralmente i dati del client.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

    Examples:
      | campo       | valore                            |
      | nome        | Client API aggiornato             |
      | descrizione | Descrizione aggiornata del client |

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_METADATA_8] - Impossibilità per un amministratore M2M di modificare congiuntamente nome e descrizione di un client API
  Dato un client API appartenente all'ente dell'amministratore,
  quando l'amministratore M2M ne richiede la modifica congiunta di nome e descrizione,
  allora il sistema rifiuta l'intera operazione senza modificare il client.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client API aggiornato        |
      | descrizione | Nuova descrizione del client |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_9] - Conservazione dell'operatività del client Consumer dopo la modifica
  Dato un client Consumer con chiavi crittografiche e una finalità utilizzabile associata,
  quando l'amministratore ne modifica nome e descrizione,
  allora le chiavi e le associazioni rimangono invariate
  e il client continua a consentire l'ottenimento di un voucher.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin di PagoPA richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                        |
      | nome        | Client Consumer aggiornato    |
      | descrizione | Descrizione aggiornata        |
    Then il client "CL-1" conserva chiavi e associazioni
    And il client "CL-1" consente di ottenere un nuovo voucher

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [CLIENT_CONSUMER_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato un client consumer modificabile con metadati già valorizzati,
  quando l'utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "CL-1"
    Then il form della risorsa "CL-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "CL-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "CL-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [CLIENT_API_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato un client API modificabile con metadati già valorizzati,
  quando l'utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "CL-1"
    Then il form della risorsa "CL-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "CL-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "CL-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [CLIENT_CONSUMER_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When il form di modifica dei metadati di una risorsa "CL-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "CL-1" e richiede il salvataggio
    Then il form della risorsa "CL-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [CLIENT_API_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When il form di modifica dei metadati di una risorsa "CL-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "CL-1" e richiede il salvataggio
    Then il form della risorsa "CL-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [CLIENT_CONSUMER_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When il form di modifica dei metadati della risorsa "CL-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "CL-1"
    Then il form della risorsa "CL-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "CL-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [CLIENT_API_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When il form di modifica dei metadati della risorsa "CL-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "CL-1"
    Then il form della risorsa "CL-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "CL-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin da PagoPA richiede la modifica dei metadati della risorsa "CL-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "CL-1" viene rifiutata
    And la risorsa "CL-1" conserva metadati, stati, versioni e relazioni iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato dal Comune di Milano, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "CL-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "CL-1" viene rifiutata
    And la risorsa "CL-1" conserva metadati, stati, versioni e relazioni iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin di PagoPA aggiorna i metadati della risorsa "CL-1" con nuovi valori validi
    And un admin di PagoPA ripete gli stessi valori per la risorsa "CL-1"
    Then la ripetizione della modifica della risorsa "CL-1" viene accettata
    And la risorsa "CL-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato dal Comune di Milano, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano aggiorna i metadati della risorsa "CL-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "CL-1"
    Then la ripetizione della modifica della risorsa "CL-1" viene accettata
    And la risorsa "CL-1" conserva stati, versioni e relazioni
