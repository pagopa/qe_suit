@private-catalog
@private-catalog-ordering-pagination
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Ordinamento e paginazione nel catalogo privato

  Come fruitore
  Voglio che i risultati siano ordinati e paginati in modo coerente
  Per scorrere il catalogo senza perdere né duplicare e-service.

  Assunzioni:
  - gli e-service della tabella sono creati nell'ordine delle righe: ES-1 è il più vecchio;
  - gli scenari di ordinamento puro non usano parole chiave e verificano l'ordine relativo degli e-service predisposti su tutte le pagine;
  - gli altri scenari usano una parola chiave improbabile per delimitare il perimetro dei risultati;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_1] Ordinamento predefinito: dal più recente
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service Ordine Alfa  |
      | ES-2  | Comune di Milano | E-service Ordine Beta  |
      | ES-3  | Comune di Milano | E-service Ordine Gamma |
    When PagoPA cerca nel catalogo senza indicare alcuna parola con i seguenti filtri:
      | filtro    | valore           |
      | erogatore | Comune di Milano |
    Then tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:
      | alias |
      | ES-3  |
      | ES-2  |
      | ES-1  |

  Scenario Outline: [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                  |
      | ES-1  | Comune di Milano | E-service Sorte Beta  |
      | ES-2  | Comune di Milano | E-service Sorte Gamma |
      | ES-3  | Comune di Milano | E-service Sorte Alfa  |
    When PagoPA cerca nel catalogo senza indicare alcuna parola con i seguenti filtri:
      | filtro      | valore           |
      | erogatore   | Comune di Milano |
      | ordinamento | <ordinamento>    |
    Then tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome          | descrizione                          |
      | ES-1  | Comune di Milano | Zeta Quarzo   | Consultazione dei dati del servizio. |
      | ES-2  | Comune di Milano | Alfa Pratiche | Gestione delle pratiche Quarzo.      |
    When PagoPA cerca nel catalogo "Quarzo" con i seguenti filtri:
      | filtro      | valore         |
      | ordinamento | nome crescente |
    Then tra i risultati della ricerca sono presenti, in quest'ordine, i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_4] Pagine consecutive senza duplicati e senza omissioni
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                     |
      | ES-1  | Comune di Milano | E-service Pagina Uno     |
      | ES-2  | Comune di Milano | E-service Pagina Due     |
      | ES-3  | Comune di Milano | E-service Pagina Tre     |
      | ES-4  | Comune di Milano | E-service Pagina Quattro |
      | ES-5  | Comune di Milano | E-service Pagina Cinque  |
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                |
      | ES-1  | Comune di Milano | E-service Oltre Uno |
      | ES-2  | Comune di Milano | E-service Oltre Due |
    When PagoPA cerca nel catalogo "Oltre" con i seguenti filtri:
      | filtro                  | valore |
      | a partire dal risultato | 10     |
      | risultati per pagina    | 2      |
    Then la ricerca termina senza errori
    And la pagina è vuota e il totale dichiarato è 2

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_6] Il totale tiene conto dei filtri applicati
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                    | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Conta Uno     | attivo        |
      | ES-2  | Comune di Milano | E-service Conta Due     | non attivo    |
      | ES-3  | Comune di Milano | E-service Conta Tre     | attivo        |
      | ES-4  | Comune di Milano | E-service Conta Quattro | non impostato |
      | ES-5  | Comune di Milano | E-service Conta Cinque  | attivo        |
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 |
      | ES-1  | Comune di Milano | E-service Molti Nodi |
    And la versione 2 dell'e-service "ES-1" del Comune di Milano pubblicata
    And la versione 3 dell'e-service "ES-1" del Comune di Milano pubblicata
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And l'e-service "ES-1" sottoscritto da PagoPA con richiesta di fruizione attiva
    When PagoPA cerca nel catalogo "Nodi"
    Then l'e-service "ES-1" è presente una sola volta tra i risultati della ricerca
