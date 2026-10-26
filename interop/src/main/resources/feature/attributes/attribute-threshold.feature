@eservice-attribute-threshold
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Soglie differenziate

  Scenario Outline: [ATTRIBUTE_THRESHOLD_REMOVE_ESERVICE] Rimozione della soglia differenziata impostata per l'attributo certificato dell'e-service.
  Dato un e-service in bozza o pubblicato con un attributo certificato e una soglia personalizzata configurata
  quando l'erogatore rimuove la soglia associata all'attributo certificato
  allora l'attributo non ha più una soglia personalizzata definita

    Given un e-service in <status>, creato dal Comune di Milano, con un attributo certificato e una soglia personalizzata per fruitore pari a 100
      | consumerThreshold | 50   |
      | totalThreshold    | 1000 |
    When l'utente rimuove la soglia personalizzata dall'attributo certificato 1 del gruppo 1 dall'e-service
    Then viene confermato l'esito di successo per la rimozione della soglia personalizzata

    Examples:
      | status     |
      | bozza      |
      | pubblicato |

  Scenario Outline: [ATTRIBUTE_THRESHOLD_REMOVE_ESERVICE_NEW_VERSION] Rimozione della soglia differenziata impostata per l'attributo certificato della nuova versione dell'e-service.
  Dato una nuova versione dell'e-service in bozza o pubblicata con un attributo certificato e una soglia personalizzata configurata
  quando l'erogatore rimuove la soglia associata all'attributo certificato
  allora l'attributo non ha più una soglia personalizzata definita

    Given una nuova versione dell'e-service in <status>, creata dal Comune di Milano, con un attributo certificato e una soglia personalizzata per fruitore pari a 100
      | consumerThreshold | 50   |
      | totalThreshold    | 1000 |
    When l'utente rimuove la soglia personalizzata dall'attributo certificato 1 del gruppo 1 dall'e-service
    Then viene confermato l'esito di successo per la rimozione della soglia personalizzata

    Examples:
      | status     |
      | bozza      |
      | pubblicato |

  Scenario: [ATTRIBUTE_THRESHOLD_REMOVE_ESERVICE_INSTANCE_TEMPLATE] Rimozione della soglia differenziata impostata per l'attributo certificato dell'istanza di e-service template in bozza.
  Dato un'istanza di template e-service in bozza con un attributo certificato e una soglia personalizzata configurata
  quando l'erogatore rimuove la soglia associata all'attributo certificato
  allora l'attributo non ha più una soglia personalizzata definita

    Given una istanza dell'e-service template in bozza creato dal Comune di Milano con un attributo certificato e una soglia personalizzata di 100
    When l'utente rimuove la soglia personalizzata dall'attributo certificato 1 del gruppo 1 dall'e-service
    Then viene confermato l'esito di successo per la rimozione della soglia personalizzata
