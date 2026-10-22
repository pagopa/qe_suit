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
  - PagoPA e il Comune di Milano sono abilitati a ricevere notifiche.
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
      | Theta   | in attesa di approvazione | non è presente |

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
      | pubblicata     | in attesa di approvazione | 1                   |
      | pubblicata     | archiviata                | 1                   |

  @producer-delegation-settings
  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_5] Stessa lista per produttore, fruitore e delegato
    Given i seguenti enti abilitati a ricevere deleghe di erogazione:
      | ente               |
      | Comune di Pozzallo |
    And i seguenti e-service predisposti per la pubblicazione:
      | alias | erogatore        | nome            |
      | ES-1  | Comune di Milano | E-service Ruoli |
    And la versione 1 dell'e-service "ES-1" del Comune di Milano pubblicata
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

  Scenario: [PRIVATE_CATALOG_VISIBILITY_7] Notifica non letta presente per un ente abilitato alle notifiche
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 |
      | ES-1  | Comune di Milano | E-service Notificato |
    And una notifica non letta generata per il fruitore sull'e-service "ES-1"
    When PagoPA cerca nel catalogo "E-service Notificato"
    Then per l'e-service "ES-1" il risultato riporta:
      | proprietà           | valore |
      | notifiche non lette | sì     |

  # Assunzione: il Comune di Pozzallo non è abilitato a ricevere notifiche.
  # Un esito "nessuna notifica" non distingue l'assenza di notifiche da un errore
  # del servizio notifiche: lo scenario verifica solo che la lista risponda.
  Scenario: [PRIVATE_CATALOG_VISIBILITY_8] Nessuna notifica per un ente non abilitato alle notifiche
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                     |
      | ES-1  | Comune di Milano | E-service Non Notificato |
    And una notifica non letta generata per il fruitore sull'e-service "ES-1"
    When il Comune di Pozzallo cerca nel catalogo "E-service Non Notificato"
    Then la ricerca termina senza errori
    And per l'e-service "ES-1" il risultato riporta:
      | proprietà           | valore |
      | notifiche non lette | no     |
