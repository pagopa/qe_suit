@client-assertion-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella verifica della client assertion di un altro ente

  Come amministratore di un ente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando verifico una client assertion di un altro ente
  Per sapere che devo cambiare profilo o utilizzare un client del mio ente.

  # Feature in attesa dell'implementazione degli step mancanti.
  # L'operazione sotto test è l'invio della form WEB dello strumento, non una chiamata M2M.
  # Client e chiavi sono fixture dedicate; nessuna credenziale reale deve essere inserita nel file.
  # Il cambio di ente mantiene esplicito il client della assertion, senza sostituirlo con uno del nuovo ente.
  # Lo step di invio esistente attende i risultati della validazione: il tentativo negativo
  # deve riusare l'invio della form senza richiedere risultati assenti in caso di rifiuto.
  # La lingua va verificata nell'interfaccia; il Then verifica la snackbar visibile
  # e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.

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
