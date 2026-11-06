@client-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei client e delle loro chiavi

  Come amministratore o operatore di sicurezza di un ente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un client o una sua chiave non può essere gestito
  Per comprendere il problema e correggere i dati.

  # Feature in attesa dell'implementazione degli step mancanti.
  # Ogni scenario utilizza client e materiale crittografico di test dedicati.
  # I riferimenti nominati identificano fixture specifiche, non l'ultima entità creata.
  # Le chiavi sono generate per il test; non utilizzare credenziali reali o condivise.
  # Ogni fixture non valida deve violare soltanto la regola oggetto del messaggio.
  # La lingua va verificata nell'interfaccia; il Then verifica la snackbar visibile
  # e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.
  # La riproducibilità WEB resta da verificare, senza aggirare controlli UI o simulare risposte.

  Scenario Outline: [CLIENT_ERROR_MESSAGE_1] - Eliminazione di una chiave aggiunta da un altro operatore
    Given un client API di PagoPA con due operatori di sicurezza distinti associati, identificati come "autore della chiave" e "operatore corrente"
    And una chiave pubblica RSA valida aggiunta dall'"autore della chiave" al client e identificata come "chiave da eliminare"
    And l'"operatore corrente" è autenticato esclusivamente con ruolo security
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre le chiavi del client
    And l'utente tenta di eliminare la "chiave da eliminare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_USER_NOT_ALLOWED_TO_DELETE_CLIENT_KEY" in lingua "<lingua>" con i valori delle fixture:
      | placeholder | valore                              |
      | keyName     | nome della "chiave da eliminare"     |

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_2] - Modifica di un client con un link nei dati generali
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali del client
    And l'utente tenta di salvare un link nella descrizione del client lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__PATCH_CLIENTS_CLIENT_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_3] - Inserimento di una chiave con un link nel nome
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una nuova chiave pubblica RSA di almeno 2048 bit in formato PEM non registrata su PDND
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client la chiave pubblica predisposta con un link nel nome e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_4] - Inserimento di una chiave RSA troppo corta
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una nuova chiave pubblica RSA di 1024 bit completa in formato PEM non registrata su PDND
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client la chiave pubblica predisposta con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_INVALID_KEY_LENGTH__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_5] - Inserimento di una chiave pubblica incompleta
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una fixture di chiave pubblica PEM con contenuto troncato, senza chiavi private, certificati o blocchi aggiuntivi
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client la fixture crittografica predisposta con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_INVALID_PUBLIC_KEY__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_6] - Inserimento di una chiave già registrata su PDND
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And la chiave pubblica del client è identificata come "chiave già registrata"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere nuovamente al client la "chiave già registrata" con un nome diverso, univoco e privo di link
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_KEY_ALREADY_EXISTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_7] - Inserimento di un certificato al posto della chiave pubblica
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And un certificato X.509 di test in formato PEM contenente una nuova chiave pubblica RSA di almeno 2048 bit
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client il certificato predisposto al posto della chiave pubblica con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_ALLOWED_CERTIFICATE_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_8] - Inserimento di più chiavi nello stesso campo
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una fixture con due blocchi PEM completi di nuove chiavi pubbliche RSA di almeno 2048 bit non registrate su PDND
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client entrambi i blocchi predisposti nello stesso campo con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_ALLOWED_MULTIPLE_KEYS_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_9] - Inserimento di una chiave privata al posto della pubblica
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una nuova chiave privata RSA di test di almeno 2048 bit in formato PEM
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client la chiave privata predisposta al posto della pubblica con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_ALLOWED_PRIVATE_KEY_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_10] - Inserimento di una chiave pubblica non RSA
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And una nuova chiave pubblica EC completa e valida in formato PEM
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di aggiungere al client la chiave pubblica EC predisposta con un nome univoco privo di link e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_NOT_AN_RSAKEY__POST_CLIENTS_CLIENT_ID_KEYS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_11] - Creazione di un client API Interop con un link nei dati generali
    Given un utente autenticato di PagoPA con ruolo admin
    And un nome univoco disponibile per un nuovo client API
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un client API Interop
    And l'utente tenta di creare il client con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_API" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [CLIENT_ERROR_MESSAGE_12] - Creazione di un client fruitore con un link nei dati generali
    Given un utente autenticato di PagoPA con ruolo admin
    And un nome univoco disponibile per un nuovo client CONSUMER
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di un client fruitore
    And l'utente tenta di creare il client con il nome univoco predisposto, un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_CONSUMER" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
