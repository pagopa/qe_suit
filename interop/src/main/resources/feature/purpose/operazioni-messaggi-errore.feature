@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per finalità e analisi del rischio

  # POST /purposes · duplicatedPurposeTitle
  Scenario: [ERROR_018] L’operatore autorizzato crea la finalità con il nome già in uso — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When l’operatore autorizzato crea la finalità con il nome già in uso
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES"

  # POST /reverse/purposes · duplicatedPurposeTitle
  Scenario: [ERROR_019] L’operatore autorizzato crea la finalità per un e-service in modalità ricezione con il nome già in uso — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When l’operatore autorizzato crea la finalità per un e-service in modalità ricezione con il nome già in uso
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_REVERSE_PURPOSES"

  # POST /purposes/:purposeId · duplicatedPurposeTitle
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_020] Il fruitore modifica la finalità impostando il nome "Catalogo duplicato" — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When il fruitore modifica la finalità impostando il nome "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES_PURPOSE_ID"

  # POST /purposes/:purposeId · hyperlinkDetectionError
  Scenario: [ERROR_021] Il fruitore modifica la finalità impostando il nome "Valore https://example.org" — la finalità o il template in bozza con gli altri dati validi
    Given la finalità o il template in bozza con gli altri dati validi
    When il fruitore modifica la finalità impostando il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__POST_PURPOSES_PURPOSE_ID"

  # POST /purposes/:purposeId/clone · duplicatedPurposeTitle
  Scenario: [ERROR_022] L’operatore autorizzato duplica la finalità — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When l’operatore autorizzato duplica la finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES_PURPOSE_ID_CLONE"

  # POST /purposeTemplates/:purposeTemplateId/purposes · duplicatedPurposeTitle
  Scenario: [ERROR_023] L’operatore autorizzato crea la finalità a partire dal template con il nome già in uso — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When l’operatore autorizzato crea la finalità a partire dal template con il nome già in uso
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES"

  # PATCH /purposeTemplates/:purposeTemplateId/purposes/:purposeId · duplicatedPurposeTitle
  Scenario: [ERROR_024] L’operatore autorizzato modifica la finalità creata da template con il nome già in uso — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When l’operatore autorizzato modifica la finalità creata da template con il nome già in uso
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES_PURPOSE_ID"

  # POST /purposes/:purposeId/riskAnalysis/sign · riskAnalysisAlreadyApproved
  # Conservare i dati letti prima della modifica. Per il mismatch usare la versione metadata precedente e una richiesta altrimenti valida. Il CSV contiene la riga, ma il simbolo non è presente nel catalogo generato; controllare i codici del workflow valutatore sul branch target.
  Scenario: [ERROR_025] Il valutatore firma l’analisi del rischio — un’analisi del rischio già approvata
    Given un’analisi del rischio già approvata
    When il valutatore firma l’analisi del rischio
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_SIGN"

  # POST /purposes/:purposeId/riskAnalysis/sign · riskAnalysisAlreadyRejected
  # Conservare i dati letti prima della modifica. Per il mismatch usare la versione metadata precedente e una richiesta altrimenti valida. Il CSV contiene la riga, ma il simbolo non è presente nel catalogo generato; controllare i codici del workflow valutatore sul branch target.
  Scenario: [ERROR_026] Il valutatore firma l’analisi del rischio — un’analisi del rischio già rifiutata
    Given un’analisi del rischio già rifiutata
    When il valutatore firma l’analisi del rischio
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_SIGN"

  # POST /purposes/:purposeId/riskAnalysis/sign · purposeMetadataVersionMismatch
  Scenario: [ERROR_027] Il valutatore firma l’analisi del rischio — un’analisi del rischio aggiornata dopo che il valutatore ha letto la versione precedente
    Given un’analisi del rischio aggiornata dopo che il valutatore ha letto la versione precedente
    When il valutatore firma l’analisi del rischio
    Then il messaggio di errore corrisponde al caso "PURPOSE_PURPOSE_METADATA_VERSION_MISMATCH"

  # POST /purposes/:purposeId/riskAnalysis/reject · riskAnalysisAlreadyApproved
  # Conservare i dati letti prima della modifica. Per il mismatch usare la versione metadata precedente e una richiesta altrimenti valida. Il CSV contiene la riga, ma il simbolo non è presente nel catalogo generato; controllare i codici del workflow valutatore sul branch target.
  Scenario: [ERROR_028] Il valutatore rifiuta l’analisi del rischio con la motivazione "Informazioni insufficienti" — un’analisi del rischio già approvata
    Given un’analisi del rischio già approvata
    When il valutatore rifiuta l’analisi del rischio con la motivazione "Informazioni insufficienti"
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT"

  # POST /purposes/:purposeId/riskAnalysis/reject · riskAnalysisAlreadyRejected
  # Conservare i dati letti prima della modifica. Per il mismatch usare la versione metadata precedente e una richiesta altrimenti valida. Il CSV contiene la riga, ma il simbolo non è presente nel catalogo generato; controllare i codici del workflow valutatore sul branch target.
  Scenario: [ERROR_029] Il valutatore rifiuta l’analisi del rischio con la motivazione "Informazioni insufficienti" — un’analisi del rischio già rifiutata
    Given un’analisi del rischio già rifiutata
    When il valutatore rifiuta l’analisi del rischio con la motivazione "Informazioni insufficienti"
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT"

  # PUT /purposes/:purposeId/riskAnalysis/form · riskAnalysisAlreadyApproved
  # Conservare i dati letti prima della modifica. Per il mismatch usare la versione metadata precedente e una richiesta altrimenti valida. Il CSV contiene la riga, ma il simbolo non è presente nel catalogo generato; controllare i codici del workflow valutatore sul branch target.
  Scenario: [ERROR_030] Il valutatore salva le risposte dell’analisi del rischio — un’analisi del rischio già approvata
    Given un’analisi del rischio già approvata
    When il valutatore salva le risposte dell’analisi del rischio
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__PUT_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_FORM"

  # PUT /purposes/:purposeId/riskAnalysis/form · riskAnalysisAlreadyRejected
  # Conservare i dati letti prima della modifica. Per il mismatch usare la versione metadata precedente e una richiesta altrimenti valida. Il CSV contiene la riga, ma il simbolo non è presente nel catalogo generato; controllare i codici del workflow valutatore sul branch target.
  Scenario: [ERROR_031] Il valutatore salva le risposte dell’analisi del rischio — un’analisi del rischio già rifiutata
    Given un’analisi del rischio già rifiutata
    When il valutatore salva le risposte dell’analisi del rischio
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__PUT_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_FORM"

  # POST /purposes/:purposeId/riskAnalysis/reject · hyperlinkDetectionError
  Scenario: [ERROR_032] Il valutatore rifiuta l’analisi del rischio con la motivazione "Motivazione https://example.org" — la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    Given la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    When il valutatore rifiuta l’analisi del rischio con la motivazione "Motivazione https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT"

  # PATCH /purposes/:purposeId · duplicatedPurposeTitle
  # Per una modifica usare un oggetto distinto. Per la duplicazione della finalità controllare il nome derivato dal clone. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_033] Il fruitore aggiorna parzialmente la finalità impostando il nome "Finalità duplicata" — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When il fruitore aggiorna parzialmente la finalità impostando il nome "Finalità duplicata"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_PURPOSES_PURPOSE_ID"

  # PATCH /purposes/:purposeId · hyperlinkDetectionError
  # Usare https://example.org nel campo indicato e valori validi per gli altri campi. Il body deve superare la validazione dello schema. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_034] Il fruitore aggiorna parzialmente la finalità impostando il nome "Finalità https://example.org" — la finalità o il template in bozza con gli altri dati validi
    Given la finalità o il template in bozza con gli altri dati validi
    When il fruitore aggiorna parzialmente la finalità impostando il nome "Finalità https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__PATCH_PURPOSES_PURPOSE_ID"

  # PATCH /reverse/purposes/:purposeId · duplicatedPurposeTitle
  # Per una modifica usare un oggetto distinto. Per la duplicazione della finalità controllare il nome derivato dal clone. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_035] Il fruitore aggiorna parzialmente la finalità per un e-service in modalità ricezione impostando il nome "Finalità duplicata" — una finalità omonima dello stesso fruitore per lo stesso e-service
    Given una finalità omonima dello stesso fruitore per lo stesso e-service
    When il fruitore aggiorna parzialmente la finalità per un e-service in modalità ricezione impostando il nome "Finalità duplicata"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_REVERSE_PURPOSES_PURPOSE_ID"

  # PATCH /reverse/purposes/:purposeId · hyperlinkDetectionError
  # Usare https://example.org nel campo indicato e valori validi per gli altri campi. Il body deve superare la validazione dello schema. Nessuna rotta PATCH nel commit analizzato. Verificare il branch di rilascio e l’eventuale sostituzione funzionale. Non rimappare questo ID su un altro endpoint.
  Scenario: [ERROR_036] Il fruitore aggiorna parzialmente la finalità per un e-service in modalità ricezione impostando il nome "Finalità https://example.org" — la finalità o il template in bozza con gli altri dati validi
    Given la finalità o il template in bozza con gli altri dati validi
    When il fruitore aggiorna parzialmente la finalità per un e-service in modalità ricezione impostando il nome "Finalità https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__PATCH_REVERSE_PURPOSES_PURPOSE_ID"
