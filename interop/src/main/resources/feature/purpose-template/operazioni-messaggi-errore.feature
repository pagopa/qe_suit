@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per la gestione dei template finalità

  # POST /purposeTemplates/:purposeTemplateId/unsuspend · invalidAssociatedEServiceForPublicationError
  Scenario: [ERROR_138] L’operatore autorizzato riattiva il template finalità — un template finalità pronto per l’operazione con un e-service suggerito sospeso
    Given un template finalità pronto per l’operazione con un e-service suggerito sospeso
    When l’operatore autorizzato riattiva il template finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_ASSOCIATED_ESERVICE_FOR_PUBLICATION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_UNSUSPEND"

  # POST /purposeTemplates/:purposeTemplateId/publish · invalidAssociatedEServiceForPublicationError
  Scenario: [ERROR_139] L’operatore autorizzato pubblica il template finalità — un template finalità pronto per l’operazione con un e-service suggerito sospeso
    Given un template finalità pronto per l’operazione con un e-service suggerito sospeso
    When l’operatore autorizzato pubblica il template finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_ASSOCIATED_ESERVICE_FOR_PUBLICATION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PUBLISH"

  # POST /purposeTemplates/:purposeTemplateId/riskAnalysis/answers/:answerId/annotation/documents · conflictDocumentPrettyNameDuplicate
  Scenario: [ERROR_140] L'operatore allega il file "allegato-b.pdf" all'annotazione del template finalità con il nome "Allegato A" — un documento già allegato nell’annotazione del template finalità
    Given un’annotazione del template finalità in bozza con il documento "Allegato A" caricato dalla fixture "allegato-a.pdf"
    When l'operatore allega il file "allegato-b.pdf" all'annotazione del template finalità con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_CONFLICT_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION_DOCUMENTS"

  # POST /purposeTemplates/:purposeTemplateId/riskAnalysis/answers/:answerId/annotation/documents · conflictDuplicatedDocument
  Scenario: [ERROR_141] L'operatore allega il file "allegato-a.pdf" all'annotazione del template finalità con il nome "Allegato B" — un file già allegato nell’annotazione del template finalità
    Given un’annotazione del template finalità in bozza con il documento "Allegato A" caricato dalla fixture "allegato-a.pdf"
    When l'operatore allega il file "allegato-a.pdf" all'annotazione del template finalità con il nome "Allegato B"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_CONFLICT_DUPLICATED_DOCUMENT"

  # POST /purposeTemplates/:purposeTemplateId/riskAnalysis/answers · hyperlinkDetectionError
  Scenario: [ERROR_142] L’operatore autorizzato aggiunge una risposta con annotazione all’analisi del rischio del template finalità con un collegamento nella annotazione — il template finalità in bozza con analisi del rischio e domanda predisposta per le annotazioni
    Given il template finalità in bozza con analisi del rischio e domanda predisposta per le annotazioni
    When l’operatore autorizzato aggiunge una risposta con annotazione all’analisi del rischio del template finalità con un collegamento nella annotazione
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS"

  # PUT /purposeTemplates/:purposeTemplateId/riskAnalysis/answers/:answerId/annotation · hyperlinkDetectionError
  Scenario: [ERROR_143] L’operatore autorizzato aggiorna l’annotazione della risposta del template finalità con un collegamento nella annotazione — il template finalità in bozza con analisi del rischio e domanda predisposta per le annotazioni
    Given il template finalità in bozza con analisi del rischio e domanda predisposta per le annotazioni
    When l’operatore autorizzato aggiorna l’annotazione della risposta del template finalità con un collegamento nella annotazione
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PUT_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION"

  # POST /purposeTemplates/:purposeTemplateId/riskAnalysis/answers/:answerId/annotation/documents/:documentId/update · conflictDocumentPrettyNameDuplicate
  Scenario: [ERROR_144] L’operatore autorizzato rinomina l’allegato dell’annotazione del template finalità usando il nome dell’altro documento — due documenti distinti già allegati nell’annotazione del template finalità
    Given due documenti distinti già allegati nell’annotazione del template finalità
    When l’operatore autorizzato rinomina l’allegato dell’annotazione del template finalità usando il nome dell’altro documento
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_CONFLICT_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION_DOCUMENTS_DOCUMENT_ID_UPDATE"

  # GET /purposeTemplates/:purposeTemplateId/riskAnalysisDocument/signed · unauthorizedError
  Scenario: [ERROR_145] L’operatore di sicurezza scarica il documento firmato di analisi del rischio del template finalità — un documento disponibile e un operatore di sicurezza dell’ente abilitato alla consultazione della risorsa
    Given un documento disponibile e un operatore di sicurezza dell’ente abilitato alla consultazione della risorsa
    When l’operatore di sicurezza scarica il documento firmato di analisi del rischio del template finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_UNAUTHORIZED_ERROR"

  # PATCH /purposeTemplates/:purposeTemplateId · purposeTemplateTitleConflict
  # Per una modifica usare un oggetto distinto. Per la duplicazione della finalità controllare il nome derivato dal clone. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_146] L’operatore autorizzato modifica il template finalità con il nome già in uso — un template finalità omonimo nello stesso ambito
    Given un template finalità omonimo nello stesso ambito
    When l’operatore autorizzato modifica il template finalità con il nome già in uso
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_PURPOSE_TEMPLATE_TITLE_CONFLICT__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID"

  # PATCH /purposeTemplates/:purposeTemplateId · hyperlinkDetectionError
  # Usare https://example.org nel campo indicato e valori validi per gli altri campi. Il body deve superare la validazione dello schema. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_147] L’operatore autorizzato modifica il template finalità con un collegamento nel nome — la finalità o il template in bozza con gli altri dati validi
    Given la finalità o il template in bozza con gli altri dati validi
    When l’operatore autorizzato modifica il template finalità con un collegamento nel nome
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID"
