@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per i dati e gli attributi dell’ente

  # POST /tenants/:tenantId/attributes/verified/:attributeId · expirationDateCannotBeInThePast
  Scenario: [ERROR_012] L’operatore autorizzato aggiorna la scadenza della verifica dell’attributo — un attributo verificabile o già verificato per l’ente, con una data di scadenza precedente a oggi
    Given un attributo verificabile o già verificato per l’ente, con una data di scadenza precedente a oggi
    When l’operatore autorizzato aggiorna la scadenza della verifica dell’attributo
    Then il messaggio di errore corrisponde al caso "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED_ATTRIBUTE_ID"

  # POST /tenants/:tenantId/attributes/certified · certifiedAttributeAlreadyAssigned
  Scenario: [ERROR_013] Il certificatore assegna l'attributo "ATTR-1" all'ente "PagoPA" — un attributo certificato già assegnato all’ente destinatario e il certificatore autorizzato
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias |
      | ATTR-1 |
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias |
      | ATTR-1 |
    When il certificatore assegna l'attributo "ATTR-1" all'ente "PagoPA"
    Then il messaggio di errore corrisponde al caso "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED"

  # POST /tenants/:tenantId/attributes/certifiedDiscrete · certifiedDiscreteAttributeAlreadyAssigned
  # Il JSON attuale conserva il vecchio ID. Atteso codice Tenant 005-0032. Il catalogo BE analizzato associa 005-0014: riallineare il mapping e il manifest prima di eseguire.
  Scenario: [ERROR_014] Il certificatore assegna l'attributo certificato discreto "ATTR-1" all'ente "PagoPA" — un attributo certificato già assegnato all’ente destinatario e il certificatore autorizzato
    Given i seguenti attributi discreti creati da un admin del Comune di Milano:
      | alias |
      | ATTR-1 |
    And l'attributo certificato discreto "ATTR-1" già assegnato all'ente "PagoPA"
    When il certificatore assegna l'attributo certificato discreto "ATTR-1" all'ente "PagoPA"
    Then il messaggio di errore corrisponde al caso "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED_DISCRETE"

  # POST /tenants/:tenantId/attributes/verified · expirationDateCannotBeInThePast
  Scenario: [ERROR_015] L’operatore autorizzato verifica l’attributo dell’ente impostando una scadenza — un attributo verificabile o già verificato per l’ente, con una data di scadenza precedente a oggi
    Given un attributo verificabile o già verificato per l’ente, con una data di scadenza precedente a oggi
    When l’operatore autorizzato verifica l’attributo dell’ente impostando una scadenza
    Then il messaggio di errore corrisponde al caso "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED"

  # POST /tenants/delegatedFeatures/update · operationForbidden
  Scenario: [ERROR_016] L’amministratore dell’ente aggiorna la disponibilità alle deleghe — un ente autenticato che non è una Pubblica Amministrazione
    Given un ente autenticato che non è una Pubblica Amministrazione
    When l’amministratore dell’ente aggiorna la disponibilità alle deleghe
    Then il messaggio di errore corrisponde al caso "TENANT_OPERATION_FORBIDDEN"

  # POST /tenants/:tenantId/mails · hyperlinkDetectionError
  Scenario: [ERROR_017] L’operatore autorizzato salva il contatto email dell’ente con un collegamento nel note del contatto email — l’ente e un contatto email di tipo ammesso, con indirizzo valido
    Given l’ente e un contatto email di tipo ammesso, con indirizzo valido
    When l’operatore autorizzato salva il contatto email dell’ente con un collegamento nel note del contatto email
    Then il messaggio di errore corrisponde al caso "TENANT_HYPERLINK_DETECTION_ERROR"
