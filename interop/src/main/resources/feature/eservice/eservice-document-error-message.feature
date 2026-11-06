@eservice-document-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei documenti delle versioni degli e-service

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un documento non può essere gestito
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  # Feature in attesa dell'implementazione degli step mancanti.
  # Ogni scenario utilizza e-service e documenti dedicati; i riferimenti nominati sono espliciti.
  # I documenti di supporto sono distinti dall'interfaccia tecnica della versione.
  # I casi di nome e contenuto duplicati devono violare una sola regola alla volta.
  # I Given concorrenti non devono aggiornare la pagina né sostituire l'identità WEB.
  # Il setup delle deleghe deve evitare interferenze sulla disponibilità condivisa degli enti.
  # La lingua va verificata nell'interfaccia; il Then verifica la snackbar visibile
  # e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.
  # Verificare la riproducibilità WEB senza aggirare controlli UI o simulare risposte.

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
