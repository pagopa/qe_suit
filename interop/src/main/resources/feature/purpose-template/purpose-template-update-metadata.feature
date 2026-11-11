Feature: Modifica dei metadati dei template finalità
  Come creatore di un template finalità
  Voglio modificarne titolo descrizione e destinatari
  Per mantenere invariato lo stato del template e le finalità già derivate.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_1] - Aggiornamento dei metadati nei diversi stati del template
  Dato un template finalità nello stato previsto,
  quando il creatore aggiorna un metadato,
  allora il sistema salva il nuovo valore senza modificare stato o configurazione operativa.

    Given un template finalità "TP-1" creato da PagoPA in stato <stato>
    When un admin di PagoPA richiede la modifica dei metadati del template finalità "TP-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il template finalità "TP-1" presenta i metadati richiesti
    And il template finalità "TP-1" conserva i metadati non modificati
    And il template finalità "TP-1" conserva stato e configurazione operativa

    Examples:
      | stato     | campo       | valore                                |
      | DRAFT     | titolo      | Template finalità aggiornato          |
      | PUBLISHED | descrizione | Descrizione aggiornata del template   |
      | SUSPENDED | destinatari | Testo dei destinatari aggiornato      |
      | ARCHIVED  | titolo      | Template finalità aggiornato          |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_2] - Modifica congiunta senza completare l’analisi del rischio
  Dato un template finalità in bozza con analisi del rischio incompleta,
  quando il creatore aggiorna esclusivamente i metadati,
  allora la modifica è accettata senza richiedere il completamento dell’analisi del rischio.

    Given un template finalità "TP-1" creato da PagoPA in bozza con analisi del rischio incompleta
    When un admin di PagoPA richiede la modifica dei metadati del template finalità "TP-1":
      | campo       | valore                              |
      | titolo      | Template finalità aggiornato        |
      | descrizione | Descrizione aggiornata del template |
      | destinatari | Testo dei destinatari aggiornato    |
    Then il template finalità "TP-1" presenta i metadati richiesti
    And il template finalità "TP-1" conserva stato, analisi del rischio e configurazione operativa

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_3] - Le finalità esistenti non ereditano gli aggiornamenti del template
  Dato un template finalità con finalità derivate già esistenti,
  quando il creatore aggiorna tutti i metadati del template,
  allora le finalità già create conservano metadati identificativi versioni e stati.

    Given un template finalità "TP-1" creato da PagoPA con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | DRAFT  |
      | P-2   | ACTIVE |
    When un admin di PagoPA richiede la modifica dei metadati del template finalità "TP-1":
      | campo       | valore                              |
      | titolo      | Template finalità aggiornato        |
      | descrizione | Descrizione aggiornata del template |
      | destinatari | Testo dei destinatari aggiornato    |
    Then il template finalità "TP-1" presenta i metadati richiesti
    And le finalità "P-1" e "P-2" conservano metadati, identificativi, versioni e stati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PURPOSE_TEMPLATE_METADATA_WEB_1] - Form precompilato e conferma dopo il salvataggio
  Dato un template finalità modificabile con metadati già valorizzati,
  quando l’utente apre il form modifica e salva nuovi valori,
  allora il sistema mostra la conferma e il form riaperto presenta i nuovi metadati.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | DRAFT  |
    When un admin del Comune di Milano apre la modifica dei metadati della risorsa "TP-1"
    Then il form della risorsa "TP-1" mostra i metadati correnti e i campi previsti
    When un admin del Comune di Milano modifica e salva i metadati della risorsa "TP-1"
    Then il sistema conferma il salvataggio e chiude il form
    And il dettaglio e il form riaperto della risorsa "TP-1" presentano i metadati aggiornati

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PURPOSE_TEMPLATE_METADATA_WEB_2] - Conservazione degli input in presenza di un errore di validazione
  Dato un form di modifica aperto e compilato,
  quando l’utente tenta di salvare un valore non valido,
  allora il form resta aperto con i testi inseriti e indica il campo errato.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | DRAFT  |
    When il form di modifica dei metadati di una risorsa "TP-1" del Comune di Milano aperto
    And un admin del Comune di Milano inserisce metadati non validi nel form della risorsa "TP-1" e richiede il salvataggio
    Then il form della risorsa "TP-1" rimane aperto con i testi inseriti
    And il campo non valido mostra il messaggio di errore previsto

  @work-item:QA-19036
  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [PURPOSE_TEMPLATE_METADATA_WEB_3] - Conservazione degli input in presenza di un conflitto di scrittura
  Dato un form di modifica aperto su metadati che sono stati modificati nel frattempo,
  quando l’utente prova a salvare i propri valori,
  allora il form conserva gli input e segnala che il salvataggio non è avvenuto.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | DRAFT  |
    When il form di modifica dei metadati della risorsa "TP-1" del Comune di Milano aperto con una modifica concorrente
    And un admin del Comune di Milano richiede il salvataggio dei metadati nel form della risorsa "TP-1"
    Then il form della risorsa "TP-1" rimane aperto con i testi inseriti
    And il sistema segnala il mancato salvataggio della risorsa "TP-1"

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | ACTIVE  |
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "TP-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "TP-1" viene rifiutata
    And la risorsa "TP-1" conserva metadati, stati, versioni e relazioni iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_PROTECTION_2] - Rifiuto atomico di campi operativi non modificabili
  Dato una risorsa fuori bozza con configurazione operativa,
  quando l’attore invia un metadato valido insieme a un campo operativo vietato,
  allora il sistema rifiuta l’aggiornamento anche quando il campo operativo conserva il valore precedente.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | ACTIVE  |
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "TP-1" insieme a un campo operativo vietato <variazione>
    Then la modifica della risorsa "TP-1" viene rifiutata
    And la risorsa "TP-1" conserva metadati, stati, versioni e relazioni iniziali

    Examples:
      | variazione           |
      | con valore nuovo     |
      | con valore invariato |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | DRAFT  |
    When un admin del Comune di Milano aggiorna i metadati della risorsa "TP-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "TP-1"
    Then la ripetizione della modifica della risorsa "TP-1" viene accettata
    And la risorsa "TP-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given un template finalità "TP-1" creato dal Comune di Milano con le seguenti finalità derivate:
      | alias | stato  |
      | P-1   | DRAFT  |
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "TP-1" confermando gli altri valori correnti
    Then la risorsa "TP-1" presenta il metadato aggiornato
    And la risorsa "TP-1" conserva stati, versioni, relazioni e metadati invariati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_PROTECTION_5] - Impossibilità di modificare direttamente metadati di una finalità derivata
  Dato una finalità derivata da un template,
  quando il fruitore tenta di aggiornarne i metadati governati dal template,
  allora la finalità e il template rimangono invariati.

    Given una finalità "P-1" di PagoPA derivata dal template finalità "TP-1"
    When un admin di PagoPA richiede la modifica dei metadati della finalità "P-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la modifica dei metadati della finalità "P-1" viene rifiutata
    And la finalità "P-1" e il template finalità "TP-1" conservano i metadati iniziali

    Examples:
      | campo       | valore                               |
      | titolo      | Nuovo titolo della finalità          |
      | descrizione | Nuova descrizione della finalità     |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_UNIQUENESS_1] - Rifiuto della rinomina in conflitto per un template finalità
  Dato due risorse nello stesso ambito di unicità con nomi distinti,
  quando l’attore rinomina una risorsa usando il nome dell’altra e una descrizione valida,
  allora la modifica è respinta integralmente lasciando intatte entrambe le risorse.

    Given due template finalità "TP-1" e "TP-2" nel medesimo ambito di unicità
    When un admin del Comune di Milano richiede di assegnare alla risorsa "TP-1" il nome di "TP-2" e una nuova descrizione
    Then la modifica della risorsa "TP-1" viene rifiutata
    And le risorse "TP-1" e "TP-2" conservano tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_UNIQUENESS_2] - Unicità non aggirabile con differenze di maiuscole e minuscole per un template finalità
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata senza applicare anche la descrizione valida.

    Given due template finalità "TP-1" e "TP-2" nel medesimo ambito di unicità
    When un admin del Comune di Milano richiede per "TP-1" il nome di "TP-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica della risorsa "TP-1" viene rifiutata
    And le risorse "TP-1" e "TP-2" conservano tutti i dati iniziali

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PURPOSE_TEMPLATE_METADATA_UNIQUENESS_3] - Omonimia consentita tra template finalità in ambiti distinti
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given due template finalità "TP-1" e "TP-2" in ambiti di unicità distinti
    When un admin del Comune di Milano richiede di assegnare alla risorsa "TP-1" il nome di "TP-2"
    Then la rinomina della risorsa "TP-1" viene accettata
    And la risorsa "TP-2" conserva tutti i dati iniziali
