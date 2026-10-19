@private-catalog
@private-catalog-high-impact-filters
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtri ad alto impatto nel catalogo privato

  Come fruitore
  Voglio restringere il catalogo con i filtri di business
  Per vedere solo gli e-service attivi, già sottoscritti, richiedibili, con template di finalità o con Signal Hub.

  Assunzioni sull'ambiente QA:
  - gli attributi usati negli scenari di fruibilità sono già presenti sui tenant e non vengono modificati;
  - gli attributi discreti sono usati solo se la relativa feature flag è attiva.

  # --- onlyActiveEservices ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_1] Solo e-service attivi
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                    | descrizione                | stato della versione |
      | ES-1  | Comune di Milano | Servizio attivo Auroria | Servizio con più versioni. | <prima versione>     |
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "<seconda versione>"
    When PagoPA cerca nel catalogo la parola "Auroria" solo tra gli e-service attivi
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
      | alias | erogatore        | nome                     | descrizione            | stato della versione |
      | ES-1  | Comune di Milano | Servizio catena Brumalis | Catena di tre versioni | sospesa              |
      | ES-2  | Comune di Milano | Servizio solo Brumalis   | Una sola versione      | sospesa              |
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "deprecata"
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Brumalis" solo tra gli e-service attivi
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_3] Senza il filtro compaiono anche gli e-service non attivi
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione       | stato della versione |
      | ES-1  | Comune di Milano | Servizio Cirrus     | Servizio sospeso. | sospesa              |
      | ES-2  | Comune di Milano | Servizio Cirrus bis | Servizio attivo.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Cirrus"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  # --- subscribedByRequester ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_4] Solo e-service a cui il fruitore è iscritto
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome            | descrizione        | stato della versione |
      | ES-1  | Comune di Milano | Servizio Dorsum | Servizio iscritto. | pubblicata           |
    And PagoPA ha un'adesione <stato adesione> all'e-service predisposto "ES-1"
    When PagoPA cerca nel catalogo la parola "Dorsum" solo tra gli e-service a cui è iscritto
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
      | alias | erogatore        | nome               | descrizione           | stato della versione |
      | ES-1  | Comune di Milano | Servizio Ebrio     | Servizio sottoscritto | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Ebrio bis | Servizio libero       | pubblicata           |
    And PagoPA ha un'adesione attiva all'e-service predisposto "ES-1"
    And Comune di Pozzallo ha un'adesione attiva all'e-service predisposto "ES-2"
    When PagoPA cerca nel catalogo la parola "Ebrio" solo tra gli e-service a cui è iscritto
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"

  # --- availableForRequester ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_6] Nessun requisito: e-service richiedibile da tutti
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome            | descrizione      | stato della versione |
      | ES-1  | Comune di Milano | Servizio Fulgor | Senza requisiti. | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Fulgor" solo tra gli e-service che può richiedere
    Then tra i risultati compare l'e-service predisposto "ES-1"

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_7] Fruibilità in base agli attributi certificati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome          | descrizione    | stato della versione |
      | ES-1  | Comune di Milano | Servizio Gelu | Con requisiti. | pubblicata           |
    And all'e-service predisposto "ES-1" è richiesto un gruppo di attributi certificati: <gruppo 1>
    And all'e-service predisposto "ES-1" è richiesto un gruppo di attributi certificati: <gruppo 2>
    When PagoPA cerca nel catalogo la parola "Gelu" solo tra gli e-service che può richiedere
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples: Un solo gruppo
      | gruppo 1                             | gruppo 2            | esito       |
      | un attributo posseduto da PagoPA     | nessun altro gruppo | compare     |
      | un attributo non posseduto da PagoPA | nessun altro gruppo | non compare |

    Examples: Attributi alternativi nello stesso gruppo
      | gruppo 1                                             | gruppo 2            | esito   |
      | un attributo non posseduto e uno posseduto da PagoPA | nessun altro gruppo | compare |

    Examples: Più gruppi, tutti devono essere soddisfatti
      | gruppo 1                         | gruppo 2                             | esito       |
      | un attributo posseduto da PagoPA | un attributo posseduto da PagoPA     | compare     |
      | un attributo posseduto da PagoPA | un attributo non posseduto da PagoPA | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_8] Attributi dichiarati e verificati non limitano la fruibilità
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione          | stato della versione |
      | ES-1  | Comune di Milano | Servizio Hiems | Con altri requisiti. | pubblicata           |
    And all'e-service predisposto "ES-1" è richiesto un gruppo di attributi dichiarati: un attributo non posseduto da PagoPA
    And all'e-service predisposto "ES-1" è richiesto un gruppo di attributi verificati: un attributo non posseduto da PagoPA
    When PagoPA cerca nel catalogo la parola "Hiems" solo tra gli e-service che può richiedere
    Then tra i risultati compare l'e-service predisposto "ES-1"

  @flag-discrete-attribute-on
  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_9] Gli attributi discreti limitano la fruibilità
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione            | stato della versione |
      | ES-1  | Comune di Milano | Servizio Iubar | Con attributo discreto | pubblicata           |
    And all'e-service predisposto "ES-1" è richiesto un gruppo di attributi discreti: un attributo non posseduto da PagoPA
    When PagoPA cerca nel catalogo la parola "Iubar" solo tra gli e-service che può richiedere
    Then tra i risultati non compare l'e-service predisposto "ES-1"

  # --- hasLinkedPurposeTemplates ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_10] Solo e-service con template di finalità collegati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione              | stato della versione |
      | ES-1  | Comune di Milano | Servizio Lumen | Con template di finalità | pubblicata           |
    And all'e-service predisposto "ES-1" è collegato un template di finalità <stato template>
    When PagoPA cerca nel catalogo la parola "Lumen" solo tra gli e-service con template di finalità collegati
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples:
      | stato template | esito       |
      | pubblicato     | compare     |
      | in bozza       | non compare |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_11] Template di finalità collegato a una versione precedente
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome          | descrizione   | stato della versione |
      | ES-1  | Comune di Milano | Servizio Mare | Due versioni. | pubblicata           |
    And all'e-service predisposto "ES-1" è collegato un template di finalità pubblicato
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Mare" solo tra gli e-service con template di finalità collegati
    Then tra i risultati compare l'e-service predisposto "ES-1"

  # --- onlySignalHubEnabled ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_12] Signal Hub attivo: compaiono solo gli e-service con Signal Hub attivo
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | Servizio Nox attivo | Servizio.   | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | Servizio Nox spento | Servizio.   | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | Servizio Nox libero | Servizio.   | pubblicata           | non impostato |
    When PagoPA cerca nel catalogo la parola "Nox" solo tra gli e-service con Signal Hub attivo
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_13] Signal Hub non attivo: include anche i valori non impostati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | Servizio Pix attivo | Servizio.   | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | Servizio Pix spento | Servizio.   | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | Servizio Pix libero | Servizio.   | pubblicata           | non impostato |
    When PagoPA cerca nel catalogo la parola "Pix" solo tra gli e-service con Signal Hub non attivo
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_14] Senza filtro Signal Hub nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome               | descrizione | stato della versione | Signal Hub    |
      | ES-1  | Comune di Milano | Servizio Ox attivo | Servizio.   | pubblicata           | attivo        |
      | ES-2  | Comune di Milano | Servizio Ox spento | Servizio.   | pubblicata           | non attivo    |
      | ES-3  | Comune di Milano | Servizio Ox libero | Servizio.   | pubblicata           | non impostato |
    When PagoPA cerca nel catalogo la parola "Ox"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
