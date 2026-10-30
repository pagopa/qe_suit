@bff @informal @comunicazioniBonarie @channel:Given=BFF,When=BFF,Then=BFF
Feature: Consultazione comunicazione bonaria via API
  Verifico il comportamento delle API del mittente per il dettaglio e la timeline di una comunicazione bonaria

  Scenario: [COMBO_DETTAGLIO_IUN_INESISTENTE] Il dettaglio di una comunicazione bonaria inesistente non viene trovato
    When la PA Comune_1 richiede il dettaglio della comunicazione bonaria con IUN "AAAA-BBBB-CCCC-202609-Z-A"
    Then la richiesta viene rifiutata con status 404

  Scenario: [COMBO_TIMELINE_ORDINAMENTO] La timeline di una comunicazione bonaria è ordinata e traccia i canali
    Given una comunicazione bonaria di campagna "FattOrd" creata dalla PA Comune_1 per User_1
    When la PA Comune_1 consulta la timeline della comunicazione bonaria
    Then gli stati della timeline sono in ordine cronologico decrescente
    And la timeline traccia gli step dei canali
