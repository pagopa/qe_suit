@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la verifica della client assertion

  Come amministratore di un ente
  Voglio sapere perché la verifica di una client assertion è stata rifiutata
  Per controllare che il client appartenga all'ente per cui sto operando.

  Scenario: [ERROR_180] - Impossibilità di verificare la client assertion di un client di un altro ente
  Dato un client di fruizione del Comune di Milano associato a una finalità attiva e una client assertion valida per quella finalità
  Quando un amministratore di PagoPA tenta di verificarla
  Allora il sistema rifiuta la verifica e segnala che l'ente non è autorizzato.

    Given un EService creato dal Comune di Pozzallo con una richiesta di fruizione e una finalità associate dal Comune di Milano
    And un client CONSUMER creato dal Comune di Milano, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And una client assertion valida generata usando il client creato e la finalità creata
    And un admin appartenente al fruitore PagoPA
    # Binding esistente; manca l'adattatore BFF di DebugClientAssertionGateway per il rifiuto atteso.
    When l'utente inoltra la richiesta di validazione specificando client assertion creata e client creato
    Then il messaggio di errore corrisponde al caso "BFF_TENANT_NOT_ALLOWED"
