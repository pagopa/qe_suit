@private-catalog
@private-catalog-producer-filter
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtro per erogatore nel catalogo privato

  Come fruitore
  Voglio restringere il catalogo a uno o più erogatori
  Per vedere solo gli e-service pubblicati da loro.

  Assunzioni:
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook;
  - ogni scenario usa una parola chiave improbabile per delimitare il perimetro dei risultati.

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_1] Un solo erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome                  |
      | ES-1  | Comune di Milano   | E-service Erogo Alfa  |
      | ES-2  | Comune di Pozzallo | E-service Erogo Beta  |
      | ES-3  | Sogecap            | E-service Erogo Gamma |
    When PagoPA cerca nel catalogo "Erogo" con i seguenti filtri:
      | filtro    | valore           |
      | erogatore | Comune di Milano |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_2] Più erogatori in alternativa
    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome                  |
      | ES-1  | Comune di Milano   | E-service Duplo Alfa  |
      | ES-2  | Comune di Pozzallo | E-service Duplo Beta  |
      | ES-3  | Sogecap            | E-service Duplo Gamma |
    When PagoPA cerca nel catalogo "Duplo" con i seguenti filtri:
      | filtro    | valore                                |
      | erogatore | Comune di Milano e Comune di Pozzallo |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_3] Elenco di erogatori vuoto: nessuna restrizione
    Given i seguenti e-service pubblicati:
      | alias | erogatore          | nome                     |
      | ES-1  | Comune di Milano   | E-service Perimetro Alfa |
      | ES-2  | Comune di Pozzallo | E-service Perimetro Beta |
    When PagoPA cerca nel catalogo "Perimetro" con i seguenti filtri:
      | filtro    | valore       |
      | erogatore | elenco vuoto |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_4] Erogatore senza e-service corrispondenti
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                  |
      | ES-1  | Comune di Milano | E-service Nessuno Uno |
    When PagoPA cerca nel catalogo "Nessuno" con i seguenti filtri:
      | filtro    | valore             |
      | erogatore | Comune di Pozzallo |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca
