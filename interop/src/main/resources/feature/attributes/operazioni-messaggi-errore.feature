@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante la creazione degli attributi nel registro

  Come amministratore di un ente abilitato alla creazione degli attributi
  Voglio sapere perché la creazione di un attributo è stata rifiutata
  Per scegliere un nome non duplicato o rimuovere i collegamenti dal nome.

  # Per i certificati usare l'identità abilitata alla creazione; per i discreti mantenere gli altri dati validi.
  Background:
    Given un admin appartenente all'erogatore Comune di Milano

  Scenario: [ERROR_093] - Impossibilità di creare un attributo certificato con un nome già presente
  Dato un attributo certificato già presente nel registro
  Quando l'amministratore tenta di creare un altro attributo certificato con lo stesso nome
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
    # Binding BFF negativo mancante; riusare createCertifiedAttribute con il nome dell'attributo registrato.
    When l'erogatore tenta di creare un attributo "certificato" con il nome dell'attributo "CERT-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_CERTIFIED_ATTRIBUTES" in lingua "it"

  Scenario: [ERROR_094] - Impossibilità di creare un attributo certificato discreto con un nome già presente
  Dato un attributo certificato discreto già presente nel registro
  Quando l'amministratore tenta di crearne un altro dello stesso tipo con lo stesso nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti attributi discreti creati da un admin del Comune di Milano:
      | alias  |
      | DISC-1 |
    # Binding BFF negativo mancante; riusare createCertifiedDiscreteAttribute con il nome registrato e dati validi.
    When l'erogatore tenta di creare un attributo "certificato discreto" con il nome dell'attributo "DISC-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_CERTIFIED_DISCRETE_ATTRIBUTES" in lingua "it"

  Scenario: [ERROR_095] - Impossibilità di creare un attributo dichiarato con un nome già presente
  Dato un attributo dichiarato già presente nel registro
  Quando l'amministratore tenta di creare un altro attributo dichiarato con lo stesso nome
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti attributi dichiarati creati da un admin del Comune di Milano:
      | alias  |
      | DECL-1 |
    # Binding BFF negativo mancante; riusare createDeclaredAttribute con il nome dell'attributo registrato.
    When l'erogatore tenta di creare un attributo "dichiarato" con il nome dell'attributo "DECL-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_DECLARED_ATTRIBUTES" in lingua "it"

  Scenario: [ERROR_096] - Impossibilità di creare un attributo verificato con un nome già presente
  Dato un attributo verificato già presente nel registro
  Quando l'amministratore tenta di creare un altro attributo verificato con lo stesso nome
  Allora il sistema rifiuta la creazione e segnala che il nome è già in uso.

    Given i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | VER-1 |
    # Binding BFF negativo mancante; riusare createVerifiedAttribute con il nome dell'attributo registrato.
    When l'erogatore tenta di creare un attributo "verificato" con il nome dell'attributo "VER-1"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_ATTRIBUTE_DUPLICATE__POST_VERIFIED_ATTRIBUTES" in lingua "it"

  Scenario: [ERROR_097] - Rifiuto di un attributo certificato con un link nel nome
  Dato un amministratore abilitato alla creazione degli attributi certificati
  Quando tenta di creare un attributo certificato con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'amministratore a rimuovere il collegamento dal nome.

    # Binding BFF negativo mancante; riusare createCertifiedAttribute con il nome indicato e gli altri dati validi.
    When l'erogatore tenta di creare un attributo "certificato" con il nome "Attributo https://example.org"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_CERTIFIED_ATTRIBUTES" in lingua "it"

  Scenario: [ERROR_098] - Rifiuto di un attributo dichiarato con un link nel nome
  Dato un amministratore abilitato alla creazione degli attributi dichiarati
  Quando tenta di creare un attributo dichiarato con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'amministratore a rimuovere il collegamento dal nome.

    # Binding BFF negativo mancante; riusare createDeclaredAttribute con il nome indicato e gli altri dati validi.
    When l'erogatore tenta di creare un attributo "dichiarato" con il nome "Attributo https://example.org"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_DECLARED_ATTRIBUTES" in lingua "it"

  Scenario: [ERROR_099] - Rifiuto di un attributo verificato con un link nel nome
  Dato un amministratore abilitato alla creazione degli attributi verificati
  Quando tenta di creare un attributo verificato con un collegamento nel nome e gli altri dati validi
  Allora il sistema rifiuta la creazione e invita l'amministratore a rimuovere il collegamento dal nome.

    # Binding BFF negativo mancante; riusare createVerifiedAttribute con il nome indicato e gli altri dati validi.
    When l'erogatore tenta di creare un attributo "verificato" con il nome "Attributo https://example.org"
    Then il messaggio di errore corrisponde al caso "ATTRIBUTE_REGISTRY_HYPERLINK_DETECTION_ERROR__POST_VERIFIED_ATTRIBUTES" in lingua "it"
