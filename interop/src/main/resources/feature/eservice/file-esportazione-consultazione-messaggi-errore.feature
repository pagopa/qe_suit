@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per file, esportazione e consultazione degli e-service

  # GET /producers/eservices/:eserviceId · invalidEserviceRequester
  Scenario: [ERROR_169] L’amministratore dell’ente delegato consulta i dettagli dell’e-service in erogazione — un e-service precedentemente gestito in delega e la delega revocata
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente delegato consulta i dettagli dell’e-service in erogazione
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ESERVICE_REQUESTER__GET_PRODUCERS_ESERVICES_ESERVICE_ID"

  # GET /producers/eservices/:eserviceId/descriptors/:descriptorId · invalidEserviceRequester
  Scenario: [ERROR_170] L’amministratore dell’ente delegato consulta i dettagli della versione in erogazione — un e-service precedentemente gestito in delega e la delega revocata
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente delegato consulta i dettagli della versione in erogazione
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ESERVICE_REQUESTER__GET_PRODUCERS_ESERVICES_ESERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID"

  # GET /export/eservices/:eserviceId/descriptors/:descriptorId · invalidEserviceRequester
  Scenario: [ERROR_171] L'utente esporta la versione dell'e-service — un e-service con delega in erogazione attiva e una versione predisposta per l’esportazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente esporta la versione dell'e-service
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ESERVICE_REQUESTER__GET_EXPORT_ESERVICES_ESERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · eserviceDescriptorNotFound
  Scenario: [ERROR_172] L'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test" — un e-service esistente e l’identificativo di una versione in bozza eliminata
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    And la versione in bozza non è più disponibile
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "BFF_ESERVICE_DESCRIPTOR_NOT_FOUND"

  # POST /import/eservices · invalidZipStructure
  Scenario: [ERROR_173] L’operatore autorizzato importa un e-service da archivio ZIP — un archivio ZIP valido privo del file configuration.json richiesto
    Given un archivio ZIP valido privo del file configuration.json richiesto
    When l’operatore autorizzato importa un e-service da archivio ZIP
    Then il messaggio di errore corrisponde al caso "BFF_INVALID_ZIP_STRUCTURE"

  # GET /export/eservices/:eserviceId/descriptors/:descriptorId · delegatedEserviceNotExportable
  Scenario: [ERROR_174] L'utente esporta la versione dell'e-service — una versione dell’e-service con delega in erogazione attiva o pendente
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente esporta la versione dell'e-service
    Then il messaggio di errore corrisponde al caso "BFF_DELEGATED_ESERVICE_NOT_EXPORTABLE"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · invalidEserviceInterfaceFileDetected
  Scenario: [ERROR_181] L'erogatore carica il file "allegato-a.pdf" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test" — una versione in bozza e un file valido come documento ma incompatibile con la tecnologia dell’interfaccia
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "allegato-a.pdf" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_ESERVICE_INTERFACE_FILE_DETECTED"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · openapiVersionNotRecognized
  Scenario: [ERROR_182] L'erogatore carica il file "openapi-versione-non-supportata.yaml" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test" — una versione in bozza e una specifica JSON o YAML con versione OpenAPI non supportata
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "openapi-versione-non-supportata.yaml" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPENAPI_VERSION_NOT_RECOGNIZED__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · invalidContentTypeDetected
  Scenario: [ERROR_183] L'erogatore carica il file "formato-non-rilevabile.bin" come "documento" sulla versione dell'e-service con il nome "Allegato di test" — la risorsa predisposta per il caricamento e un file il cui tipo non è determinabile
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "formato-non-rilevabile.bin" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_CONTENT_TYPE_DETECTED"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · interfaceExtractingSoapFieldValueError
  Scenario: [ERROR_184] L'erogatore carica il file "soap-senza-indirizzo.wsdl" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test" — una versione SOAP in bozza e un WSDL ben formato ma privo dell’indirizzo del servizio o della definizione delle operazioni
    Given un admin appartenente all'erogatore Comune di Milano
    And una versione in bozza senza interfaccia di un e-service con tecnologia "SOAP"
    When l'erogatore carica il file "soap-senza-indirizzo.wsdl" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INTERFACE_EXTRACTING_SOAP_FIELD_VALUE_ERROR"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · invalidServerUrl
  Scenario: [ERROR_185] L'erogatore carica il file "openapi-server-non-valido.yaml" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test" — una versione in bozza e una specifica valida salvo l’URL del server
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "openapi-server-non-valido.yaml" come "interfaccia" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_SERVER_URL"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · contentTooLargeError
  # Dimensionare la fixture oltre il limite applicativo configurato e sotto il limite del proxy.
  Scenario: [ERROR_186] L'erogatore carica il file "pdf-oltre-limite.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test" — una versione in bozza e un documento con MIME ammesso e dimensione superiore al limite applicativo
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "pdf-oltre-limite.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_CONTENT_TOO_LARGE_ERROR"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · invalidFileUploadError
  Scenario: [ERROR_187] L'erogatore carica il file "pdf-con-contenuto-non-valido.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test" — una risorsa in bozza e un file con MIME ammesso ma con contenuto incompatibile con il formato dichiarato
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "pdf-con-contenuto-non-valido.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "CATALOG_INVALID_FILE_UPLOAD_ERROR"
