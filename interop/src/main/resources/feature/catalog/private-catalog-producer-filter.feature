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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Erogo Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Pozzallo con i seguenti dati:
      | nome | E-service Erogo Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Erogo Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Erogo" con i seguenti filtri:
      | filtro    | valore           |
      | erogatore | Comune di Milano |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_2] Più erogatori in alternativa
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Duplo Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Pozzallo con i seguenti dati:
      | nome | E-service Duplo Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Duplo Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Duplo" con i seguenti filtri:
      | filtro    | valore                                |
      | erogatore | Comune di Milano e Comune di Pozzallo |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_3] Elenco di erogatori vuoto: nessuna restrizione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Perimetro Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Pozzallo con i seguenti dati:
      | nome | E-service Perimetro Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    When PagoPA cerca nel catalogo "Perimetro" con i seguenti filtri:
      | filtro    | valore       |
      | erogatore | elenco vuoto |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_4] Erogatore senza e-service corrispondenti
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Nessuno Uno |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    When PagoPA cerca nel catalogo "Nessuno" con i seguenti filtri:
      | filtro    | valore             |
      | erogatore | Comune di Pozzallo |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca
