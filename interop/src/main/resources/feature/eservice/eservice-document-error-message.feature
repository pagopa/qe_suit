@eservice-document-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei documenti e delle interfacce delle versioni degli e-service

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un documento o un'interfaccia non può essere gestito
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_1] - Eliminazione di un documento dopo la revoca della delega
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido allegato alla versione in bozza e identificato come "documento da eliminare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di eliminare il "documento da eliminare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_2] - Caricamento di un documento con contenuto già presente nella versione
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido allegato alla versione in bozza e identificato come "documento esistente"
    And una copia del "documento esistente" con lo stesso contenuto e un nome visualizzato diverso e non utilizzato nella versione, identificata come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di allegare il "documento da caricare" come documento di supporto della versione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_CHECKSUM_DUPLICATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_3] - Caricamento di un documento con nome già presente nella versione
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido allegato alla versione in bozza e identificato come "documento esistente"
    And un altro documento di supporto valido con contenuto diverso dal "documento esistente" e lo stesso nome visualizzato, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di allegare il "documento da caricare" come documento di supporto della versione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_4] - Caricamento di un documento con un link nel nome
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido con nome file privo di link e contenuto non ancora allegato alla versione, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di allegare il "documento da caricare" con un link nel nome visualizzato e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_5] - Caricamento di un documento dopo la revoca della delega
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido con nome e contenuto non ancora presenti nella versione, identificato come "documento da caricare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiunta di un documento di supporto alla versione e predispone il "documento da caricare" senza confermare
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di confermare il caricamento del "documento da caricare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_6] - Rinomina di un documento con un nome già presente nella versione
    Given un e-service in bozza creato dal Comune di Milano
    And due documenti di supporto validi con nomi e contenuti diversi allegati alla versione, identificati come "documento esistente" e "documento da rinominare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di rinominare il "documento da rinominare" con il nome visualizzato del "documento esistente"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_7] - Rinomina di un documento con un link
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido allegato alla versione in bozza e identificato come "documento da rinominare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di rinominare il "documento da rinominare" inserendo un link nel nome visualizzato e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_8] - Rinomina di un documento dopo la revoca della delega
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido allegato alla versione in bozza e identificato come "documento da rinominare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la rinomina del "documento da rinominare" e prepara un nome univoco valido senza salvarlo
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di salvare il nuovo nome del "documento da rinominare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_9] - Caricamento di un documento oltre la dimensione consentita
    Given un e-service in bozza creato dal Comune di Milano
    And un documento di supporto valido salvo la dimensione di 11 MiB, con nome e contenuto non ancora presenti nella versione, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di allegare il "documento da caricare" come documento di supporto della versione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_CONTENT_TOO_LARGE_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_10] - Caricamento di un documento su una bozza di versione non più disponibile
    Given un e-service del Comune di Milano con una versione pubblicata e una nuova versione in bozza
    And la nuova versione in bozza è identificata come "versione destinataria"
    And un documento di supporto valido con nome e contenuto non ancora presenti nella versione, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiunta di un documento alla "versione destinataria" e predispone il "documento da caricare" senza confermare
    Given la "versione destinataria" viene eliminata da un'altra sessione del Comune di Milano
    When l'utente tenta di confermare il caricamento del "documento da caricare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_ESERVICE_DESCRIPTOR_NOT_FOUND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_11] - Caricamento di un WSDL senza indirizzo del servizio
    Given un e-service SOAP in bozza creato dal Comune di Milano
    And un file WSDL riconoscibile e ben formato, privo dell'indirizzo del servizio ma con le altre definizioni valide, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia SOAP della versione in bozza
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_INTERFACE_EXTRACTING_SOAP_FIELD_VALUE_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_12] - Caricamento di un file di cui non è possibile riconoscere il formato
    Given un e-service in bozza creato dal Comune di Milano
    And un file di test con formato non riconoscibile, nome valido e dimensione consentita, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di allegare il "documento da caricare" come documento di supporto della versione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_INVALID_CONTENT_TYPE_DETECTED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_13] - Caricamento di un'interfaccia REST con struttura non valida
    Given un e-service REST in bozza creato dal Comune di Milano
    And un file OpenAPI di versione supportata e formato riconoscibile ma con struttura dell'interfaccia non valida, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia REST della versione in bozza
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_INVALID_ESERVICE_INTERFACE_FILE_DETECTED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_14] - Caricamento di un file riconoscibile ma non supportato
    Given un e-service in bozza creato dal Comune di Milano
    And un file di test con formato riconoscibile ma non supportato per i documenti della versione, nome valido e dimensione consentita, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione in bozza dell'e-service
    And l'utente tenta di allegare il "documento da caricare" come documento di supporto della versione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_INVALID_FILE_UPLOAD_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_15] - Caricamento di un'interfaccia con URL del server non valido
    Given un e-service REST in bozza creato dal Comune di Milano
    And un file OpenAPI di versione supportata valido salvo l'URL del server, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia REST della versione in bozza
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_INVALID_SERVER_URL" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_DOCUMENT_ERROR_MESSAGE_16] - Caricamento di un'interfaccia con versione OpenAPI non supportata
    Given un e-service REST in bozza creato dal Comune di Milano
    And un file di interfaccia riconoscibile con versione OpenAPI non supportata e gli altri dati validi, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia REST della versione in bozza
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPENAPI_VERSION_NOT_RECOGNIZED__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
