@producer-keychain-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei portachiavi dell'erogatore

  Come amministratore di un ente erogatore
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un portachiavi o una sua chiave non può essere gestito
  Per comprendere il problema e correggere i dati.

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_1] - Modifica di un portachiavi con un link nei dati generali
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali del portachiavi
    And l'utente tenta di salvare un link nella descrizione del portachiavi lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__PATCH_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_2] - Creazione di un portachiavi con un link nei dati generali
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un nome univoco disponibile per un nuovo portachiavi
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un portachiavi dell'erogatore
    And l'utente tenta di creare il portachiavi con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCER_KEYCHAINS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_3] - Inserimento di una chiave con un link nel nome
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And una nuova chiave pubblica RSA di almeno 2048 bit in formato PEM non registrata su PDND
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi la chiave pubblica predisposta con un link nel nome e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_4] - Inserimento di una chiave RSA troppo corta
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And una nuova chiave pubblica RSA di 1024 bit completa in formato PEM non registrata su PDND
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi la chiave pubblica predisposta con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_INVALID_KEY_LENGTH__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_5] - Inserimento di una chiave pubblica incompleta
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And una fixture di chiave pubblica PEM con contenuto troncato, senza chiavi private, certificati o blocchi aggiuntivi
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi la fixture crittografica predisposta con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_INVALID_PUBLIC_KEY__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_6] - Inserimento di un certificato al posto della chiave pubblica
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And un certificato X.509 di test in formato PEM contenente una nuova chiave pubblica RSA di almeno 2048 bit
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi il certificato predisposto al posto della chiave pubblica con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_ALLOWED_CERTIFICATE_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_7] - Inserimento di più chiavi nello stesso campo
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And una fixture con due blocchi PEM completi di nuove chiavi pubbliche RSA di almeno 2048 bit non registrate su PDND
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi entrambi i blocchi predisposti nello stesso campo con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_ALLOWED_MULTIPLE_KEYS_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_8] - Inserimento di una chiave privata al posto della pubblica
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And una nuova chiave privata RSA di test di almeno 2048 bit in formato PEM
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi la chiave privata predisposta al posto della pubblica con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_ALLOWED_PRIVATE_KEY_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PRODUCER_KEYCHAIN_ERROR_MESSAGE_9] - Inserimento di una chiave pubblica non RSA
    Given un portachiavi creato dal Comune di Milano e dedicato allo scenario
    And una nuova chiave pubblica EC completa e valida in formato PEM
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al portachiavi la chiave pubblica EC predisposta con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_AN_RSAKEY__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
