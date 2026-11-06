@purpose-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione delle finalità e delle analisi del rischio

  Come amministratore o valutatore di un ente fruitore
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'operazione non può essere completata
  Per comprendere il problema e correggere i dati o aggiornare la pagina.

  # Feature in attesa dell'implementazione degli step mancanti.
  # Ogni scenario utilizza e-service, finalità e analisi del rischio dedicati.
  # I riferimenti nominati identificano fixture specifiche, non l'ultima entità creata.
  # I Given concorrenti non devono aggiornare la pagina né sostituire l'identità WEB.
  # I due valutatori devono essere utenti distinti, entrambi assegnati alla stessa analisi.
  # La selezione della lingua deve essere verificata nell'interfaccia, anche per l'italiano.
  # Il Then verifica la snackbar visibile e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.
  # Restano da chiarire le note editoriali sui duplicati in creazione ordinaria, inversa e clonazione.
  # Verificare anche la distinzione WEB tra modifica dei dati generali e salvataggio completo della bozza.
  # Non simulare risposte né aggirare controlli UI per raggiungere un messaggio.

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_1] - Rinomina di una finalità derivata da template con nome duplicato
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And la finalità creata è identificata come "finalità esistente"
    And un template finalità pubblicato utilizzabile da PagoPA per l'e-service
    And una finalità in bozza derivata dal template per lo stesso e-service, con nome diverso e identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della finalità "finalità da modificare" derivata dal template
    And l'utente tenta di salvare il nome della "finalità esistente" per la "finalità da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_2] - Modifica dei dati generali di una finalità ordinaria con nome duplicato
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And la finalità creata è identificata come "finalità esistente"
    And un'altra finalità ordinaria in bozza di PagoPA per lo stesso e-service, con nome diverso e identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei soli dati generali della finalità ordinaria "finalità da modificare"
    And l'utente tenta di salvare il nome della "finalità esistente" per la "finalità da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_3] - Modifica dei dati generali di una finalità ordinaria con un link
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità ordinaria in bozza di PagoPA per l'e-service identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica dei soli dati generali della finalità ordinaria "finalità da modificare"
    And l'utente tenta di salvare un link nella descrizione della "finalità da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_HYPERLINK_DETECTION_ERROR__PATCH_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_4] - Modifica di una finalità inversa con nome duplicato
    Given un e-service in erogazione inversa del Comune di Milano con analisi del rischio valida e richiesta di fruizione attiva di PagoPA
    And due finalità inverse di PagoPA per l'e-service con nomi diversi, una esistente e una in bozza identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della finalità inversa "finalità da modificare"
    And l'utente tenta di salvare il nome della finalità inversa esistente per la "finalità da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__PATCH_REVERSE_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_5] - Modifica di una finalità inversa con un link
    Given un e-service in erogazione inversa del Comune di Milano con analisi del rischio valida e richiesta di fruizione attiva di PagoPA
    And una finalità inversa in bozza di PagoPA per l'e-service identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della finalità inversa "finalità da modificare"
    And l'utente tenta di salvare un link nella descrizione della "finalità da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_HYPERLINK_DETECTION_ERROR__PATCH_REVERSE_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_6] - Creazione di una finalità da template con nome duplicato
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And la finalità creata è identificata come "finalità esistente"
    And un template finalità pubblicato utilizzabile da PagoPA per l'e-service
    And i dati predisposti per la nuova finalità da template produrrebbero il nome della "finalità esistente" e soddisfano gli altri requisiti
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di creare una finalità per l'e-service dal template con i dati predisposti
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSE_TEMPLATES_PURPOSE_TEMPLATE_ID_PURPOSES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_7] - Creazione di una finalità ordinaria con nome duplicato
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And la finalità creata è identificata come "finalità esistente"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di creare una finalità ordinaria per l'e-service con il nome della "finalità esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_8] - Salvataggio completo di una bozza con nome duplicato
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And la finalità creata è identificata come "finalità esistente"
    And un'altra finalità ordinaria in bozza di PagoPA per lo stesso e-service, con nome diverso e identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la compilazione completa della bozza della finalità "finalità da modificare"
    And l'utente tenta di salvare la bozza completa con il nome della "finalità esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_9] - Salvataggio completo di una bozza con un link
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità ordinaria in bozza di PagoPA per l'e-service identificata come "finalità da modificare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la compilazione completa della bozza della finalità "finalità da modificare"
    And l'utente tenta di salvare la bozza completa con un link nella descrizione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_HYPERLINK_DETECTION_ERROR__POST_PURPOSES_PURPOSE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_10] - Duplicazione di una finalità con conflitto sul nome risultante
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And la finalità creata è identificata come "finalità da duplicare"
    And un'altra finalità di PagoPA per lo stesso e-service con il nome che risulterebbe dalla duplicazione della "finalità da duplicare"
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di duplicare la "finalità da duplicare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_PURPOSES_PURPOSE_ID_CLONE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_11] - Rifiuto di un'analisi del rischio con un link nella motivazione
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And un valutatore di PagoPA assegnato all'analisi del rischio e autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio per valutarla
    And il valutatore tenta di rifiutare l'analisi del rischio con un link nella motivazione e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_HYPERLINK_DETECTION_ERROR__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_12] - Rifiuto di un'analisi del rischio già approvata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio per valutarla
    Given l'analisi del rischio viene approvata e firmata dall'"altro valutatore"
    When il valutatore tenta di rifiutare l'analisi del rischio senza aggiornare la pagina e con una motivazione valida
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_13] - Rifiuto di un'analisi del rischio già rifiutata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio per valutarla
    Given l'analisi del rischio viene rifiutata dall'"altro valutatore" con una motivazione valida
    When il valutatore tenta di rifiutare l'analisi del rischio senza aggiornare la pagina e con una motivazione valida
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_REJECT" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_14] - Firma di un'analisi del rischio modificata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio per valutarla
    Given l'"altro valutatore" salva una modifica valida all'analisi del rischio senza approvarla né rifiutarla
    When il valutatore tenta di approvare e firmare l'analisi del rischio senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_PURPOSE_METADATA_VERSION_MISMATCH" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_15] - Firma di un'analisi del rischio già approvata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio per valutarla
    Given l'analisi del rischio viene approvata e firmata dall'"altro valutatore"
    When il valutatore tenta di approvare e firmare l'analisi del rischio senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_SIGN" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_16] - Firma di un'analisi del rischio già rifiutata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio per valutarla
    Given l'analisi del rischio viene rifiutata dall'"altro valutatore" con una motivazione valida
    When il valutatore tenta di approvare e firmare l'analisi del rischio senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__POST_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_SIGN" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_17] - Creazione di una finalità inversa con nome duplicato
    Given un e-service in erogazione inversa del Comune di Milano con analisi del rischio valida e richiesta di fruizione attiva di PagoPA
    And una finalità inversa di PagoPA per l'e-service identificata come "finalità inversa esistente"
    And i dati predisposti per la nuova finalità inversa produrrebbero il nome della "finalità inversa esistente" e soddisfano gli altri requisiti
    And un utente autenticato di PagoPA con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente tenta di creare una finalità inversa per l'e-service con i dati predisposti
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_DUPLICATED_PURPOSE_TITLE__POST_REVERSE_PURPOSES" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_18] - Salvataggio di un'analisi del rischio già approvata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio e prepara una modifica valida alle risposte senza salvarla
    Given l'analisi del rischio viene approvata e firmata dall'"altro valutatore"
    When il valutatore tenta di salvare le risposte modificate dell'analisi del rischio senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_RISK_ANALYSIS_ALREADY_APPROVED__PUT_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_FORM" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [PURPOSE_ERROR_MESSAGE_19] - Salvataggio di un'analisi del rischio già rifiutata da un altro valutatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione associata da PagoPA
    And una finalità in bozza di PagoPA per l'e-service con analisi del rischio compilata e ancora da valutare
    And due valutatori distinti di PagoPA assegnati all'analisi del rischio, identificati come "valutatore corrente" e "altro valutatore"
    And il "valutatore corrente" è autenticato
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And il valutatore apre l'analisi del rischio e prepara una modifica valida alle risposte senza salvarla
    Given l'analisi del rischio viene rifiutata dall'"altro valutatore" con una motivazione valida
    When il valutatore tenta di salvare le risposte modificate dell'analisi del rischio senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "PURPOSE_RISK_ANALYSIS_ALREADY_REJECTED__PUT_PURPOSES_PURPOSE_ID_RISK_ANALYSIS_FORM" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
