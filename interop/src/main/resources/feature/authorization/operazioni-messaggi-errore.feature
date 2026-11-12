@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione dei client, dei portachiavi e delle chiavi pubbliche

  Come amministratore o operatore di sicurezza
  Voglio sapere perché la creazione, la modifica o la registrazione di una chiave sono state rifiutate
  Per correggere il formato, usare una chiave ammessa o verificare i permessi sul contenitore.

  # Le fixture crittografiche sono dati di test dello scenario; le chiavi private devono essere effimere.
  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  @producer-delegation-settings
  Scenario: [ERROR_203] - Impossibilità per il delegato di associare un portachiavi a un e-service gestito in delega
  Dati un e-service con una delega in erogazione attiva e un portachiavi dell'ente delegato
  Quando il delegato tenta di associare il proprio portachiavi all'e-service del delegante
  Allora il sistema rifiuta l'associazione e segnala che può effettuarla soltanto l'ente delegante.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un portachiavi erogatore creato dal Comune di Pozzallo
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding negativo mancante, condiviso con ERROR_204; riusare l'associazione BFF del portachiavi.
    When l'utente tenta di associare il portachiavi erogatore all'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_TENANT_NOT_ALLOWED_ON_ESERVICE__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_ESERVICES"

  Scenario: [ERROR_204] - Impossibilità di associare un portachiavi a un e-service archiviato
  Dati un e-service interamente archiviato e un portachiavi appartenenti allo stesso erogatore
  Quando l'erogatore tenta di associare il portachiavi all'e-service
  Allora il sistema rifiuta l'associazione e segnala che l'e-service è archiviato.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Setup e binding mancanti per l'archiviazione conclusa dell'intero e-service.
    # La sola richiesta di archiviazione con preavviso non soddisfa questo prerequisito.
    And l'e-service "ES-1" del Comune di Milano interamente archiviato
    And un portachiavi erogatore creato dal Comune di Milano
    And un admin appartenente all'erogatore Comune di Milano
    # Binding negativo mancante, condiviso con ERROR_203; riusare l'associazione BFF del portachiavi.
    When l'utente tenta di associare il portachiavi erogatore all'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_ESERVICE_IN_ARCHIVED_STATE__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_ESERVICES"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_205] - Impossibilità di associare un client a una finalità mai attivata
  Dati una finalità in bozza senza versioni attive o sospese e un client di fruizione dello stesso fruitore
  Quando il fruitore tenta di associare il client alla finalità
  Allora il sistema rifiuta l'associazione e segnala che la finalità non è attiva.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato DRAFT associate da PagoPA
    # Binding di setup mancante; riusare createClient con tipo CONSUMER senza associare la finalità.
    # Non usare il setup del client API o quello CONSUMER che associa già la finalità.
    And un client CONSUMER creato da PagoPA senza finalità associate
    And un admin appartenente al fruitore PagoPA
    # Binding negativo mancante; riusare linkPurpose senza attendere il successo.
    When il fruitore tenta di associare il client alla finalità creata
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NO_ACTIVE_OR_SUSPENDED_PURPOSE_VERSION_FOUND__POST_CLIENTS_CLIENT_ID_PURPOSES"

  Scenario: [ERROR_100] - Rifiuto della registrazione di una chiave privata nel client
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave privata effimera di test al posto di una chiave pubblica
  Allora il sistema rifiuta la registrazione e segnala che le chiavi private non sono ammesse.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "chiave-privata-effimera-pem" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_PRIVATE_KEY_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_101] - Rifiuto della registrazione di un certificato X.509 nel client
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare un certificato X.509 PEM di test contenente una chiave pubblica
  Allora il sistema rifiuta la registrazione e segnala che i certificati non sono ammessi.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "certificato-x509" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_CERTIFICATE_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_102] - Rifiuto della registrazione di più chiavi pubbliche insieme nel client
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare due chiavi pubbliche PEM di test concatenate
  Allora il sistema rifiuta la registrazione e segnala che occorre inviare una sola chiave.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "due-chiavi-pubbliche-pem" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_MULTIPLE_KEYS_EXCEPTION__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_103] - Rifiuto di una chiave pubblica incompleta nel client
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica PEM di test incompleta
  Allora il sistema rifiuta la registrazione e segnala che la chiave pubblica non è valida.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "pem-incompleto" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_PUBLIC_KEY__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_104] - Rifiuto di una chiave pubblica non RSA nel client
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica EC valida di test
  Allora il sistema rifiuta la registrazione e segnala che è richiesta una chiave RSA.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "ec-pubblica" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_AN_RSAKEY__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_105] - Rifiuto di una chiave RSA con lunghezza insufficiente nel client
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica RSA di test da 1024 bit
  Allora il sistema rifiuta la registrazione e segnala che la lunghezza della chiave non è ammessa.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "rsa-pubblica-1024-bit" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_KEY_LENGTH__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_106] - Impossibilità di registrare nuovamente una chiave già presente nel client
  Dato un client con una chiave pubblica di test già registrata
  Quando l'erogatore tenta di aggiungere nuovamente la stessa chiave
  Allora il sistema rifiuta la registrazione e segnala che la chiave è già presente.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # La fixture identifica la chiave effettiva registrata dal Given, non una nuova chiave RSA.
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "rsa-pubblica-già-registrata" al "client" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_KEY_ALREADY_EXISTS"

  Scenario: [ERROR_107] - Impossibilità di eliminare una chiave del client registrata da un altro operatore
  Dato un client con una chiave registrata da un operatore di sicurezza
  Quando un altro operatore di sicurezza dello stesso ente tenta di eliminare la chiave
  Allora il sistema rifiuta l'eliminazione e segnala che l'operatore non è autorizzato.

    # Binding di setup mancante; riusare createClient e addKey con due utenti distinti associati al client.
    Given un client con una chiave registrata da un operatore di sicurezza e un altro operatore di sicurezza dello stesso ente associato al client
    # Binding di identità mancante; il secondo operatore non deve essere sostituito dall'admin nel When.
    And il secondo operatore di sicurezza selezionato come utente corrente
    # Operazione BFF e binding negativo mancanti per l'eliminazione della chiave del client.
    When l'utente tenta di eliminare la chiave registrata dall'altro operatore nel client
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_USER_NOT_ALLOWED_TO_DELETE_CLIENT_KEY"

  Scenario: [ERROR_108] - Rifiuto della registrazione di una chiave privata nel portachiavi
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave privata effimera di test al posto di una chiave pubblica
  Allora il sistema rifiuta la registrazione e segnala che le chiavi private non sono ammesse.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "chiave-privata-effimera-pem" al "portachiavi" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_PRIVATE_KEY_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_109] - Rifiuto della registrazione di un certificato X.509 nel portachiavi
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare un certificato X.509 PEM di test contenente una chiave pubblica
  Allora il sistema rifiuta la registrazione e segnala che i certificati non sono ammessi.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "certificato-x509" al "portachiavi" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_CERTIFICATE_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_110] - Rifiuto della registrazione di più chiavi pubbliche insieme nel portachiavi
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare due chiavi pubbliche PEM di test concatenate
  Allora il sistema rifiuta la registrazione e segnala che occorre inviare una sola chiave.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "due-chiavi-pubbliche-pem" al "portachiavi" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_ALLOWED_MULTIPLE_KEYS_EXCEPTION__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_111] - Rifiuto di una chiave pubblica incompleta nel portachiavi
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica PEM di test incompleta
  Allora il sistema rifiuta la registrazione e segnala che la chiave pubblica non è valida.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "pem-incompleto" al "portachiavi" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_PUBLIC_KEY__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_112] - Rifiuto di una chiave pubblica non RSA nel portachiavi
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica EC valida di test
  Allora il sistema rifiuta la registrazione e segnala che è richiesta una chiave RSA.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "ec-pubblica" al "portachiavi" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_NOT_AN_RSAKEY__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_113] - Rifiuto di una chiave RSA con lunghezza insufficiente nel portachiavi
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica RSA di test da 1024 bit
  Allora il sistema rifiuta la registrazione e segnala che la lunghezza della chiave non è ammessa.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "rsa-pubblica-1024-bit" al "portachiavi" con il nome "Chiave di test"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_INVALID_KEY_LENGTH__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_114] - Rifiuto della creazione di un client di fruizione con un link nel nome
  Dato un amministratore abilitato alla creazione dei client
  Quando tenta di creare un client di fruizione con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'amministratore a rimuovere il collegamento.

    # Binding negativo parametrico mancante; riusare createConsumerClient o createApiClient in base al tipo.
    When l'utente tenta di creare un client "CONSUMER" con il nome "Client https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_CONSUMER"

  Scenario: [ERROR_115] - Rifiuto della creazione di un client API con un link nel nome
  Dato un amministratore abilitato alla creazione dei client
  Quando tenta di creare un client API con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'amministratore a rimuovere il collegamento.

    # Binding negativo parametrico mancante; riusare createConsumerClient o createApiClient in base al tipo.
    When l'utente tenta di creare un client "API" con il nome "Client https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_API"

  Scenario: [ERROR_116] - Rifiuto di una chiave del client con un link nel nome
  Dato un client abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica RSA valida e inedita con un collegamento nel nome
  Allora il sistema rifiuta la registrazione e invita l'erogatore a rimuovere il collegamento.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "rsa-pubblica-valida" al "client" con il nome "Chiave https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_CLIENTS_CLIENT_ID_KEYS"

  Scenario: [ERROR_117] - Rifiuto della creazione di un portachiavi con un link nel nome
  Dato un amministratore abilitato alla creazione dei portachiavi di erogazione
  Quando tenta di creare un portachiavi con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'amministratore a rimuovere il collegamento.

    # Binding BFF negativo mancante; riusare createProducerKeychain senza attendere il successo.
    When l'utente tenta di creare un portachiavi di erogazione con il nome "Portachiavi https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCER_KEYCHAINS"

  Scenario: [ERROR_118] - Rifiuto di una chiave del portachiavi con un link nel nome
  Dato un portachiavi di erogazione abilitato alla gestione delle chiavi
  Quando l'erogatore tenta di registrare una chiave pubblica RSA valida e inedita con un collegamento nel nome
  Allora il sistema rifiuta la registrazione e invita l'erogatore a rimuovere il collegamento.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Binding negativo parametrico mancante; riusare addKey per il client e createProducerKey per il portachiavi.
    When l'erogatore tenta di aggiungere la chiave di test "rsa-pubblica-valida" al "portachiavi" con il nome "Chiave https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID_KEYS"

  Scenario: [ERROR_119] - Rifiuto della modifica di un client con un link nel nome
  Dato un client esistente e un amministratore abilitato alla sua gestione
  Quando l'amministratore tenta di inserire un collegamento nel nome lasciando validi gli altri dati
  Allora il sistema rifiuta la modifica e invita l'amministratore a rimuovere il collegamento.

    Given un client API creato dal Comune di Milano in cui è presente un utente admin e una coppia di chiavi crittografiche
    # Operazione BFF e binding negativo parametrico mancanti; verificare il contratto PATCH di rilascio.
    # Non rimappare questo ID su un'altra operazione.
    When l'utente tenta di modificare i dati del "client" con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__PATCH_CLIENTS_CLIENT_ID"

  Scenario: [ERROR_120] - Rifiuto della modifica di un portachiavi con un link nel nome
  Dato un portachiavi di erogazione esistente e un amministratore abilitato alla sua gestione
  Quando l'amministratore tenta di inserire un collegamento nel nome lasciando validi gli altri dati
  Allora il sistema rifiuta la modifica e invita l'amministratore a rimuovere il collegamento.

    Given un portachiavi erogatore creato dal Comune di Milano
    # Operazione BFF e binding negativo parametrico mancanti; verificare il contratto PATCH di rilascio.
    # Non rimappare questo ID su un'altra operazione.
    When l'utente tenta di modificare i dati del "portachiavi" con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "AUTHORIZATION_HYPERLINK_DETECTION_ERROR__PATCH_PRODUCER_KEYCHAINS_PRODUCER_KEYCHAIN_ID"
