Feature:
  Come utente

  Scenario: [CREATE_ASYNC_ESERVICE_TECHNICAL_SPECIFICATION_1]
    Given un admin del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    And compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | asyncExchange | mode |
      | true          |      |
    And clicca sul button 'Salva bozza e prosegui'
    And si trova allo step Soglie e attributi
    And clicca sul button 'Salva bozza e prosegui'
    And si trova allo step Specifiche tecniche
    When cancella i valori da tutti gli input delle specifiche tecniche
    And clicca sul button 'Salva bozza e prosegui'
