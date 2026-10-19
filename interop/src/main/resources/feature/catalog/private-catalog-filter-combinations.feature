@private-catalog
@private-catalog-filter-combinations
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Combinazioni di filtri nel catalogo privato

  Come fruitore
  Voglio combinare più filtri nella stessa ricerca
  Per ottenere solo gli e-service che rispettano tutti i criteri insieme.

  Assunzioni:
  - i filtri combinati si applicano in AND;
  - ogni scenario ha almeno un e-service escluso per ciascun filtro combinato, così un filtro ignorato fa fallire lo scenario;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook;
  - ogni scenario usa una parola chiave improbabile per delimitare il perimetro dei risultati.

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_1] Parola chiave e solo e-service attivi
    Given i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome                      |
      | ES-1  | Comune di Milano | E-service Attivo Nebula   |
      | ES-2  | Comune di Milano | E-service Sospeso Nebula  |
      | ES-3  | Comune di Milano | E-service Estraneo Cumulo |
    And la versione 1 dell'e-service "ES-1" pubblicata
    And la versione 1 dell'e-service "ES-2" sospesa
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Nebula" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_2] E-service richiedibili e non ancora sottoscritti
    Given i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome                        |
      | ES-1  | Comune di Milano | E-service Fruibile Libero   |
      | ES-2  | Comune di Milano | E-service Fruibile Iscritto |
      | ES-3  | Comune di Milano | E-service Fruibile Bloccato |
    And la versione 1 dell'e-service "ES-1" pubblicata
    And la versione 1 dell'e-service "ES-2" pubblicata
    And la versione 1 dell'e-service "ES-3" configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo                            |
      | certificato | 1      | un attributo non posseduto da PagoPA |
    And la versione 1 dell'e-service "ES-3" pubblicata
    And l'e-service "ES-2" sottoscritto da PagoPA con richiesta di fruizione attiva
    When PagoPA cerca nel catalogo "Fruibile" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
      | sottoscritti dal fruitore | no     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_3] Istanze di template con template di finalità collegati
    Given i seguenti template e-service pubblicati:
      | alias | nome                 | tecnologia |
      | TPL-1 | Template Giunto Alfa | SOAP       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso   |
      | ES-1  | TPL-1    | eservice-1 |
      | ES-2  | TPL-1    | eservice-2 |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome                  |
      | ES-3  | Comune di Milano | E-service Giunto Beta |
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And l'e-service "ES-3" collegato a un template di finalità pubblicato
    When PagoPA cerca nel catalogo "Giunto" con i seguenti filtri:
      | filtro                             | valore |
      | istanze di template                | sì     |
      | con template di finalità collegati | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_4] Ruolo di delegato ed erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       |
      | ES-1  | Comune di Milano | E-service Mandato Delegato |
      | ES-2  | Comune di Milano | E-service Mandato Libero   |
      | ES-3  | Sogecap          | E-service Mandato Estraneo |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And l'e-service "ES-3" delegato in erogazione da Sogecap al Comune di Pozzallo con delega attiva
    When il Comune di Pozzallo cerca nel catalogo "Mandato" con i seguenti filtri:
      | filtro          | valore           |
      | ruolo di delega | delegato         |
      | erogatore       | Comune di Milano |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_5] Modalità, scambio asincrono e Signal Hub non attivo
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       | modalità   | Signal Hub    | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Canale Giusto    | erogazione | non attivo    | sì                |
      | ES-2  | Comune di Milano | E-service Canale Libero    | erogazione | non impostato | sì                |
      | ES-3  | Comune di Milano | E-service Canale Attivo    | erogazione | attivo        | sì                |
      | ES-4  | Comune di Milano | E-service Canale Ricezione | ricezione  | non attivo    | sì                |
      | ES-5  | Comune di Milano | E-service Canale Sincrono  | erogazione | non attivo    | no                |
    When PagoPA cerca nel catalogo "Canale" con i seguenti filtri:
      | filtro            | valore     |
      | modalità          | erogazione |
      | scambio asincrono | sì         |
      | Signal Hub        | non attivo |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-3  |
      | ES-4  |
      | ES-5  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_6] Parola chiave, categoria dell'erogatore e istanze di template
    Given le categorie degli enti rilevate dagli attributi certificati con i seguenti dati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And i seguenti template di e-service pubblicati:
      | alias | nome                  | tecnologia |
      | TPL-1 | Template Sintesi Alfa | SOAP       |
    And le seguenti istanze di template pubblicate:
      | alias | template |
      | ES-1  | TPL-1    |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome                      |
      | ES-2  | Comune di Milano | E-service Sintesi Beta    |
      | ES-3  | Sogecap          | E-service Sintesi Gamma   |
      | ES-4  | Comune di Milano | E-service Estraneo Quarzo |
    When PagoPA cerca nel catalogo "Sintesi" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | CAT-1  |
      | istanze di template      | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |
      | ES-4  |
