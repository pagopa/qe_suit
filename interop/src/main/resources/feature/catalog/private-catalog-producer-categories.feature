@private-catalog
@private-catalog-producer-categories
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtro per categoria dell'erogatore nel catalogo privato

  Come fruitore
  Voglio restringere il catalogo alle categorie degli erogatori
  Per vedere solo gli e-service di enti appartenenti a quelle categorie.

  Assunzioni:
  - le categorie degli enti sono lette dagli attributi certificati già presenti sui tenant QA e non vengono mai modificate;
  - l'appartenenza o non appartenenza alle categorie indicate è verificata come precondizione sui dati reali;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_1] Una sola categoria
    Given gli enti appartengono alle categorie indicate secondo i seguenti dati:
      | ente               | categoria | appartenenza |
      | Comune di Milano   | COMUNI    | sì           |
      | Comune di Pozzallo | COMUNI    | sì           |
      | Sogecap            | COMUNI    | no           |
    And i seguenti e-service pubblicati:
      | alias | erogatore          | nome                      |
      | ES-1  | Comune di Milano   | E-service Categoria Alfa  |
      | ES-2  | Comune di Pozzallo | E-service Categoria Beta  |
      | ES-3  | Sogecap            | E-service Categoria Gamma |
    When PagoPA cerca nel catalogo "Categoria" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | COMUNI |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_2] Più categorie in alternativa
    Given gli enti appartengono alle categorie indicate secondo i seguenti dati:
      | ente             | categoria                                    | appartenenza |
      | Comune di Milano | COMUNI                                       | sì           |
      | Comune di Milano | STAZIONI_APPALTANTI_GESTORI_PUBBLICI_SERVIZI | no           |
      | Kyma             | STAZIONI_APPALTANTI_GESTORI_PUBBLICI_SERVIZI | sì           |
      | Kyma             | COMUNI                                       | no           |
      | Sogecap          | COMUNI                                       | no           |
      | Sogecap          | STAZIONI_APPALTANTI_GESTORI_PUBBLICI_SERVIZI | no           |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome                |
      | ES-1  | Comune di Milano | E-service Duo Alfa  |
      | ES-2  | Kyma             | E-service Duo Beta  |
      | ES-3  | Sogecap          | E-service Duo Gamma |
    When PagoPA cerca nel catalogo "Duo" con i seguenti filtri:
      | filtro                   | valore                                                |
      | categoria dell'erogatore | COMUNI e STAZIONI_APPALTANTI_GESTORI_PUBBLICI_SERVIZI |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_3] Categoria senza e-service corrispondenti
    Given gli enti appartengono alle categorie indicate secondo i seguenti dati:
      | ente             | categoria | appartenenza |
      | Comune di Milano | COMUNI    | sì           |
      | Sogecap          | COMUNI    | no           |
    And i seguenti e-service pubblicati:
      | alias | erogatore | nome                   |
      | ES-1  | Sogecap   | E-service Assente Alfa |
    When PagoPA cerca nel catalogo "Assente" con i seguenti filtri:
      | filtro                   | valore |
      | categoria dell'erogatore | COMUNI |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_PRODUCER_CATEGORIES_4] Elenco di categorie vuoto: nessuna restrizione
    Given gli enti appartengono alle categorie indicate secondo i seguenti dati:
      | ente             | categoria | appartenenza |
      | Comune di Milano | COMUNI    | sì           |
      | Sogecap          | COMUNI    | no           |
    And i seguenti e-service pubblicati:
      | alias | erogatore        | nome                  |
      | ES-1  | Comune di Milano | E-service Ambito Alfa |
      | ES-2  | Sogecap          | E-service Ambito Beta |
    When PagoPA cerca nel catalogo "Ambito" con i seguenti filtri:
      | filtro                   | valore       |
      | categoria dell'erogatore | elenco vuoto |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
