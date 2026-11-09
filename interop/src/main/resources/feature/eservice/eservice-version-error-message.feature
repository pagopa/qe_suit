@eservice-version-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione delle versioni degli e-service

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando una versione non può essere gestita
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_1] - Eliminazione della bozza di versione dopo la revoca della delega
    Given i seguenti e-service pubblicati:
      | alias     | erogatore        |
      | e-service | Comune di Milano |
    And la versione 2 dell'e-service "e-service" del Comune di Milano in bozza
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della versione in bozza dell'e-service
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di eliminare la versione in bozza senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_2] - Creazione di una versione quando la precedente contiene un link
    Given un e-service del Comune di Milano con una versione pubblicata contenente un link nella descrizione e nessuna nuova versione in bozza
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di creare una nuova versione dell'e-service a partire dalla versione pubblicata
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_3] - Creazione di una versione dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con una delega in erogazione al Comune di Pozzallo in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della versione pubblicata per crearne una nuova
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di creare una nuova versione dell'e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_4] - Riattivazione di una versione dopo la revoca della delega
    Given i seguenti e-service pubblicati:
      | alias     | erogatore        |
      | e-service | Comune di Milano |
    And la versione 1 dell'e-service "e-service" del Comune di Milano sospesa
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della versione sospesa dell'e-service
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di riattivare la versione sospesa senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_ACTIVATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_5] - Modifica della modalità di approvazione dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della modalità di approvazione delle richieste di fruizione e seleziona una modalità alternativa valida
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare la modalità di approvazione selezionata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_AGREEMENT_APPROVAL_POLICY_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_6] - Modifica dei requisiti di accesso dopo la revoca della delega
    Given i seguenti e-service in bozza:
      | erogatore        |
      | Comune di Milano |
    And i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias           |
      | VER-disponibile |
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei requisiti di accesso della versione e prepara una variazione valida senza salvarla
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare i requisiti di accesso della versione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_ATTRIBUTES_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_7] - Richiesta di pubblicazione di una versione dopo la revoca della delega
    Given un e-service del Comune di Milano con una versione in bozza e tutti i requisiti di pubblicazione soddisfatti
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il riepilogo della versione per richiederne la pubblicazione al delegante
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di richiedere la pubblicazione della versione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_PUBLISH" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_8] - Rifiuto della pubblicazione con un link nella motivazione
    Given i seguenti e-service in bozza:
      | alias     | erogatore        |
      | e-service | Comune di Milano |
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And la versione 1 dell'e-service "e-service" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la valutazione della richiesta di pubblicazione della versione
    And l'utente tenta di rifiutare la pubblicazione della versione inserendo un link nella motivazione e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_9] - Sospensione di una versione dopo la revoca della delega
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della versione pubblicata dell'e-service
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di sospendere la versione pubblicata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUSPEND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_VERSION_ERROR_MESSAGE_10] - Salvataggio delle modifiche di una versione dopo la revoca della delega
    Given un e-service del Comune di Milano con una versione in bozza configurata con dati validi
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati della versione e prepara una variazione valida senza salvarla
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare le modifiche della versione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
