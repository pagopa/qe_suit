Feature: Modifica dei metadati degli e-service
  Come erogatore proprietario
  Voglio modificare nome e descrizione di un e-service indipendentemente dallo stato dei descrittori
  Per mantenere aggiornate le informazioni senza modificare le versioni operative.

  @work-item:QA-19036
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

  @work-item:QA-19036
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

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ESERVICE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato un e-service modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "ES-1"
    Then il form della risorsa "ES-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "ES-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "ES-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ESERVICE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When il form di modifica dei metadati di una risorsa "ES-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "ES-1" e richiede il salvataggio
    Then il form della risorsa "ES-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ESERVICE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When il form di modifica dei metadati della risorsa "ES-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "ES-1"
    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "ES-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato un e-service modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "ES-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "ES-1" viene rifiutata
    And la risorsa "ES-1" conserva metadati, stati, versioni e relazioni iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_PROTECTION_2] - Rifiuto atomico di campi operativi non modificabili
  Dato una risorsa fuori bozza con configurazione operativa,
  quando l’attore invia un metadato valido insieme a un campo operativo vietato,
  allora il sistema rifiuta l’aggiornamento anche quando il campo operativo conserva il valore precedente.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "ES-1" insieme a un campo operativo vietato <variazione>
    Then la modifica della risorsa "ES-1" viene rifiutata
    And la risorsa "ES-1" conserva metadati, stati, versioni e relazioni iniziali

    Examples:
      | variazione       |
      | con valore nuovo |
      | con valore invariato |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un admin del Comune di Milano aggiorna i metadati della risorsa "ES-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "ES-1"
    Then la ripetizione della modifica della risorsa "ES-1" viene accettata
    And la risorsa "ES-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "ES-1" confermando gli altri valori correnti
    Then la risorsa "ES-1" presenta il metadato aggiornato
    And la risorsa "ES-1" conserva stati, versioni, relazioni e metadati invariati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_AUTHORIZATION_1] - Rispetto della titolarità e delle deleghe sulle risorse delegabili
  Dato una risorsa delegabile con delega nello stato previsto,
  quando un ente delegato richiede la modifica dei metadati,
  allora il sistema accetta soltanto le richieste compatibili con ruolo e delega.

    Given un e-service "ES-1" del Comune di Milano delegato in erogazione a PagoPA con delega <stato>
    When un admin di PagoPA richiede la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                             |
      | nome  | Servizio anagrafico aggiornato     |
    Then la modifica dell'e-service "ES-1" ha esito <esito>
    And in caso di rifiuto l'e-service "ES-1" conserva tutti i dati iniziali

    Examples:
      | stato               | esito     |
      | ACTIVE              | accettata |
      | WAITING_FOR_APPROVAL| rifiutata |
      | REJECTED            | rifiutata |
      | REVOKED             | rifiutata |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_1] - Rifiuto della rinomina in conflitto per un e-service
  Dato due risorse nello stesso ambito di unicità con nomi distinti,
  quando l’attore rinomina una risorsa usando il nome dell’altra e una descrizione valida,
  allora la modifica è respinta integralmente lasciando intatte entrambe le risorse.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
      | ES-2  | Comune di Milano | E-service Nome 2 |
    When un admin del Comune di Milano richiede di assegnare alla risorsa "ES-1" il nome di "ES-2" e una nuova descrizione
    Then la modifica della risorsa "ES-1" viene rifiutata
    And le risorse "ES-1" e "ES-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_2] - Unicità non aggirabile con differenze di maiuscole e minuscole per un e-service
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata senza applicare anche la descrizione valida.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
      | ES-2  | Comune di Milano | E-service Nome 2 |
    When un admin del Comune di Milano richiede per "ES-1" il nome di "ES-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica della risorsa "ES-1" viene rifiutata
    And le risorse "ES-1" e "ES-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_3] - Omonimia consentita tra e-service in ambiti distinti
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
      | ES-2  | Comune di Pozzallo | E-service Nome 2 |
    When un admin del Comune di Milano richiede di assegnare alla risorsa "ES-1" il nome di "ES-2"
    Then la rinomina della risorsa "ES-1" viene accettata
    And la risorsa "ES-2" conserva tutti i dati iniziali
