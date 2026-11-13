@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la consultazione, il trasferimento e il caricamento di file degli e-service

  Come erogatore o delegato
  Voglio sapere perché la consultazione, l'importazione, l'esportazione o il caricamento di un file sono stati rifiutati
  Per verificare la delega, scegliere una versione disponibile o correggere il file.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  @producer-delegation-settings
  # Endpoint: GET /producers/eservices/:eserviceId
  Scenario: [ERROR_169] - Impossibilità di consultare un e-service dopo la revoca della delega
  Dato un e-service dell'erogatore precedentemente delegato a un altro ente e una delega revocata
  Quando l'amministratore del delegato tenta di consultare i dettagli dell'e-service in erogazione
  Allora il sistema rifiuta la consultazione e segnala che l'ente non è autorizzato.

    Given un e-service pubblicato dal Comune di Milano con una delega in erogazione al Comune di Pozzallo in stato ACTIVE
    And la delega in erogazione viene revocata dal Comune di Milano
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare readEService senza attendere il successo.
    When l'utente tenta di consultare i dettagli dell'e-service in erogazione
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ESERVICE_REQUESTER__GET_PRODUCERS_ESERVICES_ESERVICE_ID" in lingua "it"

  @producer-delegation-settings
  # Endpoint: GET /producers/eservices/:eserviceId/descriptors/:descriptorId
  Scenario: [ERROR_170] - Impossibilità di consultare una versione dopo la revoca della delega
  Dato un e-service dell'erogatore precedentemente delegato a un altro ente e una delega revocata
  Quando l'amministratore del delegato tenta di consultare i dettagli della versione in erogazione
  Allora il sistema rifiuta la consultazione e segnala che l'ente non è autorizzato.

    Given un e-service pubblicato dal Comune di Milano con una delega in erogazione al Comune di Pozzallo in stato ACTIVE
    And la delega in erogazione viene revocata dal Comune di Milano
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare readDescriptor senza attendere il successo.
    When l'utente tenta di consultare i dettagli della versione 1 dell'e-service in erogazione
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ESERVICE_REQUESTER__GET_PRODUCERS_ESERVICES_ESERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "it"

  @producer-delegation-settings
  # Endpoint: GET /export/eservices/:eserviceId/descriptors/:descriptorId
  Scenario: [ERROR_171] - Impossibilità per il delegato di esportare una versione dell'e-service
  Dato un e-service dell'erogatore con una versione pubblicata e una delega attiva a un ente delegato
  Quando l'amministratore del delegato tenta di esportare la versione
  Allora il sistema rifiuta l'esportazione e segnala che l'ente non è autorizzato.

    Given un e-service pubblicato dal Comune di Milano con una delega in erogazione al Comune di Pozzallo in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    # Operazione BFF e binding negativo comuni mancanti per l'esportazione della versione.
    When l'utente tenta di esportare la versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ESERVICE_REQUESTER__GET_EXPORT_ESERVICES_ESERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_172] - Impossibilità di allegare un documento a una versione eliminata
  Dato un e-service con una versione pubblicata e una versione successiva in bozza poi eliminata
  Quando l'erogatore tenta di allegare un documento alla bozza eliminata
  Allora il sistema rifiuta il caricamento e segnala che la versione non è più disponibile.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Setup e binding mancanti; riusare addDescriptor, eliminare solo la bozza e conservarne il riferimento.
    # Manca l'eliminazione BFF della versione; deleteEService eliminerebbe l'intero e-service.
    And la versione successiva dell'e-service creata in bozza e poi eliminata
    # Binding BFF comune mancante per il caricamento; riusare addDocument sulla versione eliminata.
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "BFF_ESERVICE_DESCRIPTOR_NOT_FOUND" in lingua "it"

  # Endpoint: POST /import/eservices
  Scenario: [ERROR_173] - Rifiuto dell'importazione di un archivio senza la configurazione dell'e-service
  Dato un amministratore abilitato all'importazione degli e-service
  Quando tenta di importare un archivio ZIP valido ma privo della configurazione richiesta
  Allora il sistema rifiuta l'importazione e segnala che l'archivio non contiene la struttura necessaria.

    # Fixture da predisporre: ZIP leggibile, privo di configuration.json e con gli altri contenuti validi.
    # Operazione BFF e binding mancanti per l'importazione da file.
    When l'erogatore tenta di importare un e-service dal file "zip-senza-configurazione.zip"
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ZIP_STRUCTURE" in lingua "it"

  @producer-delegation-settings
  # Endpoint: GET /export/eservices/:eserviceId/descriptors/:descriptorId
  Scenario: [ERROR_174] - Impossibilità per l'erogatore di esportare un e-service con delega attiva
  Dato un e-service dell'erogatore con una versione pubblicata e una delega attiva a un ente delegato
  Quando l'erogatore tenta di esportare la versione
  Allora il sistema rifiuta l'esportazione e segnala che l'e-service delegato non è esportabile.

    Given un e-service pubblicato dal Comune di Milano con una delega in erogazione al Comune di Pozzallo in stato ACTIVE
    And un admin appartenente all'erogatore Comune di Milano
    # Operazione BFF e binding negativo comuni mancanti per l'esportazione della versione.
    When l'utente tenta di esportare la versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "BFF_DELEGATED_ESERVICE_NOT_EXPORTABLE" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_181] - Rifiuto di un PDF caricato come interfaccia REST
  Dato un e-service REST con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare un PDF come interfaccia della versione
  Allora il sistema rifiuta il caricamento e segnala che il file non è valido come interfaccia.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding BFF comune mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "allegato-a.pdf" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_ESERVICE_INTERFACE_FILE_DETECTED" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_182] - Rifiuto di un'interfaccia con una versione OpenAPI non supportata
  Dato un e-service REST con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare una specifica che dichiara una versione OpenAPI non supportata
  Allora il sistema rifiuta il caricamento e indica le versioni OpenAPI ammesse.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding BFF comune mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "openapi-versione-non-supportata.yaml" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPENAPI_VERSION_NOT_RECOGNIZED__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_183] - Rifiuto di un documento il cui formato non è verificabile
  Dato un e-service con una versione in bozza disponibile per il caricamento di documenti
  Quando l'erogatore tenta di allegare un file il cui formato non può essere determinato
  Allora il sistema rifiuta il caricamento e segnala che non è stato possibile verificare il formato.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding BFF comune mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "formato-non-rilevabile.bin" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_CONTENT_TYPE_DETECTED" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_184] - Rifiuto di un'interfaccia SOAP senza indirizzo del servizio
  Dato un e-service SOAP con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare un WSDL ben formato ma privo dell'indirizzo del servizio
  Allora il sistema rifiuta il caricamento e invita l'erogatore a completare il WSDL.

    Given un e-service SOAP in bozza creato dall'erogatore
    When l'erogatore tenta di caricare l'interfaccia SOAP "missing-soap-address.wsdl" sul descrittore dell'e-service ottenendo un rifiuto per "assenza di soap:address"
    Then il messaggio di errore corrisponde al caso "CATALOG_INTERFACE_EXTRACTING_SOAP_FIELD_VALUE_ERROR" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_185] - Rifiuto di un'interfaccia REST con un URL del server non valido
  Dato un e-service REST con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare una specifica OpenAPI con un URL del server non valido
  Allora il sistema rifiuta il caricamento e invita l'erogatore a correggere l'URL.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding BFF comune mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "openapi-server-non-valido.yaml" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_SERVER_URL" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_186] - Rifiuto di un documento che supera la dimensione massima
  Dato un e-service con una versione in bozza disponibile per il caricamento di documenti
  Quando l'erogatore tenta di allegare un PDF valido che supera la dimensione massima consentita
  Allora il sistema rifiuta il caricamento e indica il limite di dimensione per i documenti.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Dimensionare la fixture oltre il limite applicativo configurato e sotto il limite del proxy.
    # Binding BFF comune mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "pdf-oltre-limite.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_CONTENT_TOO_LARGE_ERROR" in lingua "it"

  # Endpoint: POST /eservices/:eServiceId/descriptors/:descriptorId/documents
  Scenario: [ERROR_187] - Rifiuto di un documento PDF con contenuto non valido
  Dato un e-service con una versione in bozza disponibile per il caricamento di documenti
  Quando l'erogatore tenta di allegare un file identificato come PDF il cui contenuto non è valido
  Allora il sistema rifiuta il caricamento e invita l'erogatore a controllare il file.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding BFF comune mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "pdf-con-contenuto-non-valido.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_FILE_UPLOAD_ERROR" in lingua "it"
