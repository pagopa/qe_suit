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
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                        | descrizione | stato della versione | modalità   |
      | ES-1  | Comune di Milano | E-service Canale Erogazione | E-service.  | pubblicata           | erogazione |
      | ES-2  | Comune di Milano | E-service Canale Ricezione  | E-service.  | pubblicata           | ricezione  |
    When PagoPA cerca nel catalogo la parola "Canale" solo tra gli e-service in modalità <modalità>
    Then tra i risultati compare l'e-service predisposto "<incluso>"
    And tra i risultati non compare l'e-service predisposto "<escluso>"

    Examples:
      | modalità   | incluso | escluso |
      | erogazione | ES-1    | ES-2    |
      | ricezione  | ES-2    | ES-1    |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_2] Senza filtro per modalità compaiono entrambe
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                        | descrizione | stato della versione | modalità   |
      | ES-1  | Comune di Milano | E-service Flusso Erogazione | E-service.  | pubblicata           | erogazione |
      | ES-2  | Comune di Milano | E-service Flusso Ricezione  | E-service.  | pubblicata           | ricezione  |
    When PagoPA cerca nel catalogo la parola "Flusso"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_3] Scambio asincrono: solo gli e-service con scambio asincrono
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                   | descrizione | stato della versione | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Tempo Attivo | E-service.  | pubblicata           | sì                |
      | ES-2  | Comune di Milano | E-service Tempo Spento | E-service.  | pubblicata           | no                |
      | ES-3  | Comune di Milano | E-service Tempo Libero | E-service.  | pubblicata           | non impostato     |
    When PagoPA cerca nel catalogo la parola "Tempo" solo tra gli e-service con scambio asincrono
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_4] Scambio non asincrono: include anche i valori non impostati
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                   | descrizione | stato della versione | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Ritmo Attivo | E-service.  | pubblicata           | sì                |
      | ES-2  | Comune di Milano | E-service Ritmo Spento | E-service.  | pubblicata           | no                |
      | ES-3  | Comune di Milano | E-service Ritmo Libero | E-service.  | pubblicata           | non impostato     |
    When PagoPA cerca nel catalogo la parola "Ritmo" solo tra gli e-service senza scambio asincrono
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service predisposto "ES-1"

  Scenario: [PRIVATE_CATALOG_E_SERVICE_PROPERTIES_5] Senza filtro sullo scambio asincrono nessuna restrizione
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                  | descrizione | stato della versione | scambio asincrono |
      | ES-1  | Comune di Milano | E-service Onda Attivo | E-service.  | pubblicata           | sì                |
      | ES-2  | Comune di Milano | E-service Onda Spento | E-service.  | pubblicata           | no                |
      | ES-3  | Comune di Milano | E-service Onda Libero | E-service.  | pubblicata           | non impostato     |
    When PagoPA cerca nel catalogo la parola "Onda"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
