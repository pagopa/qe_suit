@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi per la gestione del registro degli attributi

  # POST /certifiedAttributes · attributeDuplicate
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_093] L'erogatore crea un attributo "certificato" con il nome dell'attributo "ATTR-1" — un attributo omonimo dello stesso tipo nel registro
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias |
      | ATTR-1 |
    When l'erogatore crea un attributo "certificato" con il nome dell'attributo "ATTR-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_CERTIFIED_ATTRIBUTES"

  # POST /certifiedDiscreteAttributes · attributeDuplicate
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_094] L'erogatore crea un attributo "certificato discreto" con il nome dell'attributo "ATTR-1" — un attributo omonimo dello stesso tipo nel registro
    Given i seguenti attributi discreti creati da un admin del Comune di Milano:
      | alias |
      | ATTR-1 |
    When l'erogatore crea un attributo "certificato discreto" con il nome dell'attributo "ATTR-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_CERTIFIED_DISCRETE_ATTRIBUTES"

  # POST /declaredAttributes · attributeDuplicate
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_095] L'erogatore crea un attributo "dichiarato" con il nome dell'attributo "ATTR-1" — un attributo omonimo dello stesso tipo nel registro
    Given i seguenti attributi dichiarati creati da un admin del Comune di Milano:
      | alias |
      | ATTR-1 |
    When l'erogatore crea un attributo "dichiarato" con il nome dell'attributo "ATTR-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_DECLARED_ATTRIBUTES"

  # POST /verifiedAttributes · attributeDuplicate
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_096] L'erogatore crea un attributo "verificato" con il nome dell'attributo "ATTR-1" — un attributo omonimo dello stesso tipo nel registro
    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | ATTR-1 |
    When l'erogatore crea un attributo "verificato" con il nome dell'attributo "ATTR-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_VERIFIED_ATTRIBUTES"

  # POST /certifiedAttributes · hyperlinkDetectionError
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_097] L'erogatore crea un attributo "certificato" con il nome "Attributo https://example.org" — l’operatore abilitato alla creazione con dati altrimenti validi
    Given un admin appartenente all'erogatore Comune di Milano
    When l'erogatore crea un attributo "certificato" con il nome "Attributo https://example.org"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_CERTIFIED_ATTRIBUTES"

  # POST /declaredAttributes · hyperlinkDetectionError
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_098] L'erogatore crea un attributo "dichiarato" con il nome "Attributo https://example.org" — l’operatore abilitato alla creazione con dati altrimenti validi
    Given un admin appartenente all'erogatore Comune di Milano
    When l'erogatore crea un attributo "dichiarato" con il nome "Attributo https://example.org"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_DECLARED_ATTRIBUTES"

  # POST /verifiedAttributes · hyperlinkDetectionError
  # Per gli attributi certificati usare l’identità abilitata dal fixture setup e, per il discreto, un seed con valori ammessi.
  Scenario: [ERROR_099] L'erogatore crea un attributo "verificato" con il nome "Attributo https://example.org" — l’operatore abilitato alla creazione con dati altrimenti validi
    Given un admin appartenente all'erogatore Comune di Milano
    When l'erogatore crea un attributo "verificato" con il nome "Attributo https://example.org"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_VERIFIED_ATTRIBUTES"
