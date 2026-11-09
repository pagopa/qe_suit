@eservice-risk-analysis-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nelle analisi del rischio degli e-service in erogazione inversa

  Come amministratore di un ente erogatore
  Voglio ricevere un messaggio di errore nella lingua selezionata quando una finalità dell'e-service ha un nome già utilizzato
  Per scegliere un nome diverso e completare la configurazione dell'analisi del rischio.

  Scenario Outline: [ESERVICE_RISK_ANALYSIS_ERROR_MESSAGE_1] - Aggiunta di una finalità con nome già presente nell'e-service inverso
    Given i seguenti e-service in bozza:
      | erogatore        | modalità  |
      | Comune di Milano | ricezione |
    And una finalità con analisi del rischio valida nell'e-service e identificata come "finalità esistente"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'aggiunta di una finalità nell'e-service in erogazione inversa
    And l'utente tenta di salvare una nuova finalità con il nome della "finalità esistente" e gli altri dati validi
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_RISK_ANALYSIS_DUPLICATED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_RISK_ANALYSIS_ERROR_MESSAGE_2] - Modifica di una finalità con nome già presente nell'e-service inverso
    Given i seguenti e-service in bozza:
      | erogatore        | modalità  |
      | Comune di Milano | ricezione |
    And due finalità con analisi del rischio valide e nomi diversi nell'e-service, identificate come "finalità esistente" e "finalità da modificare"
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre la modifica della "finalità da modificare" nell'e-service in erogazione inversa
    And l'utente tenta di salvare il nome della "finalità esistente" per la "finalità da modificare" lasciando validi gli altri dati
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "CATALOG_RISK_ANALYSIS_DUPLICATED__POST_ESERVICES_E_SERVICE_ID_RISK_ANALYSIS_RISK_ANALYSIS_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
