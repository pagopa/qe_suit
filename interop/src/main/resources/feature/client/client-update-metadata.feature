@PIN-11321
Feature: Modifica dei metadati dei client
  Come amministratore dell'ente proprietario
  Voglio aggiornare il nome e la descrizione dei client
  Per mantenerne aggiornate le informazioni senza alterare identità, chiavi e associazioni.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_METADATA_1] - Modifica di un singolo metadato di un client Consumer
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore ne modifica soltanto il nome o la descrizione,
  allora il sistema salva il nuovo valore, conserva quello omesso
  e mantiene invariati identità, chiavi e associazioni del client.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva i metadati non modificati
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

    Examples:
      | campo       | valore                            |
      | nome        | Client aggiornato                 |
      | descrizione | Descrizione aggiornata del client |

  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [CLIENT_METADATA_2] - Modifica di un singolo metadato di un client Consumer da parte dell'amministratore M2M
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore M2M ne modifica soltanto il nome o la descrizione,
  allora il sistema salva il nuovo valore, conserva quello omesso
  e mantiene invariati identità, chiavi e associazioni del client.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva i metadati non modificati
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

    Examples:
      | campo       | valore                            |
      | nome        | Client aggiornato                 |
      | descrizione | Descrizione aggiornata del client |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_3] - Modifica congiunta di nome e descrizione di un client Consumer
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore ne modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i valori senza alterare identità, chiavi e associazioni.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client Consumer aggiornato   |
      | descrizione | Nuova descrizione del client |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_METADATA_4] - Modifica congiunta di nome e descrizione di un client Consumer da parte dell'amministratore M2M
  Dato un client Consumer appartenente all'ente amministrato,
  quando l'amministratore M2M ne modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i valori senza alterare identità, chiavi e associazioni.

    Given un client "CL-1" CONSUMER creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client Consumer aggiornato   |
      | descrizione | Nuova descrizione del client |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_METADATA_5] - Modifica dei metadati di un client API
  Dato un client API appartenente all'ente amministrato,
  quando l'amministratore ne modifica il nome o la descrizione,
  allora il sistema salva il metadato richiesto senza alterare identità, chiavi e associazioni.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva i metadati non modificati
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

    Examples:
      | campo       | valore                            |
      | nome        | Client API aggiornato             |
      | descrizione | Descrizione aggiornata del client |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_6] - Modifica congiunta di nome e descrizione di un client API
  Dato un client API appartenente all'ente amministrato,
  quando l'amministratore ne modifica contemporaneamente nome e descrizione,
  allora il sistema salva entrambi i metadati lasciando invariate le proprietà operative del client.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client API aggiornato        |
      | descrizione | Nuova descrizione del client |
    Then il client "CL-1" presenta i metadati richiesti
    And il client "CL-1" conserva identificativo, tipologia, proprietario, chiavi e associazioni

  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [CLIENT_METADATA_7] - Impossibilità per un amministratore M2M di modificare un client API
  Dato un client API appartenente all'ente dell'amministratore,
  quando l'amministratore M2M ne richiede la modifica dei metadati,
  allora il sistema rifiuta l'operazione e conserva integralmente i dati del client.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

    Examples:
      | campo       | valore                            |
      | nome        | Client API aggiornato             |
      | descrizione | Descrizione aggiornata del client |

  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_METADATA_8] - Impossibilità per un amministratore M2M di modificare congiuntamente nome e descrizione di un client API
  Dato un client API appartenente all'ente dell'amministratore,
  quando l'amministratore M2M ne richiede la modifica congiunta di nome e descrizione,
  allora il sistema rifiuta l'intera operazione senza modificare il client.

    Given un client "CL-1" API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                       |
      | nome        | Client API aggiornato        |
      | descrizione | Nuova descrizione del client |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_9] - Conservazione dell'operatività del client Consumer dopo la modifica
  Dato un client Consumer con chiavi crittografiche e una finalità utilizzabile associata,
  quando l'amministratore ne modifica nome e descrizione,
  allora le chiavi e le associazioni rimangono invariate
  e il client continua a consentire l'ottenimento di un voucher.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin di PagoPA richiede la modifica dei metadati del client "CL-1":
      | campo       | valore                        |
      | nome        | Client Consumer aggiornato    |
      | descrizione | Descrizione aggiornata        |
    Then il client "CL-1" conserva chiavi e associazioni
    And il client "CL-1" consente di ottenere un nuovo voucher
