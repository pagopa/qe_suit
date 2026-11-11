@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per la verifica della client assertion

  # POST /tools/validateTokenGeneration · tenantNotAllowed
  Scenario: [ERROR_180] L’operatore autorizzato verifica la client assertion — una client assertion di test valida per un client di un altro ente
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato
    And un admin appartenente al fruitore PagoPA
    When l’operatore autorizzato verifica la client assertion
    Then il messaggio di errore corrisponde al caso "BFF_TENANT_NOT_ALLOWED"
