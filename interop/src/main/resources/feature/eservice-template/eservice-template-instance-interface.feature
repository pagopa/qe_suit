@PIN-10024
@eservice-template-instance
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Conservazione dell'interfaccia nell'istanziazione da template e-service
  Come erogatore
  Voglio personalizzare l'interfaccia di un e-service creato da template
  Affinché gli indirizzi siano aggiornati senza alterare le parti
  del documento che devono rimanere invariate.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_1] Conservazione del WSDL con un indirizzo SOAP attivo
    Given un template e-service SOAP pubblicato con l'interfaccia "<fixture>"
    When l'erogatore configura con successo l'interfaccia SOAP di un nuovo e-service dal template con i seguenti indirizzi:
      | servizio          | porta    | indirizzo  |
      | ServizioPagamenti | PortaUno | <nuovoUrl> |
    And l'erogatore recupera l'interfaccia dell'e-service
    Then l'e-service ha la prima versione in stato bozza
    And ogni porta del WSDL contiene l'indirizzo configurato per quella porta
    And il WSDL è identico byte per byte all'originale salvo i valori degli indirizzi configurati
    And il WSDL è XML ben formato
    And l'interfaccia del template è rimasta invariata

    Examples: Singolo indirizzo
      | fixture                  | nuovoUrl                       |
      | soap11-un-indirizzo.wsdl | https://erogatore.example/soap |
      | soap12-un-indirizzo.wsdl | https://erogatore.example/soap |

    Examples: Indirizzi commentati
      | fixture                                | nuovoUrl                       |
      | soap11-indirizzo-commentato-prima.wsdl | https://erogatore.example/soap |
      | soap11-indirizzo-commentato-dopo.wsdl  | https://erogatore.example/soap |
      | soap12-indirizzo-commentato-prima.wsdl | https://erogatore.example/soap |
      | soap12-indirizzo-commentato-dopo.wsdl  | https://erogatore.example/soap |

    Examples: Indirizzo con parametri di query
      | fixture                  | nuovoUrl                                   |
      | soap11-un-indirizzo.wsdl | https://erogatore.example/soap?p1=v1&p2=v2 |
      | soap12-un-indirizzo.wsdl | https://erogatore.example/soap?p1=v1&p2=v2 |

    Examples: Prefissi SOAP alternativi
      | fixture                          | nuovoUrl                       |
      | soap11-prefisso-alternativo.wsdl | https://erogatore.example/soap |
      | soap12-prefisso-alternativo.wsdl | https://erogatore.example/soap |

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_2] Corretta assegnazione di più indirizzi SOAP
    Given un template e-service SOAP pubblicato con l'interfaccia "<fixture>"
    When l'erogatore configura con successo l'interfaccia SOAP di un nuovo e-service dal template con i seguenti indirizzi:
      | servizio          | porta    | indirizzo                     |
      | ServizioPagamenti | PortaUno | https://erogatore.example/uno |
      | ServizioPagamenti | PortaDue | https://erogatore.example/due |
    And l'erogatore recupera l'interfaccia dell'e-service
    Then l'e-service ha la prima versione in stato bozza
    And ogni porta del WSDL contiene l'indirizzo configurato per quella porta
    And il WSDL è identico byte per byte all'originale salvo i valori degli indirizzi configurati
    And il WSDL è XML ben formato
    And l'interfaccia del template è rimasta invariata

    Examples: Più indirizzi e indirizzi originali uguali
      | fixture                                    |
      | soap11-due-indirizzi.wsdl                  |
      | soap12-due-indirizzi.wsdl                  |
      | soap11-soap12-due-indirizzi.wsdl           |
      | soap11-due-indirizzi-originali-uguali.wsdl |
      | soap12-due-indirizzi-originali-uguali.wsdl |

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_3] Rifiuto di un numero insufficiente di indirizzi SOAP
    Given un template e-service SOAP pubblicato con l'interfaccia "<fixture>"
    When l'erogatore tenta di configurare l'interfaccia SOAP di un nuovo e-service dal template ottenendo un rifiuto per "numero di URL non corrispondente" con i seguenti indirizzi:
      | servizio          | porta    | indirizzo                     |
      | ServizioPagamenti | PortaUno | https://erogatore.example/uno |
    Then non è disponibile un'interfaccia dell'istanza accettata come valida
    And l'interfaccia del template è rimasta invariata

    Examples: Due indirizzi attivi e uno fornito
      | fixture                          |
      | soap11-due-indirizzi.wsdl        |
      | soap12-due-indirizzi.wsdl        |
      | soap11-soap12-due-indirizzi.wsdl |

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_4] Rifiuto di indirizzi SOAP in eccesso
    Given un template e-service SOAP pubblicato con l'interfaccia "<fixture>"
    When l'erogatore tenta di configurare l'interfaccia SOAP di un nuovo e-service dal template ottenendo un rifiuto per "numero di URL non corrispondente" con i seguenti indirizzi:
      | servizio          | porta    | indirizzo                     |
      | ServizioPagamenti | PortaUno | https://erogatore.example/uno |
      | ServizioPagamenti | PortaDue | https://erogatore.example/due |
    Then non è disponibile un'interfaccia dell'istanza accettata come valida
    And l'interfaccia del template è rimasta invariata

    Examples: Un indirizzo attivo e due forniti
      | fixture                                |
      | soap11-un-indirizzo.wsdl               |
      | soap12-un-indirizzo.wsdl               |
      | soap11-indirizzo-commentato-prima.wsdl |

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_5] Rifiuto della configurazione SOAP senza indirizzi
    Given un template e-service SOAP pubblicato con l'interfaccia "<fixture>"
    When l'erogatore tenta di configurare l'interfaccia SOAP di un nuovo e-service dal template ottenendo un rifiuto per "numero di URL non corrispondente" con i seguenti indirizzi:
      | servizio | porta | indirizzo |
    Then non è disponibile un'interfaccia dell'istanza accettata come valida
    And l'interfaccia del template è rimasta invariata

    Examples: Nessun indirizzo fornito
      | fixture                   |
      | soap11-un-indirizzo.wsdl  |
      | soap12-un-indirizzo.wsdl  |
      | soap11-due-indirizzi.wsdl |

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_6] Rifiuto di valori non ammessi come indirizzi SOAP
    Given un template e-service SOAP pubblicato con l'interfaccia "<fixture>"
    When l'erogatore tenta di configurare l'interfaccia SOAP di un nuovo e-service dal template ottenendo un rifiuto per "URL non ammesso" con i seguenti indirizzi:
      | servizio          | porta    | indirizzo   |
      | ServizioPagamenti | PortaUno | <indirizzo> |
    Then non è disponibile un'interfaccia dell'istanza accettata come valida
    And l'interfaccia del template è rimasta invariata

    Examples: Markup e virgolette
      | fixture                  | indirizzo                                 |
      | soap11-un-indirizzo.wsdl | https://erogatore.example/soap<elemento/> |
      | soap12-un-indirizzo.wsdl | https://erogatore.example/soap<elemento/> |
      | soap11-un-indirizzo.wsdl | https://erogatore.example/soap"           |
      | soap12-un-indirizzo.wsdl | https://erogatore.example/soap'           |

  Scenario Outline: [ESERVICE_TEMPLATE_INTERFACE_7] Personalizzazione REST con conservazione della struttura OpenAPI
    Given un template e-service REST pubblicato con l'interfaccia "<fixture>"
    When l'erogatore configura con successo l'interfaccia REST di un nuovo e-service dal template con i seguenti dati:
      | campo                 | valore                               |
      | serverUrls            | ["https://erogatore.example/api"]    |
      | contactName           | Referente Erogatore                  |
      | contactEmail          | referente@erogatore.example          |
      | contactUrl            | https://erogatore.example/contatti   |
      | termsAndConditionsUrl | https://erogatore.example/condizioni |
    And l'erogatore recupera l'interfaccia dell'e-service
    Then l'e-service ha la prima versione in stato bozza
    And la specifica OpenAPI contiene i dati configurati
    And la specifica OpenAPI conserva schemi e riferimenti senza espansioni o alias introdotti dalla validazione
    And la specifica OpenAPI è valida
    And l'interfaccia del template è rimasta invariata

    Examples: JSON e YAML
      | fixture                   |
      | openapi3-riferimenti.json |
      | openapi3-riferimenti.yaml |
