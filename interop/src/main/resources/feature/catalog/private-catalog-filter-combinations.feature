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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                      | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Attivo Nebula   | E-service.  | pubblicata           |
      | ES-2  | Comune di Milano | E-service Sospeso Nebula  | E-service.  | sospesa              |
      | ES-3  | Comune di Milano | E-service Estraneo Cumulo | E-service.  | pubblicata           |
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                        | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Fruibile Libero   | E-service.  | pubblicata           |
      | ES-2  | Comune di Milano | E-service Fruibile Iscritto | E-service.  | pubblicata           |
      | ES-3  | Comune di Milano | E-service Fruibile Bloccato | E-service.  | pubblicata           |
    And all'e-service predisposto "ES-3" sono richiesti i seguenti gruppi di attributi:
      | tipo        | attributi                            |
      | certificati | un attributo non posseduto da PagoPA |
    And PagoPA ha un'adesione attiva all'e-service predisposto "ES-2"
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                  | descrizione | stato della versione |
      | ES-3  | Comune di Milano | E-service Giunto Beta | E-service.  | pubblicata           |
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                       | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Mandato Delegato | E-service.  | pubblicata           |
      | ES-2  | Comune di Milano | E-service Mandato Libero   | E-service.  | pubblicata           |
      | ES-3  | Sogecap          | E-service Mandato Estraneo | E-service.  | pubblicata           |
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                       | descrizione | stato della versione | modalità   | scambio asincrono | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Canale Giusto    | E-service.  | pubblicata           | erogazione | sì                | non attivo    |
      | ES-2  | Comune di Milano | E-service Canale Libero    | E-service.  | pubblicata           | erogazione | sì                | non impostato |
      | ES-3  | Comune di Milano | E-service Canale Attivo    | E-service.  | pubblicata           | erogazione | sì                | attivo        |
      | ES-4  | Comune di Milano | E-service Canale Ricezione | E-service.  | pubblicata           | ricezione  | sì                | non attivo    |
      | ES-5  | Comune di Milano | E-service Canale Sincrono  | E-service.  | pubblicata           | erogazione | no                | non attivo    |
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                      | descrizione | stato della versione |
      | ES-2  | Comune di Milano | E-service Sintesi Beta    | E-service.  | pubblicata           |
      | ES-3  | Sogecap          | E-service Sintesi Gamma   | E-service.  | pubblicata           |
      | ES-4  | Comune di Milano | E-service Estraneo Quarzo | E-service.  | pubblicata           |
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
