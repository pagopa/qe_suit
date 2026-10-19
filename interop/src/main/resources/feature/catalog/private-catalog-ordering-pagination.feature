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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ordine Alfa |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ordine Beta |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ordine Gamma |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Ordine"
    Then i risultati sono, in quest'ordine, gli e-service predisposti:
      | alias |
      | ES-3  |
      | ES-2  |
      | ES-1  |

  Scenario Outline: [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Sorte Beta |
      | descrizione | E-service.           |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Sorte Gamma |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Sorte Alfa |
      | descrizione | E-service.           |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Sorte" con i seguenti filtri:
      | filtro      | valore        |
      | ordinamento | <ordinamento> |
    Then i risultati sono, in quest'ordine, gli e-service predisposti:
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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Zeta Quarzo |
      | descrizione | E-service.  |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Alfa Pratiche                  |
      | descrizione | E-service che gestisce Quarzo. |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Quarzo" con i seguenti filtri:
      | filtro      | valore         |
      | ordinamento | nome crescente |
    Then i risultati sono, in quest'ordine, gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_4] Pagine consecutive senza duplicati e senza omissioni
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pagina Uno |
      | descrizione | E-service.           |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pagina Due |
      | descrizione | E-service.           |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pagina Tre |
      | descrizione | E-service.           |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    And l'e-service "ES-4" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pagina Quattro |
      | descrizione | E-service.               |
    And la versione 1 dell'e-service "ES-4" nello stato "pubblicata"
    And l'e-service "ES-5" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pagina Cinque |
      | descrizione | E-service.              |
    And la versione 1 dell'e-service "ES-5" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Pagina" con i seguenti filtri:
      | filtro               | valore |
      | risultati per pagina | 2      |
    Then le pagine contengono i seguenti risultati, con totale dichiarato pari alla loro somma:
      | pagina | risultati |
      | 1      | 2         |
      | 2      | 2         |
      | 3      | 1         |
    And nessun e-service compare in più di una pagina
    And tutte le pagine insieme contengono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
      | ES-4  |
      | ES-5  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_5] Pagina oltre l'ultima
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Oltre Uno |
      | descrizione | E-service.          |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Oltre Due |
      | descrizione | E-service.          |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Oltre" con i seguenti filtri:
      | filtro                  | valore |
      | a partire dal risultato | 10     |
      | risultati per pagina    | 2      |
    Then la ricerca termina senza errori
    And la pagina è vuota e il totale dichiarato è 2

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_6] Il totale tiene conto dei filtri applicati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Conta Uno |
      | descrizione | E-service.          |
      | Signal Hub  | attivo              |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Conta Due |
      | descrizione | E-service.          |
      | Signal Hub  | non attivo          |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Conta Tre |
      | descrizione | E-service.          |
      | Signal Hub  | attivo              |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    And l'e-service "ES-4" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Conta Quattro |
      | descrizione | E-service.              |
      | Signal Hub  | non impostato           |
    And la versione 1 dell'e-service "ES-4" nello stato "pubblicata"
    And l'e-service "ES-5" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Conta Cinque |
      | descrizione | E-service.             |
      | Signal Hub  | attivo                 |
    And la versione 1 dell'e-service "ES-5" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Conta" con i seguenti filtri:
      | filtro               | valore |
      | Signal Hub           | attivo |
      | risultati per pagina | 2      |
    Then le pagine contengono i seguenti risultati, con totale dichiarato pari alla loro somma:
      | pagina | risultati |
      | 1      | 2         |
      | 2      | 1         |
    And nessun e-service compare in più di una pagina
    And tutte le pagine insieme contengono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-3  |
      | ES-5  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_7] Un e-service con molte relazioni compare una sola volta
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Molti Nodi |
      | descrizione | E-service.           |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And la versione 2 dell'e-service "ES-1" nello stato "pubblicata"
    And la versione 3 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-1" ha un template di finalità collegato in stato "pubblicato"
    And l'e-service "ES-1" ha un template di finalità collegato in stato "pubblicato"
    And PagoPA ha una richiesta di fruizione attiva per l'e-service "ES-1"
    When PagoPA cerca nel catalogo la parola "Nodi"
    Then tra i risultati compare una sola volta l'e-service "ES-1"
