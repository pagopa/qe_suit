Feature: Modifica dei metadati degli e-service
  Come erogatore proprietario
  Voglio modificare nome e descrizione di un e-service indipendentemente dallo stato dei descrittori
  Per mantenere aggiornate le informazioni senza modificare le versioni operative.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_1a] - Modifica di un singolo metadato indipendente dallo stato del descrittore
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
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

    Examples:
      | stato      | campo       | valore                              |
      | in bozza   | nome        | Servizio anagrafico aggiornato      |
      | pubblicata | descrizione | Descrizione aggiornata del servizio |
      | sospesa    | nome        | Servizio anagrafico aggiornato      |
      | deprecata  | descrizione | Descrizione aggiornata del servizio |
      # Non è possibile archiviare un e-service in tempi accettabili.
      # | archiviata | nome        | Servizio anagrafico aggiornato      |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_1a_M2M] - Modifica di un singolo metadato indipendente dallo stato del descrittore
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <stato>
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

    Examples:
      | stato      | campo       | valore                              |
      | in bozza   | nome        | Servizio anagrafico aggiornato      |
      | pubblicata | descrizione | Descrizione aggiornata del servizio |
      | sospesa    | nome        | Servizio anagrafico aggiornato      |
      | deprecata  | descrizione | Descrizione aggiornata del servizio |
      # Non è possibile archiviare un e-service in tempi accettabili.
      # | archiviata | nome        | Servizio anagrafico aggiornato      |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_1b] Modifica di un singolo metadato con lo stato del descrittore in attesa di approvazione.
  Dato un e-service non derivato da template con un descrittore nello stato in attesa di approvazione,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 1 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    When un admin del Comune di Pozzallo richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_1b_M2M] Modifica di un singolo metadato con lo stato del descrittore in attesa di approvazione.
  Dato un e-service non derivato da template con un descrittore nello stato in attesa di approvazione,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 1 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Pozzallo richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_1c] - Modifica di un singolo metadato con lo stato del descrittore in archiviazione
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_1c_M2M] - Modifica di un singolo metadato con lo stato del descrittore in archiviazione
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_1d] - Modifica di un singolo metadato co lo stato del descrittore in archiviazione sospesa
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    And la versione 1 dell'e-service "ES-1" del Comune di Milano in archiviazione sospesa
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_1d_M2M] - Modifica di un singolo metadato co lo stato del descrittore in archiviazione sospesa
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore ne modifica soltanto il nome o la descrizione,
  allora il sistema aggiorna il valore richiesto conservando gli altri metadati e lo stato dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    And la versione 1 dell'e-service "ES-1" del Comune di Milano in archiviazione sospesa
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_2a] - Modifica congiunta dei metadati indipendente dallo stato del descrittore
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
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

    Examples:
      | stato      |
      | in bozza   |
      | pubblicata |
      | sospesa    |
      | deprecata  |
      # Non è possibile archiviare un e-service in tempi accettabili.
      # | archiviata |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_2a_M2M] - Modifica congiunta dei metadati indipendente dallo stato del descrittore
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <stato>
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

    Examples:
      | stato      |
      | in bozza   |
      | pubblicata |
      | sospesa    |
      | deprecata  |
      # Non è possibile archiviare un e-service in tempi accettabili.
      # | archiviata |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_2b] - Modifica congiunta dei metadati con lo stato del descrittore in attesa di approvazione.
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 1 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    When un admin del Comune di Pozzallo richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_2b_M2M] - Modifica congiunta dei metadati con lo stato del descrittore in attesa di approvazione.
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 1 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Pozzallo richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_2c] - Modifica congiunta dei metadati con lo stato del descrittore in archiviazione.
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_2c_M2M] - Modifica congiunta dei metadati con lo stato del descrittore in archiviazione.
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_2d] - Modifica congiunta dei metadati con lo stato del descrittore in archiviazione sospesa.
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    And la versione 1 dell'e-service "ES-1" del Comune di Milano in archiviazione sospesa
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_2d_M2M] - Modifica congiunta dei metadati con lo stato del descrittore in archiviazione sospesa.
  Dato un e-service non derivato da template con un descrittore nello stato previsto,
  quando l'erogatore modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati senza alterare lo stato, le versioni e le configurazioni dei descrittori.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano interamente in archiviazione
    And la versione 1 dell'e-service "ES-1" del Comune di Milano in archiviazione sospesa
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Servizio anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del servizio |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  # TODO Questo scenario sarà scomposto in due integration test:
  # - il primo verifica la mappatura della UI con i dati ricevuti da BFF
  # - il secondo verifica i dati inviati all'endpoint BFF
  #  @work-item:QA-19036
  #  @channel:Given=BFF,When=WEB,Then=WEB
  #  Scenario: [ESERVICE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  #  Dato un e-service modificabile con metadati già valorizzati,
  #  quando l’utente apre il form modifica e salva nuovi valori,
  #  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.
  #    Given i seguenti e-service in bozza:
  #      | alias | erogatore        | nome                   |
  #      | ES-1  | Comune di Milano | E-service Tempo Attivo |
  #    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "ES-1"
  #    Then il form della risorsa "ES-1" mostra i metadati correnti e i campi previsti
  #    When un admin del Comune di Milano modifica e salva i metadati della risorsa "ES-1"
  #    Then il sistema conferma il salvataggio e chiude il form
  #    And il dettaglio e il form riaperto della risorsa "ES-1" presentano i metadati aggiornati

  #  @work-item:QA-19036
  #  @channel:Given=BFF,When=WEB,Then=WEB
  #  Scenario: [ESERVICE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  #  Dato un form di modifica aperto e compilato,
  #  quando l’utente tenta di salvare un valore non valido,
  #  allora il form resta aperto con i testi inseriti e indica il campo errato.
  #
  #    Given i seguenti e-service in bozza:
  #      | alias | erogatore        | nome                   |
  #      | ES-1  | Comune di Milano | E-service Tempo Attivo |
  #    When il form di modifica dei metadati di una risorsa "ES-1" del Comune di Milano aperto
  #    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "ES-1" e richiede il salvataggio
  #    Then il form della risorsa "ES-1" rimane aperto con i testi inseriti
  #    And il campo non valido mostra il messaggio di errore previsto
  #
  #  @work-item:QA-19036
  #  @channel:Given=BFF,When=WEB,Then=WEB
  #  Scenario: [ESERVICE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  #  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  #  quando l’utente prova a salvare i propri valori,
  #  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.
  #
  #    Given i seguenti e-service in bozza:
  #      | alias | erogatore        | nome                   |
  #      | ES-1  | Comune di Milano | E-service Tempo Attivo |
  #    When il form di modifica dei metadati della risorsa "ES-1" del Comune di Milano aperto con una modifica concorrente
  #    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "ES-1"
  #    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
  #    And il sistema segnala il mancato salvataggio della risorsa "ES-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_PROTECTION_1] - Rifiuto della richiesta di modifica in presenza di metadati non ammessi
  Dato un e-service modificabile da un attore autorizzato,
  quando l'attore invia una modifica con un metadato valido e un altro non ammesso,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un admin del Comune di Milano tenta la modifica dei metadati dell'e-service "ES-1":
      | campo    | valore    |
      | <campo1> | <valore_ammesso> |
      | <campo2> | <valore_non_ammesso> |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per <errore>

    Examples:
      | campo1 | valore_ammesso                 | campo2      | valore_non_ammesso | errore                       |
      | nome   | Servizio anagrafico aggiornato | descrizione | desc               | INVALID_DATA                 |
      | nome   | Servizio anagrafico aggiornato | technology  | REST               | OUT_OF_DRAFT_OPERATION_FIELD |
      | nome   | Servizio anagrafico aggiornato | technology  | %last-used%        | OUT_OF_DRAFT_OPERATION_FIELD |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_PROTECTION_1_M2M] - Rifiuto della richiesta di modifica in presenza di metadati non ammessi
  Dato un e-service modificabile da un attore autorizzato,
  quando l'attore invia una modifica con un metadato valido e un altro non ammesso,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano tenta la modifica dei metadati dell'e-service "ES-1":
      | campo    | valore    |
      | <campo1> | <valore_ammesso> |
      | <campo2> | <valore_non_ammesso> |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per <errore>

    Examples:
      | campo1 | valore_ammesso                 | campo2      | valore_non_ammesso | errore                       |
      | nome   | Servizio anagrafico aggiornato | descrizione | desc               | INVALID_DATA                 |
      | nome   | Servizio anagrafico aggiornato | technology  | REST               | OUT_OF_DRAFT_OPERATION_FIELD |
      | nome   | Servizio anagrafico aggiornato | technology  | %last-used%        | OUT_OF_DRAFT_OPERATION_FIELD |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_PROTECTION_2] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore            |
      | nome        | Nuovo nome        |
      | descrizione | Nuova descrizione |
    And un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore      |
      | nome        | %last-used% |
      | descrizione | %last-used% |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_PROTECTION_2_M2M] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore            |
      | nome        | Nuovo nome        |
      | descrizione | Nuova descrizione |
    And un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore      |
      | nome        | %last-used% |
      | descrizione | %last-used% |
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_AUTHORIZATION_1] - Rifiuto della modifica dei metadati per ruolo non autorizzato.
  Dato un e-service pubblicato,
  quando un operatore con ruolo non autorizzato o che non possiede la titolarità tenta la modifica dei metadati,
  allora il sistema non accetta le richiesta.

    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome                   |
      | ES-1  | Comune di Pozzallo | E-service Tempo Attivo |
    When un <ruolo> di <ente> tenta la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                         |
      | nome  | Servizio anagrafico aggiornato |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per UNAUTHORIZED

    Examples:
      | ente               | ruolo    |
      | Comune di Pozzallo | SECURITY |
      | Comune di Pozzallo | SUPPORT  |
      | Comune di Pozzallo | REVIEWER |
      | Comune di Pozzallo | VIEWER   |
      | Comune di Milano   | ADMIN    |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_AUTHORIZATION_1_M2M] - Rifiuto della modifica dei metadati per ruolo non autorizzato.
  Dato un e-service pubblicato,
  quando un operatore con ruolo non autorizzato o che non possiede la titolarità tenta la modifica dei metadati,
  allora il sistema non accetta le richiesta.

    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome                   |
      | ES-1  | Comune di Pozzallo | E-service Tempo Attivo |
    When un sistema informatico autenticato con ruolo <ruolo> per conto del <ente> tenta la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                         |
      | nome  | Servizio anagrafico aggiornato |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per UNAUTHORIZED

    Examples:
      | ente               | ruolo     |
      | Comune di Pozzallo | M2M       |
      | Comune di Milano   | M2M_ADMIN |
      | Comune di Milano   | M2M       |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_AUTHORIZATION_2] - Rispetto della titolarità e delle deleghe sulle risorse delegabili
  Dato una risorsa delegabile con delega nello stato previsto,
  quando un ente delegato richiede la modifica dei metadati,
  allora il sistema accetta soltanto le richieste compatibili con ruolo e delega.
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega <stato>
    When un admin del Comune di Pozzallo tenta la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                         |
      | nome  | Servizio anagrafico aggiornato |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per UNAUTHORIZED

    Examples:
      | stato                     |
      | in attesa di accettazione |
      | rifiutata                 |
      | revocata                  |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_AUTHORIZATION_2_M2M] - Rispetto della titolarità e delle deleghe sulle risorse delegabili
  Dato una risorsa delegabile con delega nello stato previsto,
  quando un ente delegato richiede la modifica dei metadati,
  allora il sistema accetta soltanto le richieste compatibili con ruolo e delega.
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Tempo Attivo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega <stato>
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Pozzallo tenta la modifica dei metadati dell'e-service "ES-1":
      | campo | valore                         |
      | nome  | Servizio anagrafico aggiornato |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per UNAUTHORIZED

    Examples:
      | stato                     |
      | in attesa di accettazione |
      | rifiutata                 |
      | revocata                  |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_1] - Unicità non aggirabile con differenze di maiuscole e minuscole per un e-service
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
      | ES-2  | Comune di Milano | E-service Nome 2 |
    When un admin del Comune di Milano richiede per l'e-service "ES-1" il nome di "ES-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per CONFLICT

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_1_M2M] - Unicità non aggirabile con differenze di maiuscole e minuscole per un e-service
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
      | ES-2  | Comune di Milano | E-service Nome 2 |
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede per l'e-service "ES-1" il nome di "ES-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per CONFLICT

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_2] - Omonimia consentita tra e-service in ambiti distinti
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome             |
      | ES-1  | Comune di Milano   | E-service Nome 1 |
      | ES-2  | Comune di Pozzallo | E-service Nome 2 |
    And la versione 1 dell'e-service "ES-2" del Comune di Pozzallo pubblicata
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un admin del Comune di Milano richiede di assegnare all'e-service "ES-1" il nome di "ES-2"
    Then la rinomina della risorsa "ES-1" viene accettata e ha lo stesso nome di "ES-2"

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_UNIQUENESS_2_M2M] - Omonimia consentita tra e-service in ambiti distinti
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome             |
      | ES-1  | Comune di Milano   | E-service Nome 1 |
      | ES-2  | Comune di Pozzallo | E-service Nome 2 |
    And la versione 1 dell'e-service "ES-2" del Comune di Pozzallo pubblicata
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede di assegnare all'e-service "ES-1" il nome di "ES-2"
    Then la rinomina della risorsa "ES-1" viene accettata e ha lo stesso nome di "ES-2"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_VALIDATION_1] - Rifiuto di modifica dei metadati per le richieste che superano i limiti di lunghezza consentiti.
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo oltre la dimensione minima o massima,
  allora il sistema rifiuta le modifiche parziali.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
    When un admin del Comune di Milano tenta la modifica del campo "<campo>" dell'e-service "ES-1" con un valore di <lunghezza> caratteri
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per INVALID_DATA

    Examples:
      | campo       | lunghezza |
      | nome        | 4         |
      | nome        | 61        |
      | descrizione | 9         |
      | descrizione | 401       |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_VALIDATION_1_M2M] - Rifiuto di modifica dei metadati per le richieste che superano i limiti di lunghezza consentiti.
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo oltre la dimensione minima o massima,
  allora il sistema rifiuta le modifiche parziali.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Pozzallo tenta la modifica del campo "<campo>" dell'e-service "ES-1" con un valore di <lunghezza> caratteri
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per INVALID_DATA

    Examples:
      | campo       | lunghezza |
      | nome        | 4         |
      | nome        | 61        |
      | descrizione | 9         |
      | descrizione | 401       |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_VALIDATION_2] - Le modifiche ai metadati vengono accettate per le richieste che rispettano i limiti di lunghezza consentiti.
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
    When un admin del Comune di Milano richiede la modifica del campo "<campo>" dell'e-service "ES-1" con un valore di <lunghezza> caratteri
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

    Examples:
      | campo       | lunghezza |
      | nome        | 5         |
      | nome        | 60        |
      | descrizione | 10        |
      | descrizione | 400       |

  @work-item:QA-19036 @m2m
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [ESERVICE_METADATA_VALIDATION_2_M2M] - Le modifiche ai metadati vengono accettate per le richieste che rispettano i limiti di lunghezza consentiti.
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica del campo "<campo>" dell'e-service "ES-1" con un valore di <lunghezza> caratteri
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori

    Examples:
      | campo       | lunghezza |
      | nome        | 5         |
      | nome        | 60        |
      | descrizione | 10        |
      | descrizione | 400       |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_METADATA_VALIDATION_3] - Rifiuto di valori non ammessi nei metadati di un e-service
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
    When un admin del Comune di Milano tenta la modifica dell'e-service "ES-1" con un payload vuoto
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per INVALID_DATA

  @work-item:QA-19036 @mem
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [ESERVICE_METADATA_VALIDATION_3_M2M] - Rifiuto di valori non ammessi nei metadati di un e-service
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Nome 1 |
    When un sistema informatico autenticato con ruolo M2M_ADMIN per conto del Comune di Milano richiede la modifica dell'e-service "ES-1" con un payload vuoto
    Then l'e-service "ES-1" presenta i metadati richiesti e conserva quelli non modificati, le versioni, gli stati e le configurazioni dei descrittori
