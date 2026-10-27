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
  - gli scenari di fruibilità creano attributi come admin del Comune di Milano, come nei contract test, con alias locali;
  - le assegnazioni create sono revocate dall'hook; gli attributi issuer non sono eliminabili e restano registrati;
  - gli attributi dichiarati, verificati e discreti appena creati non sono assegnati al fruitore;
  - gli attributi discreti sono usati solo se la relativa feature flag è attiva.

  # --- onlyActiveEservices ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_1] Solo e-service attivi
    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                     |
      | ES-1  | Comune di Milano | E-service attivo Auroria |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <prima versione>
    And la versione 2 dell'e-service "ES-1" del Comune di Milano <seconda versione>
    When PagoPA cerca nel catalogo "Auroria" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then l'e-service "ES-1" <esito> tra i risultati della ricerca

    Examples: Versione sospesa non superata
      | prima versione | seconda versione | esito          |
      | sospesa        | in bozza         | non è presente |
      # | sospesa        | archiviata       | non è presente | - Non è possibile archiviare una versione visto il grace period

    Examples: Versione sospesa superata da una versione successiva
      | prima versione | seconda versione | esito          |
      | sospesa        | pubblicata       | è presente     |
      | sospesa        | sospesa          | non è presente |
      # Per deprecare una versione è necessario che sia presente una versione successiva pubblicata, quindi l'e-service sarà presente in catalogo
      | sospesa        | deprecata        | è presente     |

    Examples: Versione pubblicata
      | prima versione | seconda versione | esito      |
      | pubblicata     | in bozza         | è presente |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_2] Solo e-service attivi con una catena di tre versioni
    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                      |
      | ES-1  | Comune di Milano | E-service catena Brumalis |
      | ES-2  | Comune di Milano | E-service solo Brumalis   |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And la versione 1 dell'e-service "ES-2" del Comune di Milano sospesa
    And la versione 2 dell'e-service "ES-1" del Comune di Milano deprecata
    And la versione 3 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "Brumalis" con i seguenti filtri:
      | filtro                | valore |
      | solo e-service attivi | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And l'e-service "ES-2" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_3] Senza il filtro compaiono anche gli e-service non attivi
    Given i seguenti e-service in bozza:
      | alias | erogatore        | nome                 |
      | ES-1  | Comune di Milano | E-service Cirrus     |
      | ES-2  | Comune di Milano | E-service Cirrus bis |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano sospesa
    And la versione 1 dell'e-service "ES-2" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "Cirrus"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  # --- subscribedByRequester ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_4] Solo e-service a cui il fruitore è iscritto
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Dorsum |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione <stato richiesta>
    When PagoPA cerca nel catalogo "Dorsum" con i seguenti filtri:
      | filtro                    | valore |
      | sottoscritti dal fruitore | sì     |
    Then l'e-service "ES-1" <esito> tra i risultati della ricerca

    Examples:
      | stato richiesta | esito          |
      | attiva          | è presente     |
      | sospesa         | è presente     |
      | in bozza        | non è presente |
      | archiviata      | non è presente |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_5] L'iscrizione di un altro ente non conta
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                |
      | ES-1  | Comune di Milano | E-service Ebrio     |
      | ES-2  | Comune di Milano | E-service Ebrio bis |
    And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    And l'e-service "ES-2" del Comune di Milano sottoscritto dal Comune di Pozzallo con una richiesta di fruizione attiva
    When PagoPA cerca nel catalogo "Ebrio" con i seguenti filtri:
      | filtro                    | valore |
      | sottoscritti dal fruitore | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And l'e-service "ES-2" non è presente tra i risultati della ricerca

  # --- availableForRequester ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_6] Nessun requisito: e-service richiedibile da tutti
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Fulgor |
    When PagoPA cerca nel catalogo "Fulgor" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_7A] Fruibilità con un attributo certificato posseduto da PagoPA
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias  |
      | CERT-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service GeluOwned |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo |
      | certificato | 1      | CERT-1    |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "GeluOwned" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_7B] Fruibilità con un attributo certificato non posseduto da PagoPA
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-2 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service GeluAbsent |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo   |
      | certificato | 1      | CERT-2      |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "GeluAbsent" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_7C] Fruibilità OR fra attributi certificati nello stesso gruppo
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
      | CERT-2 |
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias  |
      | CERT-2 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service GeluEither |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo |
      | certificato | 1      | CERT-1    |
      | certificato | 1      | CERT-2    |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "GeluEither" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_8A] Fruibilità con più gruppi di attributi certificati: tutti soddisfatti
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
      | CERT-2 |
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias  |
      | CERT-1 |
      | CERT-2 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome               |
      | ES-1  | Comune di Milano | E-service GeluAll |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo |
      | certificato | 1      | CERT-1    |
      | certificato | 2      | CERT-2    |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "GeluAll" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_8B] Fruibilità bloccata se un gruppo non è soddisfatto
    Given i seguenti attributi certificati creati da un admin del Comune di Milano:
      | alias  |
      | CERT-1 |
      | CERT-2 |
    And i seguenti attributi certificati assegnati da un admin del Comune di Milano a PagoPA:
      | alias  |
      | CERT-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome               |
      | ES-1  | Comune di Milano | E-service GeluMissing |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo        | gruppo | attributo            |
      | certificato | 1      | CERT-1               |
      | certificato | 2      | CERT-2               |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "GeluMissing" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_9] Attributi dichiarati e verificati non limitano la fruibilità
    Given i seguenti attributi dichiarati creati da un admin del Comune di Milano:
      | alias  |
      | DECL-1 |
    And i seguenti attributi verificati creati da un admin del Comune di Milano:
      | alias |
      | VER-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome            |
      | ES-1  | Comune di Milano | E-service Hiems |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo       | gruppo | attributo |
      | dichiarato | 1      | DECL-1    |
      | verificato | 1      | VER-1     |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "Hiems" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

  @flag-discrete-attribute-on
  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_10] Gli attributi discreti limitano la fruibilità
    Given i seguenti attributi discreti creati da un admin del Comune di Milano:
      | alias  |
      | DISC-1 |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome            |
      | ES-1  | Comune di Milano | E-service Iubar |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano configurata con i seguenti attributi richiesti:
      | tipo     | gruppo | attributo |
      | discreto | 1      | DISC-1    |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "Iubar" con i seguenti filtri:
      | filtro                    | valore |
      | richiedibili dal fruitore | sì     |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

  # --- hasLinkedPurposeTemplates ---

  Scenario Outline: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_11] Solo e-service con template di finalità collegati
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome            |
      | ES-1  | Comune di Milano | E-service Lumen |
    And l'e-service "ES-1" collegato a un template di finalità <stato template>
    When PagoPA cerca nel catalogo "Lumen" con i seguenti filtri:
      | filtro                             | valore |
      | con template di finalità collegati | sì     |
    Then l'e-service "ES-1" <esito> tra i risultati della ricerca

    Examples:
      | stato template | esito          |
      | pubblicato     | è presente     |
      | in bozza       | non è presente |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_12] Template di finalità collegato a una versione precedente
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome           |
      | ES-1  | Comune di Milano | E-service Mare |
    And l'e-service "ES-1" collegato a un template di finalità pubblicato
    And la versione 2 dell'e-service "ES-1" del Comune di Milano pubblicata
    When PagoPA cerca nel catalogo "Mare" con i seguenti filtri:
      | filtro                             | valore |
      | con template di finalità collegati | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

  # --- onlySignalHubEnabled ---

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_13] Signal Hub attivo: compaiono solo gli e-service con Signal Hub attivo
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Nox attivo | attivo        |
      | ES-2  | Comune di Milano | E-service Nox spento | non attivo    |
      | ES-3  | Comune di Milano | E-service Nox libero | non impostato |
    When PagoPA cerca nel catalogo "Nox" con i seguenti filtri:
      | filtro     | valore |
      | Signal Hub | attivo |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_14] Signal Hub non attivo: include anche i valori non impostati
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Pix attivo | attivo        |
      | ES-2  | Comune di Milano | E-service Pix spento | non attivo    |
      | ES-3  | Comune di Milano | E-service Pix libero | non impostato |
    When PagoPA cerca nel catalogo "Pix" con i seguenti filtri:
      | filtro     | valore     |
      | Signal Hub | non attivo |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |
    And l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_HIGH_IMPACT_FILTERS_15] Senza filtro Signal Hub nessuna restrizione
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                | Signal Hub    |
      | ES-1  | Comune di Milano | E-service Ox attivo | attivo        |
      | ES-2  | Comune di Milano | E-service Ox spento | non attivo    |
      | ES-3  | Comune di Milano | E-service Ox libero | non impostato |
    When PagoPA cerca nel catalogo "Ox"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
