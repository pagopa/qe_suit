@bff @channel:Given=BFF,When=BFF,Then=BFF
Feature: Creazione notifica multi-destinatario via BFF
  verifico che una notifica con più destinatari, ciascuno con il proprio indirizzo e i propri
  documenti pagoPA/F24, venga creata e inviata correttamente componendo gli step di preparazione,
  aggiunta destinatario e invio

  Scenario: [BFF_NOTIFICA_MULTI_DESTINATARIO] Notifica con due destinatari, uno con solo indirizzo
  fisico e uno con pagoPA e domicilio digitale
    Given la PA Comune_1 predispone una nuova notifica con i seguenti dati:
      | subject               | Comunicazione di test multi-destinatario |
      | physicalCommunication | REGISTERED_LETTER_890                    |
      | feePolicy             | FLAT_RATE                                |
    And è stato aggiunto User_1 come destinatario:
      | physicalAddress_address      | Via@ok_890 |
      | physicalAddress_municipality | COLLELUNGO |
      | physicalAddress_province     | TR         |
      | physicalAddress_zip          | 05010      |
      | pagoPA_number                | 0          |
      | F24_number                   | 0          |
    And è stato aggiunto Impresa_1 come destinatario:
#      | taxId           | LELPTR04A01C352X |
      | digitalDomicile | test@pec.it      |
      | pagoPA_number   | 1                |
      | F24_number      | 0                |
    When la notifica viene sottomessa
    And la notifica raggiunge lo stato "EFFECTIVE_DATE"

