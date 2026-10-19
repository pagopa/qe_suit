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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore          | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano   | Servizio Erogo Alfa  | Servizio.   | pubblicata           |
      | ES-2  | Comune di Pozzallo | Servizio Erogo Beta  | Servizio.   | pubblicata           |
      | ES-3  | Sogecap            | Servizio Erogo Gamma | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Erogo" tra i servizi dell'erogatore Comune di Milano
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_2] Più erogatori in alternativa
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore          | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano   | Servizio Duplo Alfa  | Servizio.   | pubblicata           |
      | ES-2  | Comune di Pozzallo | Servizio Duplo Beta  | Servizio.   | pubblicata           |
      | ES-3  | Sogecap            | Servizio Duplo Gamma | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Duplo" tra i servizi degli erogatori Comune di Milano e Comune di Pozzallo
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service predisposto "ES-3"

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_3] Elenco di erogatori vuoto: nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore          | nome                    | descrizione | stato della versione |
      | ES-1  | Comune di Milano   | Servizio Perimetro Alfa | Servizio.   | pubblicata           |
      | ES-2  | Comune di Pozzallo | Servizio Perimetro Beta | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Perimetro" con l'elenco degli erogatori vuoto
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_PRODUCER_FILTER_4] Erogatore senza e-service corrispondenti
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Nessuno Uno | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Nessuno" tra i servizi dell'erogatore Comune di Pozzallo
    Then la ricerca termina senza errori
    And tra i risultati non compare l'e-service predisposto "ES-1"
