Feature: Modifica dei metadati dei template finalità
  Come creatore di un template finalità
  Voglio modificare titolo, descrizione e destinatari
  preservando lo stato del template e le finalità già derivate.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_1] - Aggiornamento dei metadati nei diversi stati del template
  Dato un template finalità nello stato previsto,
  quando il creatore aggiorna un metadato,
  allora il sistema salva il nuovo valore senza modificare stato o configurazione operativa.

    Given un template finalità creato dal Comune di Comun Nuovo in stato <stato>
    And i dati del template finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Comun Nuovo modifica i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
    Then il template finalità conserva lo stato e i dati di riferimento eccetto il nome

    When i dati del template finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Comun Nuovo modifica i metadati del template finalità:
      | campo       | valore unico      |
      | descrizione | prima descrizione |
    Then il template finalità conserva lo stato e i dati di riferimento eccetto la descrizione

    When i dati del template finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Comun Nuovo modifica i metadati del template finalità:
      | campo       | valore unico      |
      | destinatari | primi destinatari |
    Then il template finalità conserva lo stato e i dati di riferimento eccetto i destinatari

    When i dati del template finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Comun Nuovo modifica i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
      | destinatari | nuovi destinatari |
    Then il template finalità conserva lo stato e i dati di riferimento eccetto nome, descrizione e destinatari

    Examples:
      | stato      |
      | in bozza   |
      | pubblicato |
      | sospeso    |
      | archiviato |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [PURPOSE_TEMPLATE_METADATA_2] - Aggiornamento metadati template preserva dati delle sue finalità istanziate
  Dato un template finalità pubblicato e delle finalità istanziate in stato bozza e attiva,
  create dallo stesso e un altro ente rispetto al creatore del template finalità,
  modificare tutti i metadati del template, preserva lo stato e i dati delle finalità istanziate.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service Test |
    And l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    And un template finalità creato dal Comune di Pozzallo in stato pubblicato
    And una finalità pubblicata dal Comune di Pozzallo dal template finalità
    And i dati della finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
      | destinatari | nuovi destinatari |
    Then la finalità conserva lo stato e i dati di riferimento

    When l'e-service "ES-1" del Comune di Milano sottoscritto dal Comune di Comun Nuovo con una richiesta di fruizione attiva
    And una finalità in bozza creata dal Comune di Comun Nuovo dal template finalità
    And i dati della finalità allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
      | destinatari | nuovi destinatari |
    Then un admin del Comune di Comun Nuovo
    And la finalità conserva lo stato e i dati di riferimento

#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario: [PURPOSE_TEMPLATE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
#  Dato un template finalità modificabile con metadati già valorizzati,
#  quando l’utente apre il form modifica e salva nuovi valori,
#  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.
#
#    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
#      | alias | stato  |
#      | P-1   | DRAFT  |
#    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "TP-1"
#    Then il form della risorsa "TP-1" mostra i metadati correnti e i campi previsti
#    When un admin del Comune di Milano modifica e salva i metadati della risorsa "TP-1"
#    Then il sistema conferma il salvataggio e chiude il form
#    And il dettaglio e il form riaperto della risorsa "TP-1" presentano i metadati aggiornati
#
#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario: [PURPOSE_TEMPLATE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
#  Dato un form di modifica aperto e compilato,
#  quando l’utente tenta di salvare un valore non valido,
#  allora il form resta aperto con i testi inseriti e indica il campo errato.
#
#    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
#      | alias | stato  |
#      | P-1   | DRAFT  |
#    When il form di modifica dei metadati di una risorsa "TP-1" del Comune di Milano aperto
#    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "TP-1" e richiede il salvataggio
#    Then il form della risorsa "TP-1" rimane aperto con i testi inseriti
#    And il campo non valido mostra il messaggio di errore previsto
#
#  @work-item:QA-19036
#  @channel:Given=BFF,When=WEB,Then=WEB
#  Scenario: [PURPOSE_TEMPLATE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
#  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
#  quando l’utente prova a salvare i propri valori,
#  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.
#
#    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
#      | alias | stato  |
#      | P-1   | DRAFT  |
#    When il form di modifica dei metadati della risorsa "TP-1" del Comune di Milano aperto con una modifica concorrente
#    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "TP-1"
#    Then il form della risorsa "TP-1" rimane aperto con i testi inseriti
#    And il sistema segnala il mancato salvataggio della risorsa "TP-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_PROTECTION_1] - Rifiuto atomico della richiesta di modifica di un template finalità con un metadato valido insieme a un valore non ammesso
  Dato un template finalità modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un template finalità creato dal Comune di Comun Nuovo in stato <stato>
    And i dati del template finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore            |
      | nome        | ab                |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione malformata
    And il template finalità conserva lo stato e i dati di riferimento

    When un admin del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore     |
      | nome        | primo nome |
      | descrizione | xy         |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione malformata
    And il template finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato      |
      | in bozza   |
      | pubblicato |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_PROTECTION_2] - Ripetizione parziale dei metadati correnti della richiesta di modifica di un template finalità senza effetti operativi
  Data un template finalità modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con metadati già presenti,
  allora la richiesta ha successo senza modificare versioni, stati o relazioni.

    Given un template finalità creato dal Comune di Comun Nuovo in stato <stato>
    And un admin del Comune di Comun Nuovo modifica i metadati del template finalità:
      | campo       | valore                |
      | descrizione | descrizione esistente |
    And i dati del template finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Comun Nuovo modifica i metadati del template finalità:
      | campo       | valore                |
      | descrizione | descrizione esistente |
    Then il template finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato      |
      | in bozza   |
      | pubblicato |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_UNIQUENESS_1] - Aggiornamento nome template finalità con stesso nome di un altro template rifiutato

    Given un template finalità creato dal Comune di Comun Nuovo in stato <stato>
    And un admin del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore         |
      | nome        | nome esistente |
    And un template finalità creato dal Comune di Comun Nuovo in stato <stato>
    And i dati del template finalità allo stato attuale appuntati come riferimento
    When un admin del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore         |
      | nome        | nome esistente |
    Then la richiesta di modifica dei metadati del template finalità fallisce per conflitto di risorsa
    And il template finalità conserva lo stato e i dati di riferimento

    When un admin del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore         |
      | nome        | NOME Esistente |
    Then la richiesta di modifica dei metadati del template finalità fallisce per conflitto di risorsa
    And il template finalità conserva lo stato e i dati di riferimento

    Examples:
      | stato      |
      | in bozza   |
      | pubblicato |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_VALIDATION_1] - Aggiornamento metadati di un template finalità non ammesso ai ruoli esclusi o ad altri enti
  Dato un template finalità, se un ruolo non autorizzato (api, api,security, security, support, reviewer, viewer)
  tenta di aggiornare i metadati, o un admin di un altro ente,
  allora la richiesta di modifica risulta vietata.

    Given un template finalità creato dal Comune di Comun Nuovo in stato pubblicato
    And i dati del template finalità allo stato attuale appuntati come riferimento
    When un api del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il template finalità conserva lo stato e i dati di riferimento

    When un api,security del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il template finalità conserva lo stato e i dati di riferimento

    When un security del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il template finalità conserva lo stato e i dati di riferimento

    When un support del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il template finalità conserva lo stato e i dati di riferimento

    When un reviewer del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il template finalità conserva lo stato e i dati di riferimento

    When un viewer del Comune di Comun Nuovo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il template finalità conserva lo stato e i dati di riferimento

    When un admin del Comune di Pozzallo tenta di modificare i metadati del template finalità:
      | campo       | valore unico      |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then un admin del Comune di Comun Nuovo
    And la richiesta di modifica dei metadati del template finalità fallisce come operazione vietata
