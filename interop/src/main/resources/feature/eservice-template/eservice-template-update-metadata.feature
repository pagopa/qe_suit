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

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | bozza    |
    When un admin del Comune di Milano aggiorna i metadati della risorsa "TE-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "TE-1"
    Then la ripetizione della modifica della risorsa "TE-1" viene accettata
    And la risorsa "TE-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | bozza    |
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "TE-1" confermando gli altri valori correnti
    Then la risorsa "TE-1" presenta il metadato aggiornato
    And la risorsa "TE-1" conserva stati, versioni, relazioni e metadati invariati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_PROTECTION_5] - Impossibilità di sovrascrivere i metadati di un e-service derivato
  Dato un e-service ottenuto da un template,
  quando l’erogatore tenta di modificarne direttamente i metadati ereditati,
  allora la modifica è respinta senza cambiare il template o la sua istanza.

    Given un e-service "ES-1" derivato dal template e-service "TE-1" del Comune di Milano
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "ES-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata
    And l'e-service "ES-1" e il template "TE-1" conservano i metadati iniziali

    Examples:
      | campo       | valore                                 |
      | nome        | Nome aggiornato dell'istanza           |
      | descrizione | Descrizione aggiornata dell'istanza    |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_UNIQUENESS_1] - Rifiuto della rinomina in conflitto per un template e-service
  Dato due risorse nello stesso ambito di unicità con nomi distinti,
  quando l’attore rinomina una risorsa usando il nome dell’altra e una descrizione valida,
  allora la modifica è respinta integralmente lasciando intatte entrambe le risorse.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | bozza    |
      | TE-2  | Comune di Milano | Template anagrafico   | REST       | bozza    |
    When un admin del Comune di Milano richiede di assegnare alla risorsa "TE-1" il nome di "TE-2" e una nuova descrizione
    Then la modifica della risorsa "TE-1" viene rifiutata
    And le risorse "TE-1" e "TE-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_UNIQUENESS_2] - Unicità non aggirabile con differenze di maiuscole e minuscole per un template e-service
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata senza applicare anche la descrizione valida.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                  | tecnologia | versioni |
      | TE-1  | Comune di Milano | Template anagrafico   | REST       | bozza    |
      | TE-2  | Comune di Milano | Template anagrafico   | REST       | bozza    |
    When un admin del Comune di Milano richiede per "TE-1" il nome di "TE-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica della risorsa "TE-1" viene rifiutata
    And le risorse "TE-1" e "TE-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_UNIQUENESS_3] - Omonimia consentita tra template e-service in ambiti distinti
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore          | nome                  | tecnologia | versioni |
      | TE-1  | Comune di Milano   | Template anagrafico   | REST       | bozza    |
      | TE-2  | Comune di Pozzallo | Template anagrafico   | REST       | bozza    |
    When un admin del Comune di Milano richiede di assegnare alla risorsa "TE-1" il nome di "TE-2"
    Then la rinomina della risorsa "TE-1" viene accettata
    And la risorsa "TE-2" conserva tutti i dati iniziali
