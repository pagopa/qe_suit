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
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano            |
      | nome                 | E-service Canale Erogazione |
      | descrizione          | E-service.                  |
      | stato della versione | pubblicata                  |
      | modalità             | erogazione                  |
    And l'e-service "ES-2" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano           |
      | nome                 | E-service Canale Ricezione |
      | descrizione          | E-service.                 |
      | stato della versione | pubblicata                 |
      | modalità             | ricezione                  |
    When PagoPA cerca nel catalogo la parola "Canale" con i seguenti filtri:
      | filtro   | valore     |
      | modalità | <modalità> |
    Then tra i risultati compare l'e-service predisposto "<incluso>"
    And tra i risultati non compare l'e-service predisposto "<escluso>"

    Examples:
      | modalità   | incluso | escluso |
      | erogazione | ES-1    | ES-2    |
      | ricezione  | ES-2    | ES-1    |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_2] Senza filtro per modalità compaiono entrambe
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano            |
      | nome                 | E-service Flusso Erogazione |
      | descrizione          | E-service.                  |
      | stato della versione | pubblicata                  |
      | modalità             | erogazione                  |
    And l'e-service "ES-2" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano           |
      | nome                 | E-service Flusso Ricezione |
      | descrizione          | E-service.                 |
      | stato della versione | pubblicata                 |
      | modalità             | ricezione                  |
    When PagoPA cerca nel catalogo la parola "Flusso"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_3] Scambio asincrono: solo gli e-service con scambio asincrono
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Tempo Attivo |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
      | scambio asincrono    | sì                     |
    And l'e-service "ES-2" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Tempo Spento |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
      | scambio asincrono    | no                     |
    And l'e-service "ES-3" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Tempo Libero |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
      | scambio asincrono    | non impostato          |
    When PagoPA cerca nel catalogo la parola "Tempo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | sì     |
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_4] Scambio non asincrono: include anche i valori non impostati
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Ritmo Attivo |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
      | scambio asincrono    | sì                     |
    And l'e-service "ES-2" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Ritmo Spento |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
      | scambio asincrono    | no                     |
    And l'e-service "ES-3" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano       |
      | nome                 | E-service Ritmo Libero |
      | descrizione          | E-service.             |
      | stato della versione | pubblicata             |
      | scambio asincrono    | non impostato          |
    When PagoPA cerca nel catalogo la parola "Ritmo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | no     |
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_5] Senza filtro sullo scambio asincrono nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano      |
      | nome                 | E-service Onda Attivo |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
      | scambio asincrono    | sì                    |
    And l'e-service "ES-2" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano      |
      | nome                 | E-service Onda Spento |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
      | scambio asincrono    | no                    |
    And l'e-service "ES-3" è predisposto con la seguente configurazione:
      | erogatore            | Comune di Milano      |
      | nome                 | E-service Onda Libero |
      | descrizione          | E-service.            |
      | stato della versione | pubblicata            |
      | scambio asincrono    | non impostato         |
    When PagoPA cerca nel catalogo la parola "Onda"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
