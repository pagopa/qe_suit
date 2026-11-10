@private-catalog
@eservice-template-interface
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Validazione del file WSDL nel caricamento dell'interfaccia di un template e-service SOAP
  Come erogatore
  Voglio ricevere una segnalazione puntuale quando carico un file WSDL non valido su una versione in bozza di un template e-service SOAP
  Affinché possa identificare il problema del file senza che venga associata un'interfaccia invalida al template.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario Outline: [ESERVICE_TEMPLATE_SOAP_INTERFACE_UPLOAD_1] Rifiuto del caricamento di un WSDL non valido sulla versione in bozza del template
    Given un template e-service SOAP in bozza creato dall'erogatore
    When l'erogatore tenta di caricare l'interfaccia SOAP "<fixture>" sulla versione del template ottenendo un rifiuto per "<motivo>"
    Then non è disponibile un'interfaccia valida per la versione del template

    Examples:
      | fixture                     | motivo                            |
      | missing-soap-address.wsdl   | assenza di soap:address           |
      | not-parsable.wsdl           | file SOAP non parsabile           |
      | missing-soap-operation.wsdl | assenza di soap:operation         |
