@eservice-template-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei template e-service

  Come amministratore di un ente creatore di template e-service
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'operazione non può essere completata
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  # Feature in attesa dell'implementazione degli step mancanti.
  # Ogni scenario utilizza template, istanze e documenti dedicati.
  # I riferimenti nominati identificano fixture specifiche, non l'ultima entità creata.
  # I Given concorrenti non devono aggiornare la pagina né sostituire l'identità WEB.
  # La selezione della lingua deve essere verificata nell'interfaccia, anche per l'italiano.
  # Il Then verifica la snackbar visibile e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.
  # La modifica dei dati generali in bozza e gli aggiornamenti di singoli campi dopo la pubblicazione
  # sono mapping distinti: verificare il flusso WEB corrispondente prima di implementare i binding.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.yaml |
    And il template e-service preparato è identificato come "template principale"

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
