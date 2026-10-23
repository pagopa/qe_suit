@private-catalog
@private-catalog-visibility-and-roles
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Visibilità degli e-service nel catalogo privato

  Come utente di un ente
  Voglio vedere nel catalogo solo gli e-service con una versione fruibile
  Per non incontrare e-service non consultabili, qualunque sia il mio ruolo.

  Assunzioni:
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook;
  - ogni scenario usa una parola chiave improbabile per delimitare il perimetro dei risultati;
  - il Comune di Pozzallo NON è abilitato a ricevere notifiche;
  - la Background degli scenari sulle notifiche abilita PagoPA.
  Se la configurazione cambia, gli scenari sulle notifiche vanno rivisti.

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_1] Visibilità in base allo stato delle versioni
    Given i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome                   |
      | ES-1  | Comune di Milano | E-service di <esempio> |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <stato>
    When PagoPA cerca nel catalogo "<esempio>"
    Then l'e-service "ES-1" <esito> tra i risultati della ricerca

    Examples: Versioni fruibili
      | esempio | stato      | esito      |
      | Alfa    | pubblicata | è presente |
      | Beta    | sospesa    | è presente |

    Examples: Versioni non più fruibili
      | esempio | stato                    | esito          |
      | Gamma   | deprecata                | non è presente |
      | Delta   | in archiviazione         | non è presente |
      | Epsilon | in archiviazione sospesa | non è presente |
      | Zeta    | archiviata               | non è presente |

    Examples: Versioni non ancora pubblicate
      | esempio | stato                     | esito          |
      | Eta     | in bozza                  | non è presente |

  @producer-delegation-settings
  Scenario: [PRIVATE_CATALOG_VISIBILITY_1_APPROVAL] Una versione delegata in attesa di approvazione non compare
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome               |
      | ES-1  | Comune di Milano | E-service di Theta |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 1 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    When PagoPA cerca nel catalogo "Theta"
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_VISIBILITY_2] Un e-service con sola bozza non compare nemmeno per il produttore
    Given i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome            |
      | ES-1  | Comune di Milano | E-service Bozza |
    When il Comune di Milano cerca nel catalogo "E-service Bozza"
    Then la ricerca termina senza errori
    And l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_VISIBILITY_3] Versione pubblicata e nuova bozza: compare solo la versione pubblicata
    Given i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome             |
      | ES-1  | Comune di Milano | E-service Doppio |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
    And la versione 2 dell'e-service "ES-1" del Comune di Milano in bozza
    When PagoPA cerca nel catalogo "E-service Doppio"
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And per l'e-service "ES-1" il risultato riporta:
      | proprietà           | valore |
      | versione presentata | 1      |

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_4] Versione presentata nel risultato
    Given i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome               |
      | ES-1  | Comune di Milano | E-service Versioni |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano <prima versione>
    And la versione 2 dell'e-service "ES-1" del Comune di Milano <seconda versione>
    When PagoPA cerca nel catalogo "E-service Versioni"
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And per l'e-service "ES-1" il risultato riporta:
      | proprietà           | valore                |
      | versione presentata | <versione presentata> |

    Examples:
      | prima versione | seconda versione          | versione presentata |
      | deprecata      | pubblicata                | 2                   |
      | pubblicata     | in bozza                  | 1                   |
      | pubblicata     | archiviata                | 1                   |

  @producer-delegation-settings
  Scenario: [PRIVATE_CATALOG_VISIBILITY_4_APPROVAL] La nuova versione delegata in attesa di approvazione non sostituisce quella pubblicata
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome               |
      | ES-1  | Comune di Milano | E-service Versioni |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    And la versione 2 dell'e-service "ES-1" predisposta dal delegato Comune di Pozzallo in attesa di approvazione
    When PagoPA cerca nel catalogo "E-service Versioni"
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And per l'e-service "ES-1" il risultato riporta:
      | proprietà           | valore |
      | versione presentata | 1      |

  @producer-delegation-settings
  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_5] Stessa lista per produttore, fruitore e delegato
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome            |
      | ES-1  | Comune di Milano | E-service Ruoli |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    When <tenant> cerca nel catalogo "E-service Ruoli"
    Then l'e-service "ES-1" è presente tra i risultati della ricerca

    Examples:
      | tenant             |
      | Comune di Milano   |
      | PagoPA             |
      | Comune di Pozzallo |

  # TODO: test di business valido solo per il canale BFF (l'indicazione "è mio"
  # non è replicabile sul frontend): non estenderlo agli altri canali.
  @producer-delegation-settings
  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_6] Indicazione "è mio" solo per il produttore
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome          |
      | ES-1  | Comune di Milano | E-service Mio |
    And l'e-service "ES-1" delegato in erogazione dal Comune di Milano al Comune di Pozzallo con delega attiva
    When <tenant> cerca nel catalogo "E-service Mio"
    Then per l'e-service "ES-1" il risultato riporta:
      | proprietà | valore   |
      | è mio     | <valore> |

    Examples:
      | tenant             | valore |
      | Comune di Milano   | sì     |
      | PagoPA             | no     |
      | Comune di Pozzallo | no     |

  @catalog-notification-settings
  Rule: Notifiche del catalogo per un fruitore esplicito
    Background:
      Given il fruitore PagoPA è abilitato alle notifiche in-app
      And l'utente admin del fruitore PagoPA è abilitato alle notifiche in-app

    Scenario: [PRIVATE_CATALOG_VISIBILITY_7] Notifica non letta presente per un ente abilitato alle notifiche
      Given i seguenti e-service pubblicati:
        | alias | erogatore        | nome                 |
        | ES-1  | Comune di Milano | E-service Notificato |
      And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
      And la versione 2 dell'e-service "ES-1" del Comune di Milano pubblicata
      And una notifica non letta generata per l'utente admin del fruitore PagoPA sull'e-service "ES-1"
      When PagoPA cerca nel catalogo "E-service Notificato"
      Then per l'e-service "ES-1" il risultato riporta:
        | proprietà           | valore |
        | notifiche non lette | sì     |

    # La notifica appartiene a PagoPA; la ricerca come Pozzallo conserva il controllo
    # negativo per un tenant che non è abilitato a ricevere notifiche.
    Scenario: [PRIVATE_CATALOG_VISIBILITY_8] Nessuna notifica visibile per un ente non abilitato
      Given i seguenti e-service pubblicati:
        | alias | erogatore        | nome                     |
        | ES-1  | Comune di Milano | E-service Non Notificato |
      And l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
      And la versione 2 dell'e-service "ES-1" del Comune di Milano pubblicata
      And una notifica non letta generata per l'utente admin del fruitore PagoPA sull'e-service "ES-1"
      When il Comune di Pozzallo cerca nel catalogo "E-service Non Notificato"
      Then la ricerca termina senza errori
      And per l'e-service "ES-1" il risultato riporta:
        | proprietà           | valore |
        | notifiche non lette | no     |
