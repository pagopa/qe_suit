@fe @mittente @comunicazioniBonarie @scenario8
Feature: [FE] [PA] Dettaglio Comunicazione Bonaria - Visualizzazione e Feedback Canali

  # Il controllo del layout della pagina (sezioni principali, pulsante timeline) è un test di contratto
  # e si trova in WebComboDetailsContractTest.java.
  # Questo file descrive i flussi di business: quali canali vengono mostrati e come cambia il loro stato.

  Background:
    Given la PA Grossini è loggata al portale SEND tramite Browser

  @caso8.1 @sezioniDinamiche
  Scenario Outline: [CASO_8_1_DINAMICHE] Visualizzazione condizionale delle sezioni Documenti e Pagamenti
    Given una comunicazione bonaria di campagna "<campaignId>" per il destinatario "PF" con IUN "<iun>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then la sezione "Documenti" è visibile: "<mostraDocumenti>"
    And la sezione "Pagamenti" è visibile: "<mostraPagamenti>"

    Examples:
      | campaignId | iun                                          | mostraDocumenti | mostraPagamenti |
      | FattOrd    | ${combo.iun.fattord.viewed}                  | true            | true            |
      | FattOrd    | ${combo.iun.fattord.no-docs-no-payments}     | false           | false           |

  @caso8.2 @canali
  Scenario Outline: [CASO_8_2] Visualizzazione canali abilitati per tipologia destinatario in base alla campagna
    Given una comunicazione bonaria di campagna "<tipoCampagna>" per il destinatario "<tipoDestinatario>" con IUN "<iun>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then la sezione dettaglio invio per canale mostra esclusivamente i canali "<canaliAbilitati>"

    Examples:
      | tipoCampagna | tipoDestinatario | campaignId | iun                                                | canaliAbilitati             |
      | FattOrd      | PF               | FattOrd    | ${combo.iun.fattord.flow-io-fail-email-ok}         | SEND, IO, EMAIL             |
      | FattOrd      | PG               | FattOrd    | ${combo.iun.fattord.pg}                            | SEND, PEC                   |
      | Reminder     | PF               | Reminder   | ${combo.iun.reminder.flow-io-fail-email-delivered} | SEND, IO, EMAIL, SMS        |
      | Reminder     | PG               | Reminder   | ${combo.iun.reminder.pg}                            | SEND, PEC, SMS              |
      | MessaMora    | PF               | MessaMora  | ${combo.iun.messamora.pf}                          | SEND, IO, SERVIZIO POSTALE  |
      | MessaMora    | PG               | MessaMora  | ${combo.iun.messamora.pg}                          | SEND, PEC, SERVIZIO POSTALE |

  @caso8.3 @feedback @labelStati
  Scenario Outline: [CASO_8_3_LABELS] Verifica label di stato per singolo canale in base all'avanzamento del workflow
    Given una comunicazione bonaria con IUN "<iun>" e flusso in stato "<statoFlusso>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then il canale "<canale>" mostra la label di stato "<labelStato>"

    Examples:
      | campaignId | iun                                          | statoFlusso       | canale           | labelStato      |
      | FattOrd    | ${combo.iun.fattord.flow-io-fail-email-ok}   | DEPOSITATA        | SEND             | DEPOSITATA      |
      | FattOrd    | ${combo.iun.fattord.flow-io-fail-email-ok}   | NON_CONSEGNATA    | IO               | NON CONSEGNATA  |
      | FattOrd    | ${combo.iun.fattord.flow-io-fail-email-ok}   | CONSEGNATA        | EMAIL            | CONSEGNATA      |
      | FattOrd    | ${combo.iun.fattord.flow-io-ok-email-unavail}| DEPOSITATA        | SEND             | DEPOSITATA      |
      | FattOrd    | ${combo.iun.fattord.flow-io-ok-email-unavail}| CONSEGNATA        | IO               | CONSEGNATA      |
      | FattOrd    | ${combo.iun.fattord.flow-io-ok-email-unavail}| NON_DISPONIBILE   | EMAIL            | NON DISPONIBILE |
      | FattOrd    | ${combo.iun.fattord.viewed}                  | LETTA             | SEND             | LETTA           |
      | Reminder   | ${combo.iun.reminder.viewed}                 | LETTA             | SEND             | LETTA           |
      | Reminder   | ${combo.iun.reminder.pg}                     | CONSEGNATA        | PEC              | CONSEGNATA      |
      | MessaMora  | ${combo.iun.messamora.pf}                    | CONSEGNATA        | SERVIZIO POSTALE | CONSEGNATA      |
      | MessaMora  | ${combo.iun.messamora.pg}                    | CONSEGNATA        | PEC              | CONSEGNATA      |

  @caso8.3 @feedback @invioInCorso
  Scenario Outline: [CASO_8_3_INVIO_IN_CORSO] Descrizione per canali in attesa durante invio in corso
    Given una comunicazione bonaria per la campagna "<campaignId>" con flusso in stato "INVIO_IN_CORSO"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then il canale "<canale>" mostra la label di stato "IN ATTESA"
    And il canale "<canale>" mostra la descrizione "In attesa del completamento dell'invio sul canale precedente."

    Examples:
      | campaignId | iun                                 | canale |
      | FattOrd    | ${combo.iun.fattord.in-progress}    | EMAIL  |
      | Reminder   | ${combo.iun.reminder.in-progress}   | SMS    |

  @caso8.3 @feedback @workflowCompletato
  Scenario Outline: [CASO_8_3_WORKFLOW_COMPLETATO] Testo esplicativo per canali non utilizzati per workflow interrotto
    Given una comunicazione bonaria con IUN "<iun>" e flusso in stato "WORKFLOW_COMPLETATO"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then il canale "<canale>" non mostra alcun badge ed ha la descrizione "Non sarà utilizzata, perché la comunicazione ha già raggiunto l'esito richiesto."

    Examples:
      | campaignId | iun                                 | canale           |
      | FattOrd    | ${combo.iun.fattord.viewed}         | EMAIL            |
      | Reminder   | ${combo.iun.reminder.interrupted}    | EMAIL            |
      | Reminder   | ${combo.iun.reminder.interrupted}    | SMS              |
      | MessaMora  | ${combo.iun.messamora.pg-viewed}    | SERVIZIO POSTALE |
