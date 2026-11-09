@PIN-11320
Feature: Modifica dei metadati dei template finalità
  Come creatore di un template finalità
  Voglio modificarne titolo descrizione e destinatari
  Per mantenere invariato lo stato del template e le finalità già derivate.

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

