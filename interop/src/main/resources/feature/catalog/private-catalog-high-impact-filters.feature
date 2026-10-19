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
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                     | descrizione                 | stato della versione |
      | ES-1  | Comune di Milano | E-service attivo Auroria | E-service con più versioni. | <prima versione>     |
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "<seconda versione>"
    When PagoPA cerca nel catalogo la parola "Auroria" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

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
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                      | descrizione            | stato della versione |
      | ES-1  | Comune di Milano | E-service catena Brumalis | Catena di tre versioni | sospesa              |
      | ES-2  | Comune di Milano | E-service solo Brumalis   | Una sola versione      | sospesa              |
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "deprecata"
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Brumalis" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_3] Senza il filtro compaiono anche gli e-service non attivi
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione        | stato della versione |
      | ES-1  | Comune di Milano | E-service Cirrus     | E-service sospeso. | sospesa              |
      | ES-2  | Comune di Milano | E-service Cirrus bis | E-service attivo.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Cirrus"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  # --- subscribedByRequester ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_4] Solo e-service a cui il fruitore è iscritto
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome             | descrizione         | stato della versione |
      | ES-1  | Comune di Milano | E-service Dorsum | E-service iscritto. | pubblicata           |
    And PagoPA ha un'adesione <stato adesione> all'e-service predisposto "ES-1"
    When PagoPA cerca nel catalogo la parola "Dorsum" con i seguenti filtri:
      | filtro                    | valore |
      | sottoscritti dal fruitore | sì     |
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples:
      | stato adesione | esito       |
      | attiva         | compare     |
      | sospesa        | compare     |
      | in bozza       | non compare |
      | archiviata     | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_5] L'iscrizione di un altro ente non conta
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione            | stato della versione |
      | ES-1  | Comune di Milano | E-service Ebrio     | E-service sottoscritto | pubblicata           |
      | ES-2  | Comune di Milano | E-service Ebrio bis | E-service libero       | pubblicata           |
    And PagoPA ha un'adesione attiva all'e-service predisposto "ES-1"
    And Comune di Pozzallo ha un'adesione attiva all'e-service predisposto "ES-2"
    When PagoPA cerca nel catalogo la parola "Ebrio" con i seguenti filtri:
      | filtro                    | valore |
      | sottoscritti dal fruitore | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"

  # --- availableForRequester ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_6] Nessun requisito: e-service richiedibile da tutti
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome             | descrizione      | stato della versione |
      | ES-1  | Comune di Milano | E-service Fulgor | Senza requisiti. | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Fulgor" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_7] Fruibilità con un solo gruppo di attributi certificati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione    | stato della versione |
      | ES-1  | Comune di Milano | E-service Gelu | Con requisiti. | pubblicata           |
    And all'e-service predisposto "ES-1" sono richiesti i seguenti gruppi di attributi:
      | tipo        | attributi   |
      | certificati | <attributi> |
    When PagoPA cerca nel catalogo la parola "Gelu" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples:
      | attributi                                            | esito       |
      | un attributo posseduto da PagoPA                     | compare     |
      | un attributo non posseduto da PagoPA                 | non compare |
      | un attributo non posseduto e uno posseduto da PagoPA | compare     |

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_8] Fruibilità con più gruppi di attributi certificati: tutti devono essere soddisfatti
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome               | descrizione    | stato della versione |
      | ES-1  | Comune di Milano | E-service Gelu bis | Con requisiti. | pubblicata           |
    And all'e-service predisposto "ES-1" sono richiesti i seguenti gruppi di attributi:
      | tipo        | attributi  |
      | certificati | <gruppo 1> |
      | certificati | <gruppo 2> |
    When PagoPA cerca nel catalogo la parola "Gelu" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples:
      | gruppo 1                         | gruppo 2                             | esito       |
      | un attributo posseduto da PagoPA | un attributo posseduto da PagoPA     | compare     |
      | un attributo posseduto da PagoPA | un attributo non posseduto da PagoPA | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_9] Attributi dichiarati e verificati non limitano la fruibilità
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome            | descrizione          | stato della versione |
      | ES-1  | Comune di Milano | E-service Hiems | Con altri requisiti. | pubblicata           |
    And all'e-service predisposto "ES-1" sono richiesti i seguenti gruppi di attributi:
      | tipo       | attributi                            |
      | dichiarati | un attributo non posseduto da PagoPA |
      | verificati | un attributo non posseduto da PagoPA |
    When PagoPA cerca nel catalogo la parola "Hiems" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"

  @flag-discrete-attribute-on
  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_10] Gli attributi discreti limitano la fruibilità
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome            | descrizione            | stato della versione |
      | ES-1  | Comune di Milano | E-service Iubar | Con attributo discreto | pubblicata           |
    And all'e-service predisposto "ES-1" sono richiesti i seguenti gruppi di attributi:
      | tipo     | attributi                            |
      | discreti | un attributo non posseduto da PagoPA |
    When PagoPA cerca nel catalogo la parola "Iubar" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then tra i risultati non compare l'e-service predisposto "ES-1"

  # --- hasLinkedPurposeTemplates ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_11] Solo e-service con template di finalità collegati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome            | descrizione              | stato della versione |
      | ES-1  | Comune di Milano | E-service Lumen | Con template di finalità | pubblicata           |
    And all'e-service predisposto "ES-1" è collegato un template di finalità <stato template>
    When PagoPA cerca nel catalogo la parola "Lumen" con i seguenti filtri:
      | filtro                             | valore |
      | con template di finalità collegati | sì     |
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples:
      | stato template | esito       |
      | pubblicato     | compare     |
      | in bozza       | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_12] Template di finalità collegato a una versione precedente
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione   | stato della versione |
      | ES-1  | Comune di Milano | E-service Mare | Due versioni. | pubblicata           |
    And all'e-service predisposto "ES-1" è collegato un template di finalità pubblicato
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Mare" con i seguenti filtri:
      | filtro                             | valore |
      | con template di finalità collegati | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"

  # --- onlySignalHubEnabled ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_13] Signal Hub attivo: compaiono solo gli e-service con Signal Hub attivo
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Nox attivo | E-service.  | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | E-service Nox spento | E-service.  | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | E-service Nox libero | E-service.  | pubblicata           | non impostato |
    When PagoPA cerca nel catalogo la parola "Nox" con i seguenti filtri:
      | filtro     | valore |
      | Signal Hub | attivo |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_14] Signal Hub non attivo: include anche i valori non impostati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Pix attivo | E-service.  | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | E-service Pix spento | E-service.  | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | E-service Pix libero | E-service.  | pubblicata           | non impostato |
    When PagoPA cerca nel catalogo la parola "Pix" con i seguenti filtri:
      | filtro     | valore     |
      | Signal Hub | non attivo |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_15] Senza filtro Signal Hub nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Ox attivo | E-service.  | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | E-service Ox spento | E-service.  | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | E-service Ox libero | E-service.  | pubblicata           | non impostato |
    When PagoPA cerca nel catalogo la parola "Ox"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
