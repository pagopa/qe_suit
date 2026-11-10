@purpose-template
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Associazione finalità e template finalità a e-service in ricezione
  Come ente fruitore
  Voglio che il sistema impedisca l'associazione di un e-service o e-service template in modalità di ricezione ad una finalità o a un template di finalità
  Affinché siano rispettati i vincoli di compatibilità tra la modalità del servizio e la finalità.

  # PST: https://pagopa.atlassian.net/wiki/x/IQDwzQ
  # CASO DI TEST 1.1: Verificare che un e-service in modalità di ricezione non possa essere associato a un template di finalità
  @work-item:QA-17458
  Scenario: [PURPOSE_TEMPLATE_RECEIVE_ESERVICE_1.1] Impossibilità di associare un e-service in modalità di ricezione ad un template di finalità
    Dato un e-service in modalità di ricezione pubblicato dal Comune di Milano
    e un template di finalità avviato in creazione da PagoPA
    quando PagoPA tenta di associare al proprio template di finalità l'e-service in modalità di ricezione
    allora il sistema nega l'associazione dell'e-service al template di finalità

    Given un e-service in modalità di ricezione creato e pubblicato da Comune di Milano
    And un template di finalità avviato in creazione da PagoPA
    When PagoPA tenta di associare al proprio template di finalità l'e-service in modalità di ricezione
    Then l'associazione dell'e-service al template di finalità è negata

  # PST: https://pagopa.atlassian.net/wiki/x/IQDwzQ
  # CASO DI TEST 1.2: Verificare che un e-service template in modalità di ricezione non possa essere associato a un template di finalità
  @work-item:QA-17458
  Scenario: [PURPOSE_TEMPLATE_RECEIVE_ESERVICE_1.2] Impossibilità di associare un e-service template in modalità di ricezione ad un template di finalità
    Dato un template e-service in modalità di ricezione pubblicato dal Comune di Milano
    e un template di finalità avviato in creazione da PagoPA
    quando PagoPA tenta di associare al proprio template di finalità il template e-service in modalità di ricezione
    allora il sistema nega l'associazione del template e-service al template di finalità

    Given un template e-service in modalità di ricezione creato e pubblicato da Comune di Milano
    And un template di finalità avviato in creazione da PagoPA
    When PagoPA tenta di associare al proprio template di finalità il template e-service in modalità di ricezione
    Then l'associazione del template e-service al template di finalità è negata

  # PST: https://pagopa.atlassian.net/wiki/x/IQDwzQ
  # CASO DI TEST 1.3: Verificare che un e-service in modalità di ricezione non possa essere associato a una finalità creata a partire da un template
  @work-item:QA-17458
  Scenario: [PURPOSE_TEMPLATE_RECEIVE_ESERVICE_1.3] Impossibilità di associare un e-service in modalità di ricezione a una finalità creata da template
    Dato un e-service in modalità di ricezione pubblicato dal Comune di Milano
    e un template di finalità creato e pubblicato da PagoPA
    quando PagoPA tenta di creare una finalità a partire dal proprio template associando l'e-service in modalità di ricezione
    allora il sistema nega la creazione della finalità

    Given un e-service in modalità di ricezione creato e pubblicato da Comune di Milano
    And una richiesta di fruizione attiva verso l'e-service da parte di PagoPA
    And un template di finalità creato e pubblicato da PagoPA
    When PagoPA tenta di creare una finalità a partire dal proprio template associando l'e-service in modalità di ricezione
    Then la creazione della finalità è negata
