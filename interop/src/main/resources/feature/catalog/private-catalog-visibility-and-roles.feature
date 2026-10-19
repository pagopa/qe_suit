@private-catalog
@private-catalog-visibility-and-roles
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Visibilità degli e-service nel catalogo privato

  Come utente di un ente
  Voglio vedere nel catalogo solo gli e-service con una versione fruibile
  Per non incontrare servizi non consultabili, qualunque sia il mio ruolo.

  Assunzioni sull'ambiente QA:
  - il Comune di Pozzallo NON è abilitato a ricevere notifiche;
  - PagoPA e il Comune di Milano sono abilitati a ricevere notifiche.
  Se la configurazione cambia, gli scenari sulle notifiche vanno rivisti.

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_1] Visibilità in base allo stato delle versioni
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione               | stato della versione |
      | ES-1  | Comune di Milano | Servizi di <esempio> | Descrizione di <esempio>. | <stato>              |
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione              | stato della versione |
      | ES-1  | Comune di Milano | Servizio Bozza | Servizio con sola bozza. | in bozza             |
    When Comune di Milano cerca nel catalogo la parola "Servizio Bozza"
    Then la ricerca termina senza errori
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_VISIBILITY_3] Versione pubblicata e nuova bozza: compare solo la versione pubblicata
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome            | descrizione            | stato della versione |
      | ES-1  | Comune di Milano | Servizio Doppio | Servizio con due fasi. | pubblicata           |
    And all'e-service predisposto "ES-1" viene aggiunta una nuova versione in bozza
    When PagoPA cerca nel catalogo la parola "Servizio Doppio"
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And per l'e-service predisposto "ES-1" è presentata la versione 1

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_4] Versione presentata nel risultato
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome              | descrizione           | stato della versione |
      | ES-1  | Comune di Milano | Servizio Versioni | Servizio con versioni | <prima versione>     |
    And all'e-service predisposto "ES-1" viene aggiunta una versione nello stato "<seconda versione>"
    When PagoPA cerca nel catalogo la parola "Servizio Versioni"
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And per l'e-service predisposto "ES-1" è presentata la versione <versione presentata>

    Examples:
      | prima versione | seconda versione          | versione presentata |
      | deprecata      | pubblicata                | 2                   |
      | pubblicata     | in bozza                  | 1                   |
      | pubblicata     | in attesa di approvazione | 1                   |
      | pubblicata     | archiviata                | 1                   |

  Scenario Outline: [PRIVATE_CATALOG_VISIBILITY_5] Stessa lista per produttore, fruitore e delegato
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome           | descrizione         | stato della versione |
      | ES-1  | Comune di Milano | Servizio Ruoli | Servizio per ruoli. | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo
    When <tenant> cerca nel catalogo la parola "Servizio Ruoli"
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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome         | descrizione       | stato della versione |
      | ES-1  | Comune di Milano | Servizio Mio | Servizio proprio. | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo
    When <tenant> cerca nel catalogo la parola "Servizio Mio"
    Then per l'e-service predisposto "ES-1" l'indicazione "è mio" è <valore>

    Examples:
      | tenant             | valore |
      | Comune di Milano   | vera   |
      | PagoPA             | falsa  |
      | Comune di Pozzallo | falsa  |

  Scenario: [PRIVATE_CATALOG_VISIBILITY_7] Notifica non letta presente per un ente abilitato alle notifiche
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione          | stato della versione |
      | ES-1  | Comune di Milano | Servizio Notificato | Servizio notificato. | pubblicata           |
    And viene generata una notifica non letta per il fruitore sull'e-service predisposto "ES-1"
    When PagoPA cerca nel catalogo la parola "Servizio Notificato"
    Then per l'e-service predisposto "ES-1" risultano notifiche non lette

  # Assunzione: il Comune di Pozzallo non è abilitato a ricevere notifiche.
  # Un esito "nessuna notifica" non distingue l'assenza di notifiche da un errore
  # del servizio notifiche: lo scenario verifica solo che la lista risponda.
  Scenario: [PRIVATE_CATALOG_VISIBILITY_8] Nessuna notifica per un ente non abilitato alle notifiche
    Given un admin appartenente al fruitore Comune di Pozzallo
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                    | descrizione              | stato della versione |
      | ES-1  | Comune di Milano | Servizio Non Notificato | Servizio non notificato. | pubblicata           |
    And viene generata una notifica non letta per il fruitore sull'e-service predisposto "ES-1"
    When Comune di Pozzallo cerca nel catalogo la parola "Servizio Non Notificato"
    Then la ricerca termina senza errori
    And per l'e-service predisposto "ES-1" non risultano notifiche non lette
