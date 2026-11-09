@eservice-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nei dati generali e nella configurazione degli e-service

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un e-service non può essere gestito
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_1] - Eliminazione di una bozza da parte del delegato
    Given i seguenti e-service in bozza:
      | alias     | erogatore        |
      | e-service | Comune di Milano |
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di eliminare l'e-service in bozza gestito in delega
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_2] - Modifica della bozza con nome di un e-service già esistente
    Given i seguenti e-service in bozza:
      | alias                   | erogatore        | nome                    |
      | e-service esistente     | Comune di Milano | E-service esistente     |
      | e-service da modificare | Comune di Milano | E-service da modificare |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali in bozza dell'"e-service da modificare"
    And l'utente tenta di salvare il nome dell'"e-service esistente" per l'"e-service da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__PATCH_ESERVICES_E_SERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_3] - Modifica della bozza con nome in conflitto con un template
    Given i seguenti e-service in bozza:
      | alias                   | erogatore        |
      | e-service da modificare | Comune di Milano |
    And un template e-service con nome diverso identificato come "template esistente" il cui nome non è utilizzato da altri e-service del Comune di Milano
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali in bozza dell'"e-service da modificare"
    And l'utente tenta di salvare il nome del "template esistente" per l'"e-service da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__PATCH_ESERVICES_E_SERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_4] - Modifica della bozza con un link nei dati generali
    Given i seguenti e-service in bozza:
      | alias                   | erogatore        |
      | e-service da modificare | Comune di Milano |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali in bozza dell'"e-service da modificare"
    And l'utente tenta di salvare un link nella descrizione dell'e-service lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__PATCH_ESERVICES_E_SERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_5] - Creazione di un e-service con nome duplicato
    Given i seguenti e-service in bozza:
      | alias               | erogatore        |
      | e-service esistente | Comune di Milano |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un e-service
    And l'utente tenta di creare un e-service con il nome dell'"e-service esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_ESERVICES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_6] - Creazione di un e-service con nome in conflitto con un template
    Given un template e-service identificato come "template esistente" il cui nome non è utilizzato da e-service del Comune di Milano
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un e-service
    And l'utente tenta di creare un e-service con il nome del "template esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__POST_ESERVICES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_7] - Creazione di un e-service con un link nei dati generali
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un nome univoco disponibile per un nuovo e-service e non in conflitto con un template
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un e-service
    And l'utente tenta di creare l'e-service con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_8] - Modifica delle preferenze di fruizione tramite delega dopo la revoca
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica delle preferenze di fruizione tramite delega e prepara una variazione valida senza salvarla
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare le preferenze di fruizione tramite delega senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DELEGATION_FLAGS_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_9] - Aggiornamento della sola descrizione con un link
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento della sola descrizione dell'e-service
    And l'utente tenta di salvare un link nella descrizione dell'e-service lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTION_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_10] - Aggiornamento della descrizione dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento della sola descrizione dell'e-service e prepara una modifica valida senza salvarla
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare la descrizione dell'e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTION_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_11] - Aggiornamento del solo nome con un nome duplicato
    Given i seguenti e-service pubblicati:
      | alias                   | erogatore        | nome                    |
      | e-service esistente     | Comune di Milano | E-service esistente     |
      | e-service da modificare | Comune di Milano | E-service da modificare |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del solo nome dell'"e-service da modificare"
    And l'utente tenta di salvare il nome dell'"e-service esistente" per l'"e-service da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_12] - Aggiornamento del solo nome con un nome in conflitto con un template
    Given i seguenti e-service pubblicati:
      | alias                   | erogatore        |
      | e-service da modificare | Comune di Milano |
    And un template e-service con nome diverso identificato come "template esistente" il cui nome non è utilizzato da altri e-service del Comune di Milano
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del solo nome dell'"e-service da modificare"
    And l'utente tenta di salvare il nome del "template esistente" per l'"e-service da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_13] - Aggiornamento del solo nome con un link
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del solo nome dell'e-service
    And l'utente tenta di salvare un nome contenente un link lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_14] - Aggiornamento del nome dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del solo nome dell'e-service e prepara un nome univoco valido senza salvarlo
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare il nuovo nome dell'e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_15] - Modifica dell'indicazione sui dati personali dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dell'indicazione sul trattamento dei dati personali e prepara una variazione valida senza salvarla
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare l'indicazione sul trattamento dei dati personali senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_PERSONAL_DATA_FLAG" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_16] - Modifica dell'adesione a Signal Hub dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dell'adesione a Signal Hub e prepara una variazione valida senza salvarla
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare l'adesione a Signal Hub senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_SIGNALHUB_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_17] - Consultazione di un e-service non più gestito in delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'elenco dei propri e-service e individua l'e-service gestito in delega
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di aprire il dettaglio dell'e-service dall'elenco senza aggiornarlo
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_INVALID_ESERVICE_REQUESTER__GET_PRODUCERS_ESERVICES_ESERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ERROR_MESSAGE_18] - Consultazione di una versione non più gestita in delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio dell'e-service gestito in delega e individua la versione pubblicata
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di aprire il dettaglio della versione pubblicata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_INVALID_ESERVICE_REQUESTER__GET_PRODUCERS_ESERVICES_ESERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
