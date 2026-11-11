@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore per i documenti dei template e-service

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  # POST /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId/documents · documentPrettyNameDuplicate
  Scenario: [ERROR_122] Caricamento di un documento con un nome già in uso
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | DRAFT | openapi3-riferimenti.json |
    And i seguenti documenti PDF allegati alla versione del template:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
    When l'erogatore carica il PDF "allegato-b.pdf" con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS"

  # POST /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId/documents · checksumDuplicate
  Scenario: [ERROR_123] Caricamento di un documento con contenuto già presente
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | DRAFT | openapi3-riferimenti.json |
    And i seguenti documenti PDF allegati alla versione del template:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
    When l'erogatore carica il PDF "allegato-a.pdf" con il nome "Allegato B"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_CHECKSUM_DUPLICATE"

  # POST /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId/documents/:documentId/update · documentPrettyNameDuplicate
  Scenario: [ERROR_124] Rinomina con il nome di un altro documento
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | DRAFT | openapi3-riferimenti.json |
    And i seguenti documenti PDF allegati alla versione del template:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
      | Allegato B | allegato-b.pdf |
    When l'erogatore rinomina il documento "Allegato B" in "Allegato A"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"

  # GET /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId/documents/:documentId · eserviceTemplateDocumentNotFound
  Scenario: [ERROR_127] Scaricamento di un documento assente dalla versione del template
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | PUBLISHED | openapi3-riferimenti.json |
    When l'erogatore scarica un documento assente dalla versione del template
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DOCUMENT_NOT_FOUND"

  # POST /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId/documents · hyperlinkDetectionError
  Scenario: [ERROR_130] Caricamento di un documento con un link nel nome
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | DRAFT | openapi3-riferimenti.json |
    When l'erogatore carica il PDF "allegato-a.pdf" con il nome "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS"

  # POST /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId/documents/:documentId/update · hyperlinkDetectionError
  Scenario: [ERROR_131] Rinomina di un documento con un link nel nome
    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | DRAFT | openapi3-riferimenti.json |
    And i seguenti documenti PDF allegati alla versione del template:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
    When l'erogatore rinomina il documento "Allegato A" in "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"
