@tenant-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nella gestione dell'ente e dei suoi attributi

  Come amministratore di un ente o ente certificatore
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un'operazione non può essere completata
  Per comprendere il problema e sapere come proseguire.

  Scenario Outline: [TENANT_ERROR_MESSAGE_1] - Assegnazione di un attributo certificato già posseduto dall'ente
    Given un admin del Comune di Milano crea un attributo certificato
    And l'attributo certificato creato è identificato come "certificato da assegnare"
    And PagoPA non possiede l'attributo "certificato da assegnare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'assegnazione a PagoPA dell'attributo "certificato da assegnare"
    Given l'attributo "certificato da assegnare" viene assegnato a PagoPA da un'altra sessione del Comune di Milano
    When l'utente tenta di confermare l'assegnazione dell'attributo "certificato da assegnare" a PagoPA senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED" in lingua "<lingua>" con i valori delle fixture:
      | placeholder   | valore                                                    |
      | attributeName | nome dell'attributo "certificato da assegnare"              |

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [TENANT_ERROR_MESSAGE_2] - Assegnazione di un attributo certificato discreto già posseduto dall'ente
    Given un admin del Comune di Milano crea un attributo certificato discreto identificato come "discreto da assegnare"
    And PagoPA non possiede l'attributo "discreto da assegnare"
    And un valore numerico valido per l'attributo "discreto da assegnare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'assegnazione a PagoPA dell'attributo "discreto da assegnare" e inserisce il valore numerico predisposto
    Given l'attributo "discreto da assegnare" viene assegnato a PagoPA con il valore numerico predisposto da un'altra sessione del Comune di Milano
    When l'utente tenta di confermare l'assegnazione dell'attributo "discreto da assegnare" a PagoPA senza aggiornare la pagina
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "TENANT_CERTIFIED_ATTRIBUTE_ALREADY_ASSIGNED__POST_TENANTS_TENANT_ID_ATTRIBUTES_CERTIFIED_DISCRETE" in lingua "<lingua>" con i valori delle fixture:
      | placeholder   | valore                                      |
      | attributeName | nome dell'attributo "discreto da assegnare"  |

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [TENANT_ERROR_MESSAGE_3] - Verifica di un attributo con una data di scadenza passata
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale e un attributo verificato richiesto identificato come "requisito da verificare"
    And una richiesta di fruizione in stato PENDING per l'e-service, creata da PagoPA
    And il Comune di Milano non ha ancora verificato per PagoPA l'attributo "requisito da verificare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la verifica dell'attributo "requisito da verificare" nella richiesta di fruizione ricevuta
    And l'utente tenta di confermare la verifica dell'attributo "requisito da verificare" con scadenza ieri e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [TENANT_ERROR_MESSAGE_4] - Aggiornamento della scadenza di un attributo verificato con una data passata
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale e un attributo verificato richiesto identificato come "requisito verificato"
    And una richiesta di fruizione in stato PENDING per l'e-service, creata da PagoPA
    And il Comune di Milano ha verificato per PagoPA l'attributo "requisito verificato" con scadenza futura
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della scadenza dell'attributo "requisito verificato" nella richiesta di fruizione ricevuta
    And l'utente tenta di salvare la scadenza dell'attributo "requisito verificato" impostandola a ieri e lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "TENANT_EXPIRATION_DATE_CANNOT_BE_IN_THE_PAST__POST_TENANTS_TENANT_ID_ATTRIBUTES_VERIFIED_ATTRIBUTE_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [TENANT_ERROR_MESSAGE_5] - Salvataggio di un indirizzo email con un link nelle note
    Given un utente autenticato del Comune di Milano con ruolo admin
    And un indirizzo email valido dedicato allo scenario e non ancora presente tra i contatti del Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiunta di un indirizzo email nei contatti del proprio ente
    And l'utente tenta di salvare l'indirizzo email predisposto con un link nel campo Note e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "TENANT_HYPERLINK_DETECTION_ERROR" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [TENANT_ERROR_MESSAGE_6] - Aggiornamento della disponibilità alle deleghe da parte di un ente non pubblico
    Given un utente autenticato con ruolo admin di un tenant di tipo PRIVATE
    And l'ente corrente non è una Pubblica Amministrazione e non è disponibile a ricevere deleghe in erogazione
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la gestione della disponibilità alle deleghe del proprio ente
    And l'utente tenta di rendere il proprio ente disponibile a ricevere deleghe in erogazione
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "TENANT_OPERATION_FORBIDDEN" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
