@private-catalog
@private-catalog-e-service-properties
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtri sulle proprietà dell'e-service nel catalogo privato

  Come fruitore
  Voglio restringere il catalogo per modalità di scambio dati e per scambio asincrono
  Per trovare gli e-service con le caratteristiche tecniche che mi servono.

  Assunzioni:
  - la modalità può essere "erogazione" (DELIVER) o "ricezione" (RECEIVE);
  - lo scambio asincrono può essere "sì", "no" oppure "non impostato";
  - "non impostato" è trattato come "no" dal filtro;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook;
  - ogni scenario usa una parola chiave improbabile per delimitare il perimetro dei risultati.

  Scenario Outline: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_1] Filtro per modalità
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome     | E-service Canale Erogazione |
      | modalità | erogazione                  |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome     | E-service Canale Ricezione |
      | modalità | ricezione                  |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    When PagoPA cerca nel catalogo "Canale" con i seguenti filtri:
      | filtro   | valore     |
      | modalità | <modalità> |
    Then l'e-service "<incluso>" è presente tra i risultati della ricerca
    And l'e-service "<escluso>" non è presente tra i risultati della ricerca

    Examples:
      | modalità   | incluso | escluso |
      | erogazione | ES-1    | ES-2    |
      | ricezione  | ES-2    | ES-1    |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_2] Senza filtro per modalità compaiono entrambe
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome     | E-service Flusso Erogazione |
      | modalità | erogazione                  |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome     | E-service Flusso Ricezione |
      | modalità | ricezione                  |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" pubblicata
    When PagoPA cerca nel catalogo "Flusso"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_3] Scambio asincrono: solo gli e-service con scambio asincrono
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Tempo Attivo |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Tempo Spento |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" configurata con i seguenti dati:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Tempo Libero |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" configurata con i seguenti dati:
      | scambio asincrono | non impostato |
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Tempo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_4] Scambio non asincrono: include anche i valori non impostati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ritmo Attivo |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ritmo Spento |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" configurata con i seguenti dati:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Ritmo Libero |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" configurata con i seguenti dati:
      | scambio asincrono | non impostato |
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Ritmo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | no     |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |
    And l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_5] Senza filtro sullo scambio asincrono nessuna restrizione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Onda Attivo |
    And la versione 1 dell'e-service "ES-1" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-1" configurata con i seguenti dati:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" pubblicata
    And l'e-service "ES-2" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Onda Spento |
    And la versione 1 dell'e-service "ES-2" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-2" configurata con i seguenti dati:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-2" pubblicata
    And l'e-service "ES-3" predisposto dal Comune di Milano con i seguenti dati:
      | nome | E-service Onda Libero |
    And la versione 1 dell'e-service "ES-3" predisposta per la pubblicazione
    And la versione 1 dell'e-service "ES-3" configurata con i seguenti dati:
      | scambio asincrono | non impostato |
    And la versione 1 dell'e-service "ES-3" pubblicata
    When PagoPA cerca nel catalogo "Onda"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
