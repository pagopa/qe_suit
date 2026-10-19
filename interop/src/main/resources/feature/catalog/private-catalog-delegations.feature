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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                    |
      | ES-1  | Comune di Milano | E-service Delegato Alfa |
      | ES-2  | Comune di Milano | E-service Delegato Beta |
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                     |
      | ES-1  | Comune di Milano | E-service Delegante Alfa |
      | ES-2  | Comune di Milano | E-service Delegante Beta |
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome                   |
      | ES-1  | Comune di Milano   | E-service Unione Alfa  |
      | ES-2  | Comune di Pozzallo | E-service Unione Beta  |
      | ES-3  | Sogecap            | E-service Unione Gamma |
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                  |
      | ES-1  | Comune di Milano | E-service Libero Alfa |
      | ES-2  | Comune di Milano | E-service Libero Beta |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    When il Comune di Pozzallo cerca nel catalogo "Libero"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
