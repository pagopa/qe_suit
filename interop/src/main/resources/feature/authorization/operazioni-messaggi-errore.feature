@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per client, portachiavi e chiavi pubbliche

  # POST /clients/:clientId/keys · notAllowedPrivateKeyException
  Scenario: [ERROR_100] L'erogatore aggiunge al client la chiave di test "chiave-privata-effimera-pem" con il nome "Chiave di test" — una chiave privata effimera di test in PEM, generata per lo scenario
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "chiave-privata-effimera-pem" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_PRIVATE_KEY_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /clients/:clientId/keys · notAllowedCertificateException
  Scenario: [ERROR_101] L'erogatore aggiunge al client la chiave di test "certificato-x509" con il nome "Chiave di test" — un certificato X.509 PEM di test contenente una chiave pubblica
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "certificato-x509" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_CERTIFICATE_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /clients/:clientId/keys · notAllowedMultipleKeysException
  Scenario: [ERROR_102] L'erogatore aggiunge al client la chiave di test "due-chiavi-pubbliche-pem" con il nome "Chiave di test" — due blocchi di chiave pubblica PEM di test concatenati
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "due-chiavi-pubbliche-pem" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_MULTIPLE_KEYS_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /clients/:clientId/keys · invalidPublicKey
  Scenario: [ERROR_103] L'erogatore aggiunge al client la chiave di test "pem-incompleto" con il nome "Chiave di test" — un contenitore di chiavi valido e una chiave pubblica PEM di test incompleta
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "pem-incompleto" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_PUBLIC_KEY__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /clients/:clientId/keys · notAnRSAKey
  Scenario: [ERROR_104] L'erogatore aggiunge al client la chiave di test "ec-pubblica" con il nome "Chiave di test" — una chiave pubblica EC valida di test in PEM
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "ec-pubblica" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_AN_RSAKEY__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /clients/:clientId/keys · invalidKeyLength
  Scenario: [ERROR_105] L'erogatore aggiunge al client la chiave di test "rsa-pubblica-1024-bit" con il nome "Chiave di test" — un contenitore di chiavi valido e una chiave pubblica RSA PEM di test da 1024 bit
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "rsa-pubblica-1024-bit" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_KEY_LENGTH__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /clients/:clientId/keys · keyAlreadyExists
  # Riutilizzare la chiave pubblica già associata dal Given. Le fixture PEM devono essere dati di test.
  Scenario: [ERROR_106] L'erogatore aggiunge al client la chiave di test "rsa-pubblica-già-registrata" con il nome "Chiave di test" — una chiave pubblica di test già registrata e il contenitore abilitato
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "rsa-pubblica-già-registrata" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_KEY_ALREADY_EXISTS"

  # DELETE /clients/:clientId/keys/:keyId · userNotAllowedToDeleteClientKey
  Scenario: [ERROR_107] L’operatore di sicurezza che non ha aggiunto la chiave elimina la chiave del client — un client e una chiave aggiunta da un altro operatore di sicurezza dello stesso ente
    Given un client e una chiave aggiunta da un altro operatore di sicurezza dello stesso ente
    When l’operatore di sicurezza che non ha aggiunto la chiave elimina la chiave del client
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_USER_NOT_ALLOWED_TO_DELETE_CLIENT_KEY"

  # POST /producerKeychains/:producerKeychainId/keys · notAllowedPrivateKeyException
  Scenario: [ERROR_108] L'erogatore aggiunge al portachiavi la chiave di test "chiave-privata-effimera-pem" con il nome "Chiave di test" — una chiave privata effimera di test in PEM, generata per lo scenario
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "chiave-privata-effimera-pem" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_PRIVATE_KEY_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # POST /producerKeychains/:producerKeychainId/keys · notAllowedCertificateException
  Scenario: [ERROR_109] L'erogatore aggiunge al portachiavi la chiave di test "certificato-x509" con il nome "Chiave di test" — un certificato X.509 PEM di test contenente una chiave pubblica
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "certificato-x509" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_CERTIFICATE_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # POST /producerKeychains/:producerKeychainId/keys · notAllowedMultipleKeysException
  Scenario: [ERROR_110] L'erogatore aggiunge al portachiavi la chiave di test "due-chiavi-pubbliche-pem" con il nome "Chiave di test" — due blocchi di chiave pubblica PEM di test concatenati
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "due-chiavi-pubbliche-pem" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_MULTIPLE_KEYS_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # POST /producerKeychains/:producerKeychainId/keys · invalidPublicKey
  Scenario: [ERROR_111] L'erogatore aggiunge al portachiavi la chiave di test "pem-incompleto" con il nome "Chiave di test" — un contenitore di chiavi valido e una chiave pubblica PEM di test incompleta
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "pem-incompleto" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_PUBLIC_KEY__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # POST /producerKeychains/:producerKeychainId/keys · notAnRSAKey
  Scenario: [ERROR_112] L'erogatore aggiunge al portachiavi la chiave di test "ec-pubblica" con il nome "Chiave di test" — una chiave pubblica EC valida di test in PEM
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "ec-pubblica" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_AN_RSAKEY__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # POST /producerKeychains/:producerKeychainId/keys · invalidKeyLength
  Scenario: [ERROR_113] L'erogatore aggiunge al portachiavi la chiave di test "rsa-pubblica-1024-bit" con il nome "Chiave di test" — un contenitore di chiavi valido e una chiave pubblica RSA PEM di test da 1024 bit
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "rsa-pubblica-1024-bit" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_KEY_LENGTH__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # POST /clientsConsumer · hyperlinkDetectionError
  Scenario: [ERROR_114] L’operatore autorizzato crea un client di fruizione con un collegamento nel nome — l’operatore abilitato alla creazione con dati altrimenti validi
    Given l’operatore abilitato alla creazione con dati altrimenti validi
    When l’operatore autorizzato crea un client di fruizione con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_CONSUMER"

  # POST /clientsApi · hyperlinkDetectionError
  Scenario: [ERROR_115] L’operatore autorizzato crea un client API con un collegamento nel nome — l’operatore abilitato alla creazione con dati altrimenti validi
    Given l’operatore abilitato alla creazione con dati altrimenti validi
    When l’operatore autorizzato crea un client API con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_API"

  # POST /clients/:clientId/keys · hyperlinkDetectionError
  Scenario: [ERROR_116] L'erogatore aggiunge al client la chiave di test "rsa-pubblica-valida" con il nome "Chiave https://example.org" — il contenitore delle chiavi predisposto e una chiave pubblica RSA valida e inedita
    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    When l'erogatore aggiunge al client la chiave di test "rsa-pubblica-valida" con il nome "Chiave https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_CLIENT_ID_KEYS"

  # POST /producerKeychains · hyperlinkDetectionError
  Scenario: [ERROR_117] L’operatore autorizzato crea un portachiavi di erogazione con un collegamento nel nome — l’operatore abilitato alla creazione con dati altrimenti validi
    Given l’operatore abilitato alla creazione con dati altrimenti validi
    When l’operatore autorizzato crea un portachiavi di erogazione con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCER_KEYCHAINS"

  # POST /producerKeychains/:producerKeychainId/keys · hyperlinkDetectionError
  Scenario: [ERROR_118] L'erogatore aggiunge al portachiavi la chiave di test "rsa-pubblica-valida" con il nome "Chiave https://example.org" — il contenitore delle chiavi predisposto e una chiave pubblica RSA valida e inedita
    Given un portachiavi di erogazione predisposto per le chiavi
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore aggiunge al portachiavi la chiave di test "rsa-pubblica-valida" con il nome "Chiave https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  # PATCH /clients/:clientId · hyperlinkDetectionError
  # Usare https://example.org nel campo indicato e valori validi per gli altri campi. Il body deve superare la validazione dello schema. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_119] L’operatore autorizzato modifica i dati del client con un collegamento nel nome — l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    Given l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    When l’operatore autorizzato modifica i dati del client con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__PATCH_CLIENTS_CLIENT_ID"

  # PATCH /producerKeychains/:producerKeychainId · hyperlinkDetectionError
  # Usare https://example.org nel campo indicato e valori validi per gli altri campi. Il body deve superare la validazione dello schema. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_120] L’operatore autorizzato modifica i dati del portachiavi con un collegamento nel nome — l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    Given l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    When l’operatore autorizzato modifica i dati del portachiavi con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__PATCH_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID"
