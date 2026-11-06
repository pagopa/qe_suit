@eservice-archiving-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nell'archiviazione degli e-service e delle loro versioni

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'archiviazione non può essere richiesta o annullata
  Per comprendere il problema e scegliere un preavviso corretto o aggiornare la pagina.

  Scenario Outline: [ESERVICE_ARCHIVING_ERROR_MESSAGE_1] - Annullamento della richiesta di archiviazione di una versione dopo la revoca
    Given un e-service del Comune di Milano con una versione pubblicata e archiviabile
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And il Comune di Pozzallo ha richiesto al Comune di Milano l'archiviazione della versione con un preavviso valido e la richiesta è in attesa di approvazione
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di archiviazione della versione
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di annullare la richiesta di archiviazione della versione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ARCHIVING_ERROR_MESSAGE_2] - Annullamento della richiesta di archiviazione dell'e-service dopo la revoca
    Given un e-service del Comune di Milano con una versione pubblicata e archiviabile
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And il Comune di Pozzallo ha richiesto al Comune di Milano l'archiviazione dell'intero e-service con un preavviso valido e la richiesta è in attesa di approvazione
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di archiviazione dell'intero e-service
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di annullare la richiesta di archiviazione dell'intero e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ARCHIVING_ERROR_MESSAGE_3] - Richiesta di archiviazione di una versione dopo la revoca della delega
    Given un e-service del Comune di Milano con una versione pubblicata e archiviabile
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la richiesta di archiviazione della versione e prepara un preavviso e una motivazione validi senza confermare
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di confermare la richiesta di archiviazione della versione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ARCHIVING_ERROR_MESSAGE_4] - Archiviazione dell'e-service prima di una versione già in archiviazione
    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di 120 giorni
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'erogatore tenta di archiviare l'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_GRACE_PERIOD_DAYS_LOWER_THAN_DESCRIPTOR__POST_ESERVICES_E_SERVICE_ID_SCHEDULE_ARCHIVE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ARCHIVING_ERROR_MESSAGE_5] - Richiesta in delega di archiviare l'e-service prima di una versione già in archiviazione
    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di 120 giorni
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il delegato tenta di richiedere al delegante l'archiviazione dell'intero e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_GRACE_PERIOD_DAYS_LOWER_THAN_DESCRIPTOR__POST_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_ARCHIVING_ERROR_MESSAGE_6] - Richiesta di archiviazione dell'e-service dopo la revoca della delega
    Given un e-service del Comune di Milano con una versione pubblicata e archiviabile
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la richiesta di archiviazione dell'intero e-service e prepara un preavviso e una motivazione validi senza confermare
    Given la delega in erogazione viene revocata dal Comune di Milano
    When l'utente tenta di confermare la richiesta di archiviazione dell'intero e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
