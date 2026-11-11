@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per contratto e allegati di fruizione

  # GET /agreements/:agreementId/signedContract · contractException
  # Richiede controllo deterministico del generatore documentale. Evitare finestre temporali e retry nel When.
  Scenario: [ERROR_168] L’operatore autorizzato scarica il documento firmato della richiesta di fruizione — una richiesta di fruizione esistente il cui documento firmato non è ancora disponibile
    Given una richiesta di fruizione esistente il cui documento firmato non è ancora disponibile
    When l’operatore autorizzato scarica il documento firmato della richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "BFF_CONTRACT_EXCEPTION"

  # POST /agreements/:agreementId/consumer-documents · invalidFileUploadError
  Scenario: [ERROR_197] L'utente allega alla richiesta di fruizione il file "pdf-con-contenuto-non-valido.pdf" — una risorsa in bozza e un file con MIME ammesso ma con contenuto incompatibile con il formato dichiarato
    Given una risorsa in bozza e un file con MIME ammesso ma con contenuto incompatibile con il formato dichiarato
    When l'utente allega alla richiesta di fruizione il file "pdf-con-contenuto-non-valido.pdf"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_INVALID_FILE_UPLOAD_ERROR"
