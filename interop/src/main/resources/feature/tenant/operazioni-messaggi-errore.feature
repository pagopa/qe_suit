@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@channel:Given=BFF:en,When=BFF:en,Then=BFF:en
Feature: Messaggi di errore durante la gestione dei dati e degli attributi dell'ente

  Come amministratore o certificatore
  Voglio sapere perché una verifica, un'assegnazione o una modifica dei dati dell'ente sono state rifiutate
  Per correggere la scadenza, controllare le assegnazioni o verificare le operazioni consentite.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  # Endpoint: POST /tenants/:tenantId/attributes/verified/:attributeId
  Scenario: [ERROR_012] - Impossibilità di impostare nel passato la scadenza di un attributo verificato
  Dato un attributo verificato già assegnato dall'erogatore al fruitore con una scadenza valida
  Quando l'erogatore tenta di modificare la scadenza dell'attributo verificato impostandola a una data precedente a oggi
  Allora il sistema rifiuta la modifica e segnala che la scadenza non può essere nel passato.

    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | VER-1 |
    When l'erogatore tenta di modificare la scadenza dell'attributo verificato "VER-1" di PagoPA impostandola a ieri
    Then il messaggio di errore corrisponde al caso "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED_ATTRIBUTE_ID"

  # Endpoint: POST /tenants/:tenantId/attributes/certified
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
    When il certificatore tenta di assegnare nuovamente l'attributo "CERT-1" all'ente PagoPA
    Then il messaggio di errore corrisponde al caso "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED"

  # NON REPLICABILE A FRONTEND
  # Endpoint: POST /tenants/:tenantId/attributes/certifiedDiscrete
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
    # Tentativo discreto ancora da implementare con un valore ammesso; non usare l'assegnazione certificata ordinaria.
    When il certificatore tenta di assegnare nuovamente l'attributo "DISC-1" all'ente PagoPA
    Then il messaggio di errore corrisponde al caso "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED_DISCRETE"

  # Endpoint: POST /tenants/:tenantId/attributes/verified
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
    When l'erogatore tenta di assegnare come attributo verificato l'attributo "VER-1" di PagoPA con scadenza ieri
    Then il messaggio di errore corrisponde al caso "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED"

  # NON REPLICABILE A FRONTEND
  # Endpoint: POST /tenants/delegatedFeatures/update
  Scenario: [ERROR_016] - Impossibilità per un ente non pubblico di abilitarsi alle deleghe
  Dato un amministratore di un ente non pubblico
  Quando l'ente tenta di abilitarsi a ricevere deleghe
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given un admin appartenente all'erogatore Sogecap
    When Sogecap tenta di abilitarsi a ricevere deleghe di erogazione
    Then il messaggio di errore corrisponde al caso "TENANT_OPERATION_FORBIDDEN"

  # Endpoint: POST /tenants/:tenantId/mails
  Scenario: [ERROR_017] - Rifiuto di un contatto email con un link nelle note
  Dato un amministratore abilitato alla gestione dei contatti dell'ente
  Quando tenta di salvare un contatto email valido con un collegamento nelle note
  Allora il sistema rifiuta il salvataggio e invita l'amministratore a rimuovere il collegamento.

    When l'utente tenta di salvare un contatto email dell'ente inserendo un link nelle note
    Then il messaggio di errore corrisponde al caso "TENANT_HYPERLINK_DETECTION_ERROR"
