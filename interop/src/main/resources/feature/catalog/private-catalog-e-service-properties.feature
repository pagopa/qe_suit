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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                        | modalità   |
      | ES-1  | Comune di Milano | E-service Canale Erogazione | erogazione |
      | ES-2  | Comune di Milano | E-service Canale Ricezione  | ricezione  |
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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                        | modalità   |
      | ES-1  | Comune di Milano | E-service Flusso Erogazione | erogazione |
      | ES-2  | Comune di Milano | E-service Flusso Ricezione  | ricezione  |
    When PagoPA cerca nel catalogo "Flusso"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_3] Scambio asincrono: solo gli e-service con scambio asincrono
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Tempo Attivo | sì                |
      | ES-2  | Comune di Milano | E-service Tempo Spento | no                |
      # | ES-3  | Comune di Milano | E-service Tempo Libero | non impostato     | - In BFF nonostante la property non sia obbligatoria viene sollevato in 400 se non impostata, quindi non è possibile testare il caso "non impostato"
    When PagoPA cerca nel catalogo "Tempo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | sì     |
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And tra i risultati della ricerca non sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      # | ES-3  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_4] Scambio non asincrono: include anche i valori non impostati
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                   | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Ritmo Attivo | sì                |
      | ES-2  | Comune di Milano | E-service Ritmo Spento | no                |
      | ES-3  | Comune di Milano | E-service Ritmo Libero | non impostato     |
    When PagoPA cerca nel catalogo "Ritmo" con i seguenti filtri:
      | filtro            | valore |
      | scambio asincrono | no     |
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-2  |
      | ES-3  |
    And l'e-service "ES-1" non è presente tra i risultati della ricerca

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_5] Senza filtro sullo scambio asincrono nessuna restrizione
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                  | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Onda Attivo | si                |
      | ES-2  | Comune di Milano | E-service Onda Spento | no                |
      | ES-3  | Comune di Milano | E-service Onda Libero | non impostato     |
    When PagoPA cerca nel catalogo "Onda"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
