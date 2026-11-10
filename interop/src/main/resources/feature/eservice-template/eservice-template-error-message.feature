@eservice-template-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei template e-service

  Come amministratore di un ente creatore di template e-service
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'operazione non può essere completata
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | alias               | tecnologia | stato | interfaccia               |
      | template principale | REST       | DRAFT | openapi3-riferimenti.yaml |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_1] - Download di un documento non più disponibile
    Given un documento valido allegato alla versione in bozza del template "template principale" e identificato come "documento da scaricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    Given il "documento da scaricare" viene eliminato da un'altra sessione del Comune di Milano
    When l'utente tenta di scaricare il "documento da scaricare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DOCUMENT_NOT_FOUND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_2] - Modifica dei dati generali in bozza con nome duplicato
    Given un altro template e-service del Comune di Milano con nome diverso e identificato come "template esistente"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali in bozza del template "template principale"
    And l'utente tenta di salvare il nome del "template esistente" come nome del "template principale" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_3] - Modifica dei dati generali in bozza con un link
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali in bozza del template "template principale"
    And l'utente tenta di salvare un link nella descrizione del template "template principale" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_4] - Modifica del nome con conflitto su un e-service derivato
    Given il template "template principale" viene pubblicato e istanziato da PagoPA
    And un e-service indipendente di PagoPA il cui nome coinciderebbe con quello dell'istanza dopo la rinomina del template "template principale"
    And il nuovo nome predisposto per il template non è usato da altri template e-service
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali del template "template principale"
    And l'utente tenta di salvare il nuovo nome predisposto per il template "template principale" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_INSTANCE_NAME_CONFLICT__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_5] - Creazione di un template con nome duplicato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un template e-service
    And l'utente tenta di creare un template e-service con il nome del "template principale" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__POST_ESERVICES_TEMPLATES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_6] - Creazione di un template con un link nei dati generali
    Given un nome univoco disponibile per un nuovo template e-service
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un template e-service
    And l'utente tenta di creare un template e-service con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_7] - Aggiornamento della descrizione del template con un link
    Given il template "template principale" viene pubblicato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento della sola descrizione del template "template principale"
    And l'utente tenta di salvare un link nella descrizione del template "template principale" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_DESCRIPTION_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_8] - Aggiornamento dei destinatari del template con un link
    Given il template "template principale" viene pubblicato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del campo "A chi è rivolto il template" del template "template principale"
    And l'utente tenta di salvare un link nel campo "A chi è rivolto il template" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_INTENDED_TARGET_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_9] - Aggiornamento del solo nome con un nome duplicato
    Given il template "template principale" viene pubblicato
    And un altro template e-service del Comune di Milano con nome diverso e identificato come "template esistente"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del solo nome del template "template principale"
    And l'utente tenta di salvare il nome del "template esistente" come nome del "template principale" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_NAME_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_10] - Aggiornamento del solo nome con un link
    Given il template "template principale" viene pubblicato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento del solo nome del template "template principale"
    And l'utente tenta di salvare un nome contenente un link per il template "template principale" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_NAME_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_11] - Aggiunta di una finalità con nome già presente nel template
    Given un template e-service in erogazione inversa creato dal Comune di Milano e identificato come "template inverso"
    And una finalità con analisi del rischio valida nel "template inverso" e identificata come "finalità esistente"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiunta di una finalità nel "template inverso"
    And l'utente tenta di salvare una nuova finalità con il nome della "finalità esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_RISK_ANALYSIS_NAME_DUPLICATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_12] - Modifica della descrizione della versione con un link
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della versione in bozza del template "template principale"
    And l'utente tenta di salvare un link nella descrizione della versione lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_13] - Caricamento di un documento con contenuto duplicato
    Given un documento valido allegato alla versione in bozza del template "template principale" e identificato come "documento esistente"
    And una copia del "documento esistente" con lo stesso contenuto e un nome visualizzato diverso e non utilizzato nella versione, identificata come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di allegare il "documento da caricare" alla versione del template "template principale"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_CHECKSUM_DUPLICATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_14] - Caricamento di un documento con nome duplicato
    Given un documento valido allegato alla versione in bozza del template "template principale" e identificato come "documento esistente"
    And un altro documento valido con contenuto diverso dal "documento esistente" e lo stesso nome visualizzato, identificato come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di allegare il "documento da caricare" alla versione del template "template principale"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_15] - Caricamento di un documento con un link nel nome
    Given un documento valido con nome file privo di link e contenuto non ancora allegato alla versione del template "template principale", identificato come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di allegare il "documento da caricare" con un link nel nome visualizzato e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_16] - Rinomina di un documento con un nome già presente nella versione
    Given due documenti validi con nomi e contenuti diversi allegati alla versione in bozza del template "template principale" e identificati come "documento esistente" e "documento da rinominare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di rinominare il "documento da rinominare" con il nome visualizzato del "documento esistente"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_17] - Rinomina di un documento con un link
    Given un documento valido allegato alla versione in bozza del template "template principale" e identificato come "documento da rinominare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di rinominare il "documento da rinominare" inserendo un link nel nome visualizzato e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_18] - Caricamento di un documento oltre la dimensione consentita
    Given un documento valido salvo la dimensione di 11 MiB, con nome e contenuto non ancora presenti nella versione del template "template principale", identificato come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di allegare il "documento da caricare" alla versione del template "template principale"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_CONTENT_TOO_LARGE_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_19] - Caricamento di un file su una bozza di versione non più disponibile
    Given il template "template principale" viene pubblicato e viene creata una nuova versione in bozza identificata come "versione destinataria"
    And un documento valido con nome e contenuto non ancora presenti nella "versione destinataria", identificato come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiunta di un documento alla "versione destinataria" e predispone il "documento da caricare" senza confermare
    Given la "versione destinataria" del template "template principale" viene eliminata da un'altra sessione del Comune di Milano
    When l'utente tenta di confermare il caricamento del "documento da caricare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_ESERVICE_TEMPLATE_VERSION_NOT_FOUND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_20] - Caricamento di un WSDL senza indirizzo del servizio
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia              |
      | SOAP       | DRAFT | soap11-un-indirizzo.wsdl |
    And il template e-service preparato è identificato come "template SOAP"
    And un file WSDL riconoscibile e ben formato, privo dell'indirizzo del servizio ma con le altre definizioni valide, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia SOAP della versione in bozza del "template SOAP"
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_INTERFACE_EXTRACTING_SOAP_FIELD_VALUE_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_21] - Caricamento di un file di cui non è possibile riconoscere il formato
    Given un file di test con formato non riconoscibile, nome valido e dimensione consentita, identificato come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di allegare il "documento da caricare" alla versione del template "template principale"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_INVALID_CONTENT_TYPE_DETECTED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_22] - Caricamento di un'interfaccia REST con struttura non valida
    Given un file OpenAPI di versione supportata e formato riconoscibile ma con struttura dell'interfaccia non valida, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia REST della versione in bozza del "template principale"
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_INVALID_ESERVICE_INTERFACE_FILE_DETECTED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_23] - Caricamento di un file riconoscibile ma non supportato
    Given un file di test con formato riconoscibile ma non supportato per i documenti del template, nome valido e dimensione consentita, identificato come "documento da caricare"
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre i documenti della versione del template "template principale"
    And l'utente tenta di allegare il "documento da caricare" alla versione del template "template principale"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_INVALID_FILE_UPLOAD_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_24] - Caricamento di un'interfaccia con URL del server non valido
    Given un file OpenAPI di versione supportata valido salvo l'URL del server, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia REST della versione in bozza del "template principale"
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_INVALID_SERVER_URL" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_ERROR_MESSAGE_25] - Caricamento di un'interfaccia con versione OpenAPI non supportata
    Given un file di interfaccia riconoscibile con versione OpenAPI non supportata e gli altri dati validi, identificato come "interfaccia da caricare"
    And il file "interfaccia da caricare" ha nome valido e dimensione consentita
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il caricamento dell'interfaccia REST della versione in bozza del "template principale"
    And l'utente tenta di caricare l'"interfaccia da caricare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ESERVICE_TEMPLATE_OPENAPI_VERSION_NOT_RECOGNIZED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
