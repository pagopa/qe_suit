@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per la gestione degli e-service

  # POST /eservices · eServiceNameDuplicateForProducer
  Scenario: [ERROR_037] L'erogatore crea un e-service con il nome "Catalogo duplicato" — un altro e-service omonimo dello stesso erogatore
    Given i seguenti e-service in bozza:
      | alias | erogatore | nome |
      | ES-1 | Comune di Milano | Catalogo duplicato |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore crea un e-service con il nome "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_ESERVICES"

  # POST /eservices · eserviceTemplateNameConflict
  Scenario: [ERROR_038] L'erogatore crea un e-service con il nome "Catalogo duplicato" — un template e-service omonimo
    Given un template e-service omonimo
    When l'erogatore crea un e-service con il nome "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__POST_ESERVICES"

  # POST /templates/:templateId/eservices · eServiceNameDuplicateForProducer
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_039] L'erogatore istanzia il template e-service con la parola identificativa "Catalogo duplicato" — un e-service già istanziato con la stessa parola identificativa e un template pubblicato
    Given un e-service già istanziato con la stessa parola identificativa e un template pubblicato
    When l'erogatore istanzia il template e-service con la parola identificativa "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_TEMPLATE_ID_ESERVICES"

  # POST /templates/eservices/:eServiceId · eServiceNameDuplicateForProducer
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_040] L'operatore aggiorna l'istanza del template e-service con la parola identificativa "Catalogo duplicato" — un e-service già istanziato con la stessa parola identificativa e un template pubblicato
    Given un e-service già istanziato con la stessa parola identificativa e un template pubblicato
    When l'operatore aggiorna l'istanza del template e-service con la parola identificativa "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_ESERVICES_E_SERVICE_ID"

  # POST /eservices/:eServiceId/scheduleArchive · gracePeriodDaysLowerThanDescriptor
  Scenario: [ERROR_041] L'erogatore tenta di archiviare l'e-service con un periodo di preavviso di 60 giorni e una motivazione valida — un e-service con prima versione in archiviazione con preavviso di 120 giorni e seconda pubblicata
    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service in fase di archiviazione con un periodo di preavviso di 120 giorni
    When l'erogatore tenta di archiviare l'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then il messaggio di errore corrisponde al caso "CATALOG_GRACE_PERIOD_DAYS_LOWER_THAN_DESCRIPTOR__POST_ESERVICES_E_SERVICE_ID_SCHEDULE_ARCHIVE"

  # POST /eservices/:eServiceId/submitDelegatedArchiving · gracePeriodDaysLowerThanDescriptor
  Scenario: [ERROR_042] L’amministratore delegato richiede al delegante l’archiviazione dell’intero e-service con 60 giorni di preavviso e una motivazione valida — un e-service con prima versione in archiviazione con preavviso di 120 giorni e seconda pubblicata e una delega in erogazione attiva
    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service in fase di archiviazione con un periodo di preavviso di 120 giorni
    And l'e-service delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore delegato richiede al delegante l’archiviazione dell’intero e-service con 60 giorni di preavviso e una motivazione valida
    Then il messaggio di errore corrisponde al caso "CATALOG_GRACE_PERIOD_DAYS_LOWER_THAN_DESCRIPTOR__POST_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · documentPrettyNameDuplicate
  Scenario: [ERROR_043] L'erogatore carica il file "allegato-b.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato A" — un documento già allegato nella stessa versione in bozza
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    And i seguenti documenti PDF allegati alla versione dell'e-service:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
    When l'erogatore carica il file "allegato-b.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "CATALOG_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · checksumDuplicate
  Scenario: [ERROR_044] L'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato B" — un file già allegato nella stessa versione in bozza
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    And i seguenti documenti PDF allegati alla versione dell'e-service:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato B"
    Then il messaggio di errore corrisponde al caso "CATALOG_CHECKSUM_DUPLICATE"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents/:documentId/update · documentPrettyNameDuplicate
  Scenario: [ERROR_045] L'erogatore rinomina il documento "Allegato B" della versione dell'e-service in "Allegato A" — due documenti distinti già allegati nella stessa versione in bozza
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    And i seguenti documenti PDF allegati alla versione dell'e-service:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
      | Allegato B | allegato-b.pdf |
    When l'erogatore rinomina il documento "Allegato B" della versione dell'e-service in "Allegato A"
    Then il messaggio di errore corrisponde al caso "CATALOG_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"

  # POST /eservices/:eServiceId/riskAnalysis · riskAnalysisDuplicated
  Scenario: [ERROR_046] L’operatore autorizzato aggiunge un’analisi del rischio all’e-service con il nome già in uso — un’analisi del rischio omonima nello stesso e-service
    Given un’analisi del rischio omonima nello stesso e-service
    When l’operatore autorizzato aggiunge un’analisi del rischio all’e-service con il nome già in uso
    Then il messaggio di errore corrisponde al caso "CATALOG_RISK_ANALYSIS_DUPLICATED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS"

  # POST /eservices/:eServiceId/riskAnalysis/:riskAnalysisId · riskAnalysisDuplicated
  Scenario: [ERROR_047] L’operatore autorizzato modifica l’analisi del rischio dell’e-service con il nome già in uso — un’analisi del rischio omonima nello stesso e-service
    Given un’analisi del rischio omonima nello stesso e-service
    When l’operatore autorizzato modifica l’analisi del rischio dell’e-service con il nome già in uso
    Then il messaggio di errore corrisponde al caso "CATALOG_RISK_ANALYSIS_DUPLICATED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS_RISK_ANALYSIS_ID"

  # POST /eservices/:eServiceId/name/update · eServiceNameDuplicateForProducer
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_048] L'operatore modifica il nome dell'e-service in "Catalogo duplicato" — un altro e-service omonimo dello stesso erogatore
    Given un altro e-service omonimo dello stesso erogatore
    When l'operatore modifica il nome dell'e-service in "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE"

  # POST /eservices/:eServiceId/name/update · eserviceTemplateNameConflict
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_049] L'operatore modifica il nome dell'e-service in "Catalogo duplicato" — un template e-service omonimo
    Given un template e-service omonimo
    When l'operatore modifica il nome dell'e-service in "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE"

  # POST /templates/eservices/:eServiceId/instanceLabel/update · eServiceNameDuplicateForProducer
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_050] L'operatore modifica la parola identificativa dell'istanza pubblicata in "Catalogo duplicato" — un e-service già istanziato con la stessa parola identificativa e un template pubblicato
    Given un e-service già istanziato con la stessa parola identificativa e un template pubblicato
    When l'operatore modifica la parola identificativa dell'istanza pubblicata in "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE"

  # POST /templates/eservices/:eServiceId · operationForbidden
  Scenario: [ERROR_051] L'operatore aggiorna l'istanza del template e-service con la parola identificativa "Valore disponibile" — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l'operatore aggiorna l'istanza del template e-service con la parola identificativa "Valore disponibile"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID"

  # DELETE /eservices/:eServiceId · operationForbidden
  Scenario: [ERROR_052] L’amministratore dell’ente già delegato elimina l’e-service in bozza — un e-service prima gestito in delega, con delega revocata e risorsa in bozza
    Given un e-service prima gestito in delega, con delega revocata e risorsa in bozza
    When l’amministratore dell’ente già delegato elimina l’e-service in bozza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID"

  # POST /eservices/:eServiceId/submitDelegatedArchiving · operationForbidden
  # Con delega assente o revocata, il servizio può fermarsi su noDelegationForArchivingRequest prima di operationForbidden. Il copy sulla delega non più attiva va confrontato con questa guardia.
  Scenario: [ERROR_053] Un amministratore diverso dal delegato attivo richiede al delegante l’archiviazione dell’intero e-service — una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione
    Given una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione
    When un amministratore diverso dal delegato attivo richiede al delegante l’archiviazione dell’intero e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING"

  # DELETE /eservices/:eServiceId/submitDelegatedArchiving · operationForbidden
  # Con delega assente o revocata, il servizio può fermarsi su noDelegationForArchivingRequest prima di operationForbidden. Il copy sulla delega non più attiva va confrontato con questa guardia.
  Scenario: [ERROR_054] Un amministratore diverso dal delegato attivo annulla la richiesta di archiviazione dell’intero e-service — una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione con una richiesta di archiviazione pendente
    Given una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione con una richiesta di archiviazione pendente
    When un amministratore diverso dal delegato attivo annulla la richiesta di archiviazione dell’intero e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/submitDelegatedArchiving · operationForbidden
  # Con delega assente o revocata, il servizio può fermarsi su noDelegationForArchivingRequest prima di operationForbidden. Il copy sulla delega non più attiva va confrontato con questa guardia.
  Scenario: [ERROR_055] Un amministratore diverso dal delegato attivo richiede al delegante l’archiviazione della versione — una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione
    Given una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione
    When un amministratore diverso dal delegato attivo richiede al delegante l’archiviazione della versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUBMIT_DELEGATED_ARCHIVING"

  # DELETE /eservices/:eServiceId/descriptors/:descriptorId/submitDelegatedArchiving · operationForbidden
  # Con delega assente o revocata, il servizio può fermarsi su noDelegationForArchivingRequest prima di operationForbidden. Il copy sulla delega non più attiva va confrontato con questa guardia.
  Scenario: [ERROR_056] Un amministratore diverso dal delegato attivo annulla la richiesta di archiviazione della versione — una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione con una richiesta di archiviazione pendente
    Given una delega in erogazione attiva verso un altro ente e l’e-service predisposto per l’archiviazione con una richiesta di archiviazione pendente
    When un amministratore diverso dal delegato attivo annulla la richiesta di archiviazione della versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUBMIT_DELEGATED_ARCHIVING"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · operationForbidden
  Scenario: [ERROR_057] L’amministratore dell’ente già delegato carica il file sulla versione dell’e-service — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato carica il file sulla versione dell’e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS"

  # DELETE /eservices/:eServiceId/descriptors/:descriptorId/documents/:documentId · operationForbidden
  Scenario: [ERROR_058] L’amministratore dell’ente già delegato elimina il documento della versione — un e-service prima gestito in delega, con delega revocata e risorsa in bozza
    Given un e-service prima gestito in delega, con delega revocata e risorsa in bozza
    When l’amministratore dell’ente già delegato elimina il documento della versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents/:documentId/update · operationForbidden
  Scenario: [ERROR_059] L’amministratore dell’ente già delegato rinomina il documento della versione — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato rinomina il documento della versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"

  # POST /eservices/:eServiceId/descriptors · operationForbidden
  Scenario: [ERROR_060] L’amministratore dell’ente già delegato crea una versione dell’e-service — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato crea una versione dell’e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS"

  # DELETE /eservices/:eServiceId/descriptors/:descriptorId · operationForbidden
  Scenario: [ERROR_061] L’amministratore dell’ente già delegato elimina la versione in bozza — un e-service prima gestito in delega, con delega revocata e risorsa in bozza
    Given un e-service prima gestito in delega, con delega revocata e risorsa in bozza
    When l’amministratore dell’ente già delegato elimina la versione in bozza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID"

  # POST /templates/eservices/:eServiceId/descriptors/:descriptorId · operationForbidden
  Scenario: [ERROR_062] L’amministratore dell’ente già delegato modifica la versione in bozza dell’istanza — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato modifica la versione in bozza dell’istanza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/publish · operationForbidden
  Scenario: [ERROR_063] L’amministratore dell’ente già delegato pubblica la versione — un e-service prima gestito in delega, con delega revocata e risorsa con una versione in bozza pronta per la pubblicazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa con una versione in bozza pronta per la pubblicazione
    When l’amministratore dell’ente già delegato pubblica la versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_PUBLISH"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/suspend · operationForbidden
  Scenario: [ERROR_064] L’amministratore dell’ente già delegato sospende la versione — un e-service prima gestito in delega, con delega revocata e risorsa con una versione pubblicata
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato sospende la versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUSPEND"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/activate · operationForbidden
  Scenario: [ERROR_065] L’amministratore dell’ente già delegato riattiva la versione — un e-service prima gestito in delega, con delega revocata e risorsa con una versione sospesa
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato riattiva la versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_ACTIVATE"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/update · operationForbidden
  Scenario: [ERROR_066] L’amministratore dell’ente già delegato modifica la configurazione della versione — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato modifica la configurazione della versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_UPDATE"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/agreementApprovalPolicy/update · operationForbidden
  Scenario: [ERROR_067] L’amministratore dell’ente già delegato modifica la modalità di approvazione delle richieste di fruizione — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato modifica la modalità di approvazione delle richieste di fruizione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_AGREEMENT_APPROVAL_POLICY_UPDATE"

  # POST /eservices/:eServiceId/description/update · operationForbidden
  Scenario: [ERROR_068] L’amministratore dell’ente già delegato modifica la descrizione dell’e-service — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato modifica la descrizione dell’e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTION_UPDATE"

  # POST /eservices/:eServiceId/delegationFlags/update · operationForbidden
  Scenario: [ERROR_069] L’amministratore dell’ente già delegato modifica le impostazioni di delega dell’e-service — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato modifica le impostazioni di delega dell’e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DELEGATION_FLAGS_UPDATE"

  # POST /eservices/:eServiceId/name/update · operationForbidden
  Scenario: [ERROR_070] L'operatore modifica il nome dell'e-service in "Valore disponibile" — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'operatore modifica il nome dell'e-service in "Valore disponibile"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE"

  # POST /eservices/:eServiceId/signalhub/update · operationForbidden
  Scenario: [ERROR_071] L’amministratore dell’ente già delegato modifica l’adesione dell’e-service a Signal Hub — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato modifica l’adesione dell’e-service a Signal Hub
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_SIGNALHUB_UPDATE"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/attributes/update · operationForbidden
  Scenario: [ERROR_072] L’amministratore dell’ente già delegato modifica gli attributi richiesti dalla versione — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato modifica gli attributi richiesti dalla versione
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_ATTRIBUTES_UPDATE"

  # POST /templates/eservices/:eServiceId/upgrade · operationForbidden
  Scenario: [ERROR_073] L’amministratore dell’ente già delegato aggiorna l’istanza alla nuova versione del template — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato aggiorna l’istanza alla nuova versione del template
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_UPGRADE"

  # POST /templates/eservices/:eServiceId/descriptors/:descriptorId/interface/soap · operationForbidden
  Scenario: [ERROR_074] L’amministratore dell’ente già delegato configura l’interfaccia SOAP dell’istanza — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato configura l’interfaccia SOAP dell’istanza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_SOAP"

  # POST /templates/eservices/:eServiceId/descriptors/:descriptorId/interface/rest · operationForbidden
  Scenario: [ERROR_075] L’amministratore dell’ente già delegato configura l’interfaccia REST dell’istanza — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato configura l’interfaccia REST dell’istanza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_REST"

  # POST /templates/eservices/:eServiceId/descriptors/:descriptorId/update · operationForbidden
  Scenario: [ERROR_076] L’amministratore dell’ente già delegato aggiorna la versione dell’istanza — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l’amministratore dell’ente già delegato aggiorna la versione dell’istanza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_UPDATE"

  # POST /eservices/:eServiceId/personalDataFlag · operationForbidden
  Scenario: [ERROR_077] L’amministratore dell’ente già delegato modifica l’indicazione sul trattamento dei dati personali — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l’amministratore dell’ente già delegato modifica l’indicazione sul trattamento dei dati personali
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_PERSONAL_DATA_FLAG"

  # POST /templates/eservices/:eServiceId/instanceLabel/update · operationForbidden
  Scenario: [ERROR_078] L'operatore modifica la parola identificativa dell'istanza pubblicata in "Valore disponibile" — un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    Given un e-service prima gestito in delega, con delega revocata e risorsa nello stato previsto per l’operazione
    When l'operatore modifica la parola identificativa dell'istanza pubblicata in "Valore disponibile"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE"

  # POST /templates/eservices/:eServiceId/descriptors/:descriptorId/interface/rest · openapiVersionNotRecognized
  # Verificare precedenza delle guardie MIME, dimensione, firma e parsing. Inviare multipart valido quando richiesto. Verificare se la validazione in pubblicazione impedisce di creare il template con dati non validi; in quel caso usare fixture isolata o test d’integrazione mirato.
  Scenario: [ERROR_079] L’operatore autorizzato configura l’interfaccia REST dell’istanza — un’istanza in bozza da template con interfaccia REST la cui specifica ha una versione OpenAPI non supportata
    Given un’istanza in bozza da template con interfaccia REST la cui specifica ha una versione OpenAPI non supportata
    When l’operatore autorizzato configura l’interfaccia REST dell’istanza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPENAPI_VERSION_NOT_RECOGNIZED__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_REST"

  # POST /eservices · hyperlinkDetectionError
  Scenario: [ERROR_080] L'erogatore crea un e-service con il nome "Valore https://example.org" — l’operatore abilitato alla creazione con dati altrimenti validi
    Given l’operatore abilitato alla creazione con dati altrimenti validi
    When l'erogatore crea un e-service con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES"

  # POST /templates/:templateId/eservices · hyperlinkDetectionError
  Scenario: [ERROR_081] L'erogatore istanzia il template e-service con la parola identificativa "Valore https://example.org" — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    When l'erogatore istanzia il template e-service con la parola identificativa "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_TEMPLATE_ID_ESERVICES"

  # POST /templates/eservices/:eServiceId · hyperlinkDetectionError
  Scenario: [ERROR_082] L'operatore aggiorna l'istanza del template e-service con la parola identificativa "Valore https://example.org" — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    When l'operatore aggiorna l'istanza del template e-service con la parola identificativa "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_ESERVICES_E_SERVICE_ID"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents · hyperlinkDetectionError
  Scenario: [ERROR_083] L'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Documento https://example.org" — la versione in bozza e predisposta per la gestione dei documenti
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/documents/:documentId/update · hyperlinkDetectionError
  Scenario: [ERROR_084] L'erogatore rinomina il documento "Allegato A" della versione dell'e-service in "Documento https://example.org" — la versione in bozza e predisposta per la gestione dei documenti
    Given i seguenti e-service in bozza:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano
    And i seguenti documenti PDF allegati alla versione dell'e-service:
      | nome | fixture |
      | Allegato A | allegato-a.pdf |
    When l'erogatore rinomina il documento "Allegato A" della versione dell'e-service in "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE"

  # POST /eservices/:eServiceId/descriptors · hyperlinkDetectionError
  Scenario: [ERROR_085] L’operatore autorizzato crea una versione dell’e-service con un collegamento nella descrizione della versione — l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    Given l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    When l’operatore autorizzato crea una versione dell’e-service con un collegamento nella descrizione della versione
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS"

  # POST /eservices/:eServiceId/description/update · hyperlinkDetectionError
  Scenario: [ERROR_086] L’operatore autorizzato modifica la descrizione dell’e-service con un collegamento nella descrizione dell’e-service — l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    Given l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    When l’operatore autorizzato modifica la descrizione dell’e-service con un collegamento nella descrizione dell’e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTION_UPDATE"

  # POST /eservices/:eServiceId/name/update · hyperlinkDetectionError
  Scenario: [ERROR_087] L'operatore modifica il nome dell'e-service in "Valore https://example.org" — l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    Given l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    When l'operatore modifica il nome dell'e-service in "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE"

  # POST /eservices/:eServiceId/descriptors/:descriptorId/reject · hyperlinkDetectionError
  Scenario: [ERROR_088] L’operatore autorizzato rifiuta la pubblicazione della versione proposta dal delegato con un collegamento nella motivazione del rifiuto — la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    Given la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    When l’operatore autorizzato rifiuta la pubblicazione della versione proposta dal delegato con un collegamento nella motivazione del rifiuto
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_REJECT"

  # POST /templates/eservices/:eServiceId/instanceLabel/update · hyperlinkDetectionError
  Scenario: [ERROR_089] L'operatore modifica la parola identificativa dell'istanza pubblicata in "Valore https://example.org" — il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    Given il template pubblicato per la creazione dell’istanza oppure l’istanza predisposta per l’operazione di modifica
    When l'operatore modifica la parola identificativa dell'istanza pubblicata in "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE"

  # PATCH /eservices/:eServiceId · eServiceNameDuplicateForProducer
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_090] L'erogatore aggiorna parzialmente i dati dell'e-service impostando il nome "Catalogo duplicato" — un altro e-service omonimo dello stesso erogatore
    Given un altro e-service omonimo dello stesso erogatore
    When l'erogatore aggiorna parzialmente i dati dell'e-service impostando il nome "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__PATCH_ESERVICES_E_SERVICE_ID"

  # PATCH /eservices/:eServiceId · eserviceTemplateNameConflict
  # Associare il nome richiesto alla risorsa omonima predisposta oppure alla parola identificativa che genera il nome in conflitto.
  Scenario: [ERROR_091] L'erogatore aggiorna parzialmente i dati dell'e-service impostando il nome "Catalogo duplicato" — un template e-service omonimo
    Given un template e-service omonimo
    When l'erogatore aggiorna parzialmente i dati dell'e-service impostando il nome "Catalogo duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__PATCH_ESERVICES_E_SERVICE_ID"

  # PATCH /eservices/:eServiceId · hyperlinkDetectionError
  Scenario: [ERROR_092] L'erogatore aggiorna parzialmente i dati dell'e-service impostando il nome "Valore https://example.org" — l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    Given l’operatore abilitato con la risorsa e i dati predisposti per la modifica
    When l'erogatore aggiorna parzialmente i dati dell'e-service impostando il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__PATCH_ESERVICES_E_SERVICE_ID"
