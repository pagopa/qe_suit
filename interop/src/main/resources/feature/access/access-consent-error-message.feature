@access-consent-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella verifica e nella registrazione dei consensi di accesso

  Come utente di un ente aderente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando non è possibile verificare o registrare i consensi
  Per comprendere perché non posso accedere alla piattaforma.

  # Feature in attesa dell'implementazione degli step e di precondizioni reali riproducibili.
  # Informativa non trovata e informativa assente dalla configurazione sono casi distinti.
  # Non modificare configurazioni condivise o risorse di ambiente per preparare questi scenari.
  # Se il caso non è riproducibile con fixture isolate autorizzate, mantenerlo non verificato.
  # Non simulare risposte né aggirare controlli UI; la lingua va verificata anche prima dell'accesso.
  # Il Then verifica la snackbar visibile e il testo esatto del catalogo, senza SUPPORT_FOOTER.

  Scenario Outline: [ACCESS_CONSENT_ERROR_MESSAGE_1] - Verifica del consenso con informativa non trovata
    Given un utente autenticato del Comune di Milano con ruolo admin
    And una precondizione isolata di accesso con tipo di consenso configurato ma relativa informativa non trovata
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di accedere alla piattaforma avviando la verifica dei consensi richiesti
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_PRIVACY_NOTICE_NOT_FOUND__GET_USER_CONSENT_CONSENT_TYPE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ACCESS_CONSENT_ERROR_MESSAGE_2] - Verifica del consenso con informativa assente dalla configurazione
    Given un utente autenticato del Comune di Milano con ruolo admin
    And una precondizione isolata di accesso con informativa richiesta assente dalla configurazione
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di accedere alla piattaforma avviando la verifica dei consensi richiesti
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_PRIVACY_NOTICE_NOT_FOUND_IN_CONFIGURATION" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ACCESS_CONSENT_ERROR_MESSAGE_3] - Registrazione del consenso con informativa non più disponibile
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un consenso richiesto non ancora registrato per l'utente con informativa inizialmente disponibile
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre le informative richieste per l'accesso e predispone le conferme necessarie senza inviarle
    Given una precondizione isolata rende non più reperibile l'informativa visualizzata mantenendo configurato il tipo di consenso
    When l'utente tenta di confermare i consensi senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_PRIVACY_NOTICE_NOT_FOUND__POST_USER_CONSENT_CONSENT_TYPE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
