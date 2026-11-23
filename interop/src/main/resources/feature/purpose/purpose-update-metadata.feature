Feature: Modifica dei metadati delle finalità
  Come creatore di una finalità
  Voglio aggiornare titolo e descrizione di una finalità ordinaria o inversa
  preservando lo stato e altri dati della finalità.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_1] - Il fruitore aggiorna i metadati della finalità che preserva lo stato e i dati non modificati
  Data una finalità non derivata da template per un e-service in modalità erogazione e nei vari possibili stati,
  quando il fruitore ne aggiorna titolo o descrizione,
  allora la modifica viene applicata senza alterare lo stato e i dati non modificati.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato <stato> associate dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    Then la finalità conserva lo stato e i dati di riferimento eccetto il nome

    When i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | descrizione | prima descrizione |
    Then la finalità conserva lo stato e i dati di riferimento eccetto la descrizione

    When i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
    Then la finalità conserva lo stato e i dati di riferimento eccetto il nome e la descrizione

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |
      | SUSPENDED            |
      | ARCHIVED             |
      | WAITING_FOR_APPROVAL |
      | REJECTED             |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_2] - Il fruitore aggiorna i metadati della finalità inversa che preserva lo stato e i dati non modificati
  Data una finalità inversa non derivata da template per un e-service in modalità ricezione e nei vari possibili stati,
  quando il fruitore ne aggiorna titolo o descrizione,
  allora la modifica viene applicata senza alterare lo stato e i dati non modificati.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  |
      | ES-1  | Comune di Milano | E-service Test | ricezione |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    Then la finalità conserva lo stato e i dati di riferimento eccetto il nome

    When i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | descrizione | prima descrizione |
    Then la finalità conserva lo stato e i dati di riferimento eccetto la descrizione

    When i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
    Then la finalità conserva lo stato e i dati di riferimento eccetto il nome e la descrizione

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |
      | SUSPENDED            |
      | ARCHIVED             |

#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario Outline: [PURPOSE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
#  Dato una finalità modificabile con metadati già valorizzati,
#  quando l’utente apre il form modifica e salva nuovi valori,
#  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.
#
#    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
#    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "R-1"
#    Then il form della risorsa "R-1" mostra i metadati correnti e i campi previsti
#    When un admin del Comune di Milano modifica e salva i metadati della risorsa "R-1"
#    Then il sistema conferma il salvataggio e chiude il form
#    And il dettaglio e il form riaperto della risorsa "R-1" presentano i metadati aggiornati
#
#    Examples:
#      | modalità |
#      | ordinaria |
#      | inversa   |
#
#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario Outline: [PURPOSE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
#  Dato un form di modifica aperto e compilato,
#  quando l’utente tenta di salvare un valore non valido,
#  allora il form resta aperto con i testi inseriti e indica il campo errato.
#
#    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
#    When il form di modifica dei metadati di una finalità "P-1" del Comune di Milano aperto
#    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "R-1" e richiede il salvataggio
#    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
#    And il campo non valido mostra il messaggio di errore previsto
#
#    Examples:
#      | modalità |
#      | ordinaria |
#      | inversa   |
#
#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario Outline: [PURPOSE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
#  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
#  quando l’utente prova a salvare i propri valori,
#  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.
#
#    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
#    When il form di modifica dei metadati della risorsa "P-1" del Comune di Milano aperto con una modifica concorrente
#    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "P-1"
#    Then il form della risorsa "P-1" rimane aperto con i testi inseriti
#    And il sistema segnala il mancato salvataggio della risorsa "P-1"
#
#    Examples:
#      | modalità |
#      | ordinaria |
#      | inversa   |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_1A] - Rifiuto atomico della richiesta di modifica di una finalità con un metadato valido insieme a un valore non ammesso
  Data una finalità modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato <stato> associate dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | %empty            |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione malformata
    And la finalità conserva lo stato e i dati di riferimento

    When i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | %empty            |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione malformata
    And la finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_1B] - Rifiuto atomico della richiesta di modifica di una finalità inversa con un metadato valido insieme a un valore non ammesso
  Data una finalità inversa modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  |
      | ES-1  | Comune di Milano | E-service Test | ricezione |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | %empty            |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione malformata
    And la finalità conserva lo stato e i dati di riferimento

    When i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | %empty            |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione malformata
    And la finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_2A] - Ripetizione parziale e completa dei metadati correnti della richiesta di modifica di una finalità senza effetti operativi
  Data una finalità modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con metadati già presenti,
  allora la richiesta ha successo senza modificare versioni stati o relazioni.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato <stato> associate dal Comune di Pozzallo
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la finalità conserva lo stato e i dati di riferimento

    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | nuova descrizione |
    Then la finalità conserva lo stato e i dati di riferimento eccetto la descrizione

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_PROTECTION_2B] - Ripetizione parziale e completa dei metadati correnti della richiesta di modifica di una finalità inversa senza effetti operativi
  Data una finalità inversa modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con metadati già presenti,
  allora la richiesta ha successo senza modificare versioni stati o relazioni.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  |
      | ES-1  | Comune di Milano | E-service Test | ricezione |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la finalità conserva lo stato e i dati di riferimento

    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | nuova descrizione |
    Then la finalità conserva lo stato e i dati di riferimento eccetto la descrizione

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_UNIQUENESS_1A] - Aggiornamento nome finalità con stesso nome di un'altra finalità per stesso e-service rifiutato
  Dati un solo e-service e una finalità con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per una finalità,
  allora la modifica viene rifiutata per conflitto di risorsa e vengono preservati i dati operativi.

    Given un e-service in stato pubblicato, creato dal Comune di Milano, con soglie base:
      | consumerThreshold | 10 |
      | totalThreshold    | 20 |
    And l'e-service del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato <stato> associata dal Comune di Pozzallo
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    And l'e-service con una finalità in stato <stato> associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    Then la richiesta di modifica dei metadati della finalità fallisce per conflitto di risorsa
    And la finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_UNIQUENESS_1B] - Aggiornamento nome finalità inversa con stesso nome di un'altra finalità per stesso e-service rifiutato
  Dati un solo e-service e una finalità inversa con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per una finalità,
  allora la modifica viene rifiutata per conflitto di risorsa e vengono preservati i dati operativi.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  | soglia fruitore | soglia totale |
      | ES-1  | Comune di Milano | E-service Test | ricezione | 10              | 20            |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    Then la richiesta di modifica dei metadati della finalità fallisce per conflitto di risorsa
    And la finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_UNIQUENESS_2A] - Aggiornamento nome finalità con stesso nome di un'altra finalità per diversi e-service ammessa
  Dati un e-service e una finalità con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per una finalità di un altro e-service,
  allora la modifica viene applicata e vengono preservati i dati operativi.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service Test |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato <stato> associata dal Comune di Pozzallo
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-2  | Comune di Milano | E-service Test |
    And l'e-service "ES-2" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità in stato <stato> associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    Then la finalità conserva lo stato e i dati di riferimento eccetto il nome

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_METADATA_UNIQUENESS_2B] - Aggiornamento nome finalità inversa con stesso nome di un'altra finalità per diversi e-service ammessa
  Dati un e-service e una finalità inversa con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per una finalità di un altro e-service,
  allora la modifica viene applicata e vengono preservati i dati operativi.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  |
      | ES-1  | Comune di Milano | E-service Test | ricezione |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  |
      | ES-2  | Comune di Milano | E-service Test | ricezione |
    And l'e-service "ES-2" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato <stato> associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
    Then la finalità conserva lo stato e i dati di riferimento eccetto il nome

    Examples:
      | stato                |
      | DRAFT                |
      | ACTIVE               |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_VALIDATION_1A] - Aggiornamento metadati di una finalità non ammesso ai ruoli esclusi o ad altri enti
  Data una finalità, se un ruolo non autorizzato (api, api,security, security, support, reviewer, viewer)
  tenta di aggiornare i metadati, o un admin di un altro ente,
  allora la richiesta di modifica risulta vietata.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato ACTIVE associate dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Comun Nuovo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata

    When un api del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un api,security del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un security del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un support del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un reviewer del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un viewer del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_METADATA_VALIDATION_1B] - Aggiornamento metadati di una finalità inversa non ammesso ai ruoli esclusi o ad altri enti
  Data una finalità inversa, se un ruolo non autorizzato (api, api,security, security, support, reviewer, viewer)
  tenta di aggiornare i metadati, o un admin di un altro ente,
  allora la richiesta di modifica risulta vietata.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           | modalità  |
      | ES-1  | Comune di Milano | E-service Test | ricezione |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And l'e-service con una finalità inversa in stato ACTIVE associata dal Comune di Pozzallo
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Comun Nuovo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata

    When un api del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un api,security del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un security del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un support del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un reviewer del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento

    When un viewer del Comune di Pozzallo tenta di modificare i metadati della finalità:
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati della finalità fallisce come operazione vietata
    And la finalità conserva lo stato e i dati di riferimento
