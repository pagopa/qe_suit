@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la creazione e la modifica dei template e-service

  Come creatore di template e-service
  Voglio sapere perché la creazione o la modifica di un template o di un'analisi del rischio sono state rifiutate
  Per scegliere un nome non duplicato, evitare conflitti con le istanze o correggere i dati.

  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_121] - Impossibilità di creare un template con un nome già presente
  Dato un template e-service dell'ente creatore già presente
  Quando l'erogatore tenta di creare un altro template con lo stesso nome
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome               | tecnologia |
      | T-1   | Comune di Milano | Catalogo duplicato | REST       |
    When l'erogatore tenta di creare un template e-service duplicando il nome del template "T-1"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__POST_ESERVICES_TEMPLATES" in lingua "it"

  Scenario: [ERROR_125] - Impossibilità di aggiungere un'analisi del rischio con un nome già usato nel template
  Dato un template e-service in ricezione con un'analisi del rischio completa
  Quando l'erogatore tenta di aggiungere un'altra analisi con lo stesso nome allo stesso template
  Allora il sistema rifiuta l'aggiunta e segnala che il nome dell'analisi è già in uso.

    Given un template e-service in ricezione con una versione in bozza
    # Operazione BFF e binding di setup mancanti per le analisi del rischio del template.
    And un'analisi del rischio completa associata al template
    When l'erogatore tenta di aggiungere al template un'analisi del rischio con il nome dell'analisi esistente
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_RISK_ANALYSIS_NAME_DUPLICATE" in lingua "it"

  Scenario: [ERROR_126] - Impossibilità di rinominare un template con il nome di un altro template
  Dati due template e-service distinti dello stesso ente creatore con nomi diversi
  Quando l'erogatore tenta di assegnare al secondo il nome del primo
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                  | tecnologia |
      | T-1   | Comune di Milano | Catalogo esistente    | REST       |
      | T-2   | Comune di Milano | Catalogo da rinominare | REST       |
    # Binding BFF negativo mancante; riusare updateTemplateName con il nome effettivo di T-1.
    When l'erogatore tenta di rinominare il template "T-2" con il nome del template "T-1"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_NAME_UPDATE" in lingua "it"

  Scenario: [ERROR_128] - Rifiuto della creazione di un template con un link nel nome
  Dato un amministratore abilitato alla creazione dei template e-service
  Quando l'erogatore tenta di creare un template con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'erogatore a rimuovere il collegamento.

    # Binding BFF negativo mancante; riusare createEServiceTemplate con il nome indicato.
    When l'erogatore tenta di creare un template e-service con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES" in lingua "it"

  Scenario: [ERROR_129] - Rifiuto della modifica di una versione in bozza con un link nella descrizione
  Dato un template e-service con una versione in bozza
  Quando l'erogatore tenta di inserire un collegamento nella descrizione della versione
  Allora il sistema rifiuta la modifica e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | alias | tecnologia | stato | interfaccia               |
      | T-1   | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding BFF negativo mancante; riusare updateDraftVersion senza attendere il successo.
    When l'erogatore tenta di modificare la descrizione della versione 1 del template "T-1" in "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_VERSIONS_E_SERVICE_TEMPLATE_VERSION_ID" in lingua "it"

  Scenario: [ERROR_132] - Rifiuto di un link nel destinatario di un template pubblicato
  Dato un template e-service pubblicato
  Quando l'erogatore tenta di inserire un collegamento nel campo che descrive a chi è rivolto il template
  Allora il sistema rifiuta la modifica e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | alias | tecnologia | stato     | interfaccia               |
      | T-1   | REST       | PUBLISHED | openapi3-riferimenti.json |
    # Binding BFF negativo parametrico mancante; usare updateTemplateIntendedTarget per il campo destinatario.
    When l'erogatore tenta di aggiornare il campo "destinatario" del template "T-1" con il valore "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_INTENDED_TARGET_UPDATE" in lingua "it"

  Scenario: [ERROR_133] - Rifiuto di un link nella descrizione di un template pubblicato
  Dato un template e-service pubblicato
  Quando l'erogatore tenta di inserire un collegamento nella descrizione del template
  Allora il sistema rifiuta la modifica e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | alias | tecnologia | stato     | interfaccia               |
      | T-1   | REST       | PUBLISHED | openapi3-riferimenti.json |
    # Binding BFF negativo parametrico mancante; usare updateTemplateDescription per il campo descrizione.
    When l'erogatore tenta di aggiornare il campo "descrizione" del template "T-1" con il valore "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_DESCRIPTION_UPDATE" in lingua "it"

  Scenario: [ERROR_134] - Rifiuto di un link nel nome di un template pubblicato
  Dato un template e-service pubblicato
  Quando l'erogatore tenta di inserire un collegamento nel nome del template
  Allora il sistema rifiuta la modifica e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | alias | tecnologia | stato     | interfaccia               |
      | T-1   | REST       | PUBLISHED | openapi3-riferimenti.json |
    # Binding BFF negativo parametrico mancante; usare updateTemplateName per il campo nome.
    When l'erogatore tenta di aggiornare il campo "nome" del template "T-1" con il valore "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__POST_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID_NAME_UPDATE" in lingua "it"

  # NON REPLICABILE A FRONTEND
  Scenario: [ERROR_135] - Rifiuto della modifica dei dati generali con il nome di un altro template
  Dati due template e-service distinti dello stesso ente creatore con nomi diversi
  Quando l'erogatore tenta di assegnare alla bozza il nome del template esistente modificandone i dati generali
  Allora il sistema rifiuta la modifica e segnala che il nome è già in uso.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome               | tecnologia |
      | T-1   | Comune di Milano | Catalogo esistente | REST       |
    And un template e-service preparato con la seguente configurazione:
      | alias | tecnologia | stato | interfaccia               |
      | T-2   | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding negativo mancante; verificare il contratto PATCH di rilascio: updateEServiceTemplate usa POST.
    # Non rimappare questo caso su updateTemplateName o sull'aggiornamento POST.
    When l'erogatore tenta di modificare i dati generali del template "T-2" con il nome del template "T-1"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_ESERVICE_TEMPLATE_DUPLICATE__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID" in lingua "it"

  Scenario: [ERROR_136] - Rifiuto della modifica del nome che provocherebbe un conflitto con un e-service
  Dato un template pubblicato con un'istanza e un e-service distinto dello stesso erogatore dell'istanza
  Quando il creatore tenta di rinominare il template facendo coincidere il nome derivato dell'istanza con quello dell'e-service
  Allora il sistema rifiuta la modifica e segnala il conflitto sul nome dell'istanza.

    # Non riproducibile sul branch PIN-10967_AIskill_and_markdown (a8095d9) con questo mapping:
    # PATCH aggiorna solo template interamente in bozza, dai quali non si possono creare istanze.
    # instanceNameConflict viene controllato dalla rinomina di un template pubblicato (name/update).
    # ID e mapping JSON mantenuti su indicazione dell'utente; non sostituire l'operazione sotto test.
    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | T-1   | Comune di Milano | Template di origine | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso |
      | ES-1  | T-1      | Istanza  |
    # Binding di setup mancante; predisporre un nome univoco del template e il conflitto sul nome derivato.
    # Riusare createEService con il nome effettivo derivato, senza randomizzarlo ulteriormente.
    And un nuovo nome per il template "T-1" non usato da altri template e un e-service distinto con il nome derivato dell'istanza "ES-1" dopo la rinomina
    # Binding negativo mancante; verificare il contratto PATCH di rilascio: updateEServiceTemplate usa POST.
    # Non rimappare questo caso su updateTemplateName o sull'aggiornamento POST.
    When l'erogatore tenta di modificare i dati generali del template "T-1" con il nuovo nome predisposto
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_INSTANCE_NAME_CONFLICT__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID" in lingua "it"

  Scenario: [ERROR_137] - Rifiuto della modifica dei dati generali di un template con un link nel nome
  Dato un template e-service con una versione in bozza
  Quando l'erogatore tenta di modificarne i dati generali inserendo un collegamento nel nome e lasciando validi gli altri dati
  Allora il sistema rifiuta la modifica e invita l'erogatore a rimuovere il collegamento.

    Given un template e-service preparato con la seguente configurazione:
      | alias | tecnologia | stato | interfaccia               |
      | T-1   | REST       | DRAFT | openapi3-riferimenti.json |
    # Binding negativo mancante; verificare il contratto PATCH di rilascio: updateEServiceTemplate usa POST.
    # Non rimappare questo caso su updateTemplateName o sull'aggiornamento POST; mantenere gli altri dati validi.
    When l'erogatore tenta di modificare i dati generali del template "T-1" con il nome "Valore https://example.org"
    Then il messaggio di errore corrisponde al caso "ESERVICE_TEMPLATE_HYPERLINK_DETECTION_ERROR__PATCH_ESERVICES_TEMPLATES_E_SERVICE_TEMPLATE_ID" in lingua "it"
