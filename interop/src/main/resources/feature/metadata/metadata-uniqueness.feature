@PIN-11002
Feature: Rispetto dell’unicità dei nomi e titoli delle risorse
  Come utente autorizzato
  Voglio rinominare una risorsa rispettando i vincoli di unicità
  Per evitare collisioni senza vietare omonimie ammesse.

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_UNIQUENESS_1] - Rifiuto della rinomina in conflitto nell’ambito di unicità
  Dato due risorse nello stesso ambito di unicità con nomi distinti,
  quando l’attore rinomina una risorsa usando il nome dell’altra e una descrizione valida,
  allora la modifica è respinta integralmente lasciando intatte entrambe le risorse.

    Given due risorse "R-1" e "R-2" di tipo <risorsa> nel medesimo ambito di unicità
    When un admin del Comune di Milano richiede di assegnare alla risorsa "R-1" il nome di "R-2" e una nuova descrizione
    Then la modifica della risorsa "R-1" viene rifiutata
    And le risorse "R-1" e "R-2" conservano tutti i dati iniziali

    Examples:
      | risorsa            |
      | e-service          |
      | finalità           |
      | template e-service |
      | template finalità  |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_UNIQUENESS_2] - Unicità non aggirabile con differenze di maiuscole e minuscole
  Dato due risorse nello stesso ambito di unicità,
  quando l’attore richiede un nome che coincide con quello dell’altra salvo le maiuscole,
  allora la rinomina viene rifiutata senza applicare anche la descrizione valida.

    Given due risorse "R-1" e "R-2" di tipo <risorsa> nel medesimo ambito di unicità
    When un admin del Comune di Milano richiede per "R-1" il nome di "R-2" cambiandone soltanto le maiuscole e una nuova descrizione
    Then la modifica della risorsa "R-1" viene rifiutata
    And le risorse "R-1" e "R-2" conservano tutti i dati iniziali

    Examples:
      | risorsa            |
      | e-service          |
      | finalità           |
      | template e-service |
      | template finalità  |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [METADATA_UNIQUENESS_3] - Omonimia consentita al di fuori dell’ambito di unicità
  Dato due risorse in ambiti di unicità distinti,
  quando l’attore assegna alla prima il nome della seconda,
  allora il sistema consente la rinomina e non modifica l’altra risorsa.

    Given due risorse "R-1" e "R-2" di tipo <risorsa> in ambiti di unicità distinti
    When un admin del Comune di Milano richiede di assegnare alla risorsa "R-1" il nome di "R-2"
    Then la rinomina della risorsa "R-1" viene accettata
    And la risorsa "R-2" conserva tutti i dati iniziali

    Examples:
      | risorsa            |
      | e-service          |
      | finalità           |
      | template e-service |
      | template finalità  |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [METADATA_UNIQUENESS_4] - I client possono avere lo stesso nome
  Dato due client appartenenti al medesimo ente,
  quando l’amministratore assegna a un client il nome dell’altro,
  allora la rinomina riesce senza introdurre un vincolo di unicità.

    Given due client "CL-1" e "CL-2" appartenenti al Comune di Milano
    When un admin del Comune di Milano richiede di assegnare al client "CL-1" il nome di "CL-2"
    Then la rinomina del client "CL-1" viene accettata
    And il client "CL-2" conserva tutti i dati iniziali

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [METADATA_UNIQUENESS_5] - I portachiavi possono avere lo stesso nome
  Dato due portachiavi del medesimo ente,
  quando l’amministratore assegna a un portachiavi il nome dell’altro,
  allora la rinomina riesce senza introdurre un vincolo di unicità.

    Given due portachiavi erogatore "K-1" e "K-2" del Comune di Milano
    When un admin del Comune di Milano richiede di assegnare al portachiavi "K-1" il nome di "K-2"
    Then la rinomina del portachiavi "K-1" viene accettata
    And il portachiavi "K-2" conserva tutti i dati iniziali

