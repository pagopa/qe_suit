@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione delle finalità e la valutazione delle analisi del rischio

  Come fruitore o valutatore
  Voglio sapere perché una modifica della finalità o una valutazione dell'analisi del rischio sono state rifiutate
  Per scegliere nomi distinti, correggere i dati o verificare lo stato e la versione dell'analisi.

  Background:
    Given un admin appartenente al fruitore PagoPA

  Scenario: [ERROR_198] - Rifiuto della modifica di una finalità da template con oltre un miliardo di chiamate API al giorno
  Data una finalità in bozza creata da un template pubblicato per un e-service sottoscritto dal fruitore
  Quando il fruitore tenta di impostare una stima di 1.000.000.001 chiamate API al giorno lasciando validi gli altri dati
  Allora il sistema rifiuta la modifica e invita il fruitore a ridurre la stima entro il limite di 1.000.000.000.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    # Binding di setup mancante, condiviso con ERROR_024; riusare createPurposeFromTemplate.
    And una finalità in bozza creata da PagoPA dal template finalità per l'e-service "ES-1" e identificata come "F-1"
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo mancanti; riusare patchFromTemplateRequest modificando solo la stima.
    When il fruitore tenta di modificare la finalità da template "F-1" con una stima di 1000000001 chiamate API al giorno
    Then il messaggio di errore corrisponde al caso "BFF_BAD_REQUEST__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_018] - Impossibilità di creare una finalità con un nome già usato per lo stesso e-service
  Data una finalità del fruitore per un e-service già sottoscritto dal fruitore
  Quando il fruitore tenta di creare un'altra finalità con lo stesso nome per lo stesso e-service
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given un eservice creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato DRAFT associate da PagoPA
    When il fruitore tenta di creare una finalità con lo stesso nome della finalità creata in precedenza per lo stesso e-service
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES" in lingua "it"

  Scenario: [ERROR_019] - Impossibilità di creare una finalità omonima per un e-service in ricezione
  Dato un e-service in ricezione sottoscritto dal fruitore con una finalità già creata
  Quando il fruitore tenta di crearne un'altra con lo stesso nome per lo stesso e-service
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | modalità |
      | ES-1  | Comune di Milano | RECEIVE  |
    # Binding di setup mancante; riusare addLatestRiskAnalysis per l'e-service in ricezione.
    And un'analisi del rischio completa associata all'e-service
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # Binding di setup comune mancante; riusare createReversePurpose con l'analisi dell'erogatore.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
    # Binding negativo parametrico mancante; riusare createPurpose o createReversePurpose secondo la modalità.
    When il fruitore tenta di creare una finalità per l'e-service con il nome della finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_REVERSE_PURPOSES" in lingua "it"

  Scenario: [ERROR_020] - Impossibilità di rinominare una finalità con il nome di un'altra dello stesso fruitore
  Date due finalità in bozza del fruitore per lo stesso e-service con nomi diversi
  Quando il fruitore tenta di assegnare alla seconda il nome della prima
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # Binding di setup comune mancante; riusare createPurpose e registrare gli alias con i nomi effettivi.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
      | F-2   |
    # Operazione BFF e binding negativo mancanti; riusare updateRequest per gli altri dati validi.
    When il fruitore tenta di modificare la finalità "F-2" con il nome della finalità "F-1"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_021] - Rifiuto della modifica di una finalità con un link nel nome
  Data una finalità del fruitore in bozza
  Quando il fruitore tenta di inserire un collegamento nel nome lasciando validi gli altri dati
  Allora il sistema rifiuta la modifica e invita il fruitore a rimuovere il collegamento.

    Given un eservice creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato DRAFT associate da PagoPA
    # Operazione BFF e binding negativo mancanti; riusare updateRequest per gli altri dati validi.
    When il fruitore tenta di modificare la finalità con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__POST_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_022] - Impossibilità di duplicare una finalità quando il nome derivato è già in uso
  Data una finalità del fruitore e un'altra finalità dello stesso fruitore per lo stesso e-service con il nome che deriverebbe dalla duplicazione
  Quando il fruitore tenta di duplicare la prima finalità
  Allora il sistema rifiuta la duplicazione e segnala che il nome è già in uso.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # Binding di setup comune mancante; riusare createPurpose e registrare gli alias con i nomi effettivi.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
    # Setup e binding mancanti; verificare il nome derivato dal clone, senza assumere prefissi o suffissi.
    And un'altra finalità di PagoPA per l'e-service "ES-1" con il nome che deriverebbe dalla duplicazione di "F-1"
    # Operazione BFF e binding negativo mancanti; usare F-1, non l'ultima finalità creata.
    When il fruitore tenta di duplicare la finalità "F-1"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES_PURPOSE_ID_CLONE" in lingua "it"

  Scenario: [ERROR_023] - Impossibilità di creare una finalità da template con un nome già usato
  Data una finalità del fruitore e un template finalità pubblicato applicabile allo stesso e-service
  Quando il fruitore tenta di creare una finalità da template con lo stesso nome della finalità esistente
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    # Binding di setup comune mancante; riusare createPurpose e registrare gli alias con i nomi effettivi.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
    # Binding BFF negativo mancante; riusare createPurposeFromTemplate con il nome effettivo della finalità.
    When il fruitore tenta di creare una finalità dal template finalità per l'e-service con il nome della finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES" in lingua "it"

  Scenario: [ERROR_024] - Impossibilità di rinominare una finalità da template con il nome di un'altra finalità
  Date due finalità distinte del fruitore per lo stesso e-service, una delle quali creata da template
  Quando il fruitore tenta di assegnare alla finalità da template il nome dell'altra
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    # Binding di setup comune mancante; riusare createPurpose e registrare gli alias con i nomi effettivi.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
    # Binding di setup mancante; riusare createPurposeFromTemplate e registrare la finalità distinta.
    And una finalità in bozza creata da PagoPA dal template finalità per l'e-service "ES-1" e identificata come "F-2"
    # Operazione BFF e binding negativo mancanti; riusare patchFromTemplateRequest, non updateRequest.
    When il fruitore tenta di modificare la finalità da template "F-2" con il nome della finalità "F-1"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_025] - Impossibilità di firmare un'analisi del rischio già approvata
  Data un'analisi del rischio già approvata e assegnata al valutatore del fruitore
  Quando il valutatore tenta di firmarla nuovamente
  Allora il sistema rifiuta la firma e segnala che l'analisi è già stata approvata.

    # Il Markdown del branch a8095d9 documenta reviewerWorkflowNotInSignableState, non riskAnalysisAlreadyApproved.
    # Mantenere il mapping atteso e verificare il codice sul contratto di rilascio; setup e binding valutatore mancanti.
    Given un'analisi del rischio della finalità in stato "approvata" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo comuni mancanti per la firma dell'analisi.
    When il valutatore tenta di firmare l'analisi del rischio della finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_SIGN" in lingua "it"

  Scenario: [ERROR_026] - Impossibilità di firmare un'analisi del rischio già rifiutata
  Data un'analisi del rischio già rifiutata e assegnata al valutatore del fruitore
  Quando il valutatore tenta di firmarla
  Allora il sistema rifiuta la firma e segnala che l'analisi è già stata rifiutata.

    # Il Markdown del branch a8095d9 documenta reviewerWorkflowNotInSignableState, non riskAnalysisAlreadyRejected.
    # Mantenere il mapping atteso e verificare il codice sul contratto di rilascio; setup e binding valutatore mancanti.
    Given un'analisi del rischio della finalità in stato "rifiutata" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo comuni mancanti per la firma dell'analisi.
    When il valutatore tenta di firmare l'analisi del rischio della finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_SIGN" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_027] - Impossibilità di firmare un'analisi del rischio aggiornata dopo la lettura
  Data un'analisi del rischio modificata dopo che il valutatore ne ha letto una versione precedente
  Quando il valutatore tenta di firmare usando i dati della versione letta
  Allora il sistema rifiuta la firma e segnala che la versione non è più aggiornata.

    # Richiede il workflow valutatore abilitato, una finalità non delegata per un e-service DELIVER e un'analisi firmabile.
    # L'aggiornamento deve preservare assegnazione, modalità di valutazione e stato firmabile; cambia solo la revisione letta.
    # Setup e binding parametrico mancanti per il workflow valutatore.
    Given un'analisi del rischio della finalità in stato "in attesa di valutazione" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Binding di lettura mancante; conservare la versione metadata effettiva prima dell'aggiornamento.
    And i dati dell'analisi del rischio letti dal valutatore
    # Binding di setup mancante; aggiornare l'analisi senza sostituire i dati letti dal valutatore.
    And l'analisi del rischio aggiornata dopo la lettura del valutatore
    # Operazione BFF e binding negativo mancanti; usare la versione metadata precedente con gli altri dati validi.
    When il valutatore tenta di firmare l'analisi del rischio con i dati letti prima dell'aggiornamento
    Then il messaggio di errore corrisponde al caso "PURPOSE_PURPOSE_METADATA_VERSION_MISMATCH" in lingua "it"

  Scenario: [ERROR_028] - Impossibilità di rifiutare un'analisi del rischio già approvata
  Data un'analisi del rischio già approvata e assegnata al valutatore del fruitore
  Quando il valutatore tenta di rifiutarla con una motivazione valida
  Allora il sistema rifiuta l'operazione e segnala che l'analisi è già stata approvata.

    # Il Markdown del branch a8095d9 documenta reviewerWorkflowNotInSubmittedState, non riskAnalysisAlreadyApproved.
    # Mantenere il mapping atteso; usare la modalità in cui l'admin compila e il valutatore firma.
    Given un'analisi del rischio della finalità in stato "approvata" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo parametrico mancanti per il rifiuto dell'analisi.
    When il valutatore tenta di rifiutare l'analisi del rischio della finalità con la motivazione "Informazioni insufficienti"
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT" in lingua "it"

  Scenario: [ERROR_029] - Impossibilità di rifiutare un'analisi del rischio già rifiutata
  Data un'analisi del rischio già rifiutata e assegnata al valutatore del fruitore
  Quando il valutatore tenta di rifiutarla nuovamente con una motivazione valida
  Allora il sistema rifiuta l'operazione e segnala che l'analisi è già stata rifiutata.

    # Il Markdown del branch a8095d9 documenta reviewerWorkflowNotInSubmittedState, non riskAnalysisAlreadyRejected.
    # Mantenere il mapping atteso; usare la modalità in cui l'admin compila e il valutatore firma.
    Given un'analisi del rischio della finalità in stato "rifiutata" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo parametrico mancanti per il rifiuto dell'analisi.
    When il valutatore tenta di rifiutare l'analisi del rischio della finalità con la motivazione "Informazioni insufficienti"
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT" in lingua "it"

  Scenario: [ERROR_030] - Impossibilità di modificare le risposte di un'analisi già approvata
  Data un'analisi del rischio già approvata e assegnata al valutatore del fruitore
  Quando il valutatore tenta di salvare nuove risposte valide
  Allora il sistema rifiuta il salvataggio e segnala che l'analisi è già stata approvata.

    # Il Markdown del branch a8095d9 documenta reviewerWorkflowNotEditable, non riskAnalysisAlreadyApproved.
    # Mantenere il mapping atteso; usare la modalità in cui il valutatore compila e firma.
    Given un'analisi del rischio della finalità in stato "approvata" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo comuni mancanti per il salvataggio delle risposte.
    When il valutatore tenta di salvare risposte valide nell'analisi del rischio della finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__PUT_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_FORM" in lingua "it"

  Scenario: [ERROR_031] - Impossibilità di modificare le risposte di un'analisi già rifiutata
  Data un'analisi del rischio già rifiutata e assegnata al valutatore del fruitore
  Quando il valutatore tenta di salvare nuove risposte valide
  Allora il sistema rifiuta il salvataggio e segnala che l'analisi è già stata rifiutata.

    # Il Markdown del branch a8095d9 documenta reviewerWorkflowNotEditable, non riskAnalysisAlreadyRejected.
    # Mantenere il mapping atteso; usare la modalità in cui il valutatore compila e firma.
    Given un'analisi del rischio della finalità in stato "rifiutata" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo comuni mancanti per il salvataggio delle risposte.
    When il valutatore tenta di salvare risposte valide nell'analisi del rischio della finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__PUT_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_FORM" in lingua "it"

  Scenario: [ERROR_032] - Rifiuto di una motivazione con un link nella valutazione dell'analisi del rischio
  Data un'analisi del rischio in attesa di valutazione e assegnata al valutatore del fruitore
  Quando il valutatore tenta di rifiutarla inserendo un collegamento nella motivazione
  Allora il sistema rifiuta l'operazione e invita il valutatore a rimuovere il collegamento.

    # Richiede il workflow abilitato, una finalità DELIVER non delegata e la modalità in cui l'admin compila e il valutatore firma.
    # La richiesta deve essere già sottoposta al valutatore (SUBMITTED), altrimenti prevale un errore di stato o modalità.
    # Setup e binding parametrico mancanti per il workflow valutatore.
    Given un'analisi del rischio della finalità in stato "in attesa di valutazione" assegnata al valutatore di PagoPA
    And un reviewer appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo parametrico mancanti per il rifiuto dell'analisi.
    When il valutatore tenta di rifiutare l'analisi del rischio della finalità con la motivazione "Motivazione https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_033] - Rifiuto dell'aggiornamento parziale di una finalità con il nome di un'altra
  Date due finalità in bozza del fruitore per lo stesso e-service con nomi diversi
  Quando il fruitore tenta di aggiornare parzialmente la seconda usando il nome della prima
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # Binding di setup comune mancante; riusare createPurpose e registrare gli alias con i nomi effettivi.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
      | F-2   |
    # Operazione BFF e binding negativo comuni mancanti; verificare il contratto PATCH di rilascio.
    # Il client espone POST per questa modifica; non rimappare questo ID su un altro endpoint.
    When il fruitore tenta di aggiornare parzialmente la finalità "F-2" con il nome della finalità "F-1"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_034] - Rifiuto dell'aggiornamento parziale di una finalità con un link nel nome
  Data una finalità del fruitore in bozza
  Quando il fruitore tenta di aggiornarla parzialmente inserendo un collegamento nel nome
  Allora il sistema rifiuta la modifica e invita il fruitore a rimuovere il collegamento.

    Given un eservice creato dal Comune di Milano con una richiesta di fruizione in stato ACTIVE e una finalità in stato DRAFT associate da PagoPA
    # Operazione BFF e binding negativo comuni mancanti; verificare il contratto PATCH di rilascio.
    # Il client espone POST per questa modifica; non rimappare questo ID su un altro endpoint.
    When il fruitore tenta di aggiornare parzialmente la finalità con il nome "Finalità https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__PATCH_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_035] - Rifiuto dell'aggiornamento parziale di una finalità in ricezione con nome duplicato
  Date due finalità in bozza del fruitore per lo stesso e-service in ricezione con nomi diversi
  Quando il fruitore tenta di aggiornare parzialmente la seconda usando il nome della prima
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | modalità |
      | ES-1  | Comune di Milano | RECEIVE  |
    # Binding di setup mancante; riusare addLatestRiskAnalysis per l'e-service in ricezione.
    And un'analisi del rischio completa associata all'e-service
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # Binding di setup comune mancante; riusare createReversePurpose con l'analisi dell'erogatore.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
      | F-2   |
    # Operazione BFF e binding negativo comuni mancanti; verificare il contratto PATCH di rilascio.
    # Il client espone POST per questa modifica; non rimappare questo ID su un altro endpoint.
    When il fruitore tenta di aggiornare parzialmente la finalità "F-2" con il nome della finalità "F-1"
    Then il messaggio di errore corrisponde al caso "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_REVERSE_PURPOSES_PURPOSE_ID" in lingua "it"

  Scenario: [ERROR_036] - Rifiuto dell'aggiornamento parziale di una finalità in ricezione con un link nel nome
  Data una finalità del fruitore in bozza per un e-service in ricezione
  Quando il fruitore tenta di aggiornarla parzialmente inserendo un collegamento nel nome
  Allora il sistema rifiuta la modifica e invita il fruitore a rimuovere il collegamento.

    Given i seguenti e-service in bozza:
      | alias | erogatore        | modalità |
      | ES-1  | Comune di Milano | RECEIVE  |
    # Binding di setup mancante; riusare addLatestRiskAnalysis per l'e-service in ricezione.
    And un'analisi del rischio completa associata all'e-service
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # Binding di setup comune mancante; riusare createReversePurpose con l'analisi dell'erogatore.
    And le seguenti finalità in bozza create da PagoPA per l'e-service "ES-1" con nomi distinti:
      | alias |
      | F-1   |
    # Operazione BFF e binding negativo comuni mancanti; verificare il contratto PATCH di rilascio.
    # Il client espone POST per questa modifica; non rimappare questo ID su un altro endpoint.
    When il fruitore tenta di aggiornare parzialmente la finalità con il nome "Finalità https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_HYPERLINK_DETECTION_ERROR__PATCH_REVERSE_PURPOSES_PURPOSE_ID" in lingua "it"
