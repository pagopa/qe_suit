@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Messaggi di errore durante la gestione degli e-service

  Come erogatore o delegato
  Voglio sapere perché un'operazione su un e-service è stata rifiutata
  Per correggere i dati, verificare la delega o scegliere un'operazione consentita.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_202] - Rifiuto dell'aggiunta di un'analisi del rischio con un link in una risposta
  Dato un e-service in ricezione con una versione in bozza
  Quando l'erogatore tenta di aggiungere un'analisi del rischio con un collegamento in una risposta a testo libero e gli altri dati validi
  Allora il sistema rifiuta l'aggiunta e invita l'erogatore a rimuovere il collegamento dalla risposta.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | modalità |
      | ES-1  | Comune di Milano | RECEIVE  |
    # Binding negativo mancante; riusare la costruzione del questionario valido di addLatestRiskAnalysis.
    # Alterare soltanto una risposta a testo libero, mantenendo valido anche il nome dell'analisi.
    When l'erogatore tenta di aggiungere all'e-service un'analisi del rischio con la risposta a testo libero "Motivazione https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_RISK_ANALYSIS_VALIDATION_FAILED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS" in lingua "it"

  Scenario: [ERROR_037] - Rifiuto della creazione di un e-service con un nome già usato dall'erogatore
  Dato un e-service in bozza appartenente all'erogatore
  Quando l'erogatore tenta di crearne un altro con lo stesso nome
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome               |
      | ES-1  | Comune di Milano | Catalogo duplicato |
    When l'utente tenta di creare un e-service con il nome dell'e-service e gli altri dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_ESERVICES" in lingua "it"

  Scenario: [ERROR_038] - Rifiuto della creazione di un e-service con il nome di un template
  Dato un template e-service pubblicato
  Quando l'erogatore tenta di creare un e-service con lo stesso nome
  Allora il sistema rifiuta la creazione e segnala il conflitto con il nome del template.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome               | tecnologia |
      | T-1   | Comune di Milano | Catalogo duplicato | REST       |
    # Binding esistente; shouldFailToCreateEService non è implementato sul BFF.
    When l'utente tenta di creare un e-service con il nome del template e gli altri dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__POST_ESERVICES" in lingua "it"

  Scenario: [ERROR_039] - Rifiuto della creazione di un'istanza con una parola identificativa già usata
  Dato un template pubblicato e un'istanza dell'erogatore con una parola identificativa
  Quando l'erogatore tenta di creare un'altra istanza con la stessa parola identificativa
  Allora il sistema rifiuta la creazione e segnala che il nome dell'e-service è già in uso.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso  |
      | ES-1  | T-1      | Duplicato |
    # Binding BFF mancante con parola identificativa; riusare createEServiceInstance e gestire il rifiuto.
    When l'erogatore tenta di istanziare il template con la parola identificativa "Duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_TEMPLATE_ID_ESERVICES" in lingua "it" con i valori:
      | identifyingWord | Duplicato |

  Scenario: [ERROR_040] - Rifiuto della modifica di un'istanza con una parola identificativa già usata
  Dato un template pubblicato, un'istanza esistente e un'altra istanza in bozza
  Quando l'erogatore tenta di assegnare alla bozza la parola identificativa dell'istanza esistente
  Allora il sistema rifiuta la modifica e segnala che il nome dell'e-service è già in uso.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso  |
      | ES-1  | T-1      | Duplicato |
    And l'erogatore istanzia un nuovo e-service dal template
    When l'erogatore tenta di aggiornare i dati dell'e-service appena istanziato con la parola identificativa "Duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_ESERVICES_E_SERVICE_ID" in lingua "it" con i valori:
      | identifyingWord | Duplicato |

  @channel:Given=BFF,When=WEB,Then=WEB
  Scenario: [ERROR_041] - Rifiuto dell'archiviazione dell'e-service con un preavviso inferiore a quello di una versione
  Dato un e-service con la prima versione in archiviazione con 120 giorni di preavviso e la seconda pubblicata
  Quando l'erogatore richiede l'archiviazione dell'intero e-service con 60 giorni di preavviso
  Allora il sistema rifiuta l'operazione e segnala che il preavviso non può essere inferiore a quello già previsto.

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service in fase di archiviazione con un periodo di preavviso di 120 giorni
    When l'erogatore tenta di archiviare l'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then il messaggio di errore corrisponde al caso "CATALOG_GRACE_PERIOD_DAYS_LOWER_THAN_DESCRIPTOR__POST_ESERVICES_E_SERVICE_ID_SCHEDULE_ARCHIVE" in lingua "it"

  Scenario: [ERROR_042] - Rifiuto della richiesta del delegato di archiviare l'e-service con un preavviso insufficiente
  Dato un e-service delegato con la prima versione in archiviazione con 120 giorni di preavviso e la seconda pubblicata
  Quando il delegato richiede al delegante l'archiviazione dell'intero e-service con 60 giorni di preavviso
  Allora il sistema rifiuta la richiesta e segnala che il preavviso non può essere inferiore a quello già previsto.

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service in fase di archiviazione con un periodo di preavviso di 120 giorni
    And l'e-service delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When il delegato richiede al delegante l'archiviazione dell'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then il messaggio di errore corrisponde al caso "CATALOG_GRACE_PERIOD_DAYS_LOWER_THAN_DESCRIPTOR__POST_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "it"

  Scenario: [ERROR_043] - Rifiuto del caricamento di un documento con un nome già usato nella versione
  Dato un e-service con una versione in bozza e un documento allegato
  Quando l'erogatore tenta di allegare un altro documento con lo stesso nome
  Allora il sistema rifiuta il caricamento e segnala che il nome del documento è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti documenti allegati alla versione dell'e-service:
      | nome       | fixture                      |
      | Allegato A | assets/origin-interface.yaml |
    # Binding BFF mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "assets/eservice-template/openapi3-riferimenti.json" come "documento" sulla versione dell'e-service con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "CATALOG_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "it"

  Scenario: [ERROR_044] - Rifiuto del caricamento di un file già allegato alla versione
  Dato un e-service con una versione in bozza e un documento allegato
  Quando l'erogatore tenta di allegare lo stesso file con un nome diverso
  Allora il sistema rifiuta il caricamento e segnala che il file è già presente.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti documenti allegati alla versione dell'e-service:
      | nome       | fixture                      |
      | Allegato A | assets/origin-interface.yaml |
    # Binding BFF mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "assets/origin-interface.yaml" come "documento" sulla versione dell'e-service con il nome "Allegato B"
    Then il messaggio di errore corrisponde al caso "CATALOG_CHECKSUM_DUPLICATE" in lingua "it"

  Scenario: [ERROR_045] - Rifiuto della rinomina di un documento con un nome già usato nella versione
  Dato un e-service con una versione in bozza e due documenti distinti
  Quando l'erogatore tenta di assegnare a un documento il nome dell'altro
  Allora il sistema rifiuta la modifica e segnala che il nome del documento è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti documenti allegati alla versione dell'e-service:
      | nome       | fixture                                          |
      | Allegato A | assets/origin-interface.yaml                     |
      | Allegato B | assets/eservice-template/openapi3-riferimenti.json |
    When l'erogatore rinomina il documento "Allegato B" della versione dell'e-service in "Allegato A"
    Then il messaggio di errore corrisponde al caso "CATALOG_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "it"

  Scenario: [ERROR_046] - Rifiuto dell'aggiunta di un'analisi del rischio con un nome già usato
  Dato un e-service in ricezione con un'analisi del rischio
  Quando l'erogatore tenta di aggiungere un'altra analisi con lo stesso nome
  Allora il sistema rifiuta l'aggiunta e segnala che il nome dell'analisi è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | modalità |
      | ES-1  | Comune di Milano | RECEIVE  |
    # Binding mancante; riusare addLatestRiskAnalysis per predisporre l'analisi.
    And un'analisi del rischio completa associata all'e-service
    # Binding BFF negativo mancante; riusare addRiskAnalysis con il nome dell'analisi esistente.
    When l'erogatore tenta di aggiungere all'e-service un'analisi del rischio con il nome di quella esistente
    Then il messaggio di errore corrisponde al caso "CATALOG_RISK_ANALYSIS_DUPLICATED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS" in lingua "it"

  Scenario: [ERROR_047] - Rifiuto della modifica di un'analisi del rischio con un nome già usato
  Dato un e-service in ricezione con due analisi del rischio distinte
  Quando l'erogatore tenta di assegnare a un'analisi il nome dell'altra
  Allora il sistema rifiuta la modifica e segnala che il nome dell'analisi è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | modalità |
      | ES-1  | Comune di Milano | RECEIVE  |
    # Binding mancante per più analisi con alias e nomi; riusare addRiskAnalysis.
    And le seguenti analisi del rischio complete associate all'e-service:
      | alias | nome      |
      | AR-1  | Analisi A |
      | AR-2  | Analisi B |
    When l'erogatore tenta di rinominare l'analisi del rischio "AR-2" dell'e-service con il nome di "AR-1"
    Then il messaggio di errore corrisponde al caso "CATALOG_RISK_ANALYSIS_DUPLICATED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS_RISK_ANALYSIS_ID" in lingua "it"

  Scenario: [ERROR_048] - Rifiuto della modifica del nome con quello di un altro e-service dell'erogatore
  Dati due e-service pubblicati appartenenti allo stesso erogatore
  Quando l'erogatore tenta di assegnare a un e-service il nome dell'altro
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 |
      | ES-1  | Comune di Milano | Catalogo esistente   |
      | ES-2  | Comune di Milano | Catalogo da cambiare |
    When l'erogatore tenta di modificare il nome dell'e-service "ES-2" con quello dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "it"

  Scenario: [ERROR_049] - Rifiuto della modifica del nome di un e-service con quello di un template
  Dati un template e-service e un e-service pubblicati
  Quando l'erogatore tenta di assegnare all'e-service il nome del template
  Allora il sistema rifiuta la modifica e segnala il conflitto con il nome del template.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome               | tecnologia |
      | T-1   | Comune di Milano | Catalogo duplicato | REST       |
    And i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    When l'erogatore tenta di modificare il nome dell'e-service con quello del template
    Then il messaggio di errore corrisponde al caso "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "it"

  Scenario: [ERROR_050] - Rifiuto dell'aggiornamento della sola parola identificativa con un valore già usato
  Date due istanze pubblicate dello stesso template con parole identificative distinte
  Quando l'erogatore tenta di assegnare a un'istanza la parola identificativa dell'altra
  Allora il sistema rifiuta la modifica e segnala che il nome dell'e-service è già in uso.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso   |
      | ES-1  | T-1      | Duplicato  |
      | ES-2  | T-1      | Modificare |
    When l'erogatore tenta di modificare la sola parola identificativa dell'istanza "ES-2" in "Duplicato"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE" in lingua "it" con i valori:
      | identifyingWord | Duplicato |

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_051] - Impossibilità di modificare i dati di un'istanza dopo la revoca della delega
  Data un'istanza in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificarne la parola identificativa insieme ai dati generali
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And l'erogatore istanzia un nuovo e-service dal template
    And l'e-service appena istanziato delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And la delega in erogazione viene revocata dal Comune di Milano
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di aggiornare i dati dell'e-service appena istanziato con la parola identificativa "Disponibile"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_052] - Impossibilità di eliminare un e-service in bozza dopo la revoca della delega
  Dato un e-service in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di eliminare l'e-service
  Allora il sistema rifiuta l'eliminazione e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare deleteEService e gestire il rifiuto.
    When l'erogatore tenta di eliminare l'e-service in bozza
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_053] - Impossibilità per un ente diverso dal delegato di richiedere l'archiviazione dell'e-service
  Dato un e-service pubblicato con una delega in erogazione attiva verso un altro ente
  Quando un amministratore che non appartiene al delegato richiede l'archiviazione dell'intero e-service
  Allora il sistema rifiuta la richiesta e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente all'erogatore PagoPA
    # Mantenere la delega attiva: una delega assente o revocata può produrre un errore diverso.
    When il delegato richiede al delegante l'archiviazione dell'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_054] - Impossibilità per un ente diverso dal delegato di annullare l'archiviazione dell'e-service
  Dato un e-service delegato con una richiesta di archiviazione dell'intero e-service in attesa di valutazione
  Quando un amministratore che non appartiene al delegato tenta di annullare la richiesta
  Allora il sistema rifiuta l'annullamento e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente al delegato Comune di Pozzallo
    And una richiesta di archiviazione dell'intero e-service inoltrata al delegante con 60 giorni di preavviso e una motivazione valida
    And un admin appartenente all'erogatore PagoPA
    # Mantenere la delega attiva: una delega assente o revocata può produrre un errore diverso.
    When il delegato tenta di annullare la richiesta di archiviazione dell'intero e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_055] - Impossibilità per un ente diverso dal delegato di richiedere l'archiviazione di una versione
  Dato un e-service pubblicato con una delega in erogazione attiva verso un altro ente
  Quando un amministratore che non appartiene al delegato richiede l'archiviazione della versione
  Allora il sistema rifiuta la richiesta e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente all'erogatore PagoPA
    # Mantenere la delega attiva: una delega assente o revocata può produrre un errore diverso.
    When il delegato richiede al delegante l'archiviazione della versione 1 dell'e-service con 60 giorni di preavviso e una motivazione valida
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_056] - Impossibilità per un ente diverso dal delegato di annullare l'archiviazione di una versione
  Dato un e-service delegato con una richiesta di archiviazione della versione in attesa di valutazione
  Quando un amministratore che non appartiene al delegato tenta di annullare la richiesta
  Allora il sistema rifiuta l'annullamento e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And un admin appartenente al delegato Comune di Pozzallo
    And una richiesta di archiviazione della versione 1 dell'e-service inoltrata al delegante con 60 giorni di preavviso e una motivazione valida
    And un admin appartenente all'erogatore PagoPA
    # Mantenere la delega attiva: una delega assente o revocata può produrre un errore diverso.
    When il delegato tenta di annullare la richiesta di archiviazione della versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUBMIT_DELEGATED_ARCHIVING" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_057] - Impossibilità di allegare un documento dopo la revoca della delega
  Dato un e-service con una versione in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di allegare un documento alla versione
  Allora il sistema rifiuta il caricamento e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "allegato-a.pdf" come "documento" sulla versione dell'e-service con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_058] - Impossibilità di eliminare un documento dopo la revoca della delega
  Dato un e-service con una versione in bozza e un documento allegato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di eliminare il documento
  Allora il sistema rifiuta l'eliminazione e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti documenti allegati alla versione dell'e-service:
      | nome       | fixture                      |
      | Allegato A | assets/origin-interface.yaml |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di eliminare il documento "Allegato A" della versione dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_059] - Impossibilità di rinominare un documento dopo la revoca della delega
  Dato un e-service con una versione in bozza e un documento allegato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di rinominare il documento
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti documenti allegati alla versione dell'e-service:
      | nome       | fixture                      |
      | Allegato A | assets/origin-interface.yaml |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore rinomina il documento "Allegato A" della versione dell'e-service in "Allegato B"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_060] - Impossibilità di creare una versione dopo la revoca della delega
  Dato un e-service pubblicato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di creare una nuova versione
  Allora il sistema rifiuta la creazione e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare addDescriptor e gestire il rifiuto.
    When l'erogatore tenta di creare una nuova versione dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_061] - Impossibilità di eliminare una versione in bozza dopo la revoca della delega
  Dato un e-service con una versione in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di eliminare la versione
  Allora il sistema rifiuta l'eliminazione e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di eliminare la versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__DELETE_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_062] - Impossibilità di modificare la versione in bozza di un'istanza dopo la revoca della delega
  Data un'istanza di template con una versione in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare la configurazione della versione
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And l'erogatore istanzia un nuovo e-service dal template
    And l'e-service appena istanziato delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And la delega in erogazione viene revocata dal Comune di Milano
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare updateEServiceInstanceDescriptor per la bozza.
    When l'erogatore tenta di modificare la configurazione della versione 1 dell'e-service appena istanziato con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_063] - Impossibilità di pubblicare una versione dopo la revoca della delega
  Dato un e-service con una versione in bozza pronta per la pubblicazione e una delega in erogazione revocata
  Quando l'ex delegato tenta di pubblicare la versione
  Allora il sistema rifiuta la pubblicazione e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service predisposta per la pubblicazione
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare publishDescriptor e gestire il rifiuto.
    When l'erogatore tenta di pubblicare la versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_PUBLISH" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_064] - Impossibilità di sospendere una versione dopo la revoca della delega
  Dato un e-service con una versione pubblicata la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di sospendere la versione
  Allora il sistema rifiuta la sospensione e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare suspendDescriptor e gestire il rifiuto.
    When l'erogatore tenta di sospendere la versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_SUSPEND" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_065] - Impossibilità di riattivare una versione dopo la revoca della delega
  Dato un e-service con una versione sospesa la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di riattivare la versione
  Allora il sistema rifiuta la riattivazione e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di riattivare la versione 1 dell'e-service
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_ACTIVATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_066] - Impossibilità di modificare la configurazione di una versione dopo la revoca della delega
  Dato un e-service con una versione in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare la configurazione della versione
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare updateDescriptor e gestire il rifiuto.
    When l'erogatore tenta di modificare la configurazione della versione 1 dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_067] - Impossibilità di modificare l'approvazione delle fruizioni dopo la revoca della delega
  Dato un e-service con una versione pubblicata la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare la modalità di approvazione delle richieste di fruizione
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare la modalità di approvazione delle richieste di fruizione della versione 1 dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_AGREEMENT_APPROVAL_POLICY_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_068] - Impossibilità di modificare la descrizione di un e-service dopo la revoca della delega
  Dato un e-service pubblicato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare la descrizione dell'e-service
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare la descrizione dell'e-service in "Descrizione aggiornata"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTION_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_069] - Impossibilità di modificare le impostazioni di delega dopo la revoca della delega
  Dato un e-service pubblicato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare le impostazioni di delega dell'e-service
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare le impostazioni di delega dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DELEGATION_FLAGS_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_070] - Impossibilità di modificare il nome di un e-service dopo la revoca della delega
  Dato un e-service pubblicato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di assegnare all'e-service un nome disponibile
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare il nome dell'e-service con un nome disponibile
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_071] - Impossibilità di modificare l'adesione a Signal Hub dopo la revoca della delega
  Dato un e-service pubblicato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare l'adesione dell'e-service a Signal Hub
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare l'adesione dell'e-service a Signal Hub con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_SIGNALHUB_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_072] - Impossibilità di modificare gli attributi richiesti dopo la revoca della delega
  Dato un e-service con una versione pubblicata la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare gli attributi richiesti dalla versione
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare gli attributi richiesti dalla versione 1 dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_ATTRIBUTES_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_073] - Impossibilità di aggiornare un'istanza al nuovo template dopo la revoca della delega
  Data un'istanza pubblicata con una nuova versione del template disponibile e una delega in erogazione revocata
  Quando l'ex delegato tenta di aggiornare l'istanza alla nuova versione del template
  Allora il sistema rifiuta l'aggiornamento e segnala che l'operazione non è consentita.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso   |
      | ES-1  | T-1      | Aggiornare |
    And una nuova versione del template pubblicata e disponibile per l'aggiornamento dell'e-service
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di aggiornare l'e-service alla nuova versione del template
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_UPGRADE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_074] - Impossibilità di configurare l'interfaccia SOAP di un'istanza dopo la revoca della delega
  Data un'istanza SOAP in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di configurare l'interfaccia con indirizzi validi
  Allora il sistema rifiuta la configurazione e segnala che l'operazione non è consentita.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia              |
      | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And l'erogatore istanzia un nuovo e-service dal template
    And l'e-service delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And la delega in erogazione viene revocata dal Comune di Milano
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante per il divieto; riusare addEServiceTemplateInstanceInterfaceSoap.
    When l'erogatore tenta di configurare l'interfaccia SOAP della versione 1 dell'e-service con indirizzi validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_SOAP" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_075] - Impossibilità di configurare l'interfaccia REST di un'istanza dopo la revoca della delega
  Data un'istanza REST in bozza la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di configurare l'interfaccia con dati validi
  Allora il sistema rifiuta la configurazione e segnala che l'operazione non è consentita.

    Given un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.json |
    And l'erogatore istanzia un nuovo e-service dal template
    And l'e-service delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And la delega in erogazione viene revocata dal Comune di Milano
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare addEServiceTemplateInstanceInterfaceRest.
    When l'erogatore tenta di configurare l'interfaccia REST della versione 1 dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_REST" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_076] - Impossibilità di aggiornare la versione pubblicata di un'istanza dopo la revoca della delega
  Data un'istanza di template pubblicata la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare i dati aggiornabili della versione
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso   |
      | ES-1  | T-1      | Modificare |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    # Binding BFF negativo mancante; riusare updateEServiceInstanceDescriptor per la versione pubblicata.
    When l'erogatore tenta di modificare la configurazione della versione 1 dell'istanza "ES-1" con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_077] - Impossibilità di modificare l'indicazione sui dati personali dopo la revoca della delega
  Dato un e-service pubblicato la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di modificare l'indicazione sul trattamento dei dati personali
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare l'indicazione sul trattamento dei dati personali dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_ESERVICES_E_SERVICE_ID_PERSONAL_DATA_FLAG" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_078] - Impossibilità di aggiornare la sola parola identificativa dopo la revoca della delega
  Data un'istanza pubblicata la cui delega in erogazione è stata revocata
  Quando l'ex delegato tenta di assegnarle una parola identificativa disponibile
  Allora il sistema rifiuta la modifica e segnala che l'operazione non è consentita.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso   |
      | ES-1  | T-1      | Modificare |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega revocata
    And un admin appartenente al delegato Comune di Pozzallo
    When l'erogatore tenta di modificare la sola parola identificativa dell'istanza "ES-1" in "Disponibile"
    Then il messaggio di errore corrisponde al caso "CATALOG_OPERATION_FORBIDDEN__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_079] - Rifiuto della configurazione REST di un'istanza con una versione OpenAPI non supportata
  Data un'istanza in bozza derivata da un template con una versione OpenAPI non supportata
  Quando l'erogatore tenta di configurare l'interfaccia REST dell'istanza
  Allora il sistema rifiuta la configurazione e indica le versioni OpenAPI ammesse.

    # Stato non ottenibile dal normale setup BFF: il caricamento dell'interfaccia del template rifiuta già la versione OpenAPI.
    # Richiede un dato preesistente isolato con template pubblicato e interfaccia non supportata.
    # Non simulare il prerequisito cambiando solo la richiesta REST dell'istanza: essa contiene dati di configurazione, non un file.
    Given un'istanza REST in bozza derivata da un template con una versione OpenAPI non supportata
    # Binding BFF negativo mancante; riusare addEServiceTemplateInstanceInterfaceRest.
    When l'erogatore tenta di configurare l'interfaccia REST della versione 1 dell'e-service con dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_OPENAPI_VERSION_NOT_RECOGNIZED__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_INTERFACE_REST" in lingua "it"

  Scenario: [ERROR_080] - Rifiuto della creazione di un e-service con un link nel nome
  Dato un amministratore abilitato alla creazione di e-service
  Quando l'erogatore tenta di creare un e-service con un link nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita a rimuovere il link dal nome.

    # Binding esistente; shouldFailToCreateEService non è implementato sul BFF.
    When l'utente tenta di creare un e-service con un link nel nome e gli altri dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES" in lingua "it"

  Scenario: [ERROR_081] - Rifiuto della creazione di un'istanza con un link nella parola identificativa
  Dato un template e-service pubblicato
  Quando l'erogatore tenta di creare un'istanza con un link nella parola identificativa
  Allora il sistema rifiuta la creazione e invita a rimuovere il link dalla parola identificativa.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    # Binding BFF mancante con parola identificativa; riusare createEServiceInstance e gestire il rifiuto.
    When l'erogatore tenta di istanziare il template con la parola identificativa "https://a.it"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_TEMPLATE_ID_ESERVICES" in lingua "it"

  Scenario: [ERROR_082] - Rifiuto della modifica dei dati di un'istanza con un link nella parola identificativa
  Data un'istanza di template in bozza
  Quando l'erogatore tenta di aggiornarne i dati generali inserendo un link nella parola identificativa
  Allora il sistema rifiuta la modifica e invita a rimuovere il link dalla parola identificativa.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And l'erogatore istanzia un nuovo e-service dal template
    When l'erogatore tenta di aggiornare i dati dell'e-service appena istanziato con la parola identificativa "https://a.it"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_ESERVICES_E_SERVICE_ID" in lingua "it"

  Scenario: [ERROR_083] - Rifiuto del caricamento di un documento con un link nel nome
  Dato un e-service con una versione in bozza
  Quando l'erogatore tenta di allegare un documento valido con un link nel nome
  Allora il sistema rifiuta il caricamento e invita a rimuovere il link dal nome del documento.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # Binding BFF mancante per il caricamento; riusare addDocument e gestire il rifiuto.
    When l'erogatore carica il file "assets/origin-interface.yaml" come "documento" sulla versione dell'e-service con il nome "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS" in lingua "it"

  Scenario: [ERROR_084] - Rifiuto della rinomina di un documento con un link nel nome
  Dato un e-service con una versione in bozza e un documento allegato
  Quando l'erogatore tenta di inserire un link nel nome del documento
  Allora il sistema rifiuta la modifica e invita a rimuovere il link dal nome del documento.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And i seguenti documenti allegati alla versione dell'e-service:
      | nome       | fixture                      |
      | Allegato A | assets/origin-interface.yaml |
    When l'erogatore rinomina il documento "Allegato A" della versione dell'e-service in "Documento https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "it"

  Scenario: [ERROR_085] - Rifiuto della creazione di una versione con un link nella descrizione
  Dato un e-service pubblicato senza versioni in bozza
  Quando l'erogatore tenta di creare una nuova versione con un link nella descrizione
  Allora il sistema rifiuta l'operazione e invita a rimuovere il link dalla descrizione della versione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    # addDescriptor non accetta la descrizione: verificare come raggiungere questo errore senza spostare il caso alla modifica.
    When l'erogatore tenta di creare una nuova versione dell'e-service con un link nella descrizione e gli altri dati validi
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS" in lingua "it"

  Scenario: [ERROR_086] - Rifiuto della modifica della descrizione di un e-service con un link
  Dato un e-service pubblicato appartenente all'erogatore
  Quando l'erogatore tenta di inserire un link nella descrizione dell'e-service
  Allora il sistema rifiuta la modifica e invita a rimuovere il link dalla descrizione.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    When l'erogatore tenta di modificare la descrizione dell'e-service in "Descrizione con https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTION_UPDATE" in lingua "it"

  Scenario: [ERROR_087] - Rifiuto della modifica del nome di un e-service con un link
  Dato un e-service pubblicato appartenente all'erogatore
  Quando l'erogatore tenta di inserire un link nel nome dell'e-service
  Allora il sistema rifiuta la modifica e invita a rimuovere il link dal nome.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    When l'erogatore tenta di modificare il nome dell'e-service in "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_NAME_UPDATE" in lingua "it"

  Scenario: [ERROR_088] - Rifiuto di una motivazione con un link per la mancata approvazione della pubblicazione
  Dato un e-service delegato con una versione proposta per la pubblicazione in attesa di approvazione
  Quando il delegante tenta di rifiutare la pubblicazione inserendo un link nella motivazione
  Allora il sistema rifiuta l'operazione e invita a rimuovere il link dalla motivazione.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 1 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo WAITING_FOR_APPROVAL
    And un admin appartenente all'erogatore Comune di Milano
    # Binding BFF negativo mancante; riusare rejectDescriptor senza attendere un rifiuto riuscito.
    When l'erogatore tenta di rifiutare la pubblicazione della versione 1 dell'e-service con motivazione "Rifiuto con https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_E_SERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID_REJECT" in lingua "it"

  Scenario: [ERROR_089] - Rifiuto dell'aggiornamento della sola parola identificativa con un link
  Data un'istanza di template pubblicata
  Quando l'erogatore tenta di inserire un link nella sola parola identificativa dell'istanza
  Allora il sistema rifiuta la modifica e invita a rimuovere il link dalla parola identificativa.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso   |
      | ES-1  | T-1      | Modificare |
    When l'erogatore tenta di modificare la sola parola identificativa dell'istanza "ES-1" in "https://a.it"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_ESERVICES_E_SERVICE_ID_INSTANCE_LABEL_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_090] - Rifiuto dell'aggiornamento parziale con il nome di un altro e-service dell'erogatore
  Dati due e-service in bozza appartenenti allo stesso erogatore
  Quando l'erogatore tenta di aggiornare parzialmente un e-service usando il nome dell'altro
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                 |
      | ES-1  | Comune di Milano | Catalogo esistente   |
      | ES-2  | Comune di Milano | Catalogo da cambiare |
    When l'erogatore tenta di aggiornare parzialmente i dati dell'e-service "ES-2" con il nome dell'e-service "ES-1"
    Then il messaggio di errore corrisponde al caso "CATALOG_E_SERVICE_NAME_DUPLICATE_FOR_PRODUCER__PATCH_ESERVICES_E_SERVICE_ID" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_091] - Rifiuto dell'aggiornamento parziale di un e-service con il nome di un template
  Dati un template pubblicato e un e-service in bozza
  Quando l'erogatore tenta di aggiornare parzialmente l'e-service usando il nome del template
  Allora il sistema rifiuta la modifica e segnala il conflitto con il nome del template.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome               | tecnologia |
      | T-1   | Comune di Milano | Catalogo duplicato | REST       |
    And i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    When l'erogatore tenta di aggiornare parzialmente i dati dell'e-service con il nome del template
    Then il messaggio di errore corrisponde al caso "CATALOG_ESERVICE_TEMPLATE_NAME_CONFLICT__PATCH_ESERVICES_E_SERVICE_ID" in lingua "it"

  Scenario: [ERROR_092] - Rifiuto dell'aggiornamento parziale di un e-service con un link nel nome
  Dato un e-service in bozza appartenente all'erogatore
  Quando l'erogatore tenta di aggiornarne parzialmente i dati inserendo un link nel nome
  Allora il sistema rifiuta la modifica e invita a rimuovere il link dal nome.

    Given i seguenti e-service in bozza:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    When l'erogatore tenta di aggiornare parzialmente i dati dell'e-service impostando il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "CATALOG_HYPERLINK_DETECTION_ERROR__PATCH_ESERVICES_E_SERVICE_ID" in lingua "it"
