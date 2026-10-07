@fe @mittente @comunicazioniBonarie @scenario9
Feature: [FE] [PA] Timeline Comunicazione Bonaria - Flussi Fallback e Log Eventi

  # Il controllo del layout della pagina timeline (caricamento componenti, card eventi) è un test di contratto
  # e si trova in WebComboTimelineContractTest.java.
  # Questo file descrive i flussi di business: gli esiti per campagna, tipologia destinatario e canale.

  Background:
    Given la PA Grossini è loggata al portale SEND tramite Browser

  @caso9.1 @fattord @pf
  Scenario Outline: [CASO_9_1] Timeline campagna FattOrd per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "FattOrd" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "FattOrd" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                                         | descrizioneFlusso                                               | logCanaliAttesi                                                                                                         | esitoFinale               | tipoBox |
      | ${combo.iun.fattord.flow-io-fail-email-ok}  | CF assente su IO, Invio su MAIL riuscito                        | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, EMAIL:Consegna via email riuscita.                    | Consegnata                | VERDE   |
      | ${combo.iun.fattord.viewed}                 | Visionata e letta su portale SEND (workflow terminato)           | SEND:Depositata sulla piattaforma SEND.                                                                                 | Letta                     | VERDE   |
      | ${combo.iun.fattord.viewed}                 | CF su IO, Invio su MAIL riuscito, Esito lettura IO              | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., EMAIL:Consegna via email riuscita.               | Letta                     | VERDE   |
      | ${combo.iun.fattord.flow-email-unavail}     | CF assente su IO, Invio su MAIL non recuperato (Undeliverable) | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, EMAIL:Impossibile risalire                            | Invio fallito             | ROSSO   |
      | ${combo.iun.fattord.pf-unreached}           | CF su IO, Invio su MAIL fallito                                 | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita.                                                    | Invio completato con KO   | ROSSO   |
      | ${combo.iun.fattord.flow-io-ok-email-unavail}| CF su IO, MAIL non recuperata                                   | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., EMAIL:Impossibile risalire                        | Consegnata                | VERDE   |
      | ${combo.iun.fattord.pf-unreached}           | CF assente su IO, Invio su MAIL fallito                         | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, EMAIL:Invio e consegna ko                             | Invio completato con KO   | ROSSO   |

  @caso9.2 @fattord @pg
  Scenario Outline: [CASO_9_2] Timeline campagna FattOrd per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "FattOrd" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "FattOrd" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                             | descrizioneFlusso                                      | logCanaliAttesi                                                         | esitoFinale               | tipoBox |
      | ${combo.iun.fattord.pg}         | Invio e consegna via PEC riuscita                      | SEND:Depositata sulla piattaforma SEND., PEC:Consegna via PEC riuscita. | Consegnata                | VERDE   |
      | ${combo.iun.fattord.pg-unreached}| Invio e consegna su PEC fallita                        | SEND:Depositata sulla piattaforma SEND., PEC:Invio e KO                 | Invio completato con KO   | ROSSO   |
      | ${combo.iun.fattord.pg-unreached}| PEC non recuperata (Undeliverable)                     | SEND:Depositata sulla piattaforma SEND., PEC:Impossibile risalire       | Invio fallito             | ROSSO   |
      | ${combo.iun.fattord.pg-viewed}  | Invio e lettura su SEND, PEC non inviata (interrotto)  | SEND:Depositata sulla piattaforma SEND.                                 | Letta                     | VERDE   |

  @caso9.3 @reminder @pf
  Scenario Outline: [CASO_9_3] Timeline campagna Reminder per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "Reminder" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "Reminder" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                                                 | descrizioneFlusso                                        | logCanaliAttesi                                                                                                         | esitoFinale    | tipoBox |
      | ${combo.iun.reminder.flow-io-fail-email-delivered}  | CF su IO, Invio su MAIL riuscito, Invio su SMS non fatto | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., MAIL:Consegna via email riuscita.                 | Consegnata     | VERDE   |
      | ${combo.iun.reminder.viewed}                        | CF su IO, Esito lettura su SEND/IO, altri canali non inv | SEND:Depositata sulla piattaforma SEND.                                                                                 | Letta          | VERDE   |
      | ${combo.iun.reminder.flow-io-fail-email-delivered}  | CF su IO, Invio su MAIL fallito, Invio su SMS riuscito   | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., SMS:Consegna via SMS riuscita.                    | Consegnata     | VERDE   |
      | ${combo.iun.reminder.flow-io-fail-email-delivered}  | CF su IO, MAIL ed SMS non recuperati                     | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., MAIL:Impossibile risalire, SMS:Impossibile risalire| Consegnata     | VERDE   |
      | ${combo.iun.reminder.flow-io-fail-email-delivered}  | CF assente su IO, Invio su MAIL riuscito, SMS non fatto  | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, MAIL:Consegna via email riuscita.                     | Consegnata     | VERDE   |
      | ${combo.iun.reminder.flow-io-fail-email-delivered}  | CF assente su IO, Invio MAIL fallito, Invio su SMS ok    | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, MAIL:Invio e KO, SMS:Consegna via SMS riuscita.       | Consegnata     | VERDE   |
      | ${combo.iun.reminder.flow-io-fail-excluded}         | CF assente su IO, Invio MAIL e SMS non recuperati        | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, MAIL:Impossibile risalire, SMS:Impossibile risalire   | Invio fallito  | ROSSO   |

  @caso9.4 @reminder @pg
  Scenario Outline: [CASO_9_4] Timeline campagna Reminder per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "Reminder" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "Reminder" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                               | descrizioneFlusso                               | logCanaliAttesi                                                                  | esitoFinale   | tipoBox |
      | ${combo.iun.reminder.pg}                            | Invio su PEC ok, Invio su SMS non effettuato    | SEND:Depositata sulla piattaforma SEND., PEC:Consegna via PEC riuscita.          | Consegnata    | VERDE   |
      | ${combo.iun.reminder.flow-pec-fail-sms-ok}          | Invio su PEC ko, Invio su SMS ok                | SEND:Depositata sulla piattaforma SEND., PEC:Invio e KO, SMS:Invio e consegna    | Consegnata    | VERDE   |
      | ${combo.iun.reminder.flow-pec-fail-sms-unavail}     | Invio su PEC ko, SMS non recuperato             | SEND:Depositata sulla piattaforma SEND., PEC:Invio e KO, SMS:Impossibile risalire| Consegnata    | VERDE   |
      | ${combo.iun.reminder.pg-unreached}                  | PEC ed SMS non recuperati (Undeliverable)       | SEND:Depositata sulla piattaforma SEND., PEC:Impossibile risalire, SMS:Impossibile risalire | Invio fallito | ROSSO |

  @caso9.5 @messamora @pf
  Scenario Outline: [CASO_9_5] Timeline campagna Messa in Mora per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "MessaMora" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "MessaMora" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                                      | descrizioneFlusso                             | logCanaliAttesi                                                                                                       | esitoFinale             | tipoBox |
      | ${combo.iun.messamora.flow-io-ok-analog} | CF su IO, Indirizzo analogico OK              | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., SERVIZIO POSTALE:Consegna via raccomandata     | Consegnata              | VERDE   |
      | ${combo.iun.messamora.pg-unreached}      | CF su IO, Indirizzo analogico KO              | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., SERVIZIO POSTALE:Invio e KO                    | Invio completato con KO | ROSSO   |
      | ${combo.iun.messamora.flow-io-ok-analog} | CF su IO, Indirizzo analogico recuperato VAS  | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., SERVIZIO POSTALE:Consegna via raccomandata     | Consegnata              | VERDE   |
      | ${combo.iun.messamora.flow-io-fail-analog}| CF assente su IO, Indirizzo analogico OK     | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, SERVIZIO POSTALE:Consegna via raccomandata         | Consegnata              | VERDE   |
      | ${combo.iun.messamora.pg-unreached}      | CF assente su IO, Indirizzo analogico KO      | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, SERVIZIO POSTALE:Invio e KO                        | Invio completato con KO | ROSSO   |
      | ${combo.iun.messamora.pg-unreached}      | CF assente su IO, Indirizzo non recuperato    | SEND:Depositata sulla piattaforma SEND., IO:Impossibile risalire, SERVIZIO POSTALE:Impossibile risalire              | Invio fallito           | ROSSO   |

  @caso9.6 @messamora @pg
  Scenario Outline: [CASO_9_6] Timeline campagna Messa in Mora per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "MessaMora" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "MessaMora" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                                 | descrizioneFlusso                                | logCanaliAttesi                                                                                     | esitoFinale             | tipoBox |
      | ${combo.iun.messamora.pg}           | Invio su PEC e consegnata, Analogico non fatto   | SEND:Depositata sulla piattaforma SEND., PEC:Consegna via PEC riuscita.                             | Consegnata              | VERDE   |
      | ${combo.iun.messamora.pg}           | Invio su PEC KO, Indirizzo analogico OK          | SEND:Depositata sulla piattaforma SEND., PEC:Invio e KO, SERVIZIO POSTALE:Consegna via raccomandata | Consegnata              | VERDE   |
      | ${combo.iun.messamora.pg-unreached} | Invio su PEC KO, Indirizzo analogico KO          | SEND:Depositata sulla piattaforma SEND., PEC:Invio e KO, SERVIZIO POSTALE:Invio e KO                | Invio completato con KO | ROSSO   |
      | ${combo.iun.messamora.pg}           | Invio su PEC KO, Indirizzo analogico recuperato  | SEND:Depositata sulla piattaforma SEND., PEC:Invio e KO, SERVIZIO POSTALE:Consegna via raccomandata | Consegnata              | VERDE   |
      | ${combo.iun.messamora.pg-unreached} | PEC non recuperata, Indirizzo non recuperato     | SEND:Depositata sulla piattaforma SEND., PEC:Impossibile risalire, SERVIZIO POSTALE:Impossibile risalire | Invio fallito      | ROSSO   |

