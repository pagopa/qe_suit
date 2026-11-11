Feature: Modifica dei metadati dei template e-service
  Come creatore di un template e-service
  Voglio modificare nome, descrizione e destinatari del template
  Per mantenere aggiornate le informazioni senza modificare versioni, stati e configurazioni operative.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_1] - Modifica di un singolo metadato indipendentemente dalle versioni
  Dato un template e-service con la configurazione di versioni prevista,
  quando il creatore ne modifica un singolo metadato,
  allora il template salva il valore richiesto e conserva gli altri metadati, le versioni e le configurazioni.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni   |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | <versioni> |
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service "TE-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il template e-service "TE-1" presenta i metadati richiesti
    And il template e-service "TE-1" conserva i metadati non modificati
    And il template e-service "TE-1" conserva numero, stato e configurazione delle versioni

    Examples:
      | versioni                 | campo       | valore                               |
      | assenti                  | nome        | Template anagrafico aggiornato       |
      | in bozza                 | descrizione | Descrizione aggiornata del template  |
      | pubblicata               | destinatari | Nuovo testo dei destinatari          |
      | sospesa                  | nome        | Template anagrafico aggiornato       |
      | soltanto deprecate       | descrizione | Descrizione aggiornata del template  |
      | pubblicata e nuova bozza | destinatari | Nuovo testo dei destinatari          |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_2] - Modifica congiunta dei metadati senza creare versioni
  Dato un template e-service con una versione pubblicata e una nuova versione in bozza,
  quando il creatore modifica nome, descrizione e destinatari,
  allora i nuovi metadati vengono salvati senza creare versioni o alterare le configurazioni operative.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni                 |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | pubblicata e nuova bozza |
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service "TE-1":
      | campo       | valore                              |
      | nome        | Template anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del template |
      | destinatari | Nuovo testo dei destinatari         |
    Then il template e-service "TE-1" presenta i metadati richiesti
    And il template e-service "TE-1" conserva numero, stato e configurazione delle versioni

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ESERVICE_TEMPLATE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato un e-service template modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni                 |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | pubblicata e nuova bozza |
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "TE-1"
    Then il form della risorsa "TE-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "TE-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "TE-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ESERVICE_TEMPLATE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni                 |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | pubblicata e nuova bozza |
    When il form di modifica dei metadati di una risorsa "TE-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "TE-1" e richiede il salvataggio
    Then il form della risorsa "TE-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ESERVICE_TEMPLATE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni                 |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | pubblicata e nuova bozza |
    When il form di modifica dei metadati della risorsa "TE-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "TE-1"
    Then il form della risorsa "TE-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "TE-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni   |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | pubblicata |
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "TE-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "TE-1" viene rifiutata
    And la risorsa "TE-1" conserva metadati, stati, versioni e relazioni iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_PROTECTION_2] - Rifiuto atomico di campi operativi non modificabili
  Dato una risorsa fuori bozza con configurazione operativa,
  quando l’attore invia un metadato valido insieme a un campo operativo vietato,
  allora il sistema rifiuta l’aggiornamento anche quando il campo operativo conserva il valore precedente.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni   |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | pubblicata |
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "TE-1" insieme a un campo operativo vietato <variazione>
    Then la modifica della risorsa "TE-1" viene rifiutata
    And la risorsa "TE-1" conserva metadati, stati, versioni e relazioni iniziali

    Examples:
      | variazione           |
      | con valore nuovo     |
      | con valore invariato |
