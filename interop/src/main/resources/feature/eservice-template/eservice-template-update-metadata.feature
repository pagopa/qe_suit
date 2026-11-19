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

    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service con versione <versioni>
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service:
      | campo   | valore   |
      | <campo> | <valore> |
    Then il template e-service presenta i metadati richiesti e conserva quelli non modificati

    Examples:
      | versioni                 | campo          | valore                              |
      | in bozza                 | descrizione    | Descrizione aggiornata del template |
      | pubblicata               | intendedTarget | Nuovo testo dei destinatari         |
      | sospesa                  | nome           | Template anagrafico aggiornato      |
      #TODO | soltanto deprecate       | descrizione | Descrizione aggiornata del template  |
      | pubblicata e nuova bozza | intendedTarget | Nuovo testo dei destinatari         |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_2] - Modifica congiunta dei metadati senza creare versioni
  Dato un template e-service con una versione pubblicata e una nuova versione in bozza,
  quando il creatore modifica nome, descrizione e destinatari,
  allora i nuovi metadati vengono salvati senza creare versioni o alterare le configurazioni operative.

    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service con versione pubblicata e nuova bozza
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service:
      | campo          | valore                              |
      | nome           | Template anagrafico aggiornato      |
      | descrizione    | Descrizione aggiornata del template |
      | intendedTarget | Nuovo testo dei destinatari         |
    Then il template e-service presenta i metadati richiesti e conserva quelli non modificati

  #  @work-item:QA-19036
  #  @channel:Given=BFF,When=WEB,Then=WEB
  #  Scenario: [ESERVICE_TEMPLATE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  #  Dato un e-service template modificabile con metadati già valorizzati,
  #  quando l’utente apre il form modifica e salva nuovi valori,
  #  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.
  #
  #    Given i seguenti template e-service con versioni predisposte:
  #      | alias | erogatore        | nome                | tecnologia | versioni                 |
  #      | TE-1  | Comune di Milano | Template anagrafico | REST       | pubblicata e nuova bozza |
  #    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "TE-1"
  #    Then il form della risorsa "TE-1" mostra i metadati correnti e i campi previsti
  #    When un admin del Comune di Milano modifica e salva i metadati della risorsa "TE-1"
  #    Then il sistema conferma il salvataggio e chiude il form
  #    And il dettaglio e il form riaperto della risorsa "TE-1" presentano i metadati aggiornati

  #  @work-item:QA-19036
  #  @channel:Given=BFF,When=WEB,Then=WEB
  #  Scenario: [ESERVICE_TEMPLATE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  #  Dato un form di modifica aperto e compilato,
  #  quando l’utente tenta di salvare un valore non valido,
  #  allora il form resta aperto con i testi inseriti e indica il campo errato.
  #
  #    Given i seguenti template e-service con versioni predisposte:
  #      | alias | erogatore        | nome                | tecnologia | versioni                 |
  #      | TE-1  | Comune di Milano | Template anagrafico | REST       | pubblicata e nuova bozza |
  #    When il form di modifica dei metadati di una risorsa "TE-1" del Comune di Milano aperto
  #    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "TE-1" e richiede il salvataggio
  #    Then il form della risorsa "TE-1" rimane aperto con i testi inseriti
  #    And il campo non valido mostra il messaggio di errore previsto

  #  @work-item:QA-19036
  #  @channel:Given=BFF,When=WEB,Then=WEB
  #  Scenario: [ESERVICE_TEMPLATE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  #  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  #  quando l’utente prova a salvare i propri valori,
  #  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.
  #
  #    Given i seguenti template e-service con versioni predisposte:
  #      | alias | erogatore        | nome                | tecnologia | versioni                 |
  #      | TE-1  | Comune di Milano | Template anagrafico | REST       | pubblicata e nuova bozza |
  #    When il form di modifica dei metadati della risorsa "TE-1" del Comune di Milano aperto con una modifica concorrente
  #    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "TE-1"
  #    Then il form della risorsa "TE-1" rimane aperto con i testi inseriti
  #    And il sistema segnala il mancato salvataggio della risorsa "TE-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_PROTECTION_1] - Rifiuto della richiesta di modifica in presenza di metadati non ammessi
  Dato un template non in stato bozza e modificabile da un attore autorizzato,
  quando l'attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service con versione pubblicata
    When un admin del Comune di Milano tenta la modifica dei metadati del template e-service:
      | campo    | valore               |
      | <campo1> | <valore_ammesso>     |
      | <campo2> | <valore_non_ammesso> |
    Then la modifica dei metadati del template e-service viene rifiutata per <errore>

    Examples:
      | campo1 | valore_ammesso                 | campo2      | valore_non_ammesso | errore                       |
      | nome   | Servizio anagrafico aggiornato | descrizione | desc               | INVALID_DATA                 |
      | nome   | Servizio anagrafico aggiornato | technology  | REST               | OUT_OF_DRAFT_OPERATION_FIELD |
      | nome   | Servizio anagrafico aggiornato | technology  | %last-used%        | OUT_OF_DRAFT_OPERATION_FIELD |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_PROTECTION_2] - Ripetizione dei metadati correnti senza effetti operativi
  Dato un template i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service con versione pubblicata
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service:
      | campo       | valore            |
      | nome        | Nuovo nome        |
      | descrizione | Nuova descrizione |
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service:
      | campo       | valore      |
      | nome        | %last-used% |
      | descrizione | %last-used% |
    Then il template e-service presenta i metadati richiesti e conserva quelli non modificati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_METADATA_PROTECTION_3] - Impossibilità di sovrascrivere i metadati di un e-service derivato
  Dato un e-service ottenuto da un template,
  quando l’erogatore tenta di modificarne direttamente i metadati ereditati,
  allora la modifica è respinta senza cambiare il template o la sua istanza.

    Given i seguenti template e-service pubblicati:
      | alias | nome                  | tecnologia |
      | TPL-1 | Template Modello Alfa | SOAP       |
    And le seguenti istanze di template pubblicate:
      | alias | template |
      | ES-1  | TPL-1    |
    When un admin del Comune di Milano tenta la modifica dei metadati dell'e-service "ES-1":
      | campo       | valore                              |
      | nome        | Nome aggiornato dell'istanza        |
      | descrizione | Descrizione aggiornata dell'istanza |
    Then la modifica dei metadati dell'e-service "ES-1" viene rifiutata per NOT_ALLOWED

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_UNIQUENESS] - Rifiuto della rinomina in conflitto per un template e-service
  Dato due risorse con nomi distinti,
  quando l’attore rinomina una risorsa usando il nome dell’altra e una descrizione valida,
  allora la modifica è respinta integralmente lasciando intatte entrambe le risorse.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                  | tecnologia |
      | TPL-1 | Comune di Milano | Template Modello Alfa | REST       |
      | TPL-2 | <erogatore>      | Template Modello Beta | REST       |
    When un admin del Comune di Milano richiede di assegnare al template e-service "TPL-1" il nome del template e-service "TPL-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica dei metadati del template e-service viene rifiutata per CONFLICT

    Examples:
      | erogatore          |
      | Comune di Milano   |
      | Comune di Pozzallo |








  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_VALIDATION_1] - Rispetto dei limiti di lunghezza dei metadati
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi e rifiuta gli altri senza modifiche parziali.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                | tecnologia | versioni |
      | TE-1  | Comune di Milano | Template anagrafico | REST       | bozza    |
    When un admin del Comune di Milano richiede un valore di <lunghezza> caratteri per il campo <campo> della risorsa "TE-1"
    Then la modifica della risorsa "TE-1" ha esito <esito>
    And in caso di rifiuto la risorsa "TE-1" conserva tutti i dati iniziali

    Examples:
      | campo       | lunghezza | esito     |
      | nome        | 45        | accettata |
      | nome        | 46        | rifiutata |
      | destinatari | 250       | accettata |
      | destinatari | 251       | rifiutata |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [TEMPLATE_ESERVICE_METADATA_VALIDATION_2] - Rifiuto di valori non ammessi nei metadati di un template e-service
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given i seguenti template e-service con versioni predisposte:
      | alias | erogatore        | nome                | tecnologia | versioni |
      | TE-1  | Comune di Milano | Template anagrafico | REST       | bozza    |
    When un admin del Comune di Milano richiede la modifica del campo nome della risorsa "TE-1" con un valore vuoto
    Then la modifica della risorsa "TE-1" viene rifiutata
    And la risorsa "TE-1" conserva tutti i dati iniziali
