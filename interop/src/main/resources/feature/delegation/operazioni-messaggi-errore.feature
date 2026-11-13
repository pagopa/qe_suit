@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione delle deleghe in erogazione e in fruizione

  Come amministratore di un ente delegante o delegato
  Voglio sapere perché la proposta, l'approvazione, la revoca o il rifiuto di una delega non sono consentiti
  Per scegliere un ente ammesso, verificare lo stato della delega o correggere la motivazione del rifiuto.

  Background:
    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_200] - Impossibilità di rifiutare una delega in fruizione già attiva
  Data una delega in fruizione già approvata dal delegato
  Quando il delegato tenta di rifiutarla con una motivazione valida
  Allora il sistema rifiuta l'operazione e segnala che la delega non è più in attesa di approvazione.

    Given l'e-service è delegato in fruizione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    # Operazione BFF e binding negativi mancanti, condivisi con ERROR_011.
    # Il catalogo indica /consumer al singolare: verificare il contratto prima dell'implementazione.
    When l'utente tenta di rifiutare la delega in fruizione con la motivazione "Delega non necessaria"
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__POST_CONSUMER_DELEGATIONS_DELEGATION_ID_REJECT" in lingua "it"

  @producer-delegation-settings
  Scenario: [ERROR_201] - Impossibilità di rifiutare una delega in erogazione già attiva
  Data una delega in erogazione già approvata dal delegato
  Quando il delegato tenta di rifiutarla con una motivazione valida
  Allora il sistema rifiuta l'operazione e segnala che la delega non è più in attesa di approvazione.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding negativo mancante, condiviso con ERROR_010; riusare rejectProducerDelegation.
    When l'utente tenta di rifiutare la delega in erogazione con la motivazione "Delega non necessaria"
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_REJECT" in lingua "it"

  Scenario: [ERROR_004] - Impossibilità di proporre una delega in erogazione a un ente non pubblico
  Dato un e-service pubblicato dall'erogatore
  Quando l'erogatore tenta di conferire a un ente non pubblico una delega in erogazione per l'e-service
  Allora il sistema rifiuta la proposta e segnala che l'ente scelto non può ricevere la delega.

    # Binding BFF negativo mancante; riusare createProducerDelegation e verificare Tenant.getTenantType.
    When il Comune di Milano tenta di proporre la delega in erogazione a Sogecap per l'e-service
    Then il messaggio di errore corrisponde al caso "DELEGATION_DELEGATION_NOT_ALLOWED_FOR_TENANT__POST_PRODUCERS_DELEGATIONS" in lingua "it"

  @producer-delegation-settings
  Scenario: [ERROR_005] - Impossibilità di approvare una delega in erogazione già attiva
  Dato un e-service dell'erogatore con una delega in erogazione a un ente delegato già approvata
  Quando il delegato tenta di approvare nuovamente la delega
  Allora il sistema rifiuta l'approvazione e segnala che lo stato della delega non consente l'operazione.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare approveProducerDelegation senza attendere il successo.
    When l'utente tenta di approvare la delega in erogazione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_APPROVE" in lingua "it"

  @producer-delegation-settings
  Scenario: [ERROR_006] - Impossibilità di revocare una delega in erogazione già revocata
  Dato un e-service dell'erogatore con una delega in erogazione a un ente delegato già revocata
  Quando il delegante tenta di revocare nuovamente la delega
  Allora il sistema rifiuta la revoca e segnala che lo stato della delega non consente l'operazione.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare revokeProducerDelegation senza attendere il successo.
    When l'utente tenta di revocare la delega in erogazione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__DELETE_PRODUCERS_DELEGATIONS_DELEGATION_ID" in lingua "it"

  Scenario: [ERROR_007] - Impossibilità di proporre una delega in fruizione a un ente non pubblico
  Dato un e-service pubblicato dall'erogatore
  Quando il fruitore tenta di conferire a un ente non pubblico una delega in fruizione per l'e-service
  Allora il sistema rifiuta la proposta e segnala che l'ente scelto non può ricevere la delega.

    Given un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare createConsumerDelegation e verificare Tenant.getTenantType.
    When PagoPA tenta di proporre la delega in fruizione a Sogecap per l'e-service
    Then il messaggio di errore corrisponde al caso "DELEGATION_DELEGATION_NOT_ALLOWED_FOR_TENANT__POST_CONSUMERS_DELEGATIONS" in lingua "it"

  Scenario: [ERROR_008] - Impossibilità di approvare una delega in fruizione già attiva
  Dato un e-service pubblicato dall'erogatore e una delega in fruizione dal fruitore a un ente delegato già approvata
  Quando il delegato tenta di approvare nuovamente la delega
  Allora il sistema rifiuta l'approvazione e segnala che lo stato della delega non consente l'operazione.

    Given l'e-service è delegato in fruizione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare approveConsumerDelegation senza attendere il successo.
    When l'utente tenta di approvare la delega in fruizione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__POST_CONSUMERS_DELEGATIONS_DELEGATION_ID_APPROVE" in lingua "it"

  Scenario: [ERROR_009] - Impossibilità di revocare una delega in fruizione già revocata
  Dato un e-service pubblicato dall'erogatore e una delega in fruizione dal fruitore a un ente delegato già revocata
  Quando il delegante tenta di revocare nuovamente la delega
  Allora il sistema rifiuta la revoca e segnala che lo stato della delega non consente l'operazione.

    Given l'e-service è delegato in fruizione da PagoPA al Comune di Pozzallo con una delega in stato ACTIVE
    And la delega in fruizione viene revocata da PagoPA
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare revokeConsumerDelegation senza attendere il successo.
    When l'utente tenta di revocare la delega in fruizione
    Then il messaggio di errore corrisponde al caso "DELEGATION_INCORRECT_STATE__DELETE_CONSUMERS_DELEGATIONS_DELEGATION_ID" in lingua "it"

  @producer-delegation-settings
  Scenario: [ERROR_010] - Rifiuto di una delega in erogazione con un link nella motivazione
  Dato un e-service dell'erogatore con una delega in erogazione a un ente delegato in attesa di accettazione
  Quando il delegato tenta di rifiutare la delega inserendo un collegamento nella motivazione
  Allora il sistema rifiuta l'operazione e invita il delegato a rimuovere il collegamento dalla motivazione.

    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega in attesa di accettazione
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare rejectProducerDelegation con una motivazione parametrica.
    When l'utente tenta di rifiutare la delega in erogazione con la motivazione "Consulta https://example.com per i dettagli"
    Then il messaggio di errore corrisponde al caso "DELEGATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_REJECT" in lingua "it"

  Scenario: [ERROR_011] - Rifiuto di una delega in fruizione con un link nella motivazione
  Dato un e-service pubblicato dall'erogatore e una delega in fruizione dal fruitore a un ente delegato in attesa di accettazione
  Quando il delegato tenta di rifiutare la delega inserendo un collegamento nella motivazione
  Allora il sistema rifiuta l'operazione e invita il delegato a rimuovere il collegamento dalla motivazione.

    Given l'e-service è delegato in fruizione da PagoPA al Comune di Pozzallo con una delega in stato WAITING_FOR_APPROVAL
    And un admin appartenente al delegato Comune di Pozzallo
    # Operazione BFF e binding mancanti per il rifiuto in fruizione con motivazione.
    When l'utente tenta di rifiutare la delega in fruizione con la motivazione "Consulta https://example.com per i dettagli"
    Then il messaggio di errore corrisponde al caso "DELEGATION_HYPERLINK_DETECTION_ERROR__POST_CONSUMERS_DELEGATIONS_DELEGATION_ID_REJECT" in lingua "it"
