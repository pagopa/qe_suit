Feature:
  Come utente

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  ***

  Scenario Outline: [CREATE_ASYNC_ESERVICE_COMPONENT_VALIDATION_1]
    Given un <userRole> del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | name     | description | technology   | asyncExchange | personalData | mode |
      | $blank() | $blank()    | <technology> | true          |              |      |
    And clicca sul button 'Salva bozza e prosegui'
    Then la creazione non prosegue ed il campo Nome dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Descrizione dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo L’e-service eroga dati personali? dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"

    Examples:
      | userRole | technology |
      | admin    | REST       |
      | api      | SOAP       |

  Scenario Outline: [CREATE_ASYNC_ESERVICE_COMPONENT_VALIDATION_2]
    Given un <userRole> del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | technology   | asyncExchange | mode |
      | <technology> | true          |      |
    Then il radio group L'e-service eroga o riceve dati? è disabilitato

    Examples:
      | userRole | technology |
      | admin    | REST       |
      | api      | SOAP       |

  Scenario Outline: [CREATE_ASYNC_ESERVICE_COMPONENT_VALIDATION_3]
    Given un utente api del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | technology   | asyncExchange | mode |
      | <technology> | true          |      |
    And viene mostrato l'alert relativo al keychain in stile warning "Per gli scambi asincroni è necessario collegare un portachiavi all’e-service. Solo chi ha il ruolo di amministratore può farlo: chiedi di collegarlo prima o dopo la pubblicazione per abilitare lo scambio dei dati."

    Examples:
      | technology |
      | REST       |
      | SOAP       |

  Scenario Outline: [CREATE_ASYNC_ESERVICE_COMPONENT_VALIDATION_4]
    Given un <userRole> del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | technology | asyncExchange | mode |
      | SOAP       | true          |      |
    And viene mostrato l'alert relativo al SOAP in stile warning "La tecnologia SOAP non permette di abilitare il download a blocchi durante lo scambio asincrono dei dati."

    Examples:
      | userRole |
      | admin    |
      | api      |
