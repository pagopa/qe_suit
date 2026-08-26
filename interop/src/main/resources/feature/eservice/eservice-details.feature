Feature: Creazione di nuova versione di un e-service - Validazione dello step di creazione di nuova versione dell'eservice e successiva consultazione delle versioni vecchie (Frontend) - Test Comportamentale
  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare la corretta impostazione dell'interfaccia FE
  ***
  @WEB
  Scenario Outline: [VALIDATE_FE_ESERVICE_DETAILS_PAGE]
    Given un EService creato dal <erogatore> con una richiesta di fruizione associata dal <fruitore>
    When <erogatore> crea una nuova versione del EService
    Then il <potenzialeFruitore> consulta la pagina dell'eservice e trova il pulsante di richiesta di fruizione disabilitato per tutte le versioni antecedenti l'ultima

    Examples:
      | erogatore           |           fruitore          |       potenzialeFruitore        |
      |  Comune di Milano   |    Comune di Milano         |       Comune di Pozzallo        |
      |  Comune di Milano   |    Comune di Comun Nuovo    |       Comune di Pozzallo        |
