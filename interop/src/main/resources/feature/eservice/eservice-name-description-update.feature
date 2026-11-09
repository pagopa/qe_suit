@eservice-name-description-update
@channel:Given=BFF,When=BFF,Then=WEB
Feature: Abilitazione dell'aggiornamento di nome e descrizione senza modificare lo stato della risorsa o delle sue versioni e senza riattivare risorse archiviate

  @work-item:QA-19036
  Scenario Outline: [ESERVICE_NAME_DESCRIPTION_UPDATE_1] L'aggiornamento di nome e descrizione dell'e-service nei vari stati.
  L'aggiornamento va a buon fine quando ad essere modificati sono il nome e la descrizione.

    Given un e-service in stato <stato_descrittore> creato dal Comune di Milano
    When l'utente tenta la modifica dei metadati dell'e-service applicando la variazione su "<campi_modificati>"
    Then la piattaforma salva i metadati richiesti per il descrittore in stato <stato_descrittore> e conserva quelli omessi

    Examples:
      | campi_modificati | stato_descrittore         |
      | solo nome        | bozza                     |
      | solo descrizione | bozza                     |
      | entrambi         | bozza                     |
      | solo nome        | pubblicato                |
      | solo descrizione | pubblicato                |
      | entrambi         | pubblicato                |
      | solo nome        | sospeso                   |
      | solo descrizione | sospeso                   |
      | entrambi         | sospeso                   |
      | solo nome        | deprecato                 |
      | solo descrizione | deprecato                 |
      | entrambi         | deprecato                 |
      | solo nome        | archiviato                |
      | solo descrizione | archiviato                |
      | entrambi         | archiviato                |
      | solo nome        | in attesa di approvazione |
      | solo descrizione | in attesa di approvazione |
      | entrambi         | in attesa di approvazione |
      | solo nome        | in archiviazione          |
      | solo descrizione | in archiviazione          |
      | entrambi         | in archiviazione          |
      | solo nome        | archiviazione sospesa     |
      | solo descrizione | archiviazione sospesa     |
      | entrambi         | archiviazione sospesa     |
