@purpose
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Associazione finalità a e-service in ricezione
  Come ente fruitore
  Voglio che il sistema impedisca l'associazione di un e-service in modalità di ricezione ad una finalità
  Affinché siano rispettati i vincoli di compatibilità tra la modalità del servizio e la finalità.

  # Ticket aperto https://pagopa.atlassian.net/jira/browse/PIN-11715
  # PST: https://pagopa.atlassian.net/wiki/x/IQDwzQ
  # CASO DI TEST 1.4: Verificare che un e-service in modalità di ricezione non possa essere associato a una propria finalità
  @work-item:QA-17458
  Scenario: [PURPOSE_RECEIVE_ESERVICE_1.4] Impossibilità di associare un e-service in modalità di ricezione a una propria finalità
    Quando un ente sta creando una finalità, non può associarvi un e-service in modalità di
    ricezione creato da un altro ente.

    Given un e-service in modalità di ricezione creato e pubblicato da Comune di Milano
    And una richiesta di fruizione attiva verso l'e-service da parte di PagoPA
    When PagoPA tenta di creare una finalità associando l'e-service creato
    Then la creazione della finalità è negata

