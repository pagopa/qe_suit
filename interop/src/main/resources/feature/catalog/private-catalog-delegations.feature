@private-catalog
@private-catalog-delegations
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtro per ruolo di delega nel catalogo privato

  Come ente
  Voglio vedere nel catalogo gli e-service per cui sono delegato o che ho delegato
  Per distinguere il mio ruolo nelle deleghe di erogazione.

  Assunzioni:
  - contano solo le deleghe di erogazione (non quelle di fruizione) e solo nello stato attivo;
  - se si indicano più ruoli di delega, i risultati sono l'unione (OR) dei due insiemi;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  Scenario Outline: [PRIVATE_CATALOG_DELEGATIONS_1] Ruolo di delegato: conta solo la delega attiva
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Delegato Alfa |
      | descrizione | E-service.              |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Delegato Beta |
      | descrizione | E-service.              |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And il Comune di Milano ha delegato l'erogazione dell'e-service "ES-1" al Comune di Pozzallo con delega <stato delega>
    When Comune di Pozzallo cerca nel catalogo la parola "Delegato" con i seguenti filtri:
      | filtro          | valore   |
      | ruolo di delega | delegato |
    Then tra i risultati <esito> l'e-service "ES-1"
    And tra i risultati non compare l'e-service "ES-2"

    Examples:
      | stato delega              | esito       |
      | attiva                    | compare     |
      | in attesa di accettazione | non compare |
      | rifiutata                 | non compare |
      | revocata                  | non compare |

  Scenario Outline: [PRIVATE_CATALOG_DELEGATIONS_2] Ruolo di delegante: conta solo la delega attiva
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Delegante Alfa |
      | descrizione | E-service.               |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Delegante Beta |
      | descrizione | E-service.               |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And il Comune di Milano ha delegato l'erogazione dell'e-service "ES-1" al Comune di Pozzallo con delega <stato delega>
    When Comune di Milano cerca nel catalogo la parola "Delegante" con i seguenti filtri:
      | filtro          | valore    |
      | ruolo di delega | delegante |
    Then tra i risultati <esito> l'e-service "ES-1"
    And tra i risultati non compare l'e-service "ES-2"

    Examples:
      | stato delega              | esito       |
      | attiva                    | compare     |
      | in attesa di accettazione | non compare |
      | rifiutata                 | non compare |
      | revocata                  | non compare |

  Scenario: [PRIVATE_CATALOG_DELEGATIONS_3] Entrambi i ruoli: unione dei risultati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Unione Alfa |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Pozzallo con la seguente configurazione:
      | nome        | E-service Unione Beta |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto da Sogecap con la seguente configurazione:
      | nome        | E-service Unione Gamma |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    And il Comune di Milano ha delegato l'erogazione dell'e-service "ES-1" al Comune di Pozzallo con delega attiva
    And il Comune di Pozzallo ha delegato l'erogazione dell'e-service "ES-2" a Sogecap con delega attiva
    When Comune di Pozzallo cerca nel catalogo la parola "Unione" con i seguenti filtri:
      | filtro          | valore               |
      | ruolo di delega | delegato e delegante |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service "ES-3"

  Scenario: [PRIVATE_CATALOG_DELEGATIONS_4] Senza filtro per ruolo di delega nessuna restrizione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Libero Alfa |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Libero Beta |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And il Comune di Milano ha delegato l'erogazione dell'e-service "ES-1" al Comune di Pozzallo con delega attiva
    When Comune di Pozzallo cerca nel catalogo la parola "Libero"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
