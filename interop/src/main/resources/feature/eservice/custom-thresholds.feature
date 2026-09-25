@eservice
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Visualizzazione delle soglie di chiamate API personalizzate nella scheda e-service del fruitore

  Come Ente Fruitore autenticato nel portale Interop
  Le soglie personalizzate vengono mostrate se c'è un attributo certificato associato, anche in presenza di delega
  alla fruizione. Mentre non vengono mostrate senza un attributo certificato richiesto o se non sono configurate.
  L'ente erogatore, o l'ente delegante alla fruizione privo dell'attributo, non visualizzano la sezione soglie
  personalizzate.

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare la corretta presenza o assenza della sezione Soglie di chiamate API personalizzate.
  ***

  Scenario Outline: [CUSTOM_THRESHOLDS_VIEW_1]
  La sezione Soglie di chiamate API personalizzate viene mostrata sulla scheda e-service del fruitore
  quando l'e-service richiede un attributo certificato, il fruitore possiede quell'attributo, e la soglia
  per il fruitore è stata personalizzata. L'erogatore che non ha l'attributo certificato non visualizza
  la sezione Soglie di chiamate API personalizzate.

    Given un admin del Comune di Milano
    And crea un e-service <dataExchangeMode> con un attributo certificato e soglia personalizzata per fruitore a <customThreshold>
      | consumerThreshold | 20 |
      | totalThreshold    | 40 |
    And un admin dell'Agenzia per l'Italia Digitale possiede quell'attributo certificato
    When visualizza la pagina dell'e-service dal catalogo
    Then la sezione soglie di chiamate API personalizzate è presente e mostra il valore <customThreshold>

    When un admin del Comune di Milano visualizza la pagina dell'e-service dal catalogo
    Then la sezione soglie di chiamate API personalizzate non è presente

    Examples:
      | dataExchangeMode | customThreshold |
      | sincrono         | 30              |
      | asincrono        | 30              |

  Scenario Outline: [CUSTOM_THRESHOLDS_VIEW_2]
  La sezione Soglie di chiamate API personalizzate viene mostrata sulla scheda e-service del fruitore in delega
  quando l'e-service richiede un attributo certificato, il fruitore delegato possiede quell'attributo, e la soglia
  per il fruitore è stata personalizzata. Il fruitore delegante che non ha l'attributo certificato non visualizza
  la sezione Soglie di chiamate API personalizzate.

    Given un admin del Comune di Milano
    And crea un e-service <dataExchangeMode> con un attributo certificato e soglia personalizzata per fruitore a <customThreshold>
      | consumerThreshold | 20 |
      | totalThreshold    | 40 |
    And un admin del Comune di Pozzallo conferisce in delega di fruizione l'e-service all'Agenzia per l'Italia Digitale
    And un admin dell'Agenzia per l'Italia Digitale accetta la delega in fruizione
    And un admin dell'Agenzia per l'Italia Digitale possiede quell'attributo certificato
    When visualizza la pagina dell'e-service dal catalogo
    Then la sezione soglie di chiamate API personalizzate è presente e mostra il valore <customThreshold>

    When un admin del Comune di Pozzallo visualizza la pagina dell'e-service dal catalogo
    Then la sezione soglie di chiamate API personalizzate non è presente

    Examples:
      | dataExchangeMode | customThreshold |
      | sincrono         | 30              |
      | asincrono        | 30              |

  Scenario Outline: [CUSTOM_THRESHOLDS_VIEW_3]
  La sezione Soglie di chiamate API personalizzate non viene mostrata sulla scheda e-service del fruitore
  quando l'e-service non richiede un attributo certificato.

    Given un admin del Comune di Milano
    And crea un e-service <dataExchangeMode>
    When un admin dell'Agenzia per l'Italia Digitale visualizza la pagina dell'e-service dal catalogo
    Then la sezione soglie di chiamate API personalizzate non è presente

    Examples:
      | dataExchangeMode |
      | sincrono         |
      | asincrono        |

  Scenario Outline: [CUSTOM_THRESHOLDS_VIEW_4]
  La sezione Soglie di chiamate API personalizzate non viene mostrata sulla scheda e-service del fruitore
  quando l'e-service richiede un attributo certificato ma la soglia per il fruitore non è stata personalizzata.

    Given un admin del Comune di Milano
    And crea un e-service <dataExchangeMode> con un attributo certificato
      | consumerThreshold | 20 |
      | totalThreshold    | 40 |
    And un admin dell'Agenzia per l'Italia Digitale possiede quell'attributo certificato
    When visualizza la pagina dell'e-service dal catalogo
    Then la sezione soglie di chiamate API personalizzate non è presente

    Examples:
      | dataExchangeMode |
      | sincrono         |
      | asincrono        |
