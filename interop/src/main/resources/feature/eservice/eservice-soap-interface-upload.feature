@eservice-interface
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Validazione del file WSDL nel caricamento dell'interfaccia di un e-service SOAP
  Come erogatore
  Voglio ricevere una segnalazione puntuale quando carico un file WSDL non valido su una versione in bozza di un e-service SOAP
  Affinché possa identificare il problema del file senza che venga associata un'interfaccia invalida.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario Outline: [ESERVICE_SOAP_INTERFACE_UPLOAD_1] Rifiuto del caricamento di un WSDL non valido sul descrittore in bozza
    Given un e-service SOAP in bozza creato dall'erogatore
    When l'erogatore tenta di caricare l'interfaccia SOAP "<fixture>" sul descrittore dell'e-service ottenendo un rifiuto per "<motivo>"
    Then non è disponibile un'interfaccia valida per il descrittore dell'e-service

    Examples:
      | fixture                     | motivo                            |
      | missing-soap-address.wsdl   | assenza di soap:address           |
      | not-parsable.wsdl           | file SOAP non parsabile           |
      | missing-soap-operation.wsdl | assenza di soap:operation         |

  Scenario Outline: [ESERVICE_SOAP_IMPORT_1] Rifiuto dell'importazione di un e-service SOAP con WSDL non valido
    When l'erogatore tenta di importare un e-service SOAP con interfaccia "<fixture>" ottenendo un rifiuto per "<motivo>"
    Then l'e-service importato non è presente tra gli e-service dell'erogatore

    Examples:
      | fixture                     | motivo                            |
      | missing-soap-address.wsdl   | assenza di soap:address           |
      | not-parsable.wsdl           | file SOAP non parsabile           |
      | missing-soap-operation.wsdl | assenza di soap:operation         |

  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [M2M_ESERVICE_SOAP_INTERFACE_UPLOAD_1] Rifiuto del caricamento di un WSDL non valido sul descrittore in bozza tramite M2M
    Given un admin appartenente all'erogatore Comune di Milano
    And un e-service SOAP in bozza creato dall'erogatore
    When un M2M_ADMIN appartenente all'erogatore Comune di Milano
    And l'erogatore tenta di caricare l'interfaccia SOAP "<fixture>" sul descrittore dell'e-service ottenendo un rifiuto per "<motivo>"
    Then non è disponibile un'interfaccia valida per il descrittore dell'e-service

    Examples:
      | fixture                     | motivo                            |
      | missing-soap-address.wsdl   | assenza di soap:address           |
      | not-parsable.wsdl           | file SOAP non parsabile           |
      | missing-soap-operation.wsdl | assenza di soap:operation         |
