@PIN-11002
Feature: Protezione delle risorse durante la modifica dei metadati
  Come utente della piattaforma
  Voglio modificare i metadati soltanto quando consentito
  Per evitare aggiornamenti parziali e modifiche involontarie a dati operativi.


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
