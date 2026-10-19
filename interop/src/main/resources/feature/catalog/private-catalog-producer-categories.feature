@private-catalog
@private-catalog-producer-categories
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtro per categoria dell'erogatore nel catalogo privato

  Come fruitore
  Voglio restringere il catalogo alle categorie degli erogatori
  Per vedere solo gli e-service di enti appartenenti a quelle categorie.

  Assunzioni:
  - le categorie degli enti sono lette dagli attributi certificati già presenti sui tenant QA e non vengono mai modificate;
  - se le relazioni attese tra gli enti non sono rispettate (stessa categoria o categorie diverse), lo scenario fallisce come precondizione non soddisfatta;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  # TODO: da coprire quando saranno noti i tenant QA adatti: categoria L4 sovrapposta,
  # categorie revocate e enti non IPA.

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_1] Una sola categoria
    Given le categorie degli enti rilevate dagli attributi certificati con i seguenti dati:
      | ente               | categoria |
      | Comune di Milano   | CAT-1     |
      | Comune di Pozzallo | CAT-1     |
      | Sogecap            | CAT-2     |
    And l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Categoria Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Pozzallo con i seguenti dati:
      | nome | E-service Categoria Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Categoria Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Categoria" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | CAT-1  |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_2] Più categorie in alternativa
    Given le categorie degli enti rilevate dagli attributi certificati con i seguenti dati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
      | AgID             | CAT-3     |
    And l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Duo Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Duo Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto da AgID con i seguenti dati:
      | nome | E-service Duo Gamma |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Duo" con i seguenti filtri:
      | filtro                   | valore        |
      | categoria dell'erogatore | CAT-1 e CAT-2 |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_3] Categoria senza e-service corrispondenti
    Given le categorie degli enti rilevate dagli attributi certificati con i seguenti dati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Assente Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    When PagoPA cerca nel catalogo "Assente" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | CAT-2  |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_4] Elenco di categorie vuoto: nessuna restrizione
    Given le categorie degli enti rilevate dagli attributi certificati con i seguenti dati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ambito Alfa |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto da Sogecap con i seguenti dati:
      | nome | E-service Ambito Beta |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    When PagoPA cerca nel catalogo "Ambito" con i seguenti filtri:
      | filtro                   | valore       |
      | categoria dell'erogatore | elenco vuoto |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
