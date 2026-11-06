@purpose-template-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dei template finalità

  Come amministratore o operatore di un ente
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un template finalità non può essere gestito
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  # Feature in attesa dell'implementazione degli step mancanti.
  # Ogni scenario utilizza template, e-service e documenti dedicati.
  # I riferimenti nominati identificano fixture specifiche, non l'ultima entità creata.
  # I Given concorrenti non devono aggiornare la pagina né sostituire l'identità WEB.
  # La selezione della lingua deve essere verificata nell'interfaccia, anche per l'italiano.
  # Il Then verifica la snackbar visibile e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.
  # La riproducibilità WEB, in particolare del download con ruolo non autorizzato,
  # resta da verificare: non aggirare controlli UI né simulare risposte per raggiungere il messaggio.

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_1] - Download dell'analisi del rischio con ruolo non autorizzato
    Given un template finalità in stato PUBLISHED creato dal Comune di Milano con il documento firmato dell'analisi del rischio disponibile
    And un utente autenticato del Comune di Milano con ruolo security
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio del template finalità
    And l'utente tenta di scaricare il documento firmato dell'analisi del rischio del template finalità
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_UNAUTHORIZED_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_2] - Modifica di un template finalità con un link nei campi di testo
    Given un template finalità in stato DRAFT creato dal Comune di Milano e identificato come "template da modificare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali del template finalità "template da modificare"
    And l'utente tenta di salvare un link nella descrizione del template finalità "template da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_3] - Modifica del nome di un template finalità con un nome già esistente
    Given un template finalità in stato DRAFT creato dal Comune di Milano e identificato come "template esistente"
    And un altro template finalità in stato DRAFT creato dal Comune di Milano con un nome diverso e identificato come "template da rinominare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei dati generali del template finalità "template da rinominare"
    And l'utente tenta di assegnare al template finalità "template da rinominare" il nome del template "template esistente" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_PURPOSE_TEMPLATE_TITLE_CONFLICT__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_4] - Pubblicazione di un template finalità con e-service suggerito non più disponibile
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un template finalità in stato DRAFT creato dal Comune di Milano con l'e-service suggerito e tutti i requisiti di pubblicazione soddisfatti
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il riepilogo del template finalità per pubblicarlo
    Given il Comune di Milano sospende la versione pubblicata dell'e-service
    When l'utente tenta di pubblicare il template finalità senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_INVALID_ASSOCIATED_ESERVICE_FOR_PUBLICATION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PUBLISH" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_5] - Salvataggio delle risposte dell'analisi del rischio con un link nelle annotazioni
    Given un template finalità in stato DRAFT creato dal Comune di Milano con una domanda dell'analisi del rischio predisposta per una risposta con annotazione non ancora salvata
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la compilazione dell'analisi del rischio del template finalità
    And l'utente tenta di salvare le risposte dell'analisi del rischio inserendo un link nell'annotazione della domanda predisposta e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_6] - Caricamento di un allegato con un nome già presente nell'annotazione
    Given un template finalità in stato DRAFT creato dal Comune di Milano con un'annotazione dell'analisi del rischio identificata come "annotazione con allegati"
    And un documento PDF valido allegato all'annotazione "annotazione con allegati" e identificato come "documento esistente"
    And un altro documento PDF valido con contenuto diverso dal "documento esistente" e lo stesso nome visualizzato, identificato come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre gli allegati dell'annotazione "annotazione con allegati"
    And l'utente tenta di allegare il "documento da caricare" all'annotazione "annotazione con allegati"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_CONFLICT_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION_DOCUMENTS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_7] - Caricamento di un allegato con contenuto già presente nell'annotazione
    Given un template finalità in stato DRAFT creato dal Comune di Milano con un'annotazione dell'analisi del rischio identificata come "annotazione con allegati"
    And un documento PDF valido allegato all'annotazione "annotazione con allegati" e identificato come "documento esistente"
    And una copia del "documento esistente" con lo stesso contenuto e un nome visualizzato diverso e non utilizzato nell'annotazione, identificata come "documento da caricare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre gli allegati dell'annotazione "annotazione con allegati"
    And l'utente tenta di allegare il "documento da caricare" all'annotazione "annotazione con allegati"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_CONFLICT_DUPLICATED_DOCUMENT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_8] - Rinomina di un allegato con un nome già presente nell'annotazione
    Given un template finalità in stato DRAFT creato dal Comune di Milano con un'annotazione dell'analisi del rischio identificata come "annotazione con allegati"
    And due documenti PDF validi con nomi e contenuti diversi allegati all'annotazione "annotazione con allegati" e identificati come "documento esistente" e "documento da rinominare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre gli allegati dell'annotazione "annotazione con allegati"
    And l'utente tenta di rinominare il "documento da rinominare" con il nome visualizzato del "documento esistente"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_CONFLICT_DOCUMENT_PRETTY_NAME_DUPLICATE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION_DOCUMENTS_DOCUMENT_ID_UPDATE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_9] - Riattivazione di un template finalità con e-service suggerito non più disponibile
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And un template finalità creato dal Comune di Milano, pubblicato con l'e-service suggerito e successivamente sospeso
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio del template finalità sospeso per riattivarlo
    Given il Comune di Milano sospende la versione pubblicata dell'e-service
    When l'utente tenta di riattivare il template finalità senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_INVALID_ASSOCIATED_ESERVICE_FOR_PUBLICATION_ERROR__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_UNSUSPEND" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_TEMPLATE_ERROR_MESSAGE_10] - Modifica di un'annotazione con un link nel testo
    Given un template finalità in stato DRAFT creato dal Comune di Milano con un'annotazione dell'analisi del rischio già salvata e identificata come "annotazione da modificare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dell'annotazione "annotazione da modificare"
    And l'utente tenta di salvare un link nel testo dell'annotazione "annotazione da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PUT_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_RISK_ANALYSIS_ANSWERS_ANSWER_ID_ANNOTATION" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
