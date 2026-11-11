@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione delle richieste di fruizione

  Come erogatore o fruitore
  Voglio sapere perché un'operazione su una richiesta di fruizione o sui suoi allegati è stata rifiutata
  Per verificare gli attributi richiesti, lo stato della richiesta e della versione o i permessi sui documenti.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_148] - Impossibilità di inviare una richiesta senza un attributo dichiarato necessario
  Dato un e-service pubblicato con un attributo dichiarato richiesto e non posseduto da PagoPA e una richiesta in bozza
  Quando il fruitore tenta di inviare la richiesta senza possedere l'attributo dichiarato
  Allora il sistema rifiuta l'invio e segnala che i requisiti per la fruizione non sono soddisfatti.

    # Un certificato mancante bloccherebbe già la creazione; un verificato mancante consentirebbe l'invio in PENDING.
    Given i seguenti attributi dichiarati creati da un admin del Comune di Milano:
      | alias  |
      | DECL-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo       | gruppo | attributo |
      | dichiarato | 1      | DECL-1    |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare submit senza attendere il successo.
    When il fruitore tenta di inviare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_SUBMISSION_FAILED"

  Scenario: [ERROR_149] - Impossibilità di approvare una richiesta di fruizione già attiva
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA già attiva
  Quando l'erogatore tenta di approvare nuovamente la richiesta
  Allora il sistema rifiuta l'approvazione e segnala che lo stato della richiesta non consente l'operazione.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare activate senza l'accesso al dettaglio previsto dal binding positivo.
    When l'erogatore tenta di approvare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_APPROVE"

  Scenario: [ERROR_150] - Impossibilità di approvare una richiesta senza verificare un attributo necessario
  Dato un e-service con un attributo verificato richiesto e non posseduto da PagoPA e una richiesta in attesa di approvazione
  Quando l'erogatore tenta di approvare la richiesta
  Allora il sistema rifiuta l'attivazione e segnala che il fruitore non soddisfa i requisiti.

    # La mancanza del verificato mantiene PENDING; perdere un dichiarato o certificato può cambiare lo stato della richiesta.
    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | VER-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo       | gruppo | attributo |
      | verificato | 1      | VER-1     |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione in attesa di approvazione
    # Binding di verifica mancante; riusare getAgreement.
    And la richiesta di fruizione è ancora in stato PENDING
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare activate senza l'accesso al dettaglio WEB.
    When l'erogatore tenta di approvare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_ACTIVATION_FAILED"

  Scenario Outline: [ERROR_151] - Impossibilità di approvare una richiesta per una versione <stato versione>
  Dato un e-service con approvazione manuale e una richiesta di PagoPA in attesa per una versione archiviata o in archiviazione
  Quando l'erogatore tenta di approvare la richiesta
  Allora il sistema rifiuta l'approvazione e segnala che lo stato della versione non consente l'operazione.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA in attesa di approvazione
    # Binding di setup mancante; riusare bringEServiceDescriptorToState per ARCHIVING, non per ARCHIVED.
    # L'archiviazione definitiva richiede un setup dedicato: non equivale all'avvio dell'archiviazione.
    And la versione dell'e-service associata alla richiesta di fruizione <stato versione>
    # Binding di verifica mancante; riusare getAgreement.
    And la richiesta di fruizione è ancora in stato PENDING
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare activate senza l'accesso al dettaglio WEB.
    When l'erogatore tenta di approvare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_APPROVE"

    Examples:
      | stato versione   |
      | in archiviazione |
      | archiviata       |

  Scenario: [ERROR_152] - Impossibilità di riattivare una richiesta di fruizione già attiva
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA già attiva
  Quando il fruitore tenta di riattivare la richiesta
  Allora il sistema rifiuta la riattivazione e segnala che la richiesta non è sospesa.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding mancanti per il tentativo di riattivazione.
    When il fruitore tenta di riattivare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_UNSUSPEND"

  Scenario: [ERROR_153] - Impossibilità di riattivare una richiesta per una versione archiviata
  Data una richiesta di fruizione di PagoPA sospesa e riferita a una versione archiviata
  Quando il fruitore tenta di riattivare la richiesta
  Allora il sistema rifiuta la riattivazione e segnala che la versione non è più disponibile per la fruizione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione sospesa
    # Il binding di lifecycle non supporta ARCHIVED; serve il setup dell'archiviazione definitiva.
    And la versione dell'e-service associata alla richiesta di fruizione archiviata
    # Binding di verifica mancante; riusare getAgreement.
    And la richiesta di fruizione è ancora in stato SUSPENDED
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding mancanti per il tentativo di riattivazione.
    When il fruitore tenta di riattivare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_UNSUSPEND"

  Scenario: [ERROR_154] - Impossibilità di allegare un documento a una richiesta di fruizione attiva
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA attiva
  Quando il fruitore tenta di allegare un documento alla richiesta
  Allora il sistema rifiuta il caricamento e segnala che i documenti non possono essere modificati fuori dalla bozza.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding mancanti per il caricamento degli allegati alla richiesta.
    When il fruitore tenta di allegare alla richiesta di fruizione il file "allegato-a.pdf"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENTS_CHANGE_NOT_ALLOWED__POST_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS"

  Scenario: [ERROR_155] - Impossibilità di scaricare un documento già eliminato dalla richiesta
  Data una richiesta di fruizione di PagoPA in bozza con un documento allegato e poi eliminato
  Quando il fruitore tenta di scaricare lo stesso documento
  Allora il sistema rifiuta il download e segnala che il documento non è più disponibile.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    # Operazioni BFF e binding di setup mancanti; conservare il riferimento al documento eliminato.
    And il file "allegato-a.pdf" allegato alla richiesta di fruizione e poi eliminato
    # Operazione BFF e binding mancanti per il download dell'allegato eliminato.
    When l'utente tenta di scaricare il documento dalla richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENT_NOT_FOUND__GET_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID"

  Scenario: [ERROR_156] - Impossibilità di eliminare un documento già rimosso dalla richiesta
  Data una richiesta di fruizione di PagoPA in bozza con un documento allegato e poi eliminato
  Quando il fruitore tenta di eliminare nuovamente lo stesso documento
  Allora il sistema rifiuta l'eliminazione e segnala che il documento non è più disponibile.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    # Operazioni BFF e binding di setup mancanti; conservare il riferimento al documento eliminato.
    And il file "allegato-a.pdf" allegato alla richiesta di fruizione e poi eliminato
    # Operazione BFF e binding mancanti per l'eliminazione dell'allegato.
    When il fruitore tenta di eliminare il documento dalla richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENT_NOT_FOUND__DELETE_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID"

  Scenario: [ERROR_157] - Impossibilità di eliminare un documento da una richiesta di fruizione attiva
  Data una richiesta di fruizione di PagoPA con un documento allegato in bozza e poi attivata
  Quando il fruitore tenta di eliminare il documento dalla richiesta attiva
  Allora il sistema rifiuta l'eliminazione e segnala che i documenti non possono essere modificati fuori dalla bozza.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding di setup mancanti per gli allegati alla richiesta.
    And il file "allegato-a.pdf" allegato alla richiesta di fruizione
    # Binding di setup mancante; riusare submitAgreement e l'attivazione solo se necessaria.
    And la richiesta di fruizione inviata e attivata
    # Operazione BFF e binding mancanti per l'eliminazione dell'allegato.
    When il fruitore tenta di eliminare il documento dalla richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DOCUMENTS_CHANGE_NOT_ALLOWED__DELETE_AGREEMENTS_AGREEMENT_ID_CONSUMER_DOCUMENTS_DOCUMENT_ID"

  Scenario: [ERROR_158] - Impossibilità di sospendere una richiesta di fruizione in bozza
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA in bozza
  Quando l'erogatore tenta di sospendere la richiesta
  Allora il sistema rifiuta la sospensione e segnala che lo stato della richiesta non consente l'operazione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare suspend senza attendere il successo.
    When l'erogatore tenta di sospendere la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_SUSPEND"

  Scenario: [ERROR_159] - Impossibilità di rifiutare una richiesta di fruizione già attiva
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA già attiva
  Quando l'erogatore tenta di rifiutare la richiesta con una motivazione valida
  Allora il sistema rifiuta l'operazione e segnala che lo stato della richiesta non consente il rifiuto.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    # Operazione BFF e binding mancanti per il tentativo di rifiuto.
    When l'erogatore tenta di rifiutare la richiesta di fruizione con una motivazione valida
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_REJECT"

  Scenario: [ERROR_160] - Impossibilità di archiviare una richiesta di fruizione in bozza
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA in bozza
  Quando il fruitore tenta di archiviare la richiesta
  Allora il sistema rifiuta l'archiviazione e segnala che lo stato della richiesta non consente l'operazione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare archive senza attendere il successo.
    When il fruitore tenta di archiviare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS_AGREEMENT_ID_ARCHIVE"

  Scenario: [ERROR_161] - Impossibilità di richiedere la fruizione di una versione deprecata
  Dato un e-service con la prima versione deprecata dopo la fruizione di PagoPA e una seconda versione pubblicata
  Quando il Comune di Pozzallo tenta di richiedere la fruizione della versione deprecata
  Allora il sistema rifiuta la creazione e segnala che occorre scegliere la versione più recente.

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    When il sistema impedisce al Comune di Pozzallo di inoltrare una richiesta di fruizione per la versione deprecata dell'EService
    Then il messaggio di errore corrisponde al caso "AGREEMENT_NOT_LATEST_ESERVICE_DESCRIPTOR"

  Scenario: [ERROR_162] - Impossibilità di richiedere la fruizione della versione più recente sospesa
  Dato un e-service la cui versione più recente è sospesa
  Quando PagoPA tenta di richiedere la fruizione di quella versione
  Allora il sistema rifiuta la creazione e segnala che lo stato della versione non consente la fruizione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante per la versione sospesa; riusare shouldFailToCreateAgreement.
    When il fruitore tenta di creare una richiesta di fruizione per la versione più recente dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_DESCRIPTOR_NOT_IN_EXPECTED_STATE__POST_AGREEMENTS"

  Scenario: [ERROR_163] - Impossibilità di richiedere la fruizione senza un attributo certificato necessario
  Dato un e-service pubblicato con un attributo certificato richiesto e non assegnato a PagoPA
  Quando il fruitore tenta di creare una richiesta di fruizione
  Allora il sistema rifiuta la creazione e segnala che manca l'attributo certificato necessario.

    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo |
      | certificato | 1      | CERT-1    |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare create ed estendere i motivi di shouldFailToCreateAgreement.
    When il fruitore tenta di creare una richiesta di fruizione per la versione più recente dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_MISSING_CERTIFIED_ATTRIBUTES_ERROR__POST_AGREEMENTS"

  Scenario: [ERROR_164] - Impossibilità di creare un'altra richiesta quando la fruizione è già attiva
  Dato un e-service pubblicato e una richiesta di fruizione di PagoPA già attiva
  Quando il fruitore tenta di creare un'altra richiesta per lo stesso e-service
  Allora il sistema rifiuta la creazione e segnala che esiste già una richiesta di fruizione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 |
      | ES-1  | Comune di Milano | Fruizione già attiva |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare create ed estendere i motivi di shouldFailToCreateAgreement.
    When il fruitore tenta di creare una richiesta di fruizione per la versione più recente dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_ALREADY_EXISTS__POST_AGREEMENTS"

  Scenario: [ERROR_165] - Impossibilità di duplicare una richiesta dopo la perdita di un attributo certificato
  Data una richiesta di fruizione rifiutata di PagoPA e un attributo certificato richiesto non più posseduto dal fruitore
  Quando il fruitore tenta di duplicare la richiesta
  Allora il sistema rifiuta la duplicazione e segnala che manca l'attributo certificato necessario.

    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo |
      | certificato | 1      | CERT-1    |
    # Binding di configurazione mancante; riusare updateDescriptor prima della pubblicazione.
    And la versione 1 dell'e-service "ES-1" configurata per l'approvazione manuale delle richieste di fruizione
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias  |
      | CERT-1 |
    # cloneAgreement ammette solo REJECTED; il setup deve inviare la bozza e farla rifiutare dall'erogatore.
    # Non devono esistere altre richieste in corso per PagoPA e lo stesso e-service.
    And una richiesta di fruizione rifiutata per l'e-service, creata da PagoPA
    # Binding di revoca mancante; riusare BffTenantRestClient.revokeCertifiedAttribute con l'attributo CERT-1.
    And l'attributo certificato "CERT-1" revocato a PagoPA
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding mancanti per la duplicazione della richiesta.
    When il fruitore tenta di duplicare la richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_MISSING_CERTIFIED_ATTRIBUTES_ERROR__POST_AGREEMENTS_AGREEMENT_ID_CLONE"

  Scenario: [ERROR_166] - Impossibilità di duplicare una richiesta quando ne esiste già un'altra in corso
  Data una richiesta rifiutata di PagoPA e un'altra richiesta attiva per lo stesso e-service e lo stesso fruitore
  Quando il fruitore tenta di duplicare la prima richiesta
  Allora il sistema rifiuta la duplicazione e segnala che esiste già un'altra richiesta di fruizione.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding di configurazione mancante; riusare updateDescriptor prima della pubblicazione.
    And la versione 1 dell'e-service "ES-1" configurata per l'approvazione manuale delle richieste di fruizione
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    # cloneAgreement ammette solo REJECTED; inviare la prima bozza, rifiutarla e registrare l'alias.
    And una richiesta di fruizione rifiutata per l'e-service "ES-1", creata da PagoPA e identificata come "RF-1"
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding mancanti; usare la richiesta duplicabile, non l'ultima richiesta creata.
    When il fruitore tenta di duplicare la richiesta di fruizione "RF-1"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_AGREEMENT_ALREADY_EXISTS__POST_AGREEMENTS_AGREEMENT_ID_CLONE"

  Scenario: [ERROR_167] - Impossibilità per un operatore di sicurezza di scaricare un allegato della richiesta
  Data una richiesta di fruizione di PagoPA con un documento disponibile e un operatore di sicurezza dello stesso ente
  Quando l'operatore tenta di scaricare il documento
  Allora il sistema rifiuta il download e segnala che il ruolo non è autorizzato.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding di setup mancanti per gli allegati alla richiesta.
    And il file "allegato-a.pdf" allegato alla richiesta di fruizione
    And un security appartenente al fruitore PagoPA
    # Operazione BFF e binding mancanti per il download dell'allegato come utente corrente.
    When l'utente tenta di scaricare il documento dalla richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "AGREEMENT_UNAUTHORIZED_ERROR"
