@private-catalog
@private-catalog-high-impact-filters
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtri ad alto impatto nel catalogo privato

  Come fruitore
  Voglio restringere il catalogo con i filtri di business
  Per vedere solo gli e-service attivi, già sottoscritti, richiedibili, con template di finalità o con Signal Hub.

  Assunzioni:
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook;
  - ogni scenario usa una parola chiave improbabile per delimitare il perimetro dei risultati;
  - gli attributi usati negli scenari di fruibilità sono già presenti sui tenant e non vengono modificati;
  - gli attributi discreti sono usati solo se la relativa feature flag è attiva.

  # --- onlyActiveEservices ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_1] Solo e-service attivi
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service attivo Auroria    |
      | descrizione | E-service con più versioni. |
    And la versione 1 dell'e-service "ES-1" nello stato "<prima versione>"
    And la versione 2 dell'e-service "ES-1" nello stato "<seconda versione>"
    When PagoPA cerca nel catalogo la parola "Auroria" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then tra i risultati <esito> l'e-service "ES-1"

    Examples: Versione sospesa non superata
      | prima versione | seconda versione | esito       |
      | sospesa        | in bozza         | non compare |
      | sospesa        | archiviata       | non compare |

    Examples: Versione sospesa superata da una versione successiva
      | prima versione | seconda versione | esito   |
      | sospesa        | pubblicata       | compare |
      | sospesa        | sospesa          | compare |
      | sospesa        | deprecata        | compare |

    Examples: Versione pubblicata
      | prima versione | seconda versione | esito   |
      | pubblicata     | in bozza         | compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_2] Solo e-service attivi con una catena di tre versioni
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service catena Brumalis |
      | descrizione | Catena di tre versioni    |
    And la versione 1 dell'e-service "ES-1" nello stato "sospesa"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service solo Brumalis |
      | descrizione | Una sola versione       |
    And la versione 1 dell'e-service "ES-2" nello stato "sospesa"
    And la versione 2 dell'e-service "ES-1" nello stato "deprecata"
    And la versione 3 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Brumalis" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then tra i risultati compare l'e-service "ES-1"
    And tra i risultati non compare l'e-service "ES-2"

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_3] Senza il filtro compaiono anche gli e-service non attivi
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Cirrus   |
      | descrizione | E-service sospeso. |
    And la versione 1 dell'e-service "ES-1" nello stato "sospesa"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Cirrus bis |
      | descrizione | E-service attivo.    |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Cirrus"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  # --- subscribedByRequester ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_4] Solo e-service a cui il fruitore è iscritto
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Dorsum    |
      | descrizione | E-service iscritto. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And PagoPA ha una richiesta di fruizione <stato richiesta> per l'e-service "ES-1"
    When PagoPA cerca nel catalogo la parola "Dorsum" con i seguenti filtri:
      | filtro                    | valore |
      | sottoscritti dal fruitore | sì     |
    Then tra i risultati <esito> l'e-service "ES-1"

    Examples:
      | stato richiesta | esito       |
      | attiva          | compare     |
      | sospesa         | compare     |
      | in bozza        | non compare |
      | archiviata      | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_5] L'iscrizione di un altro ente non conta
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ebrio        |
      | descrizione | E-service sottoscritto |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ebrio bis |
      | descrizione | E-service libero    |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And PagoPA ha una richiesta di fruizione attiva per l'e-service "ES-1"
    And Comune di Pozzallo ha una richiesta di fruizione attiva per l'e-service "ES-2"
    When PagoPA cerca nel catalogo la parola "Ebrio" con i seguenti filtri:
      | filtro                    | valore |
      | sottoscritti dal fruitore | sì     |
    Then tra i risultati compare l'e-service "ES-1"
    And tra i risultati non compare l'e-service "ES-2"

  # --- availableForRequester ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_6] Nessun requisito: e-service richiedibile da tutti
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Fulgor |
      | descrizione | Senza requisiti. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Fulgor" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati compare l'e-service "ES-1"

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_7] Fruibilità con un solo gruppo di attributi certificati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Gelu |
      | descrizione | Con requisiti. |
    And la versione 1 dell'e-service "ES-1" che richiede i seguenti attributi:
      | tipo        | gruppo | attributo   |
      | certificato | 1      | <attributi> |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Gelu" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati <esito> l'e-service "ES-1"

    Examples:
      | attributi                                            | esito       |
      | un attributo posseduto da PagoPA                     | compare     |
      | un attributo non posseduto da PagoPA                 | non compare |
      | un attributo non posseduto e uno posseduto da PagoPA | compare     |

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_8] Fruibilità con più gruppi di attributi certificati: tutti devono essere soddisfatti
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Gelu bis |
      | descrizione | Con requisiti.     |
    And la versione 1 dell'e-service "ES-1" che richiede i seguenti attributi:
      | tipo        | gruppo | attributo            |
      | certificato | 1      | <attributo gruppo 1> |
      | certificato | 2      | <attributo gruppo 2> |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Gelu" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati <esito> l'e-service "ES-1"

    Examples:
      | attributo gruppo 1               | attributo gruppo 2                   | esito       |
      | un attributo posseduto da PagoPA | un attributo posseduto da PagoPA     | compare     |
      | un attributo posseduto da PagoPA | un attributo non posseduto da PagoPA | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_9] Attributi dichiarati e verificati non limitano la fruibilità
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Hiems      |
      | descrizione | Con altri requisiti. |
    And la versione 1 dell'e-service "ES-1" che richiede i seguenti attributi:
      | tipo       | gruppo | attributo                            |
      | dichiarato | 1      | un attributo non posseduto da PagoPA |
      | verificato | 1      | un attributo non posseduto da PagoPA |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Hiems" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati compare l'e-service "ES-1"

  @flag-discrete-attribute-on
  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_10] Gli attributi discreti limitano la fruibilità
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Iubar        |
      | descrizione | Con attributo discreto |
    And la versione 1 dell'e-service "ES-1" che richiede i seguenti attributi:
      | tipo     | gruppo | attributo                            |
      | discreto | 1      | un attributo non posseduto da PagoPA |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Iubar" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati non compare l'e-service "ES-1"

  # --- hasLinkedPurposeTemplates ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_11] Solo e-service con template di finalità collegati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Lumen          |
      | descrizione | Con template di finalità |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-1" ha un template di finalità collegato in stato "<stato template>"
    When PagoPA cerca nel catalogo la parola "Lumen" con i seguenti filtri:
      | filtro                             | valore |
      | con template di finalità collegati | sì     |
    Then tra i risultati <esito> l'e-service "ES-1"

    Examples:
      | stato template | esito       |
      | pubblicato     | compare     |
      | in bozza       | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_12] Template di finalità collegato a una versione precedente
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Mare |
      | descrizione | Due versioni.  |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-1" ha un template di finalità collegato in stato "pubblicato"
    And la versione 2 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Mare" con i seguenti filtri:
      | filtro                             | valore |
      | con template di finalità collegati | sì     |
    Then tra i risultati compare l'e-service "ES-1"

  # --- onlySignalHubEnabled ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_13] Signal Hub attivo: compaiono solo gli e-service con Signal Hub attivo
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Nox attivo |
      | descrizione | E-service.           |
      | Signal Hub  | attivo               |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Nox spento |
      | descrizione | E-service.           |
      | Signal Hub  | non attivo           |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Nox libero |
      | descrizione | E-service.           |
      | Signal Hub  | non impostato        |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Nox" con i seguenti filtri:
      | filtro     | valore |
      | Signal Hub | attivo |
    Then tra i risultati compare l'e-service "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_14] Signal Hub non attivo: include anche i valori non impostati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pix attivo |
      | descrizione | E-service.           |
      | Signal Hub  | attivo               |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pix spento |
      | descrizione | E-service.           |
      | Signal Hub  | non attivo           |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Pix libero |
      | descrizione | E-service.           |
      | Signal Hub  | non impostato        |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Pix" con i seguenti filtri:
      | filtro     | valore     |
      | Signal Hub | non attivo |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service "ES-1"

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_15] Senza filtro Signal Hub nessuna restrizione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ox attivo |
      | descrizione | E-service.          |
      | Signal Hub  | attivo              |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ox spento |
      | descrizione | E-service.          |
      | Signal Hub  | non attivo          |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ox libero |
      | descrizione | E-service.          |
      | Signal Hub  | non impostato       |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Ox"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
