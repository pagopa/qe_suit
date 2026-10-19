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
      | alias | erogatore          | nome                     | descrizione | stato della versione |
      | ES-1  | Comune di Milano   | Servizio Categoria Alfa  | Servizio.   | pubblicata           |
      | ES-2  | Comune di Pozzallo | Servizio Categoria Beta  | Servizio.   | pubblicata           |
      | ES-3  | Sogecap            | Servizio Categoria Gamma | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Categoria" tra i servizi degli erogatori della categoria CAT-1
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
      | alias | erogatore        | nome               | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Duo Alfa  | Servizio.   | pubblicata           |
      | ES-2  | Sogecap          | Servizio Duo Beta  | Servizio.   | pubblicata           |
      | ES-3  | AgID             | Servizio Duo Gamma | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Duo" tra i servizi degli erogatori delle categorie CAT-1 e CAT-2
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
      | alias | erogatore        | nome                  | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Assente Alfa | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Assente" tra i servizi degli erogatori della categoria CAT-2
    Then la ricerca termina senza errori
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_4] Elenco di categorie vuoto: nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And le categorie degli enti sono lette dagli attributi certificati:
      | ente             | categoria |
      | Comune di Milano | CAT-1     |
      | Sogecap          | CAT-2     |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Ambito Alfa | Servizio.   | pubblicata           |
      | ES-2  | Sogecap          | Servizio Ambito Beta | Servizio.   | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Ambito" con l'elenco delle categorie vuoto
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
