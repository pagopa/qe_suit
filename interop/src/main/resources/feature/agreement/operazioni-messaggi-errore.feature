@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per le richieste di fruizione

  # POST /agreements/:agreementId/submit · agreementSubmissionFailed
  Scenario: [ERROR_148] Il fruitore invia la richiesta di fruizione — una richiesta in bozza e il fruitore che non soddisfa gli attributi richiesti per l’invio
    Given una richiesta in bozza e il fruitore che non soddisfa gli attributi richiesti per l’invio
    When il fruitore invia la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_SUBMISSION_FAILED"

  # POST /agreements/:agreementId/approve · agreementNotInExpectedState
  Scenario: [ERROR_149] L'erogatore approva la richiesta di fruizione — una richiesta di fruizione attiva e il ruolo competente predisposto
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And un admin appartenente all'erogatore Comune di Milano
    When l'erogatore approva la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_APPROVE"

  # POST /agreements/:agreementId/approve · agreementActivationFailed
  Scenario: [ERROR_150] L'erogatore approva la richiesta di fruizione — una richiesta in attesa di approvazione e il fruitore che ha perso un attributo necessario
    Given una richiesta in attesa di approvazione e il fruitore che ha perso un attributo necessario
    When l'erogatore approva la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_ACTIVATION_FAILED"

  # POST /agreements/:agreementId/approve · descriptorNotInExpectedState
  Scenario: [ERROR_151] L'erogatore approva la richiesta di fruizione — la versione dell’e-service archiviata o in archiviazione con richiesta di fruizione in attesa di approvazione
    Given la versione dell’e-service archiviata o in archiviazione con richiesta di fruizione in attesa di approvazione
    When l'erogatore approva la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_APPROVE"

  # POST /agreements/:agreementId/unsuspend · agreementNotInExpectedState
  Scenario: [ERROR_152] Il fruitore riattiva la richiesta di fruizione — una richiesta di fruizione attiva e il ruolo competente predisposto
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And un admin appartenente al fruitore PagoPA
    When il fruitore riattiva la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_UNSUSPEND"

  # POST /agreements/:agreementId/unsuspend · descriptorNotInExpectedState
  Scenario: [ERROR_153] Il fruitore riattiva la richiesta di fruizione — una richiesta sospesa riferita a una versione archiviata
    Given una richiesta sospesa riferita a una versione archiviata
    When il fruitore riattiva la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_UNSUSPEND"

  # POST /agreements/:agreementId/consumer-documents · documentsChangeNotAllowed
  Scenario: [ERROR_154] L'utente allega alla richiesta di fruizione il file "allegato-a.pdf" — una richiesta di fruizione fuori dallo stato di bozza con allegato esistente se richiesto
    Given una richiesta di fruizione fuori dallo stato di bozza con allegato esistente se richiesto
    When l'utente allega alla richiesta di fruizione il file "allegato-a.pdf"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENTS_CHANGE_NOT_ALLOWED__POST_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS"

  # GET /agreements/:agreementId/consumer-documents/:documentId · documentNotFound
  Scenario: [ERROR_155] L'utente scarica il documento della richiesta di fruizione — il documento precedentemente allegato e poi rimosso dalla risorsa ancora disponibile
    Given il documento precedentemente allegato e poi rimosso dalla risorsa ancora disponibile
    When l'utente scarica il documento della richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENT_NOT_FOUND__GET_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID"

  # DELETE /agreements/:agreementId/consumer-documents/:documentId · documentNotFound
  Scenario: [ERROR_156] L'utente elimina il documento della richiesta di fruizione — il documento precedentemente allegato e poi rimosso dalla risorsa ancora disponibile
    Given il documento precedentemente allegato e poi rimosso dalla risorsa ancora disponibile
    When l'utente elimina il documento della richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENT_NOT_FOUND__DELETE_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID"

  # DELETE /agreements/:agreementId/consumer-documents/:documentId · documentsChangeNotAllowed
  Scenario: [ERROR_157] L'utente elimina il documento della richiesta di fruizione — una richiesta di fruizione fuori dallo stato di bozza con allegato esistente se richiesto
    Given una richiesta di fruizione fuori dallo stato di bozza con allegato esistente se richiesto
    When l'utente elimina il documento della richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENTS_CHANGE_NOT_ALLOWED__DELETE_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID"

  # POST /agreements/:agreementId/suspend · agreementNotInExpectedState
  Scenario: [ERROR_158] L’erogatore sospende la richiesta di fruizione — una richiesta di fruizione in bozza e il ruolo competente predisposto
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente all'erogatore Comune di Milano
    When l’erogatore sospende la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_SUSPEND"

  # POST /agreements/:agreementId/reject · agreementNotInExpectedState
  Scenario: [ERROR_159] L’erogatore rifiuta la richiesta di fruizione — una richiesta di fruizione attiva e il ruolo competente predisposto
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And un admin appartenente all'erogatore Comune di Milano
    When l’erogatore rifiuta la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_REJECT"

  # POST /agreements/:agreementId/archive · agreementNotInExpectedState
  Scenario: [ERROR_160] Il fruitore archivia la richiesta di fruizione — una richiesta di fruizione in bozza e il ruolo competente predisposto
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    When il fruitore archivia la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_ARCHIVE"

  # POST /agreements · notLatestEServiceDescriptor
  Scenario: [ERROR_161] Il sistema impedisce al Comune di Pozzallo di inoltrare una richiesta di fruizione per la versione deprecata dell'EService — una prima versione deprecata e una seconda versione pubblicata dello stesso e-service
    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    When il sistema impedisce al Comune di Pozzallo di inoltrare una richiesta di fruizione per la versione deprecata dell'EService
    Then il messaggio di errore corrisponde al caso "AGREEMENT_NOT_LATEST_ESERVICE_DESCRIPTOR"

  # POST /agreements · descriptorNotInExpectedState
  Scenario: [ERROR_162] Il fruitore crea una richiesta di fruizione per la versione più recente dell'e-service "ES-1" — la versione dell’e-service sospesa e più recente
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And un admin appartenente al fruitore PagoPA
    When il fruitore crea una richiesta di fruizione per la versione più recente dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS"

  # POST /agreements · missingCertifiedAttributesError
  Scenario: [ERROR_163] Il fruitore crea una richiesta di fruizione per la versione più recente dell'e-service "ES-1" — l’ultima versione pubblicata con un attributo certificato richiesto che il fruitore non possiede
    Given i seguenti e-service pubblicati:
      | alias | erogatore |
      | ES-1 | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" richiede un attributo certificato che PagoPA non possiede
    And un admin appartenente al fruitore PagoPA
    When il fruitore crea una richiesta di fruizione per la versione più recente dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_MISSING_CERTIFIED_ATTRIBUTES_ERROR__POST_AGREEMENTS"

  # POST /agreements · agreementAlreadyExists
  Scenario: [ERROR_164] Il fruitore crea una richiesta di fruizione per la versione più recente dell'e-service "ES-1" — un e-service pubblicato e già sottoscritto dal fruitore con richiesta di fruizione attiva
    Given i seguenti e-service pubblicati:
      | alias | erogatore | nome |
      | ES-1 | Comune di Milano | Fruizione già attiva |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And un admin appartenente al fruitore PagoPA
    When il fruitore crea una richiesta di fruizione per la versione più recente dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_ALREADY_EXISTS__POST_AGREEMENTS"

  # POST /agreements/:agreementId/clone · missingCertifiedAttributesError
  Scenario: [ERROR_165] Il fruitore duplica la richiesta di fruizione — l’ultima versione pubblicata con un attributo certificato richiesto che il fruitore non possiede e una richiesta clonabile dello stesso fruitore
    Given l’ultima versione pubblicata con un attributo certificato richiesto che il fruitore non possiede e una richiesta clonabile dello stesso fruitore
    When il fruitore duplica la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_MISSING_CERTIFIED_ATTRIBUTES_ERROR__POST_AGREEMENTS_AGREEMENT_ID_CLONE"

  # POST /agreements/:agreementId/clone · agreementAlreadyExists
  Scenario: [ERROR_166] Il fruitore duplica la richiesta di fruizione — una richiesta in corso o attiva per lo stesso fruitore e lo stesso e-service e una seconda richiesta clonabile
    Given una richiesta in corso o attiva per lo stesso fruitore e lo stesso e-service e una seconda richiesta clonabile
    When il fruitore duplica la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_ALREADY_EXISTS__POST_AGREEMENTS_AGREEMENT_ID_CLONE"

  # GET /agreements/:agreementId/consumer-documents/:documentId · unauthorizedError
  Scenario: [ERROR_167] L'utente scarica il documento della richiesta di fruizione — un documento disponibile e un operatore di sicurezza dell’ente abilitato alla consultazione della risorsa
    Given un documento disponibile e un operatore di sicurezza dell’ente abilitato alla consultazione della risorsa
    When l'utente scarica il documento della richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_UNAUTHORIZED_ERROR"
