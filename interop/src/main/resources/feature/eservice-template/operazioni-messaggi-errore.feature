@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per la gestione dei template e-service

  # POST /eservices/templates · eserviceTemplateDuplicate
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_121] L'erogatore crea un template e-service con il nome "Catalogo duplicato" — un template e-service omonimo nello stesso ambito
    Given un template e-service omonimo nello stesso ambito
    When l'erogatore crea un template e-service con il nome "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__POST_ESERVICES_TEMPLATES"

  # POST /eservices/templates/:eServiceTemplateId/riskAnalysis · riskAnalysisNameDuplicate
  Scenario: [ERROR_125] L’operatore autorizzato aggiunge un’analisi del rischio al template e-service con il nome già in uso — un’analisi del rischio omonima nello stesso template e-service
    Given un’analisi del rischio omonima nello stesso template e-service
    When l’operatore autorizzato aggiunge un’analisi del rischio al template e-service con il nome già in uso
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_RISK_ANALYSIS_NAME_DUPLICATE"

  # POST /eservices/templates/:eServiceTemplateId/name/update · eserviceTemplateDuplicate
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_126] L'erogatore rinomina il template e-service in "Catalogo duplicato" — un template e-service omonimo nello stesso ambito
    Given un template e-service omonimo nello stesso ambito
    When l'erogatore rinomina il template e-service in "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_NAME_UPDATE"

  # POST /eservices/templates · hyperlinkDetectionError
  Scenario: [ERROR_128] L'erogatore crea un template e-service con il nome "Valore https://example.org" — l’operatore abilitato alla creazione con dati altrimenti validi
    Given l’operatore abilitato alla creazione con dati altrimenti validi
    When l'erogatore crea un template e-service con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES"

  # POST /eservices/templates/:eServiceTemplateId/versions/:eServiceTemplateVersionId · hyperlinkDetectionError
  Scenario: [ERROR_129] L’operatore autorizzato modifica la bozza di versione del template e-service con un collegamento nella descrizione della versione — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | DRAFT | openapi3-riferimenti.json |
    When l’operatore autorizzato modifica la bozza di versione del template e-service con un collegamento nella descrizione della versione
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID"

  # POST /eservices/templates/:eServiceTemplateId/intendedTarget/update · hyperlinkDetectionError
  Scenario: [ERROR_132] L’operatore autorizzato modifica il destinatario del template e-service con un collegamento nel campo “A chi è rivolto il template” — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | PUBLISHED | openapi3-riferimenti.json |
    When l’operatore autorizzato modifica il destinatario del template e-service con un collegamento nel campo “A chi è rivolto il template”
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_INTENDED_TARGET_UPDATE"

  # POST /eservices/templates/:eServiceTemplateId/description/update · hyperlinkDetectionError
  Scenario: [ERROR_133] L’operatore autorizzato modifica la descrizione del template e-service con un collegamento nella descrizione del template — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | PUBLISHED | openapi3-riferimenti.json |
    When l’operatore autorizzato modifica la descrizione del template e-service con un collegamento nella descrizione del template
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_DESCRIPTION_UPDATE"

  # POST /eservices/templates/:eServiceTemplateId/name/update · hyperlinkDetectionError
  Scenario: [ERROR_134] L'erogatore rinomina il template e-service in "Valore https://example.org" — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato | interfaccia |
      | REST | PUBLISHED | openapi3-riferimenti.json |
    When l'erogatore rinomina il template e-service in "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_NAME_UPDATE"

  # PATCH /eservices/templates/:eServiceTemplateId · eserviceTemplateDuplicate
  # Per una modifica usare un oggetto distinto. Per la duplicazione della finalità controllare il nome derivato dal clone. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_135] L’operatore autorizzato modifica i dati del template e-service con il nome già in uso — un template e-service omonimo nello stesso ambito
    Given un template e-service omonimo nello stesso ambito
    When l’operatore autorizzato modifica i dati del template e-service con il nome già in uso
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID"

  # PATCH /eservices/templates/:eServiceTemplateId · instanceNameConflict
  # Per una modifica usare un oggetto distinto. Per la duplicazione della finalità controllare il nome derivato dal clone. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_136] L’operatore autorizzato modifica i dati del template e-service con il nome già in uso — un e-service con il nome che avrebbe una delle istanze dopo la rinomina del template
    Given un e-service con il nome che avrebbe una delle istanze dopo la rinomina del template
    When l’operatore autorizzato modifica i dati del template e-service con il nome già in uso
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INSTANCE_NAME_CONFLICT__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID"

  # PATCH /eservices/templates/:eServiceTemplateId · hyperlinkDetectionError
  # Usare https://example.org nel campo indicato e valori validi per gli altri campi. Il body deve superare la validazione dello schema. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_137] L’operatore autorizzato modifica i dati del template e-service con un collegamento nel nome — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    When l’operatore autorizzato modifica i dati del template e-service con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID"
