@bff @comunicazioniBonarie @scenario4
Feature: Recupero Dettaglio Comunicazione Bonaria via BFF API

  Background:
    Given una sessione HTTP programmatica su BFF

  Scenario: [SCENARIO_4_NEGATIVO_404] Errore se la comunicazione non esiste
    When il mittente invoca la chiamata BFF di dettaglio per la campagna "CAMP_001" e IUN "NON_EXISTENT_IUN"
    Then il sistema risponde con errore 404 ed esito non trovato

  Scenario: [SCENARIO_4_NEGATIVO_403] Errore se la comunicazione appartiene a un'altra PA
    Given la PA "Comune di Milano" richiede il dettaglio di una comunicazione creata dalla PA "Grossini"
    When il mittente invoca la chiamata BFF di dettaglio per la campagna "CAMP_002" e IUN "IUN_UNAUTHORIZED_890"
    Then il sistema risponde con errore 403 accesso non autorizzato

  Scenario Outline: [SCENARIO_4_POSITIVO] Recupero dettaglio comunicazione bonaria valida per profilazione destinatario <tipoDestinatario>
    Given una comunicazione bonaria di campagna "<tipoCampagna>" per il destinatario "<tipoDestinatario>" con IUN "<iun>"
    When il mittente invoca la chiamata BFF di dettaglio per la campagna "<campaignId>" e IUN "<iun>"
    Then il JSON di risposta del BFF contiene i metadati della comunicazione
    And il testo del messaggio e le sezioni condizionali per allegati e pagamenti sono coerenti
    And la lista dei canali abilitati corrisponde al profilo destinatario "<tipoDestinatario>" per la campagna "<tipoCampagna>"

    Examples:
      | tipoCampagna      | tipoDestinatario | campaignId | iun             |
      | FATTURA_ORDINARIA | PF               | CAMP_101   | IUN_FATT_PF_01  |
      | FATTURA_ORDINARIA | PG               | CAMP_102   | IUN_FATT_PG_01  |
      | REMINDER          | PF               | CAMP_103   | IUN_REM_PF_01   |
      | MESSA_IN_MORA     | PG               | CAMP_104   | IUN_MORA_PG_01  |
