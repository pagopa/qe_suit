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
    Given i seguenti template di e-service predisposti:
      | alias | nome                  | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Modello Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And le seguenti istanze di template predisposte:
      | alias | template |
      | ES-2  | TPL-1    |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Modello Beta |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    When PagoPA cerca nel catalogo "Modello" con i seguenti filtri:
      | filtro              | valore |
      | istanze di template | sì     |
    Then l'e-service "ES-2" è presente tra i risultati della ricerca
    And l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_2] Senza filtro compaiono sia le istanze sia gli altri e-service
    Given i seguenti template di e-service predisposti:
      | alias | nome                | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Forma Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And le seguenti istanze di template predisposte:
      | alias | template |
      | ES-2  | TPL-1    |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Forma Beta |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    When PagoPA cerca nel catalogo "Forma"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_3] Solo istanze di un template specifico
    Given i seguenti template di e-service predisposti:
      | alias | nome                 | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Stampo Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
      | TPL-2 | Template Stampo Beta | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And le seguenti istanze di template predisposte:
      | alias | template |
      | ES-1  | TPL-1    |
      | ES-2  | TPL-1    |
      | ES-3  | TPL-2    |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Stampo" con i seguenti filtri:
      | filtro               | valore |
      | istanza del template | TPL-1  |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_TEMPLATE_FILTERS_4] Template senza istanze
    Given i seguenti template di e-service predisposti:
      | alias | nome                | tecnologia | stato     | interfaccia              |
      | TPL-1 | Template Vuoto Alfa | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
      | TPL-2 | Template Vuoto Beta | SOAP       | PUBLISHED | soap11-un-indirizzo.wsdl |
    And le seguenti istanze di template predisposte:
      | alias | template |
      | ES-1  | TPL-2    |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    When PagoPA cerca nel catalogo "Vuoto" con i seguenti filtri:
      | filtro               | valore |
      | istanza del template | TPL-1  |
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca
