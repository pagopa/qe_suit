@agreement-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione delle richieste di fruizione

  Come amministratore o operatore di un ente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'operazione non può essere completata
  Per comprendere il problema e sapere come proseguire.

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_1] - Eliminazione di un documento non più allegato
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un documento valido allegato alla richiesta di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given il documento allegato alla richiesta di fruizione viene eliminato da un'altra sessione di PagoPA
    When l'utente tenta di eliminare il documento dalla richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DOCUMENT_NOT_FOUND__DELETE_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_2] - Eliminazione di un documento quando la richiesta non è più in bozza
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un documento valido allegato alla richiesta di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given la richiesta di fruizione viene inviata da un'altra sessione di PagoPA e raggiunge lo stato PENDING
    When l'utente tenta di eliminare il documento dalla richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DOCUMENTS_CHANGE_NOT_ALLOWED__DELETE_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_3] - Download di un documento non più allegato
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un documento valido allegato alla richiesta di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given il documento allegato alla richiesta di fruizione viene eliminato da un'altra sessione di PagoPA
    When l'utente tenta di scaricare il documento dalla richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DOCUMENT_NOT_FOUND__GET_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_4] - Download di un documento da parte di un operatore di sicurezza
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un documento valido allegato alla richiesta di fruizione
    And un security appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    And l'utente tenta di scaricare il documento dalla richiesta di fruizione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_UNAUTHORIZED_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_5] - Creazione di una richiesta già esistente per lo stesso e-service
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio dell'e-service nel catalogo per richiederne la fruizione
    Given una richiesta di fruizione in stato DRAFT per l'e-service viene creata da un'altra sessione di PagoPA
    When l'utente tenta di creare una richiesta di fruizione per l'e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_ALREADY_EXISTS__POST_AGREEMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_6] - Creazione di una richiesta per una versione non più disponibile
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio dell'e-service nel catalogo per richiederne la fruizione
    Given il Comune di Milano sospende la versione pubblicata dell'e-service
    When l'utente tenta di creare una richiesta di fruizione per la versione visualizzata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_7] - Creazione di una richiesta dopo la perdita di un attributo certificato
    Given un e-service in pubblicato, creato dal Comune di Milano, con un attributo certificato e una soglia personalizzata per fruitore pari a 10
      | consumerThreshold | 10 |
      | totalThreshold    | 20 |
    And un admin del Comune di Milano assegna l'attributo certificato a PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio dell'e-service nel catalogo per richiederne la fruizione
    Given l'attributo certificato richiesto dall'e-service viene revocato a PagoPA
    When l'utente tenta di creare una richiesta di fruizione per l'e-service senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_MISSING_CERTIFIED_ATTRIBUTES_ERROR__POST_AGREEMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_8] - Creazione di una richiesta per una versione non più recente
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio dell'e-service nel catalogo per richiederne la fruizione
    Given il Comune di Milano pubblica una nuova versione dell'e-service e la versione visualizzata diventa DEPRECATED
    When l'utente tenta di creare una richiesta di fruizione per la versione visualizzata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_NOT_LATEST_ESERVICE_DESCRIPTOR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_9] - Approvazione di una richiesta il cui fruitore non soddisfa più tutti gli attributi richiesti
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale e attributi di accesso richiesti
    And PagoPA soddisfa tutti gli attributi richiesti dall'e-service
    And una richiesta di fruizione in stato PENDING per l'e-service, creata da PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione ricevuta
    Given PagoPA non soddisfa più uno degli attributi richiesti dall'e-service
    And la richiesta di fruizione è ancora in stato PENDING
    When l'utente tenta di approvare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_ACTIVATION_FAILED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_10] - Approvazione di una richiesta non più in attesa di approvazione
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato PENDING per l'e-service, creata da PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione ricevuta
    Given la richiesta di fruizione viene approvata da un'altra sessione del Comune di Milano e raggiunge lo stato ACTIVE
    When l'utente tenta di approvare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_APPROVE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_11] - Approvazione di una richiesta per una versione in fase di archiviazione
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato PENDING per l'e-service, creata da PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione ricevuta
    Given il Comune di Milano avvia l'archiviazione della versione dell'e-service associata alla richiesta di fruizione con un preavviso di 60 giorni
    And la richiesta di fruizione è ancora in stato PENDING
    When l'utente tenta di approvare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_APPROVE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_12] - Archiviazione di una richiesta già archiviata
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given la richiesta di fruizione viene archiviata da un'altra sessione di PagoPA e raggiunge lo stato ARCHIVED
    When l'utente tenta di archiviare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_ARCHIVE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_13] - Duplicazione di una richiesta quando ne esiste già un'altra in corso
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato REJECTED per l'e-service, creata da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given un'altra richiesta di fruizione in stato DRAFT per lo stesso e-service viene creata da un'altra sessione di PagoPA
    When l'utente tenta di duplicare la richiesta di fruizione rifiutata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_ALREADY_EXISTS__POST_AGREEMENTS_AGREEMENT_ID_CLONE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_14] - Duplicazione di una richiesta dopo la perdita di un attributo certificato
    Given un admin del Comune di Milano crea un attributo certificato
    And un e-service pubblicato dal Comune di Milano con approvazione manuale e l'attributo certificato creato come requisito
    And un admin del Comune di Milano assegna l'attributo certificato a PagoPA
    And una richiesta di fruizione in stato REJECTED per l'e-service, creata da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given l'attributo certificato richiesto dall'e-service viene revocato a PagoPA
    When l'utente tenta di duplicare la richiesta di fruizione rifiutata senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_MISSING_CERTIFIED_ATTRIBUTES_ERROR__POST_AGREEMENTS_AGREEMENT_ID_CLONE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_15] - Caricamento di un documento quando la richiesta non è più in bozza
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un documento valido da allegare alla richiesta di fruizione
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given la richiesta di fruizione viene inviata da un'altra sessione di PagoPA e raggiunge lo stato PENDING
    When l'utente tenta di allegare il documento valido alla richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DOCUMENTS_CHANGE_NOT_ALLOWED__POST_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_16] - Rifiuto di una richiesta non più in attesa di approvazione
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato PENDING per l'e-service, creata da PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione ricevuta
    Given la richiesta di fruizione viene approvata da un'altra sessione del Comune di Milano e raggiunge lo stato ACTIVE
    When l'utente tenta di rifiutare la richiesta di fruizione senza aggiornare la pagina indicando una motivazione valida
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_17] - Invio di una richiesta dopo la perdita di un attributo richiesto
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale e attributi di accesso richiesti
    And PagoPA soddisfa tutti gli attributi richiesti dall'e-service
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given PagoPA non soddisfa più uno degli attributi richiesti dall'e-service
    And la richiesta di fruizione è ancora in stato DRAFT
    When l'utente tenta di inviare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_SUBMISSION_FAILED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_18] - Sospensione di una richiesta già archiviata
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given la richiesta di fruizione viene archiviata da un'altra sessione di PagoPA e raggiunge lo stato ARCHIVED
    When l'utente tenta di sospendere la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_SUSPEND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_19] - Riattivazione di una richiesta non più sospesa
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And la richiesta di fruizione è sospesa soltanto da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given la richiesta di fruizione viene riattivata da un'altra sessione di PagoPA e raggiunge lo stato ACTIVE
    When l'utente tenta di riattivare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_UNSUSPEND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_20] - Riattivazione di una richiesta per una versione in fase di archiviazione
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And la richiesta di fruizione è sospesa soltanto da PagoPA
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    Given il Comune di Milano avvia l'archiviazione della versione dell'e-service associata alla richiesta di fruizione con un preavviso di 60 giorni
    And la richiesta di fruizione è ancora in stato SUSPENDED
    When l'utente tenta di riattivare la richiesta di fruizione senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_UNSUSPEND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_21] - Download del documento della richiesta non ancora disponibile
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And il documento della richiesta di fruizione non è ancora disponibile per il download
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    And l'utente tenta di scaricare il documento della richiesta di fruizione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_CONTRACT_EXCEPTION" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [AGREEMENT_ERROR_MESSAGE_22] - Caricamento di un allegato in formato non supportato
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And una richiesta di fruizione in stato DRAFT per l'e-service, creata da PagoPA
    And un file di test in formato non supportato per gli allegati della richiesta, di dimensione consentita e con nome valido, identificato come "documento da caricare"
    And un admin appartenente al fruitore PagoPA
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della richiesta di fruizione inviata
    And l'utente tenta di allegare il "documento da caricare" alla richiesta di fruizione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "AGREEMENT_INVALID_FILE_UPLOAD_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
