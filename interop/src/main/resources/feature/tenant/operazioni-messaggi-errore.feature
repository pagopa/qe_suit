@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione dei dati e degli attributi dell'ente

  Come amministratore o certificatore
  Voglio sapere perché una verifica, un'assegnazione o una modifica dei dati dell'ente sono state rifiutate
  Per correggere la scadenza, controllare le assegnazioni o verificare le operazioni consentite.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_012] - Rifiuto dell'aggiornamento di una verifica con scadenza nel passato
  Dato un attributo già verificato dall'erogatore per il fruitore
  Quando l'erogatore tenta di aggiornarne la verifica con una scadenza precedente a oggi
  Allora il sistema rifiuta la modifica e segnala che la scadenza non può essere nel passato.

    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | VER-1 |
    # Binding di setup mancante; riusare assignVerifiedAttribute con una richiesta di fruizione valida.
    And l'attributo "VER-1" verificato dal Comune di Milano per PagoPA con una scadenza valida
    # Operazione BFF e binding negativo mancanti per l'aggiornamento della verifica, distinto dalla creazione.
    # Calcolare ieri rispetto alla data del test, senza una data fissa.
    When l'erogatore tenta di aggiornare la verifica dell'attributo "VER-1" di PagoPA con scadenza ieri
    Then il messaggio di errore corrisponde al caso "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED_ATTRIBUTE_ID" in lingua "it"

  Scenario: [ERROR_013] - Impossibilità di assegnare un attributo certificato già posseduto dall'ente
  Dato un attributo certificato già assegnato all'ente dallo stesso certificatore
  Quando il certificatore tenta di assegnare nuovamente l'attributo
  Allora il sistema rifiuta l'assegnazione e segnala che l'attributo è già posseduto.

    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias  |
      | CERT-1 |
    # Binding negativo comune mancante; ricavare il tipo dall'alias e riusare addCertifiedAttribute.
    When il certificatore tenta di assegnare nuovamente l'attributo "CERT-1" all'ente PagoPA
    Then il messaggio di errore corrisponde al caso "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_014] - Impossibilità di assegnare un attributo certificato discreto già posseduto dall'ente
  Dato un attributo certificato discreto già assegnato all'ente con un valore ammesso
  Quando il certificatore tenta di assegnare nuovamente l'attributo
  Allora il sistema rifiuta l'assegnazione e segnala che l'attributo è già posseduto.

    # Mapping da verificare prima dell'esecuzione: atteso Tenant 005-0032, precedente associazione 005-0014.
    Given i seguenti attributi discreti creati da un admin del Comune di Milano:
      | alias  |
      | DISC-1 |
    # Binding di setup mancante; riusare assignCertifiedDiscreteAttribute con un valore numerico ammesso.
    And l'attributo certificato discreto "DISC-1" già assegnato all'ente PagoPA con un valore ammesso
    # Binding negativo comune mancante; ricavare il tipo dall'alias e riusare addCertifiedDiscreteAttribute.
    When il certificatore tenta di assegnare nuovamente l'attributo "DISC-1" all'ente PagoPA
    Then il messaggio di errore corrisponde al caso "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED_DISCRETE" in lingua "it"

  Scenario: [ERROR_015] - Rifiuto di una nuova verifica con scadenza nel passato
  Dato un attributo richiesto per la fruizione, ancora da verificare
  Quando l'erogatore tenta di verificarlo con una scadenza precedente a oggi
  Allora il sistema rifiuta la verifica e segnala che la scadenza non può essere nel passato.

    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | VER-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo       | gruppo | attributo |
      | verificato | 1      | VER-1     |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione in attesa di approvazione
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare verifyVerifiedAttribute con scadenza ieri e gli altri dati validi.
    When l'erogatore tenta di verificare l'attributo "VER-1" di PagoPA con scadenza ieri
    Then il messaggio di errore corrisponde al caso "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_016] - Impossibilità per un ente non pubblico di abilitarsi alle deleghe
  Dato un amministratore di un ente non pubblico
  Quando l'ente tenta di abilitarsi a ricevere deleghe
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given un admin appartenente all'erogatore Sogecap
    # Binding BFF negativo mancante; riusare updateTenantDelegatedFeatures senza il flusso di successo.
    When Sogecap tenta di abilitarsi a ricevere deleghe di erogazione
    Then il messaggio di errore corrisponde al caso "TENANT_OPERATION_FORBIDDEN" in lingua "it"

  Scenario: [ERROR_017] - Rifiuto di un contatto email con un link nelle note
  Dato un amministratore abilitato alla gestione dei contatti dell'ente
  Quando tenta di salvare un contatto email valido con un collegamento nelle note
  Allora il sistema rifiuta il salvataggio e invita l'amministratore a rimuovere il collegamento.

    # Operazione BFF e binding negativo mancanti; mantenere tipo e indirizzo del contatto validi.
    When l'utente tenta di salvare un contatto email dell'ente con le note "Contatto https://example.org"
    Then il messaggio di errore corrisponde al caso "TENANT_HYPERLINK_DETECTION_ERROR" in lingua "it"
