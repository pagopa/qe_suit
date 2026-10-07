@bff @comunicazioniBonarie @scenario4
Feature: [BFF] [PA] Recupero Dettaglio Comunicazione Bonaria via BFF API

  # Gli scenari di errore HTTP (404, 403) sono test di contratto dell'API e si trovano in BffComboContractTest.java.
  # Questo file descrive solo flussi di business: come la PA mittente recupera i dati di una comunicazione.

  Background:
    Given una sessione HTTP programmatica su BFF

  @positivo @profili
  Scenario Outline: [SCENARIO_4_POSITIVO_PROFILI] Recupero dettaglio comunicazione bonaria per profilazione destinatario e campagna
    Given una comunicazione bonaria di campagna "<tipoCampagna>" per il destinatario "<tipoDestinatario>" con IUN "<iun>"
    When viene recuperato il dettaglio per la campagna "<campaignId>" con IUN "<iun>"
    Then il JSON di risposta del BFF contiene i metadati della comunicazione
    And il testo del messaggio e le sezioni condizionali per allegati e pagamenti sono coerenti
    And la lista dei canali abilitati corrisponde al profilo destinatario "<tipoDestinatario>" per la campagna "<tipoCampagna>"

    Examples:
      | tipoCampagna      | tipoDestinatario | campaignId | iun                          |
      | FATTURA_ORDINARIA | PF               | FattOrd    | ${combo.iun.fattord.pf}      |
      | FATTURA_ORDINARIA | PG               | FattOrd    | ${combo.iun.fattord.pg}      |
      | REMINDER          | PF               | Reminder   | ${combo.iun.reminder.pf}     |
      | REMINDER          | PG               | Reminder   | ${combo.iun.reminder.pg}     |
      | MESSA_IN_MORA     | PF               | MessaMora  | ${combo.iun.messamora.pf}    |
      | MESSA_IN_MORA     | PG               | MessaMora  | ${combo.iun.messamora.pg}    |

  @positivo @condizioni
  Scenario Outline: [SCENARIO_4_POSITIVO_CONDIZIONI] Recupero dettaglio comunicazione con condizioni su allegati, pagamenti e workflow
    Given una comunicazione bonaria con condizione "<condizione>" e IUN "<iun>"
    When viene recuperato il dettaglio per la campagna "<campaignId>" con IUN "<iun>"
    Then il JSON di risposta del BFF contiene i metadati della comunicazione
    And le sezioni per allegati e pagamenti risultano coerenti con la condizione "<condizione>"

    Examples:
      | campaignId | iun                                    | condizione                   |
      | FattOrd    | ${combo.iun.fattord.pf}                | TUTTI_I_CAMPI_COMPILATI      |
      | FattOrd    | ${combo.iun.fattord.flow-email-unavail}| SENZA_ALLEGATI_E_PAGAMENTI   |
      | FattOrd    | ${combo.iun.fattord.viewed}            | WORKFLOW_INTERROTTO_DA_FLUSSO|
      | Reminder   | ${combo.iun.reminder.viewed}           | WORKFLOW_INTERROTTO_DA_FLUSSO|
      | MessaMora  | ${combo.iun.messamora.pf}              | ALLEGATI_SCADUTI             |
