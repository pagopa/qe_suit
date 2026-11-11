@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per le deleghe di erogazione e fruizione

  # POST /producers/delegations · delegationNotAllowedForTenant
  Scenario: [ERROR_004] L’amministratore del delegante propone la delega in erogazione — un ente delegante abilitato e un ente candidato che non è una Pubblica Amministrazione
    Given un ente delegante abilitato e un ente candidato che non è una Pubblica Amministrazione
    When l’amministratore del delegante propone la delega in erogazione
    Then il messaggio di errore corrisponde al caso "DELEGATION_DELEGATION_NOT_ALLOWED_FOR_TENANT__POST_PRODUCERS_DELEGATIONS"

  # POST /producers/delegations/:delegationId/approve · incorrectState
  Scenario: [ERROR_005] L’ente delegato approva la delega in erogazione — una delega in erogazione già approvata
    Given una delega in erogazione già approvata
    When l’ente delegato approva la delega in erogazione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_APPROVE"

  # DELETE /producers/delegations/:delegationId · incorrectState
  Scenario: [ERROR_006] L’ente delegante revoca la delega in erogazione — una delega in erogazione già revocata
    Given una delega in erogazione già revocata
    When l’ente delegante revoca la delega in erogazione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__DELETE_PRODUCERS_DELEGATIONS_DELEGATION_ID"

  # POST /consumers/delegations · delegationNotAllowedForTenant
  Scenario: [ERROR_007] L’amministratore del delegante propone la delega in fruizione — un ente delegante abilitato e un ente candidato che non è una Pubblica Amministrazione
    Given un ente delegante abilitato e un ente candidato che non è una Pubblica Amministrazione
    When l’amministratore del delegante propone la delega in fruizione
    Then il messaggio di errore corrisponde al caso "DELEGATION_DELEGATION_NOT_ALLOWED_FOR_TENANT__POST_CONSUMERS_DELEGATIONS"

  # POST /consumers/delegations/:delegationId/approve · incorrectState
  Scenario: [ERROR_008] L’ente delegato approva la delega in fruizione — una delega in fruizione già approvata
    Given una delega in fruizione già approvata
    When l’ente delegato approva la delega in fruizione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__POST_CONSUMERS_DELEGATIONS_DELEGATION_ID_APPROVE"

  # DELETE /consumers/delegations/:delegationId · incorrectState
  Scenario: [ERROR_009] L’ente delegante revoca la delega in fruizione — una delega in fruizione già revocata
    Given una delega in fruizione già revocata
    When l’ente delegante revoca la delega in fruizione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__DELETE_CONSUMERS_DELEGATIONS_DELEGATION_ID"

  # POST /producers/delegations/:delegationId/reject · hyperlinkDetectionError
  Scenario: [ERROR_010] L’operatore autorizzato rifiuta la delega in erogazione con un collegamento nella motivazione del rifiuto — la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    Given la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    When l’operatore autorizzato rifiuta la delega in erogazione con un collegamento nella motivazione del rifiuto
    Then il messaggio di errore corrisponde al caso "DELEGATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_REJECT"

  # POST /consumers/delegations/:delegationId/reject · hyperlinkDetectionError
  Scenario: [ERROR_011] L’operatore autorizzato rifiuta la delega in fruizione con un collegamento nella motivazione del rifiuto — la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    Given la richiesta da valutare in attesa di approvazione e il valutatore autorizzato al rifiuto
    When l’operatore autorizzato rifiuta la delega in fruizione con un collegamento nella motivazione del rifiuto
    Then il messaggio di errore corrisponde al caso "DELEGATION_HYPERLINK_DETECTION_ERROR__POST_CONSUMERS_DELEGATIONS_DELEGATION_ID_REJECT"
