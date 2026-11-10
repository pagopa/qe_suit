@PIN-11319
Feature: Modifica dei metadati dei template e-service
  Come creatore di un template e-service
  Voglio modificare nome, descrizione e destinatari del template
  Per mantenere aggiornate le informazioni senza modificare versioni, stati e configurazioni operative.

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
