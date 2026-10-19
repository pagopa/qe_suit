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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Delegato Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Delegato Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega <stato delega>
    When il Comune di Pozzallo cerca nel catalogo "Delegato" con i seguenti filtri:
      | filtro          | valore   |
      | ruolo di delega | delegato |
    Then l'e-service "ES-1" <esito> tra i risultati della ricerca
    And l'e-service "ES-2" non è presente tra i risultati della ricerca

    Examples:
      | stato delega              | esito          |
      | attiva                    | è presente     |
      | in attesa di accettazione | non è presente |
      | rifiutata                 | non è presente |
      | revocata                  | non è presente |

  Scenario Outline: [PRIVATE_CATALOG_DELEGATIONS_2] Ruolo di delegante: conta solo la delega attiva
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Delegante Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Delegante Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega <stato delega>
    When il Comune di Milano cerca nel catalogo "Delegante" con i seguenti filtri:
      | filtro          | valore    |
      | ruolo di delega | delegante |
    Then l'e-service "ES-1" <esito> tra i risultati della ricerca
    And l'e-service "ES-2" non è presente tra i risultati della ricerca

    Examples:
      | stato delega              | esito          |
      | attiva                    | è presente     |
      | in attesa di accettazione | non è presente |
      | rifiutata                 | non è presente |
      | revocata                  | non è presente |

  Scenario: [PRIVATE_CATALOG_DELEGATIONS_3] Entrambi i ruoli: unione dei risultati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Unione Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Pozzallo con i seguenti dati:
      | nome | E-service Unione Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Unione Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And l'e-service "ES-2" delegato in erogazione dal Comune di Pozzallo a Sogecap con delega attiva
    When il Comune di Pozzallo cerca nel catalogo "Unione" con i seguenti filtri:
      | filtro          | valore               |
      | ruolo di delega | delegato e delegante |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_DELEGATIONS_4] Senza filtro per ruolo di delega nessuna restrizione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Libero Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Libero Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    When il Comune di Pozzallo cerca nel catalogo "Libero"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
