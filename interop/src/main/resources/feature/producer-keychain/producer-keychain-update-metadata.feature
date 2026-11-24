Feature: Modifica dei metadati dei portachiavi erogatore
  Come amministratore dell'ente e creatore di un portachiavi erogatore
  Voglio aggiornare il nome e la descrizione dei portachiavi
  preservato i dati non modificati e le associazioni al portachiavi.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [PRODUCER_KEYCHAIN_METADATA_1] - L'amministratore aggiorna i metadati del portachiavi che preserva i dati non modificati e le associazioni
  Un amministratore aggiorna i metadati di un portachiavi erogatore (solo nome, solo descrizione, nome e descrizione).
  I dati modificati vengono aggiornati, quelli non modificati vengono preservati,
  le associazioni al portachiavi vengono preservate.

    Given un portachiavi "K-1" creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un EService creato dal Comune di Milano con una richiesta di fruizione associata dal Comune di Pozzallo
    And un admin del Comune di Milano ha associato l'e-service al portachiavi "K-1"
    And i dati e le associazioni del portachiavi "K-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
    Then il portachiavi "K-1" conserva le associazioni e i dati di riferimento eccetto il nome

    When i dati e le associazioni del portachiavi "K-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | descrizione | prima descrizione |
    Then il portachiavi "K-1" conserva le associazioni e i dati di riferimento eccetto la descrizione

    When i dati e le associazioni del portachiavi "K-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
    Then il portachiavi "K-1" conserva le associazioni e i dati di riferimento eccetto il nome e la descrizione

#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario: [PRODUCER_KEYCHAIN_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
#  Dato una portachiavi erogatore modificabile con metadati già valorizzati,
#  quando l’utente apre il form modifica e salva nuovi valori,
#  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.
#
#    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
#    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "R-1"
#    Then il form della risorsa "R-1" mostra i metadati correnti e i campi previsti
#    When un admin del Comune di Milano modifica e salva i metadati della risorsa "R-1"
#    Then il sistema conferma il salvataggio e chiude il form
#    And il dettaglio e il form riaperto della risorsa "R-1" presentano i metadati aggiornati
#
#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario: [PRODUCER_KEYCHAIN_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
#  Dato un form di modifica aperto e compilato,
#  quando l’utente tenta di salvare un valore non valido,
#  allora il form resta aperto con i testi inseriti e indica il campo errato.
#
#    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
#    And il form di modifica dei metadati di una risorsa "K-1" del Comune di Milano aperto
#    When un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "K-1" e richiede il salvataggio
#    Then il form della risorsa "R-1" rimane aperto con i testi inseriti
#    And il campo non valido mostra il messaggio di errore previsto
#
#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario: [PRODUCER_KEYCHAIN_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
#  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
#  quando l’utente prova a salvare i propri valori,
#  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.
#
#    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
#    When il form di modifica dei metadati della risorsa "K-1" del Comune di Milano aperto con una modifica concorrente
#    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "K-1"
#    Then il form della risorsa "K-1" rimane aperto con i testi inseriti
#    And il sistema segnala il mancato salvataggio della risorsa "K-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [PRODUCER_KEYCHAIN_METADATA_PROTECTION_1] - Rifiuto atomico della richiesta di modifica di un portachiavi erogatore con un metadato valido insieme a un valore non ammesso
  Dato un portachiavi erogatore modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un portachiavi "K-1" creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un EService creato dal Comune di Milano con una richiesta di fruizione associata dal Comune di Pozzallo
    And un admin del Comune di Milano ha associato l'e-service al portachiavi "K-1"
    And i dati e le associazioni del portachiavi "K-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Milano tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | ab                |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione malformata
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un admin del Comune di Milano tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore     |
      | nome        | primo nome |
      | descrizione | xy         |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione malformata
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [PRODUCER_KEYCHAIN_METADATA_PROTECTION_2] - Ripetizione parziale e completa dei metadati correnti della richiesta di modifica di un portachiavi erogatore senza effetti operativi
  Dato un portachiavi erogatore modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con metadati già presenti,
  allora la richiesta ha successo senza modificare dati o relazioni.

    Given un portachiavi "K-1" creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un EService creato dal Comune di Milano con una richiesta di fruizione associata dal Comune di Pozzallo
    And un admin del Comune di Milano ha associato l'e-service al portachiavi "K-1"
    And un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati e le associazioni del portachiavi "K-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | nuova descrizione |
    Then il portachiavi "K-1" conserva le associazioni e i dati di riferimento eccetto la descrizione

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [PRODUCER_KEYCHAIN_UNIQUENESS_1] - Aggiornamento nome portachiavi erogatore con stesso nome di un altro portachiavi ammesso
  Dato un portachiavi con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per il portachiavi,
  allora la modifica viene applicata e vengono preservati i dati operativi.

    Given un portachiavi "K-1" creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin del Comune di Milano modifica i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
    And un portachiavi "K-2" creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un EService creato dal Comune di Milano con una richiesta di fruizione associata dal Comune di Pozzallo
    And un admin del Comune di Milano ha associato l'e-service al portachiavi "K-2"
    And i dati e le associazioni del portachiavi "K-2" allo stato attuale appuntati come riferimento
    When un admin del Comune di Milano modifica i metadati del portachiavi "K-2":
      | campo       | valore            |
      | nome        | primo nome        |
    Then il portachiavi "K-2" conserva le associazioni e i dati di riferimento eccetto il nome

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_VALIDATION_1] - Aggiornamento metadati di un portachiavi erogatore non ammesso ai ruoli esclusi o ad altri enti
  Dato un portachiavi erogatore, se un ruolo non autorizzato (api, api,security, security, support, reviewer, viewer)
  tenta di aggiornare i metadati, o un admin di un altro ente,
  allora la richiesta di modifica risulta vietata.

    Given un portachiavi "K-1" creato dal Comune di Comun Nuovo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un EService creato dal Comune di Comun Nuovo con una richiesta di fruizione associata dal Comune di Pozzallo
    And un admin del Comune di Comun Nuovo ha associato l'e-service al portachiavi "K-1"
    And i dati e le associazioni del portachiavi "K-1" allo stato attuale appuntati come riferimento
    When un api del Comune di Comun Nuovo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un api,security del Comune di Comun Nuovo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un security del Comune di Comun Nuovo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un support del Comune di Comun Nuovo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un reviewer del Comune di Comun Nuovo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un viewer del Comune di Comun Nuovo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento

    When un admin del Comune di Pozzallo tenta di modificare i metadati del portachiavi "K-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del portachiavi erogatore fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il portachiavi "K-1" conserva le associazioni e i dati di riferimento
