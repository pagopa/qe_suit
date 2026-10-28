@PIN-10293
@eservice-template-interface
@channel:Given=BFF,When=M2M,Then=M2M
Feature: Recupero dell'interfaccia del template e-service con M2M
  Come erogatore
  Voglio recuperare l'interfaccia del template e-service con M2M

  Scenario: [M2M_ESERVICE_TEMPLATE_INTERFACE] Download dell'interface del template e-service con M2M
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.yaml |
    When un M2M_ADMIN appartenente all'erogatore Comune di Milano
    And l'erogatore recupera l'interfaccia del template
    Then l'interfaccia del template è rimasta invariata
