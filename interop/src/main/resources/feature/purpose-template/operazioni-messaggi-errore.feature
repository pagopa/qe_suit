@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la gestione dei template finalità e delle annotazioni

  Come creatore di template finalità
  Voglio sapere perché una modifica, la pubblicazione, la riattivazione o la gestione degli allegati sono state rifiutate
  Per verificare gli e-service suggeriti, scegliere nomi distinti o correggere le annotazioni.

  Background:
    Given un admin appartenente al fruitore PagoPA

  Scenario: [ERROR_138] - Impossibilità di riattivare un template con un e-service suggerito sospeso
  Dato un template finalità sospeso con un e-service suggerito non più disponibile per la fruizione
  Quando il creatore tenta di riattivare il template
  Allora il sistema rifiuta la riattivazione e segnala che l'e-service suggerito non è valido.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And il template finalità sospeso
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And un admin appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo mancanti per la riattivazione del template.
    When l'operatore tenta di riattivare il template finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_ASSOCIATED_ESERVICE_FOR_PUBLICATION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_UNSUSPEND"

  Scenario: [ERROR_139] - Impossibilità di pubblicare un template con un e-service suggerito sospeso
  Dato un template finalità in bozza con un e-service suggerito non più disponibile per la fruizione
  Quando il creatore tenta di pubblicare il template
  Allora il sistema rifiuta la pubblicazione e segnala che l'e-service suggerito non è valido.

    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And l'e-service "ES-1" collegato a un template di finalità in bozza
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And un admin appartenente al fruitore PagoPA
    # Binding BFF negativo mancante; riusare publishPurposeTemplate senza attendere il successo.
    When l'operatore tenta di pubblicare il template finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_ASSOCIATED_ESERVICE_FOR_PUBLICATION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PUBLISH"

  Scenario: [ERROR_140] - Rifiuto di un allegato all'annotazione con un nome già in uso
  Dato un template finalità in bozza con una risposta annotata e un documento allegato
  Quando il creatore tenta di allegare un PDF diverso con lo stesso nome
  Allora il sistema rifiuta il caricamento e segnala che il nome dell'allegato è già in uso.

    # Binding di setup comune mancante; riusare createPurposeTemplate e predisporre una risposta annotata.
    Given un template finalità in bozza con una risposta annotata
    # Operazione BFF e binding di setup comuni mancanti per gli allegati alle annotazioni.
    And i seguenti documenti PDF allegati all'annotazione del template finalità:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
    # Operazione BFF e binding negativo comuni mancanti per gli allegati alle annotazioni.
    When l'operatore allega il file "allegato-b.pdf" all'annotazione del template finalità con il nome "Allegato A"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_CONFLICT_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION_DOCUMENTS"

  Scenario: [ERROR_141] - Rifiuto di un allegato all'annotazione con contenuto già presente
  Dato un template finalità in bozza con una risposta annotata e un documento allegato
  Quando il creatore tenta di allegare lo stesso PDF con un nome diverso
  Allora il sistema rifiuta il caricamento e segnala che il contenuto è già presente.

    # Binding di setup comune mancante; riusare createPurposeTemplate e predisporre una risposta annotata.
    Given un template finalità in bozza con una risposta annotata
    # Operazione BFF e binding di setup comuni mancanti per gli allegati alle annotazioni.
    And i seguenti documenti PDF allegati all'annotazione del template finalità:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
    # Operazione BFF e binding negativo comuni mancanti per gli allegati alle annotazioni.
    When l'operatore allega il file "allegato-a.pdf" all'annotazione del template finalità con il nome "Allegato B"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_CONFLICT_DUPLICATED_DOCUMENT"

  Scenario: [ERROR_142] - Rifiuto dell'aggiunta di una risposta con un link nell'annotazione
  Dato un template finalità in bozza con una domanda che consente le annotazioni
  Quando il creatore tenta di aggiungere una risposta valida con un collegamento nell'annotazione
  Allora il sistema rifiuta l'aggiunta e invita il creatore a rimuovere il collegamento.

    # Binding di setup mancante; riusare createPurposeTemplate con una domanda ancora da rispondere.
    Given un template finalità in bozza con una domanda predisposta per le annotazioni
    # Operazione BFF e binding negativo mancanti per l'aggiunta della risposta annotata.
    When l'operatore tenta di aggiungere una risposta al template finalità con l'annotazione "Motivazione https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS"

  Scenario: [ERROR_143] - Rifiuto della modifica di un'annotazione con un link
  Dato un template finalità in bozza con una risposta annotata
  Quando il creatore tenta di inserire un collegamento nell'annotazione esistente
  Allora il sistema rifiuta la modifica e invita il creatore a rimuovere il collegamento.

    # Binding di setup comune mancante; riusare createPurposeTemplate e predisporre una risposta annotata.
    Given un template finalità in bozza con una risposta annotata
    # Operazione BFF e binding negativo mancanti per la modifica dell'annotazione.
    When l'operatore tenta di aggiornare l'annotazione del template finalità con il testo "Motivazione https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PUT_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION"

  Scenario: [ERROR_144] - Impossibilità di rinominare un allegato con il nome di un altro documento
  Dato un template finalità in bozza con due documenti distinti allegati alla stessa annotazione
  Quando il creatore tenta di assegnare al secondo documento il nome del primo
  Allora il sistema rifiuta la rinomina e segnala che il nome è già in uso.

    # Binding di setup comune mancante; riusare createPurposeTemplate e predisporre una risposta annotata.
    Given un template finalità in bozza con una risposta annotata
    # Operazione BFF e binding di setup comuni mancanti per gli allegati alle annotazioni.
    And i seguenti documenti PDF allegati all'annotazione del template finalità:
      | nome       | fixture        |
      | Allegato A | allegato-a.pdf |
      | Allegato B | allegato-b.pdf |
    # Operazione BFF e binding negativo mancanti per la rinomina dell'allegato.
    When l'operatore tenta di rinominare l'allegato "Allegato B" dell'annotazione del template finalità in "Allegato A"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_CONFLICT_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION_DOCUMENTS_DOCUMENT_ID_UPDATE"

  Scenario: [ERROR_145] - Impossibilità per un operatore di sicurezza di scaricare l'analisi del rischio firmata
  Dato un template finalità con il documento firmato disponibile e un operatore di sicurezza dell'ente creatore
  Quando l'operatore tenta di scaricare il documento firmato
  Allora il sistema rifiuta il download e segnala che il ruolo non è autorizzato.

    # Binding di setup mancante; riusare createPurposeTemplate e publishPurposeTemplate.
    Given un template finalità pubblicato dal fruitore PagoPA
    # Setup e binding mancanti; verificare la disponibilità reale del documento prima del cambio di ruolo.
    And il documento firmato di analisi del rischio del template finalità disponibile
    And un security appartenente al fruitore PagoPA
    # Operazione BFF e binding negativo mancanti per il download con l'utente corrente.
    When l'utente tenta di scaricare il documento firmato di analisi del rischio del template finalità
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_UNAUTHORIZED_ERROR"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_146] - Rifiuto della modifica di un template con il nome di un altro template finalità
  Dati due template finalità in bozza dello stesso creatore con nomi diversi
  Quando il creatore tenta di assegnare al secondo il nome del primo
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    # Binding di setup comune mancante; riusare createPurposeTemplate e registrare gli alias senza cambiare i nomi.
    Given i seguenti template finalità in bozza creati da PagoPA con nomi distinti:
      | alias |
      | TF-1  |
      | TF-2  |
    # Operazione BFF e binding negativo mancanti; il client attuale espone PUT, non PATCH.
    # Verificare il contratto di rilascio e non rimappare questo ID su un'altra operazione.
    When l'operatore tenta di modificare i dati del template finalità "TF-2" con il nome del template "TF-1"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_PURPOSE_TEMPLATE_TITLE_CONFLICT__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID"

  Scenario: [ERROR_147] - Rifiuto della modifica di un template finalità con un link nel nome
  Dato un template finalità in bozza
  Quando il creatore tenta di inserire un collegamento nel nome lasciando validi gli altri dati
  Allora il sistema rifiuta la modifica e invita il creatore a rimuovere il collegamento.

    # Binding di setup comune mancante; riusare createPurposeTemplate e registrare l'alias.
    Given i seguenti template finalità in bozza creati da PagoPA con nomi distinti:
      | alias |
      | TF-1  |
    # Operazione BFF e binding negativo mancanti; il client attuale espone PUT, non PATCH.
    # Verificare il contratto di rilascio e non rimappare questo ID su un'altra operazione.
    When l'operatore tenta di modificare i dati del template finalità "TF-1" con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID"
