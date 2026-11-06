@user-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella consultazione dei membri dell'ente

  Come amministratore di un ente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un membro non è più disponibile
  Per sapere che devo aggiornare l'elenco degli utenti.

  Scenario Outline: [USER_ERROR_MESSAGE_1] - Consultazione di un membro non più attivo
    Given un membro di test attivo del Comune di Milano, distinto dall'amministratore e identificato come "membro da consultare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'elenco dei membri del proprio ente e individua il "membro da consultare"
    Given il "membro da consultare" non risulta più tra gli utenti attivi dell'ente
    When l'utente tenta di aprire i dettagli del "membro da consultare" senza aggiornare l'elenco
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_USER_NOT_FOUND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
