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
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano        |
      | nome                 | E-service Attivo Nebula |
      | descrizione          | E-service.              |
      | stato della versione | pubblicata              |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano         |
      | nome                 | E-service Sospeso Nebula |
      | descrizione          | E-service.               |
      | stato della versione | sospesa                  |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service Estraneo Cumulo |
      | descrizione          | E-service.                |
      | stato della versione | pubblicata                |
    When PagoPA cerca nel catalogo la parola "Nebula" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_2] E-service richiedibili e non ancora sottoscritti
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service Fruibile Libero |
      | descrizione          | E-service.                |
      | stato della versione | pubblicata                |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano            |
      | nome                 | E-service Fruibile Iscritto |
      | descrizione          | E-service.                  |
      | stato della versione | pubblicata                  |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore             | Comune di Milano                     |
      | nome                  | E-service Fruibile Bloccato          |
      | descrizione           | E-service.                           |
      | stato della versione  | pubblicata                           |
      | attributi certificati | un attributo non posseduto da PagoPA |
    And PagoPA ha una richiesta di fruizione attiva per l'e-service predisposto "ES-2"
    When PagoPA cerca nel catalogo la parola "Fruibile" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
      | sottoscritti dal fruitore | no     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_3] Istanze di template con template di finalità collegati
    Given un admin appartenente all'erogatore Comune di Milano
    And sono predisposti i seguenti template di e-service:
      | alias | nome                 | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Giunto Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And sono predisposte le seguenti istanze di template:
      | alias | template | stato della versione |
      | ES-1  | TPL-1    | pubblicata           |
      | ES-2  | TPL-1    | pubblicata           |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano      |
      | nome                 | E-service Giunto Beta |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
    And all'e-service predisposto "ES-1" è collegato un template di finalità pubblicato
    And all'e-service predisposto "ES-3" è collegato un template di finalità pubblicato
    When PagoPA cerca nel catalogo la parola "Giunto" con i seguenti filtri:
      | filtro                             | valore |
      | istanze di template                | sì     |
      | con template di finalità collegati | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_4] Ruolo di delegato ed erogatore
    Given un admin appartenente al fruitore Comune di Pozzallo
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano           |
      | nome                 | E-service Mandato Delegato |
      | descrizione          | E-service.                 |
      | stato della versione | pubblicata                 |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano         |
      | nome                 | E-service Mandato Libero |
      | descrizione          | E-service.               |
      | stato della versione | pubblicata               |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Sogecap                    |
      | nome                 | E-service Mandato Estraneo |
      | descrizione          | E-service.                 |
      | stato della versione | pubblicata                 |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega attiva
    And l'erogatore Sogecap ha delegato l'erogazione dell'e-service predisposto "ES-3" al Comune di Pozzallo con delega attiva
    When Comune di Pozzallo cerca nel catalogo la parola "Mandato" con i seguenti filtri:
      | filtro          | valore           |
      | ruolo di delega | delegato         |
      | erogatore       | Comune di Milano |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_5] Modalità, scambio asincrono e Signal Hub non attivo
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano        |
      | nome                 | E-service Canale Giusto |
      | descrizione          | E-service.              |
      | stato della versione | pubblicata              |
      | modalità             | erogazione              |
      | scambio asincrono    | sì                      |
      | Signal Hub           | non attivo              |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano        |
      | nome                 | E-service Canale Libero |
      | descrizione          | E-service.              |
      | stato della versione | pubblicata              |
      | modalità             | erogazione              |
      | scambio asincrono    | sì                      |
      | Signal Hub           | non impostato           |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano        |
      | nome                 | E-service Canale Attivo |
      | descrizione          | E-service.              |
      | stato della versione | pubblicata              |
      | modalità             | erogazione              |
      | scambio asincrono    | sì                      |
      | Signal Hub           | attivo                  |
    And l'e-service "ES-4" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano           |
      | nome                 | E-service Canale Ricezione |
      | descrizione          | E-service.                 |
      | stato della versione | pubblicata                 |
      | modalità             | ricezione                  |
      | scambio asincrono    | sì                         |
      | Signal Hub           | non attivo                 |
    And l'e-service "ES-5" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service Canale Sincrono |
      | descrizione          | E-service.                |
      | stato della versione | pubblicata                |
      | modalità             | erogazione                |
      | scambio asincrono    | no                        |
      | Signal Hub           | non attivo                |
    When PagoPA cerca nel catalogo la parola "Canale" con i seguenti filtri:
      | filtro            | valore     |
      | modalità          | erogazione |
      | scambio asincrono | sì         |
      | Signal Hub        | non attivo |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-3  |
      | ES-4  |
      | ES-5  |

  Scenario: [PRIVATE_CATALOG_FILTER_COMBINATIONS_6] Parola chiave, categoria dell'erogatore e istanze di template
    Given un admin appartenente all'erogatore Comune di Milano
    And le categorie degli enti sono lette dagli attributi certificati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And sono predisposti i seguenti template di e-service:
      | alias | nome                  | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Sintesi Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And sono predisposte le seguenti istanze di template:
      | alias | template | stato della versione |
      | ES-1  | TPL-1    | pubblicata           |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Sintesi Beta |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Sogecap                 |
      | nome                 | E-service Sintesi Gamma |
      | descrizione          | E-service.              |
      | stato della versione | pubblicata              |
    And l'e-service "ES-4" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service Estraneo Quarzo |
      | descrizione          | E-service.                |
      | stato della versione | pubblicata                |
    When PagoPA cerca nel catalogo la parola "Sintesi" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | CAT-1  |
      | istanze di template      | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
      | ES-4  |
