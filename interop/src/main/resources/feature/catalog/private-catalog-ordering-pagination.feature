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
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                  | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Ordine Alfa  | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Ordine Beta  | Servizio.   | pubblicata           |
      | ES-3  | Comune di Milano | Servizio Ordine Gamma | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Ordine"
    Then i risultati sono, in quest'ordine, gli e-service predisposti:
      | alias |
      | ES-3  |
      | ES-2  |
      | ES-1  |

  Scenario Outline: [PRIVATE_CATALOG_ORDERING_PAGINATION_2] Ordinamenti richiesti esplicitamente
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Sorte Beta  | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Sorte Gamma | Servizio.   | pubblicata           |
      | ES-3  | Comune di Milano | Servizio Sorte Alfa  | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Sorte" ordinando <ordinamento>
    Then i risultati sono, in quest'ordine, gli e-service predisposti:
      | alias     |
      | <primo>   |
      | <secondo> |
      | <terzo>   |

    Examples:
      | ordinamento                       | primo | secondo | terzo |
      | per nome crescente                | ES-3  | ES-1    | ES-2  |
      | per nome decrescente              | ES-2  | ES-1    | ES-3  |
      | per data di creazione crescente   | ES-1  | ES-2    | ES-3  |
      | per data di creazione decrescente | ES-3  | ES-2    | ES-1  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_3] Con una parola chiave la rilevanza precede l'ordinamento richiesto
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome          | descrizione                   | stato della versione |
      | ES-1  | Comune di Milano | Zeta Quarzo   | Servizio.                     | pubblicata           |
      | ES-2  | Comune di Milano | Alfa Pratiche | Servizio che gestisce Quarzo. | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Quarzo" ordinando per nome crescente
    Then i risultati sono, in quest'ordine, gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_4] Pagine consecutive senza duplicati e senza omissioni
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                    | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Pagina Uno     | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Pagina Due     | Servizio.   | pubblicata           |
      | ES-3  | Comune di Milano | Servizio Pagina Tre     | Servizio.   | pubblicata           |
      | ES-4  | Comune di Milano | Servizio Pagina Quattro | Servizio.   | pubblicata           |
      | ES-5  | Comune di Milano | Servizio Pagina Cinque  | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Pagina" con pagine da 2 risultati
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
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome               | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Oltre Uno | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Oltre Due | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Oltre" a partire dal risultato 10 con pagine da 2 risultati
    Then la ricerca termina senza errori
    And la pagina è vuota e il totale dichiarato è 2

  Scenario: [PRIVATE_CATALOG_ORDERING_PAGINATION_6] Il totale tiene conto dei filtri applicati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                   | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | Servizio Conta Uno     | Servizio.   | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | Servizio Conta Due     | Servizio.   | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | Servizio Conta Tre     | Servizio.   | pubblicata           | attivo        |
      | ES-4  | Comune di Milano | Servizio Conta Quattro | Servizio.   | pubblicata           | non impostato |
      | ES-5  | Comune di Milano | Servizio Conta Cinque  | Servizio.   | pubblicata           | attivo        |
    When PagoPA cerca nel catalogo la parola "Conta" solo tra gli e-service con Signal Hub attivo con pagine da 2 risultati
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
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Molti Nodi | Servizio.   | pubblicata           |
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "pubblicata"
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "pubblicata"
    And all'e-service predisposto "ES-1" è collegato un template di finalità pubblicato
    And all'e-service predisposto "ES-1" è collegato un template di finalità pubblicato
    And PagoPA ha un'adesione attiva all'e-service predisposto "ES-1"
    When PagoPA cerca nel catalogo la parola "Nodi"
    Then l'e-service predisposto "ES-1" compare esattamente una volta nei risultati
