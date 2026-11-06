@delegation-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione delle deleghe in fruizione e in erogazione

  Come amministratore di un ente delegante o delegato
  Voglio ricevere un messaggio di errore nella lingua selezionata quando una delega non può essere gestita
  Per comprendere il problema e sapere come proseguire.

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_1] - Revoca di una delega in fruizione non più attiva
    Given un e-service pubblicato dal Comune di Milano che consente la delega in fruizione
    And una delega in fruizione per l'e-service da PagoPA al Comune di Pozzallo in stato ACTIVE
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della delega in fruizione conferita
    Given la delega in fruizione viene revocata da un'altra sessione di PagoPA e non è più attiva
    When l'utente tenta di revocare la delega in fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_INCORRECT_STATE__DELETE_CONSUMERS_DELEGATIONS_DELEGATION_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_2] - Revoca di una delega in erogazione non più attiva
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della delega in erogazione conferita
    Given la delega in erogazione viene revocata da un'altra sessione del Comune di Milano e non è più attiva
    When l'utente tenta di revocare la delega in erogazione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_INCORRECT_STATE__DELETE_PRODUCERS_DELEGATIONS_DELEGATION_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_3] - Proposta di delega in fruizione a un ente non pubblico
    Given un e-service pubblicato dal Comune di Milano che consente la delega in fruizione
    And Sogecap non è una Pubblica Amministrazione
    And PagoPA non ha deleghe in fruizione in corso o attive per l'e-service
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di una delega in fruizione per l'e-service
    And l'utente tenta di proporre la delega in fruizione a Sogecap con gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_DELEGATION_NOT_ALLOWED_FOR_TENANT__POST_CONSUMERS_DELEGATIONS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_4] - Approvazione di una delega in fruizione non più in attesa
    Given un e-service pubblicato dal Comune di Milano che consente la delega in fruizione
    And una delega in fruizione per l'e-service da PagoPA al Comune di Pozzallo in stato WAITING_FOR_APPROVAL
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della delega in fruizione ricevuta
    Given la delega in fruizione viene approvata da un'altra sessione del Comune di Pozzallo e raggiunge lo stato ACTIVE
    When l'utente tenta di approvare la delega in fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_INCORRECT_STATE__POST_CONSUMERS_DELEGATIONS_DELEGATION_ID_APPROVE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_5] - Rifiuto di una delega in fruizione con un link nella motivazione
    Given un e-service pubblicato dal Comune di Milano che consente la delega in fruizione
    And una delega in fruizione per l'e-service da PagoPA al Comune di Pozzallo in stato WAITING_FOR_APPROVAL
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della delega in fruizione ricevuta
    And l'utente tenta di rifiutare la delega in fruizione inserendo un link nella motivazione e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_HYPERLINK_DETECTION_ERROR__POST_CONSUMERS_DELEGATIONS_DELEGATION_ID_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_6] - Proposta di delega in erogazione a un ente non pubblico
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And Sogecap non è una Pubblica Amministrazione
    And il Comune di Milano non ha deleghe in erogazione in corso o attive per l'e-service
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la creazione di una delega in erogazione per l'e-service
    And l'utente tenta di proporre la delega in erogazione a Sogecap con gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_DELEGATION_NOT_ALLOWED_FOR_TENANT__POST_PRODUCERS_DELEGATIONS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_7] - Approvazione di una delega in erogazione non più in attesa
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato WAITING_FOR_APPROVAL
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della delega in erogazione ricevuta
    Given la delega in erogazione viene approvata da un'altra sessione del Comune di Pozzallo e raggiunge lo stato ACTIVE
    When l'utente tenta di approvare la delega in erogazione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_INCORRECT_STATE__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_APPROVE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [DELEGATION_ERROR_MESSAGE_8] - Rifiuto di una delega in erogazione con un link nella motivazione
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato WAITING_FOR_APPROVAL
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della delega in erogazione ricevuta
    And l'utente tenta di rifiutare la delega in erogazione inserendo un link nella motivazione e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "DELEGATION_HYPERLINK_DETECTION_ERROR__POST_PRODUCERS_DELEGATIONS_DELEGATION_ID_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
