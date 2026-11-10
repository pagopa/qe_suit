@PIN-11322
Feature: Modifica dei metadati dei portachiavi erogatore
  Come amministratore dell’ente erogatore
  Voglio aggiornare nome e descrizione di un portachiavi
  Per conservarne chiavi membri e associazioni agli e-service.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PRODUCER_KEYCHAIN_METADATA_1] - Modifica di un metadato senza alterare le relazioni del portachiavi
  Dato un portachiavi erogatore appartenente all'ente amministrato e contenente chiavi,
  quando l’amministratore modifica un metadato del portachiavi,
  allora il sistema aggiorna il solo campo richiesto preservando chiavi membri e associazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva i metadati non modificati
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

    Examples:
      | campo       | valore                               |
      | nome        | Portachiavi erogatore aggiornato     |
      | descrizione | Descrizione aggiornata portachiavi   |

  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario Outline: [PRODUCER_KEYCHAIN_METADATA_2] - Modifica di un metadato senza alterare le relazioni del portachiavi
  Dato un portachiavi erogatore appartenente all'ente amministrato e contenente chiavi,
  quando l’amministratore modifica un metadato del portachiavi,
  allora il sistema aggiorna il solo campo richiesto preservando chiavi membri e associazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva i metadati non modificati
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

    Examples:
      | campo       | valore                               |
      | nome        | Portachiavi erogatore aggiornato     |
      | descrizione | Descrizione aggiornata portachiavi   |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_3] - Modifica congiunta di nome e descrizione del portachiavi
  Dato un portachiavi erogatore con chiavi e associazioni,
  quando l’amministratore modifica nome e descrizione,
  allora entrambi i metadati sono aggiornati senza alterare configurazione e relazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un admin del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo       | valore                                |
      | nome        | Portachiavi erogatore aggiornato      |
      | descrizione | Descrizione aggiornata portachiavi    |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [PRODUCER_KEYCHAIN_METADATA_4] - Modifica congiunta di nome e descrizione del portachiavi
  Dato un portachiavi erogatore con chiavi e associazioni,
  quando l’amministratore modifica nome e descrizione,
  allora entrambi i metadati sono aggiornati senza alterare configurazione e relazioni.

    Given un portachiavi erogatore "K-1" del Comune di Milano con chiavi, membri e associazioni a e-service
    When un M2M_ADMIN del Comune di Milano richiede la modifica dei metadati del portachiavi "K-1":
      | campo       | valore                                |
      | nome        | Portachiavi erogatore aggiornato      |
      | descrizione | Descrizione aggiornata portachiavi    |
    Then il portachiavi "K-1" presenta i metadati richiesti
    And il portachiavi "K-1" conserva identificativo, proprietario, chiavi, membri e associazioni

