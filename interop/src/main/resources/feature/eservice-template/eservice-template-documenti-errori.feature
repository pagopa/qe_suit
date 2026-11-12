@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore per i documenti dei template e-service

  Come creatore di template e-service
  Voglio sapere perché il caricamento, la rinomina o il download di un documento sono stati rifiutati
  Per scegliere un nome o un contenuto diverso, rimuovere i collegamenti o verificare la disponibilità del documento.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_122] - Rifiuto del caricamento di un documento con un nome già in uso
  Dato un template e-service in bozza con un documento già allegato
  Quando l'erogatore tenta di allegare un PDF diverso con lo stesso nome
  Allora il sistema rifiuta il caricamento e segnala che il nome del documento è già in uso.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding di setup comune mancante; riusare createEServiceTemplateDocument per ciascun PDF.
    And i seguenti documenti PDF allegati alla versione del template:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
    # Binding negativo comune mancante; riusare createEServiceTemplateDocument senza attendere il successo.
    When l'erogatore carica il file "allegato-b.pdf" come "documento" sulla versione del template con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS"

  Scenario: [ERROR_123] - Rifiuto del caricamento di un documento con contenuto già presente
  Dato un template e-service in bozza con un documento già allegato
  Quando l'erogatore tenta di allegare lo stesso PDF con un nome diverso
  Allora il sistema rifiuta il caricamento e segnala che il contenuto è già presente.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding di setup comune mancante; riusare createEServiceTemplateDocument per ciascun PDF.
    And i seguenti documenti PDF allegati alla versione del template:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
    # Binding negativo comune mancante; riusare createEServiceTemplateDocument senza attendere il successo.
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione del template con il nome "Allegato B"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_CHECKSUM_DUPLICATE"

  Scenario: [ERROR_124] - Impossibilità di rinominare un documento con il nome di un altro allegato
  Dato un template e-service in bozza con due documenti distinti
  Quando l'erogatore tenta di assegnare al secondo il nome del primo
  Allora il sistema rifiuta la rinomina e segnala che il nome è già in uso.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding di setup comune mancante; riusare createEServiceTemplateDocument per ciascun PDF.
    And i seguenti documenti PDF allegati alla versione del template:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
      | Allegato B | allegato-b.pdf |
    # Operazione BFF e binding negativo comuni mancanti per la rinomina del documento del template.
    When l'erogatore tenta di rinominare il documento "Allegato B" della versione del template in "Allegato A"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_127] - Impossibilità di scaricare un documento assente dalla versione del template
  Dato un template e-service pubblicato
  Quando l'erogatore tenta di scaricare un documento che non appartiene alla versione
  Allora il sistema rifiuta il download e segnala che il documento non è disponibile.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.json |
    # Binding negativo mancante; riusare getEServiceTemplateDocumentById con un riferimento assente valido.
    When l'erogatore tenta di scaricare un documento assente dalla versione del template
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DOCUMENT_NOT_FOUND"

  Scenario: [ERROR_130] - Rifiuto del caricamento di un documento con un link nel nome
  Dato un template e-service con una versione in bozza
  Quando l'erogatore tenta di allegare un PDF valido con un collegamento nel nome
  Allora il sistema rifiuta il caricamento e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding negativo comune mancante; riusare createEServiceTemplateDocument senza attendere il successo.
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione del template con il nome "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS"

  Scenario: [ERROR_131] - Rifiuto della rinomina di un documento con un link nel nome
  Dato un template e-service in bozza con un documento allegato
  Quando l'erogatore tenta di inserire un collegamento nel nome del documento
  Allora il sistema rifiuta la rinomina e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia               |
      | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding di setup comune mancante; riusare createEServiceTemplateDocument per ciascun PDF.
    And i seguenti documenti PDF allegati alla versione del template:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
    # Operazione BFF e binding negativo comuni mancanti per la rinomina del documento del template.
    When l'erogatore tenta di rinominare il documento "Allegato A" della versione del template in "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"
