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
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano     |
      | nome                 | E-service Erogo Alfa |
      | descrizione          | E-service.           |
      | stato della versione | pubblicata           |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Pozzallo   |
      | nome                 | E-service Erogo Beta |
      | descrizione          | E-service.           |
      | stato della versione | pubblicata           |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Sogecap               |
      | nome                 | E-service Erogo Gamma |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
    When PagoPA cerca nel catalogo la parola "Erogo" con i seguenti filtri:
      | filtro    | valore           |
      | erogatore | Comune di Milano |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_2] Più erogatori in alternativa
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano     |
      | nome                 | E-service Duplo Alfa |
      | descrizione          | E-service.           |
      | stato della versione | pubblicata           |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Pozzallo   |
      | nome                 | E-service Duplo Beta |
      | descrizione          | E-service.           |
      | stato della versione | pubblicata           |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Sogecap               |
      | nome                 | E-service Duplo Gamma |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
    When PagoPA cerca nel catalogo la parola "Duplo" con i seguenti filtri:
      | filtro    | valore                                |
      | erogatore | Comune di Milano e Comune di Pozzallo |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service predisposto "ES-3"

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_3] Elenco di erogatori vuoto: nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano         |
      | nome                 | E-service Perimetro Alfa |
      | descrizione          | E-service.               |
      | stato della versione | pubblicata               |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Pozzallo       |
      | nome                 | E-service Perimetro Beta |
      | descrizione          | E-service.               |
      | stato della versione | pubblicata               |
    When PagoPA cerca nel catalogo la parola "Perimetro" con i seguenti filtri:
      | filtro    | valore       |
      | erogatore | elenco vuoto |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_4] Erogatore senza e-service corrispondenti
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano      |
      | nome                 | E-service Nessuno Uno |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
    When PagoPA cerca nel catalogo la parola "Nessuno" con i seguenti filtri:
      | filtro    | valore             |
      | erogatore | Comune di Pozzallo |
    Then tra i risultati non compare l'e-service predisposto "ES-1"
