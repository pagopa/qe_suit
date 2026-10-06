@eservice-template
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Gestione delle interfacce OpenAPI negli EService Template

  Come Erogatore
  Voglio creare EService Template con un'interfaccia OpenAPI standard e istanziare EService a partire da essi
  Al fine di poter pubblicare e riutilizzare i Template senza errori sull'interfaccia (PIN-9480).

  Scenario: [ESERVICE_TEMPLATE_INTERFACE_1] - Caricamento di un'interfaccia OpenAPI standard su un Template
  Dato un EService Template in bozza
  quando l'Erogatore carica un'interfaccia OpenAPI standard
  allora il sistema salva l'interfaccia nel Template

    Given un EService Template in stato DRAFT creato dal Comune di Milano
    When il Comune di Milano carica l'interfaccia OpenAPI standard sul Template
    Then il Template contiene l'interfaccia OpenAPI caricata

  Scenario: [ESERVICE_TEMPLATE_INTERFACE_2] - Creazione di un EService a partire da un nuovo Template pubblicato
  Dato un EService Template pubblicato con un'interfaccia OpenAPI standard
  quando l'Erogatore crea un EService a partire dal Template
  allora l'EService viene creato in bozza senza errori sull'interfaccia

    Given un EService Template in stato PUBLISHED creato dal Comune di Milano
    When il Comune di Milano crea un EService a partire dal Template
    Then l'EService creato dal Template risulta in stato DRAFT
