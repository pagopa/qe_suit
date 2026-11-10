@PIN-11318
Feature: Modifica dei metadati delle finalità
  Come fruitore proprietario
  Voglio aggiornare titolo e descrizione di una finalità ordinaria o inversa
  Per conservarne stato e configurazione funzionale.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_1] - Modifica di un singolo metadato di una finalità ordinaria o inversa
  Dato una finalità non derivata da template nella modalità e nello stato previsti,
  quando il fruitore ne aggiorna soltanto titolo o descrizione,
  allora la modifica viene applicata senza alterare i dati operativi e il metadato omesso.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato <stato>
    When un admin di PagoPA richiede la modifica dei metadati della finalità "P-1":
      | campo   | valore   |
      | <campo> | <valore> |
    Then la finalità "P-1" presenta i metadati richiesti
    And la finalità "P-1" conserva i metadati non modificati
    And la finalità "P-1" conserva stato, versioni e configurazione funzionale

    Examples:
      | modalità | stato                | campo       | valore                                |
      | ordinaria| DRAFT                | titolo      | Finalità aggiornata                   |
      | ordinaria| ACTIVE               | descrizione | Descrizione aggiornata della finalità |
      | ordinaria| SUSPENDED            | titolo      | Finalità aggiornata                   |
      | ordinaria| ARCHIVED             | descrizione | Descrizione aggiornata della finalità |
      | ordinaria| WAITING_FOR_APPROVAL | titolo      | Finalità aggiornata                   |
      | ordinaria| REJECTED             | descrizione | Descrizione aggiornata della finalità |
      | inversa  | DRAFT                | titolo      | Finalità aggiornata                   |
      | inversa  | ACTIVE               | descrizione | Descrizione aggiornata della finalità |
      | inversa  | SUSPENDED            | titolo      | Finalità aggiornata                   |
      | inversa  | ARCHIVED             | descrizione | Descrizione aggiornata della finalità |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [PURPOSE_METADATA_2] - Aggiornamento congiunto della finalità senza nuove versioni
  Dato una finalità ordinaria o inversa non derivata da template,
  quando il fruitore ne aggiorna titolo e descrizione,
  allora la finalità conserva la propria configurazione e le versioni preesistenti.

    Given una finalità "P-1" <modalità> non derivata da template di PagoPA in stato ACTIVE
    When un admin di PagoPA richiede la modifica dei metadati della finalità "P-1":
      | campo       | valore                                |
      | titolo      | Finalità aggiornata                   |
      | descrizione | Descrizione aggiornata della finalità |
    Then la finalità "P-1" presenta i metadati richiesti
    And la finalità "P-1" conserva stato, versioni e configurazione funzionale

    Examples:
      | modalità |
      | ordinaria |
      | inversa   |

