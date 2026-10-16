@bff @informal @comunicazioniBonarie
Feature: Creazione comunicazione bonaria via API
  Verifico la predisposizione, composizione dei destinatari e invio di una comunicazione bonaria (informale)

  Scenario: [COMBO_CREAZIONE_COMUNICAZIONE_BONARIA] Creazione comunicazione informale per la campagna FattOrd
    Given una sessione HTTP programmatica su BFF
    And la PA Grossini predispone una nuova comunicazione bonaria con i seguenti dati:
      | subject     | Test notifica bonaria Cucumber |
      | campaignId  | FattOrd                        |
    And viene aggiunto Lucrezia come destinatario della comunicazione bonaria con i seguenti dati:
      | email                   | complaint@simulator.amazonses.com |
      | phoneNumber             | +390000032181                     |
      | physicalAddress_address | Via @OK_RIS                       |
      | physicalAddress_zip     | 00133                             |
      | physicalAddress_municipality | Roma                         |
      | physicalAddress_province| RM                                |
      | pagoPA_number           | 1                                 |
    When la comunicazione bonaria viene inviata dalla PA Grossini
