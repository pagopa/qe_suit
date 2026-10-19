@private-catalog
@private-catalog-template-filters
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtri sui template nel catalogo privato

  Come fruitore
  Voglio distinguere gli e-service creati da un template dagli altri
  Per trovare le istanze di un template specifico.

  Assunzioni:
  - un e-service creato da un template è detto "istanza di template";
  - template, e-service e istanze sono creati dallo scenario stesso dall'erogatore Comune di Milano;
  - l'istanza prende il nome del template: la parola chiave cercata è nel nome del template;
  - gli e-service e i template predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_1] Solo istanze di template
    Given un admin appartenente all'erogatore Comune di Milano
    And sono predisposti i seguenti template di e-service:
      | alias | nome                  | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Modello Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And sono predisposte le seguenti istanze di template:
      | alias | template | stato della versione |
      | ES-2  | TPL-1    | pubblicata           |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                   | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Modello Beta | E-service.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Modello" con i seguenti filtri:
      | filtro              | valore |
      | istanze di template | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-2"
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_2] Senza filtro compaiono sia le istanze sia gli altri e-service
    Given un admin appartenente all'erogatore Comune di Milano
    And sono predisposti i seguenti template di e-service:
      | alias | nome                | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Forma Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And sono predisposte le seguenti istanze di template:
      | alias | template | stato della versione |
      | ES-2  | TPL-1    | pubblicata           |
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano | E-service Forma Beta | E-service.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Forma"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_3] Solo istanze di un template specifico
    Given un admin appartenente all'erogatore Comune di Milano
    And sono predisposti i seguenti template di e-service:
      | alias | nome                 | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Stampo Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
      | TPL-2 | Template Stampo Beta | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And sono predisposte le seguenti istanze di template:
      | alias | template | stato della versione |
      | ES-1  | TPL-1    | pubblicata           |
      | ES-2  | TPL-1    | pubblicata           |
      | ES-3  | TPL-2    | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Stampo" con i seguenti filtri:
      | filtro               | valore |
      | istanza del template | TPL-1  |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service predisposto "ES-3"

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_4] Template senza istanze
    Given un admin appartenente all'erogatore Comune di Milano
    And sono predisposti i seguenti template di e-service:
      | alias | nome                | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Vuoto Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
      | TPL-2 | Template Vuoto Beta | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And sono predisposte le seguenti istanze di template:
      | alias | template | stato della versione |
      | ES-1  | TPL-2    | pubblicata           |
    When PagoPA cerca nel catalogo la parola "Vuoto" con i seguenti filtri:
      | filtro               | valore |
      | istanza del template | TPL-1  |
    Then tra i risultati non compare l'e-service predisposto "ES-1"
