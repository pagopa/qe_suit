@eservice-template-instance-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione degli e-service istanziati da template

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'istanza da template non può essere gestita
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_1] - Creazione di un'istanza con parola identificativa duplicata
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.yaml |
    And un'istanza del template creata da PagoPA con parola identificativa univoca, identificata come "istanza esistente"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un e-service dal template
    And l'utente tenta di creare un'istanza con la parola identificativa dell'"istanza esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_TEMPLATE_ID_ESERVICES" in lingua "<lingua>" con i valori delle fixture:
      | placeholder     | valore                                               |
      | identifyingWord | parola identificativa dell'"istanza esistente"       |

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_2] - Creazione di un'istanza con un link nella parola identificativa
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.yaml |
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un e-service dal template
    And l'utente tenta di creare un'istanza con un link nella parola identificativa e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_TEMPLATE_ID_ESERVICES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_3] - Modifica dei dati generali di un'istanza con parola identificativa duplicata
    Given due istanze REST in bozza dello stesso template pubblicato, create dal Comune di Milano con parole identificative diverse e identificate come "istanza esistente" e "istanza da modificare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali dell'"istanza da modificare"
    And l'utente tenta di salvare la parola identificativa dell'"istanza esistente" per l'"istanza da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_ESERVICES_E_SERVICE_ID" in lingua "<lingua>" con i valori delle fixture:
      | placeholder     | valore                                               |
      | identifyingWord | parola identificativa dell'"istanza esistente"       |

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_4] - Modifica dei dati generali di un'istanza con un link nella parola identificativa
    Given un'istanza REST in bozza di un template pubblicato, creata dal Comune di Milano e identificata come "istanza da modificare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali dell'"istanza da modificare"
    And l'utente tenta di salvare un link nella parola identificativa dell'"istanza da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_ESERVICES_E_SERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_5] - Modifica dei dati generali dopo la revoca della delega
    Given un'istanza REST in bozza di un template pubblicato, creata dal Comune di Milano e identificata come "istanza da modificare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali dell'"istanza da modificare" e prepara una modifica valida senza salvarla
    Given la delega in erogazione dell'"istanza da modificare" viene revocata dal Comune di Milano
    When l'utente tenta di salvare i dati generali dell'"istanza da modificare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_6] - Modifica della bozza di versione dopo la revoca della delega
    Given un'istanza REST in bozza di un template pubblicato, creata dal Comune di Milano e identificata come "istanza da modificare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della versione in bozza dell'"istanza da modificare" e prepara una modifica valida senza salvarla
    Given la delega in erogazione dell'"istanza da modificare" viene revocata dal Comune di Milano
    When l'utente tenta di salvare la versione in bozza dell'"istanza da modificare" senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_7] - Configurazione REST con versione OpenAPI del template non riconosciuta
    Given un'istanza REST in bozza di un template con interfaccia avente una versione OpenAPI non riconosciuta, creata dal Comune di Milano e identificata come "istanza da configurare"
    And dati validi per la personalizzazione dell'interfaccia REST dell'"istanza da configurare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la configurazione dell'interfaccia REST dell'"istanza da configurare"
    And l'utente tenta di salvare i dati validi predisposti per l'interfaccia REST
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPENAPI_VERSION_NOT_RECOGNIZED__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_REST" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_8] - Configurazione REST dopo la revoca della delega
    Given un'istanza REST in bozza di un template pubblicato con interfaccia valida, creata dal Comune di Milano e identificata come "istanza da configurare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la configurazione dell'interfaccia REST dell'"istanza da configurare" e prepara dati validi senza salvarli
    Given la delega in erogazione dell'"istanza da configurare" viene revocata dal Comune di Milano
    When l'utente tenta di salvare i dati dell'interfaccia REST senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_REST" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_9] - Configurazione SOAP dopo la revoca della delega
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia                |
      | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl   |
    And l'erogatore istanzia un nuovo e-service dal template
    And l'e-service appena istanziato è identificato come "istanza da configurare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la configurazione dell'interfaccia SOAP dell'"istanza da configurare" e prepara indirizzi validi senza salvarli
    Given la delega in erogazione dell'"istanza da configurare" viene revocata dal Comune di Milano
    When l'utente tenta di salvare gli indirizzi dell'interfaccia SOAP senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_SOAP" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_10] - Modifica della versione pubblicata dopo la revoca della delega
    Given i seguenti template e-service pubblicati:
      | alias               | erogatore        | nome                | tecnologia |
      | template di origine | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias                 | template            | suffisso   |
      | istanza da modificare | template di origine | modificare |
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati aggiornabili della versione pubblicata dell'"istanza da modificare" e prepara una modifica valida senza salvarla
    Given la delega in erogazione dell'"istanza da modificare" viene revocata dal Comune di Milano
    When l'utente tenta di salvare le modifiche della versione pubblicata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_11] - Aggiornamento della sola parola identificativa con un valore duplicato
    Given i seguenti template e-service pubblicati:
      | alias               | erogatore        | nome                | tecnologia |
      | template di origine | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias                 | template            | suffisso   |
      | istanza esistente     | template di origine | esistente  |
      | istanza da modificare | template di origine | modificare |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento della sola parola identificativa dell'"istanza da modificare"
    And l'utente tenta di salvare la parola identificativa dell'"istanza esistente" per l'"istanza da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE" in lingua "<lingua>" con i valori delle fixture:
      | placeholder     | valore                                               |
      | identifyingWord | parola identificativa dell'"istanza esistente"       |

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_12] - Aggiornamento della sola parola identificativa con un link
    Given i seguenti template e-service pubblicati:
      | alias               | erogatore        | nome                | tecnologia |
      | template di origine | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias                 | template            | suffisso   |
      | istanza da modificare | template di origine | modificare |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento della sola parola identificativa dell'"istanza da modificare"
    And l'utente tenta di salvare un link nella parola identificativa dell'"istanza da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_13] - Aggiornamento della parola identificativa dopo la revoca della delega
    Given i seguenti template e-service pubblicati:
      | alias               | erogatore        | nome                | tecnologia |
      | template di origine | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias                 | template            | suffisso   |
      | istanza da modificare | template di origine | modificare |
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento della sola parola identificativa dell'"istanza da modificare" e prepara un valore valido non utilizzato senza salvarlo
    Given la delega in erogazione dell'"istanza da modificare" viene revocata dal Comune di Milano
    When l'utente tenta di salvare la nuova parola identificativa senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_TEMPLATE_INSTANCE_ERROR_MESSAGE_14] - Aggiornamento a una nuova versione del template dopo la revoca della delega
    Given un'istanza REST pubblicata creata dal Comune di Milano da una versione del template e identificata come "istanza da aggiornare"
    And una nuova versione del template pubblicata e disponibile per l'aggiornamento dell'"istanza da aggiornare"
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiornamento dell'"istanza da aggiornare" alla nuova versione del template
    Given la delega in erogazione dell'"istanza da aggiornare" viene revocata dal Comune di Milano
    When l'utente tenta di confermare l'aggiornamento alla nuova versione del template senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_UPGRADE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
