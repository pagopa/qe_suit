@client-assertion-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella verifica della client assertion di un altro ente

  Come amministratore di un ente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando verifico una client assertion di un altro ente
  Per sapere che devo cambiare profilo o utilizzare un client del mio ente.

  Scenario Outline: [CLIENT_ASSERTION_ERROR_MESSAGE_1] - Verifica di una client assertion appartenente a un altro ente
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di verificare la client assertion del client creato per PagoPA dallo strumento del Comune di Milano
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_TENANT_NOT_ALLOWED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
