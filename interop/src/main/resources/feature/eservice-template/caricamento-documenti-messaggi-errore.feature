@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante il caricamento di file nei template e-service

  Come erogatore
  Voglio sapere perché il caricamento di un documento o di un'interfaccia è stato rifiutato
  Per capire se devo correggere il file, sceglierne un altro o aggiornare la pagina.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  @work-item:QA-18430
  Scenario: [ERROR_175] - Impossibilità di allegare un documento a una versione eliminata
  Dato un template e-service con una versione pubblicata e una versione successiva in bozza poi eliminata
  Quando l'erogatore tenta di allegare un documento alla bozza eliminata
  Allora il sistema rifiuta il caricamento e segnala che la bozza non è più disponibile.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.json |
    And la versione successiva del template creata in bozza e poi eliminata
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "BFF_ESERVICE_TEMPLATE_VERSION_NOT_FOUND" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_188] - Rifiuto di un PDF caricato come interfaccia REST
  Dato un template e-service REST con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare un PDF come interfaccia della versione
  Allora il sistema rifiuta il caricamento e segnala che il file non è valido come interfaccia del template.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il file "allegato-a.pdf" come "interfaccia" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INVALID_ESERVICE_INTERFACE_FILE_DETECTED" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_189] - Rifiuto di un'interfaccia con una versione OpenAPI non supportata
  Dato un template e-service REST con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare una specifica che dichiara una versione OpenAPI non supportata
  Allora il sistema rifiuta il caricamento e indica le versioni OpenAPI ammesse.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il file "openapi-versione-non-supportata.yaml" come "interfaccia" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_OPENAPI_VERSION_NOT_RECOGNIZED" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_190] - Rifiuto di un documento il cui formato non è verificabile
  Dato un template e-service con una versione in bozza disponibile per il caricamento di documenti
  Quando l'erogatore tenta di allegare un file il cui formato non può essere determinato
  Allora il sistema rifiuta il caricamento e segnala che non è stato possibile verificare il formato del file.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il file "formato-non-rilevabile.bin" come "documento" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INVALID_CONTENT_TYPE_DETECTED" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_191] - Rifiuto di un'interfaccia SOAP senza indirizzo del servizio
  Dato un template e-service SOAP con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare un WSDL ben formato ma privo dell'indirizzo del servizio
  Allora il sistema rifiuta il caricamento e invita l'erogatore a completare il WSDL prima di riprovare.

    Given un template e-service SOAP in bozza creato dall'erogatore
    When l'erogatore tenta di caricare l'interfaccia SOAP "missing-soap-address.wsdl" sulla versione del template ottenendo un rifiuto per "assenza di soap:address"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INTERFACE_EXTRACTING_SOAP_FIELD_VALUE_ERROR" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_192] - Rifiuto di un'interfaccia REST con un URL del server non valido
  Dato un template e-service REST con una versione in bozza senza interfaccia
  Quando l'erogatore tenta di caricare una specifica OpenAPI con un URL del server non valido
  Allora il sistema rifiuta il caricamento e invita l'erogatore a correggere l'URL prima di riprovare.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il file "openapi-server-non-valido.yaml" come "interfaccia" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INVALID_SERVER_URL" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_193] - Rifiuto di un documento che supera la dimensione massima
  Dato un template e-service con una versione in bozza disponibile per il caricamento di documenti
  Quando l'erogatore tenta di allegare un PDF valido che supera la dimensione massima consentita
  Allora il sistema rifiuta il caricamento e indica il limite di dimensione per i documenti.

    # La fixture supera il limite applicativo e rientra nel limite del proxy.
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il file "pdf-oltre-limite.pdf" come "documento" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_CONTENT_TOO_LARGE_ERROR" in lingua "it"

  @work-item:QA-18430
  Scenario: [ERROR_194] - Rifiuto di un documento PDF con contenuto non valido
  Dato un template e-service con una versione in bozza disponibile per il caricamento di documenti
  Quando l'erogatore tenta di allegare un file identificato come PDF il cui contenuto non è valido
  Allora il sistema rifiuta il caricamento e invita l'erogatore a controllare il file e a riprovare con un formato ammesso.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il file "pdf-con-contenuto-non-valido.pdf" come "documento" sulla versione del template con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INVALID_FILE_UPLOAD_ERROR" in lingua "it"