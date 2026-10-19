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
    Given un admin appartenente al fruitore PagoPA
    And le categorie degli enti sono lette dagli attributi certificati:
      | ente               | categoria |
      | Comune di Milano   | CAT-1     |
      | Comune di Pozzallo | CAT-1     |
      | Sogecap            | CAT-2     |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore          | nome                      | descrizione | stato della versione |
      | ES-1  | Comune di Milano   | E-service Categoria Alfa  | E-service.  | pubblicata           |
      | ES-2  | Comune di Pozzallo | E-service Categoria Beta  | E-service.  | pubblicata           |
      | ES-3  | Sogecap            | E-service Categoria Gamma | E-service.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Categoria" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | CAT-1  |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service predisposto "ES-3"

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_2] Più categorie in alternativa
    Given un admin appartenente al fruitore PagoPA
    And le categorie degli enti sono lette dagli attributi certificati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
      | AgID             | CAT-3     |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Duo Alfa  | E-service.  | pubblicata           |
      | ES-2  | Sogecap          | E-service Duo Beta  | E-service.  | pubblicata           |
      | ES-3  | AgID             | E-service Duo Gamma | E-service.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Duo" con i seguenti filtri:
      | filtro                   | valore        |
      | categoria dell'erogatore | CAT-1 e CAT-2 |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service predisposto "ES-3"

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_3] Categoria senza e-service corrispondenti
    Given un admin appartenente al fruitore PagoPA
    And le categorie degli enti sono lette dagli attributi certificati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                   | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Assente Alfa | E-service.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Assente" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | CAT-2  |
    Then tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_4] Elenco di categorie vuoto: nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And le categorie degli enti sono lette dagli attributi certificati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                  | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Ambito Alfa | E-service.  | pubblicata           |
      | ES-2  | Sogecap          | E-service Ambito Beta | E-service.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Ambito" con i seguenti filtri:
      | filtro                   | valore       |
      | categoria dell'erogatore | elenco vuoto |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
