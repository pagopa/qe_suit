@PIN-11002
Feature: Protezione delle risorse durante la modifica dei metadati
  Come utente della piattaforma
  Voglio modificare i metadati soltanto quando consentito
  Per evitare aggiornamenti parziali e modifiche involontarie a dati operativi.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_PROTECTION_1] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "R-1" con un valore valido e uno non ammesso
    Then la modifica della risorsa "R-1" viene rifiutata
    And la risorsa "R-1" conserva metadati, stati, versioni e relazioni iniziali

    Examples:
      | risorsa           |
      | e-service         |
      | template e-service|
      | finalità          |
      | template finalità |
      | client            |
      | portachiavi       |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_PROTECTION_2] - Rifiuto atomico di campi operativi non modificabili
  Dato una risorsa fuori bozza con configurazione operativa,
  quando l’attore invia un metadato valido insieme a un campo operativo vietato,
  allora il sistema rifiuta l’aggiornamento anche quando il campo operativo conserva il valore precedente.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano fuori bozza
    When un admin del Comune di Milano richiede la modifica dei metadati della risorsa "R-1" insieme a un campo operativo vietato <variazione>
    Then la modifica della risorsa "R-1" viene rifiutata
    And la risorsa "R-1" conserva metadati, stati, versioni e relazioni iniziali

    Examples:
      | risorsa            | variazione       |
      | e-service          | con valore nuovo |
      | e-service          | con valore invariato |
      | template e-service | con valore nuovo |
      | template finalità  | con valore invariato |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano aggiorna i metadati della risorsa "R-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "R-1"
    Then la ripetizione della modifica della risorsa "R-1" viene accettata
    And la risorsa "R-1" conserva stati, versioni e relazioni

    Examples:
      | risorsa            |
      | e-service          |
      | finalità           |
      | template e-service |
      | template finalità  |
      | client             |
      | portachiavi        |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given una risorsa "R-1" di tipo <risorsa> del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "R-1" confermando gli altri valori correnti
    Then la risorsa "R-1" presenta il metadato aggiornato
    And la risorsa "R-1" conserva stati, versioni, relazioni e metadati invariati

    Examples:
      | risorsa            |
      | e-service          |
      | finalità           |
      | template e-service |
      | template finalità  |
      | client             |
      | portachiavi        |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [METADATA_AUTHORIZATION_1] - Rifiuto della modifica da parte di un ruolo non autorizzato
  Dato una risorsa appartenente a un ente determinato,
  quando un utente dello stesso ente con ruolo escluso richiede l’aggiornamento,
  allora il sistema impedisce la modifica e conserva i dati iniziali.

    Given un client "CL-1" di tipo CONSUMER appartenente al Comune di Milano
    When un security del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo | valore             |
      | nome  | Client aggiornato  |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [METADATA_AUTHORIZATION_2] - Rifiuto della modifica da parte di un altro ente
  Dato una risorsa appartenente a un ente,
  quando un amministratore di un ente diverso prova a modificarla,
  allora il sistema rifiuta l’operazione senza aggiornare la risorsa.

    Given un client "CL-1" di tipo CONSUMER appartenente al Comune di Milano
    When un admin di PagoPA richiede la modifica dei metadati del client "CL-1":
      | campo | valore             |
      | nome  | Client aggiornato  |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_AUTHORIZATION_3] - Rispetto della titolarità e delle deleghe sulle risorse delegabili
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

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_TEMPLATE_PROTECTION_1] - Impossibilità di sovrascrivere i metadati di un e-service derivato
  Dato un e-service ottenuto da un template,
  quando l’erogatore tenta di modificarne direttamente i metadati ereditati,
  allora la modifica è respinta senza cambiare il template o la sua istanza.

    Given un e-service "EI-1" derivato dal template e-service "TE-1" del Comune di Milano
    When un admin del Comune di Milano richiede la modifica dei metadati dell'e-service "EI-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la modifica dei metadati dell'e-service "EI-1" viene rifiutata
    And l'e-service "EI-1" e il template "TE-1" conservano i metadati iniziali

    Examples:
      | campo       | valore                                 |
      | nome        | Nome aggiornato dell'istanza           |
      | descrizione | Descrizione aggiornata dell'istanza    |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_TEMPLATE_PROTECTION_2] - Impossibilità di modificare direttamente metadati di una finalità derivata
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

