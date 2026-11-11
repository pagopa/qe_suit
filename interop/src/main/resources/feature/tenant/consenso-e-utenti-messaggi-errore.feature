@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione del consenso e la consultazione degli utenti dell'ente

  Come utente di un ente
  Voglio sapere perché il consenso all'informativa o la consultazione di un membro non sono disponibili
  Per verificare la disponibilità dell'informativa o l'appartenenza dell'utente all'ente.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_176] - Impossibilità di consultare il consenso per un'informativa non configurata
  Dato un utente autenticato e un tipo di consenso privo di configurazione
  Quando l'utente tenta di verificare il consenso per quel tipo di informativa
  Allora il sistema rifiuta la consultazione e segnala che l'informativa non è configurata.

    # Setup deterministico e binding mancanti; usare un ambiente isolato senza modificare le risorse di ambiente.
    Given un tipo di consenso privo di configurazione
    # Operazione BFF e binding negativo comune mancanti per la lettura del consenso.
    When l'utente tenta di verificare il consenso per l'informativa predisposta
    Then il messaggio di errore corrisponde al caso "BFF_PRIVACY_NOTICE_NOT_FOUND_IN_CONFIGURATION"

  Scenario: [ERROR_177] - Impossibilità di consultare il consenso per un'informativa configurata ma non disponibile
  Dato un utente autenticato e un tipo di consenso configurato la cui informativa non è disponibile
  Quando l'utente tenta di verificare il consenso per quel tipo di informativa
  Allora il sistema rifiuta la consultazione e segnala che l'informativa non è disponibile.

    # Setup deterministico e binding comuni mancanti; ambiente isolato con configurazione valida e documento assente.
    Given un tipo di consenso configurato senza una versione dell'informativa nello storage
    # Operazione BFF e binding negativo comune mancanti per la lettura del consenso.
    When l'utente tenta di verificare il consenso per l'informativa predisposta
    Then il messaggio di errore corrisponde al caso "BFF_PRIVACY_NOTICE_NOT_FOUND__GET_USER_CONSENT_CONSENT_TYPE"

  Scenario: [ERROR_178] - Impossibilità di registrare il consenso per un'informativa non disponibile
  Dato un utente autenticato e un tipo di consenso configurato la cui informativa non è disponibile
  Quando l'utente tenta di registrare il consenso per quel tipo di informativa
  Allora il sistema rifiuta la registrazione e segnala che l'informativa non è disponibile.

    # Setup deterministico e binding comuni mancanti; ambiente isolato con configurazione valida e documento assente.
    Given un tipo di consenso configurato senza una versione dell'informativa nello storage
    # Operazione BFF e binding mancanti; usare un latestVersionId formalmente valido per il consenso predisposto.
    When l'utente tenta di registrare il consenso per l'informativa predisposta
    Then il messaggio di errore corrisponde al caso "BFF_PRIVACY_NOTICE_NOT_FOUND__POST_USER_CONSENT_CONSENT_TYPE"

  Scenario: [ERROR_179] - Impossibilità di consultare un utente che non è membro attivo dell'ente
  Dato un utente noto che non figura tra i membri attivi dell'ente richiedente
  Quando l'amministratore tenta di consultarne i dettagli
  Allora il sistema rifiuta la consultazione e segnala che l'utente non è presente tra i membri dell'ente.

    # Binding di setup mancante; predisporre un riferimento valido, non un identificativo malformato.
    Given un utente noto che non figura tra i membri attivi del Comune di Milano
    # Operazione BFF e binding mancanti per la consultazione del membro.
    When l'utente tenta di consultare i dettagli del membro predisposto
    Then il messaggio di errore corrisponde al caso "BFF_USER_NOT_FOUND"
