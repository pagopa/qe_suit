@eservice
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Soglie differenziate

  Scenario: [ATTRIBUTE_REMOVE_THRESHOLD_1] - Rimozione della soglia differenziata impostata per l'attributo certificato dell'e-service in bozza.
  Dato un EService in bozza con un attributo certificato e una soglia personalizzata configurata
  quando l'erogatore rimuove la soglia associata all'attributo certificato
  allora l'attributo non ha più una soglia personalizzata definita

    # Given un e-service in bozza creato dal Comune di Milano con un attributo certificato e una soglia personalizzata
    When l'utente rimuove la soglia personalizzata dall'attributo certificato 1 del gruppo 1 dall'e-service
    Then viene confermato l'esito di successo per la rimozione della soglia personalizzata

  # Scenario: [ATTRIBUTE_REMOVE_THRESHOLD_2] - Rimozione della soglia differenziata impostata per l'attributo certificato dell'e-service pubblicato.
    # Given un e-service attivo creato dal Comune di Milano con un attributo certificato e una soglia personalizzata
    # When l'utente rimuove la soglia personalizzata dall'attributo certificato 1 del gruppo 1 dall'e-service attivo
    # Then viene mostrata la snackbar con un messaggio di successo contenente "Hai rimosso la soglia di chiamate API dell’attributo certificato."

  # Scenario: [ATTRIBUTE_REMOVE_THRESHOLD_3] - Rimozione della soglia differenziata impostata per l'attributo certificato della nuova versione e-service in bozza.
    # Given una nuova versione dell'e-service in bozza creata dal Comune di Milano con un attributo certificato e una soglia personalizzata

  # Scenario: [ATTRIBUTE_REMOVE_THRESHOLD_3] - Rimozione della soglia differenziata impostata per l'attributo certificato della nuova versione e-service pubblicato.
    # Given una nuova versione dell'e-service attiva creata dal Comune di Milano con un attributo certificato e una soglia personalizzata

  # Scenario: [ATTRIBUTE_REMOVE_THRESHOLD_5] - Rimozione della soglia differenziata impostata per l'attributo certificato dell'istanza di e-service template in bozza.
    # Given una istanza dell'e-service template in bozza creato dal Comune di Milano con un attributo certificato e una soglia personalizzata
    # When l'utente rimuove la soglia personalizzata dall'attributo certificato 1 del gruppo 1 dall'e-service attivo
