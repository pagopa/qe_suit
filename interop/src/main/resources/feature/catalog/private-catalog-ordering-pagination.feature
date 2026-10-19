@private-catalog
@private-catalog-ordering-pagination
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Ordinamento e paginazione nel catalogo privato

  Come fruitore
  Voglio che i risultati siano ordinati e paginati in modo coerente
  Per scorrere il catalogo senza perdere né duplicare e-service.

  Assunzioni:
  - gli e-service della tabella sono creati nell'ordine delle righe: ES-1 è il più vecchio;
  - ogni scenario usa una parola chiave improbabile, così il perimetro dei risultati è controllato
  e il totale e l'ordine si possono verificare in modo esatto;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_1] Ordinamento predefinito: dal più recente
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ordine Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ordine Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ordine Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Ordine"
    Then tra i risultati della ricerca sono presenti, in quest'ordine, i seguenti e-service:
      | alias |
      | ES-3  |
      | ES-2  |
      | ES-1  |

  Scenario Outline: [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Sorte Beta |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Sorte Gamma |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Sorte Alfa |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Sorte" con i seguenti filtri:
      | filtro      | valore        |
      | ordinamento | <ordinamento> |
    Then tra i risultati della ricerca sono presenti, in quest'ordine, i seguenti e-service:
      | alias     |
      | <primo>   |
      | <secondo> |
      | <terzo>   |

    Examples:
      | ordinamento                   | primo | secondo | terzo |
      | nome crescente                | ES-3  | ES-1    | ES-2  |
      | nome decrescente              | ES-2  | ES-1    | ES-3  |
      | data di creazione crescente   | ES-1  | ES-2    | ES-3  |
      | data di creazione decrescente | ES-3  | ES-2    | ES-1  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_3] Con una parola chiave la rilevanza precede l'ordinamento richiesto
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | Zeta Quarzo |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | Alfa Pratiche |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    When PagoPA cerca nel catalogo "Quarzo" con i seguenti filtri:
      | filtro      | valore         |
      | ordinamento | nome crescente |
    Then tra i risultati della ricerca sono presenti, in quest'ordine, i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_4] Pagine consecutive senza duplicati e senza omissioni
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Pagina Uno |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Pagina Due |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Pagina Tre |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    And l'e-service "ES-4" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Pagina Quattro |
    And la versione 1 dell'e-service "ES-4" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-4" pubblicata
    And l'e-service "ES-5" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Pagina Cinque |
    And la versione 1 dell'e-service "ES-5" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-5" pubblicata
    When PagoPA cerca nel catalogo "Pagina" con i seguenti filtri:
      | filtro               | valore |
      | risultati per pagina | 2      |
    Then le pagine contengono i seguenti risultati, con totale dichiarato pari alla loro somma:
      | pagina | risultati |
      | 1      | 2         |
      | 2      | 2         |
      | 3      | 1         |
    And nessun e-service compare in più di una pagina
    And le pagine contengono complessivamente i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
      | ES-4  |
      | ES-5  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_5] Pagina oltre l'ultima
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Oltre Uno |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Oltre Due |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    When PagoPA cerca nel catalogo "Oltre" con i seguenti filtri:
      | filtro                  | valore |
      | a partire dal risultato | 10     |
      | risultati per pagina    | 2      |
    Then la ricerca termina senza errori
    And la pagina è vuota e il totale dichiarato è 2

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_6] Il totale tiene conto dei filtri applicati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Conta Uno |
      | Signal Hub | attivo              |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Conta Due |
      | Signal Hub | non attivo          |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Conta Tre |
      | Signal Hub | attivo              |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    And l'e-service "ES-4" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Conta Quattro |
      | Signal Hub | non impostato           |
    And la versione 1 dell'e-service "ES-4" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-4" pubblicata
    And l'e-service "ES-5" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Conta Cinque |
      | Signal Hub | attivo                 |
    And la versione 1 dell'e-service "ES-5" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-5" pubblicata
    When PagoPA cerca nel catalogo "Conta" con i seguenti filtri:
      | filtro               | valore |
      | Signal Hub           | attivo |
      | risultati per pagina | 2      |
    Then le pagine contengono i seguenti risultati, con totale dichiarato pari alla loro somma:
      | pagina | risultati |
      | 1      | 2         |
      | 2      | 1         |
    And nessun e-service compare in più di una pagina
    And le pagine contengono complessivamente i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-3  |
      | ES-5  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_7] Un e-service con molte relazioni compare una sola volta
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Molti Nodi |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And la versione 2 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 2 dell'e-service "ES-1" pubblicata
    And la versione 3 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 3 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And l'e-service "ES-1" sottoscritto da PagoPA con richiesta di fruizione attiva
    When PagoPA cerca nel catalogo "Nodi"
    Then l'e-service "ES-1" è presente una sola volta tra i risultati della ricerca
