@attribute-registry-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella creazione degli attributi

  Come amministratore di un ente abilitato alla creazione degli attributi
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un attributo non può essere creato
  Per comprendere il problema e correggere i dati inseriti.

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_1] - Creazione di un attributo certificato con un nome già esistente
    Given un admin del Comune di Milano crea un attributo certificato
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo certificato
    And l'utente tenta di creare un attributo certificato con lo stesso nome dell'attributo certificato creato e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_CERTIFIED_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_2] - Creazione di un attributo certificato con un link nella descrizione
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un nome univoco disponibile per un nuovo attributo certificato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo certificato
    And l'utente tenta di creare un attributo certificato con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_CERTIFIED_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_3] - Creazione di un attributo certificato discreto con un nome già esistente
    Given i seguenti attributi discreti creati da un admin del Comune di Milano:
      | alias          |
      | DISC-esistente |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo certificato discreto
    And l'utente tenta di creare un attributo certificato discreto con lo stesso nome dell'attributo certificato discreto creato e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_CERTIFIED_DISCRETE_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_4] - Creazione di un attributo dichiarato con un nome già esistente
    Given i seguenti attributi dichiarati creati da un admin del Comune di Milano:
      | alias          |
      | DECL-esistente |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo dichiarato
    And l'utente tenta di creare un attributo dichiarato con lo stesso nome dell'attributo dichiarato creato e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_DECLARED_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_5] - Creazione di un attributo dichiarato con un link nella descrizione
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un nome univoco disponibile per un nuovo attributo dichiarato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo dichiarato
    And l'utente tenta di creare un attributo dichiarato con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_DECLARED_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_6] - Creazione di un attributo verificato con un nome già esistente
    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias         |
      | VER-esistente |
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo verificato
    And l'utente tenta di creare un attributo verificato con lo stesso nome dell'attributo verificato creato e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_VERIFIED_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ATTRIBUTE_REGISTRY_ERROR_MESSAGE_7] - Creazione di un attributo verificato con un link nella descrizione
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un nome univoco disponibile per un nuovo attributo verificato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un attributo verificato
    And l'utente tenta di creare un attributo verificato con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_VERIFIED_ATTRIBUTES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
