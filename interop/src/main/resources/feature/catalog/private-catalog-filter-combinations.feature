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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Attivo Nebula |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Sospeso Nebula |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" sospesa
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Estraneo Cumulo |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Fruibile Libero |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Fruibile Iscritto |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Fruibile Bloccato |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
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
    Given un template e-service predisposto dal Comune di Milano con i seguenti dati:
      | alias | nome                 | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Giunto Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And la versione 1 del template e-service "TPL-1" predisposta per la pubblicazione
    And la versione 1 del template e-service "TPL-1" pubblicata
    And le seguenti istanze di template predisposte:
      | alias | template | suffisso   |
      | ES-1  | TPL-1    | eservice-1 |
      | ES-2  | TPL-1    | eservice-2 |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Giunto Beta |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Mandato Delegato |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Mandato Libero |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Mandato Estraneo |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Canale Giusto |
      | modalità   | erogazione              |
      | Signal Hub | non attivo              |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Canale Libero |
      | modalità   | erogazione              |
      | Signal Hub | non impostato           |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Canale Attivo |
      | modalità   | erogazione              |
      | Signal Hub | attivo                  |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-3" pubblicata
    And l'e-service "ES-4" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Canale Ricezione |
      | modalità   | ricezione                  |
      | Signal Hub | non attivo                 |
    And la versione 1 dell'e-service "ES-4" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-4" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-4" pubblicata
    And l'e-service "ES-5" predisposto dal Comune di Milano con i seguenti dati:
      | nome       | E-service Canale Sincrono |
      | modalità   | erogazione                |
      | Signal Hub | non attivo                |
    And la versione 1 dell'e-service "ES-5" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-5" configurata con i seguenti dati:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-5" pubblicata
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
    And i seguenti template di e-service predisposti:
      | alias | nome                  | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Sintesi Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And le seguenti istanze di template predisposte:
      | alias | template |
      | ES-1  | TPL-1    |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Sintesi Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Sintesi Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    And l'e-service "ES-4" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Estraneo Quarzo |
    And la versione 1 dell'e-service "ES-4" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-4" pubblicata
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
