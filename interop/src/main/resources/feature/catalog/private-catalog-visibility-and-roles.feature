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
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service di <esempio>    |
      | descrizione          | Descrizione di <esempio>. |
      | stato della versione | <stato>                   |
    When PagoPA cerca nel catalogo la parola "<esempio>"
    Then tra i risultati <esito> l'e-service predisposto "ES-1"

    Examples: Versioni fruibili
      | esempio | stato      | esito   |
      | Alfa    | pubblicata | compare |
      | Beta    | sospesa    | compare |

    Examples: Versioni non più fruibili
      | esempio | stato                    | esito       |
      | Gamma   | deprecata                | non compare |
      | Delta   | in archiviazione         | non compare |
      | Epsilon | in archiviazione sospesa | non compare |
      | Zeta    | archiviata               | non compare |

    Examples: Versioni non ancora pubblicate
      | esempio | stato                     | esito       |
      | Eta     | in bozza                  | non compare |
      | Theta   | in attesa di approvazione | non compare |

  Scenario: [PRIVATE_CATALOG_VISIBILITY_2] Un e-service con sola bozza non compare nemmeno per il produttore
    Given un admin appartenente all'erogatore Comune di Milano
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service Bozza           |
      | descrizione          | E-service con sola bozza. |
      | stato della versione | in bozza                  |
    When Comune di Milano cerca nel catalogo la parola "E-service Bozza"
    Then la ricerca termina senza errori
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_VISIBILITY_3] Versione pubblicata e nuova bozza: compare solo la versione pubblicata
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano        |
      | nome                 | E-service Doppio        |
      | descrizione          | E-service con due fasi. |
      | stato della versione | pubblicata              |
    And all'e-service "ES-1" viene aggiunta una versione con la seguente configurazione:
      | stato della versione | bozza |
    When PagoPA cerca nel catalogo la parola "E-service Doppio"
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And per l'e-service predisposto "ES-1" il risultato riporta:
      | proprietà           | valore |
      | versione presentata | 1      |

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_4] Versione presentata nel risultato
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Versioni     |
      | descrizione          | E-service con versioni |
      | stato della versione | <prima versione>       |
    And all'e-service "ES-1" viene aggiunta una versione con la seguente configurazione:
      | stato della versione | <seconda versione> |
    When PagoPA cerca nel catalogo la parola "E-service Versioni"
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And per l'e-service predisposto "ES-1" il risultato riporta:
      | proprietà           | valore                |
      | versione presentata | <versione presentata> |

    Examples:
      | prima versione | seconda versione          | versione presentata |
      | deprecata      | pubblicata                | 2                   |
      | pubblicata     | in bozza                  | 1                   |
      | pubblicata     | in attesa di approvazione | 1                   |
      | pubblicata     | archiviata                | 1                   |

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_5] Stessa lista per produttore, fruitore e delegato
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano     |
      | nome                 | E-service Ruoli      |
      | descrizione          | E-service per ruoli. |
      | stato della versione | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega attiva
    When <tenant> cerca nel catalogo la parola "E-service Ruoli"
    Then tra i risultati compare l'e-service predisposto "ES-1"

    Examples:
      | tenant             |
      | Comune di Milano   |
      | PagoPA             |
      | Comune di Pozzallo |

  # TODO: test di business valido solo per il canale BFF (l'indicazione "è mio"
  # non è replicabile sul frontend): non estenderlo agli altri canali.
  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_6] Indicazione "è mio" solo per il produttore
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano   |
      | nome                 | E-service Mio      |
      | descrizione          | E-service proprio. |
      | stato della versione | pubblicata         |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega attiva
    When <tenant> cerca nel catalogo la parola "E-service Mio"
    Then per l'e-service predisposto "ES-1" il risultato riporta:
      | proprietà | valore   |
      | è mio     | <valore> |

    Examples:
      | tenant             | valore |
      | Comune di Milano   | sì     |
      | PagoPA             | no     |
      | Comune di Pozzallo | no     |

  Scenario: [PRIVATE_CATALOG_VISIBILITY_7] Notifica non letta presente per un ente abilitato alle notifiche
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano      |
      | nome                 | E-service Notificato  |
      | descrizione          | E-service notificato. |
      | stato della versione | pubblicata            |
    And viene generata una notifica non letta per il fruitore sull'e-service predisposto "ES-1"
    When PagoPA cerca nel catalogo la parola "E-service Notificato"
    Then per l'e-service predisposto "ES-1" il risultato riporta:
      | proprietà           | valore |
      | notifiche non lette | sì     |

  # Assunzione: il Comune di Pozzallo non è abilitato a ricevere notifiche.
  # Un esito "nessuna notifica" non distingue l'assenza di notifiche da un errore
  # del servizio notifiche: lo scenario verifica solo che la lista risponda.
  Scenario: [PRIVATE_CATALOG_VISIBILITY_8] Nessuna notifica per un ente non abilitato alle notifiche
    Given un admin appartenente al fruitore Comune di Pozzallo
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano          |
      | nome                 | E-service Non Notificato  |
      | descrizione          | E-service non notificato. |
      | stato della versione | pubblicata                |
    And viene generata una notifica non letta per il fruitore sull'e-service predisposto "ES-1"
    When Comune di Pozzallo cerca nel catalogo la parola "E-service Non Notificato"
    Then la ricerca termina senza errori
    And per l'e-service predisposto "ES-1" il risultato riporta:
      | proprietà           | valore |
      | notifiche non lette | no     |
