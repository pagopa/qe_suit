@private-catalog
@private-catalog-delegations
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Filtro per ruolo di delega nel catalogo privato

  Come ente
  Voglio vedere nel catalogo gli e-service per cui sono delegato o che ho delegato
  Per distinguere il mio ruolo nelle deleghe di erogazione.

  Assunzioni:
  - contano solo le deleghe di erogazione (non quelle di fruizione) e solo nello stato attivo;
  - se si indicano più ruoli di delega, i risultati sono l'unione (OR) dei due insiemi;
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook.

  Scenario Outline: [PRIVATE_CATALOG_DELEGATIONS_1] Ruolo di delegato: conta solo la delega attiva
    Given un admin appartenente al fruitore Comune di Pozzallo
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                   | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Delegato Alfa | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Delegato Beta | Servizio.   | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega <stato delega>
    When Comune di Pozzallo cerca nel catalogo la parola "Delegato" tra gli e-service per cui è delegato
    Then tra i risultati <esito> l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"

    Examples:
      | stato delega              | esito       |
      | attiva                    | compare     |
      | in attesa di accettazione | non compare |
      | rifiutata                 | non compare |
      | revocata                  | non compare |

  Scenario Outline: [PRIVATE_CATALOG_DELEGATIONS_2] Ruolo di delegante: conta solo la delega attiva
    Given un admin appartenente all'erogatore Comune di Milano
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                    | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Delegante Alfa | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Delegante Beta | Servizio.   | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega <stato delega>
    When Comune di Milano cerca nel catalogo la parola "Delegante" tra gli e-service che ha delegato
    Then tra i risultati <esito> l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"

    Examples:
      | stato delega              | esito       |
      | attiva                    | compare     |
      | in attesa di accettazione | non compare |
      | rifiutata                 | non compare |
      | revocata                  | non compare |

  Scenario: [PRIVATE_CATALOG_DELEGATIONS_3] Entrambi i ruoli: unione dei risultati
    Given un admin appartenente al fruitore Comune di Pozzallo
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore          | nome                  | descrizione | stato della versione |
      | ES-1  | Comune di Milano   | Servizio Unione Alfa  | Servizio.   | pubblicata           |
      | ES-2  | Comune di Pozzallo | Servizio Unione Beta  | Servizio.   | pubblicata           |
      | ES-3  | Sogecap            | Servizio Unione Gamma | Servizio.   | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega attiva
    And l'erogatore Comune di Pozzallo ha delegato l'erogazione dell'e-service predisposto "ES-2" a Sogecap con delega attiva
    When Comune di Pozzallo cerca nel catalogo la parola "Unione" tra gli e-service per cui è delegato o che ha delegato
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
    And tra i risultati non compare l'e-service predisposto "ES-3"

  Scenario: [PRIVATE_CATALOG_DELEGATIONS_4] Senza filtro per ruolo di delega nessuna restrizione
    Given un admin appartenente al fruitore Comune di Pozzallo
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione | stato della versione |
      | ES-1  | Comune di Milano | Servizio Libero Alfa | Servizio.   | pubblicata           |
      | ES-2  | Comune di Milano | Servizio Libero Beta | Servizio.   | pubblicata           |
    And l'erogatore Comune di Milano ha delegato l'erogazione dell'e-service predisposto "ES-1" al Comune di Pozzallo con delega attiva
    When Comune di Pozzallo cerca nel catalogo la parola "Libero"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
