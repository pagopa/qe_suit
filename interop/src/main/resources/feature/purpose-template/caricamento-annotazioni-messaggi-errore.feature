@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per il caricamento negli allegati delle annotazioni

  # POST /purposeTemplates/:purposeTemplateId/riskAnalysis/answers/:answerId/annotation/documents · invalidContentTypeDetected
  Scenario: [ERROR_195] L'operatore allega il file "allegato-non-pdf.txt" all'annotazione del template finalità con il nome "Allegato di test" — un’annotazione in bozza e un file con tipo diverso da PDF
    Given un’annotazione in bozza e un file con tipo diverso da PDF
    When l'operatore allega il file "allegato-non-pdf.txt" all'annotazione del template finalità con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_CONTENT_TYPE_DETECTED"

  # POST /purposeTemplates/:purposeTemplateId/riskAnalysis/answers/:answerId/annotation/documents · invalidPdfSignatureError
  Scenario: [ERROR_196] L'operatore allega il file "pdf-con-contenuto-non-valido.pdf" all'annotazione del template finalità con il nome "Allegato di test" — un’annotazione già predisposta e un file dichiarato PDF con firma binaria non valida
    Given un’annotazione già predisposta e un file dichiarato PDF con firma binaria non valida
    When l'operatore allega il file "pdf-con-contenuto-non-valido.pdf" all'annotazione del template finalità con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_PDF_SIGNATURE_ERROR"
