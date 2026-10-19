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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Canale Erogazione |
      | descrizione | E-service.                  |
      | modalità    | erogazione                  |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Canale Ricezione |
      | descrizione | E-service.                 |
      | modalità    | ricezione                  |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Canale" con i seguenti filtri:
      | filtro   | valore     |
      | modalità | <modalità> |
    Then tra i risultati compare l'e-service "<incluso>"
    And tra i risultati non compare l'e-service "<escluso>"

    Examples:
      | modalità   | incluso | escluso |
      | erogazione | ES-1    | ES-2    |
      | ricezione  | ES-2    | ES-1    |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_2] Senza filtro per modalità compaiono entrambe
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Flusso Erogazione |
      | descrizione | E-service.                  |
      | modalità    | erogazione                  |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Flusso Ricezione |
      | descrizione | E-service.                 |
      | modalità    | ricezione                  |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Flusso"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_3] Scambio asincrono: solo gli e-service con scambio asincrono
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Tempo Attivo |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-1" con la seguente configurazione:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Tempo Spento |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-2" con la seguente configurazione:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Tempo Libero |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-3" con la seguente configurazione:
      | scambio asincrono | non impostato |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Tempo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | sì     |
    Then tra i risultati compare l'e-service "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_4] Scambio non asincrono: include anche i valori non impostati
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ritmo Attivo |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-1" con la seguente configurazione:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ritmo Spento |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-2" con la seguente configurazione:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Ritmo Libero |
      | descrizione | E-service.             |
    And la versione 1 dell'e-service "ES-3" con la seguente configurazione:
      | scambio asincrono | non impostato |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Ritmo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | no     |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service "ES-1"

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_5] Senza filtro sullo scambio asincrono nessuna restrizione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Onda Attivo |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-1" con la seguente configurazione:
      | scambio asincrono | sì |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Onda Spento |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-2" con la seguente configurazione:
      | scambio asincrono | no |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | E-service Onda Libero |
      | descrizione | E-service.            |
    And la versione 1 dell'e-service "ES-3" con la seguente configurazione:
      | scambio asincrono | non impostato |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "Onda"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
