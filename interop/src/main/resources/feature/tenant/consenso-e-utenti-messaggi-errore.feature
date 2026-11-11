@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per consenso e utenti dell’ente

  # GET /user/consent/:consentType · privacyNoticeNotFoundInConfiguration
  # Caso dipendente da configurazione o servizio a valle. Predisporre ambiente isolato e verificare quale guardia viene raggiunta per il tipo di consenso.
  Scenario: [ERROR_176] L’operatore autorizzato verifica il consenso per l’informativa — un utente autenticato e il tipo di consenso privo di configurazione
    Given un utente autenticato e il tipo di consenso privo di configurazione
    When l’operatore autorizzato verifica il consenso per l’informativa
    Then il messaggio di errore corrisponde al caso "BFF_PRIVACY_NOTICE_NOT_FOUND_IN_CONFIGURATION"

  # GET /user/consent/:consentType · privacyNoticeNotFound
  # Caso dipendente da configurazione o servizio a valle. Predisporre ambiente isolato e verificare quale guardia viene raggiunta per il tipo di consenso.
  Scenario: [ERROR_177] L’operatore autorizzato verifica il consenso per l’informativa — un utente autenticato e il tipo di consenso configurato senza una versione dell’informativa nello storage
    Given un utente autenticato e il tipo di consenso configurato senza una versione dell’informativa nello storage
    When l’operatore autorizzato verifica il consenso per l’informativa
    Then il messaggio di errore corrisponde al caso "BFF_PRIVACY_NOTICE_NOT_FOUND__GET_USER_CONSENT_CONSENT_TYPE"

  # POST /user/consent/:consentType · privacyNoticeNotFound
  # Caso dipendente da configurazione o servizio a valle. Predisporre ambiente isolato e verificare quale guardia viene raggiunta per il tipo di consenso. Usare un latestVersionId formalmente valido e storage senza versione per il tipo di consenso.
  Scenario: [ERROR_178] L’operatore autorizzato registra il consenso per l’informativa — un utente autenticato e il tipo di consenso configurato senza una versione dell’informativa nello storage
    Given un utente autenticato e il tipo di consenso configurato senza una versione dell’informativa nello storage
    When l’operatore autorizzato registra il consenso per l’informativa
    Then il messaggio di errore corrisponde al caso "BFF_PRIVACY_NOTICE_NOT_FOUND__POST_USER_CONSENT_CONSENT_TYPE"

  # GET /users/:userId · userNotFound
  Scenario: [ERROR_179] L’operatore autorizzato consulta i dettagli del membro dell’ente — un ID utente noto che non figura tra i membri attivi dell’ente richiedente
    Given un ID utente noto che non figura tra i membri attivi dell’ente richiedente
    When l’operatore autorizzato consulta i dettagli del membro dell’ente
    Then il messaggio di errore corrisponde al caso "BFF_USER_NOT_FOUND"
