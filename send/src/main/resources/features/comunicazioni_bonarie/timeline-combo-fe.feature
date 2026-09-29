@fe @mittente @comunicazioniBonarie @scenario9
Feature: [FE] [PA] Timeline Comunicazione Bonaria - Flussi Fallback e Log Eventi

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
      | iun                        | descrizioneFlusso                                   | logCanaliAttesi                                      | esitoFinale    | tipoBox |
      | XKDZ-PXRU-ARWP-202609-J-A  | Invio IO fallito ed invio EMAIL andato a buon fine | SEND:depositata, IO:invio, EMAIL:invio e consegna    | INVIO_RIUSCITO | NESSUNO |
      | GLAM-ZTPT-NZQG-202609-K-A  | Invio IO effettuato ed EMAIL non reperita           | SEND:depositata, IO:invio                            | UNDELIVERABLE  | ROSSO   |

  @caso9.2 @fattord @pg @ignore
  Scenario Outline: [CASO_9_2] Timeline campagna FattOrd per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "FattOrd" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "FattOrd" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun             | descrizioneFlusso              | logCanaliAttesi                       | esitoFinale    | tipoBox |
      | IUN_FATT_PG_OK  | Invio e consegna via PEC riuscita | SEND:depositata, PEC:invio e consegna | INVIO_RIUSCITO | NESSUNO |
      | IUN_FATT_PG_KO  | Invio e consegna su PEC fallita| SEND:depositata, PEC:invio e KO       | INVIO_KO       | NESSUNO |
      | IUN_FATT_PG_UND | PEC non recuperata             | SEND:depositata, PEC:Impossibile risalire | UNDELIVERABLE | ROSSO   |
      | IUN_FATT_PG_LET | Invio e lettura su SEND        | SEND:depositata                       | LETTURA        | VERDE   |

  @caso9.3 @reminder @pf @ignore
  Scenario Outline: [CASO_9_3] Timeline campagna Reminder per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "Reminder" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "Reminder" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun             | descrizioneFlusso                      | logCanaliAttesi                                            | esitoFinale    | tipoBox |
      | IUN_REM_PF_MAIL | Invio su MAIL riuscito                 | SEND:depositata, IO:invio e consegna, MAIL:invio e consegna | CONSEGNATA     | VERDE   |
      | IUN_REM_PF_LET  | Esito lettura su SEND o IO             | SEND:depositata, IO:invio consegna lettura                 | LETTURA        | VERDE   |
      | IUN_REM_PF_SMS  | Invio MAIL fallito e invio SMS         | SEND:depositata, IO:invio e consegna, MAIL:invio e KO, SMS:invio e consegna | INVIO_RIUSCITO | NESSUNO |
      | IUN_REM_PF_UND  | Tutti i canali non reperiti            | SEND:depositata, IO:Impossibile risalire, MAIL:Impossibile risalire, SMS:Impossibile risalire | UNDELIVERABLE | ROSSO |

  @caso9.4 @reminder @pg @ignore
  Scenario Outline: [CASO_9_4] Timeline campagna Reminder per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "Reminder" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "Reminder" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun             | descrizioneFlusso             | logCanaliAttesi                                  | esitoFinale    | tipoBox |
      | IUN_REM_PG_PEC  | Invio su PEC ok               | SEND:depositata, PEC:invio e consegna            | CONSEGNATA     | VERDE   |
      | IUN_REM_PG_SMS  | PEC fallita e SMS effettuato  | SEND:depositata, PEC:invio e KO, SMS:invio       | INVIO_RIUSCITO | NESSUNO |
      | IUN_REM_PG_UND  | PEC ed SMS non recuperati     | SEND:depositata, PEC:impossibile risalire, SMS:impossibile risalire | UNDELIVERABLE | ROSSO |

  @caso9.5 @messamora @pf @ignore
  Scenario Outline: [CASO_9_5] Timeline campagna Messa in Mora per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "MessaMora" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "MessaMora" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun             | descrizioneFlusso               | logCanaliAttesi                                                                  | esitoFinale | tipoBox |
      | IUN_MORA_PF_OK  | Invio su IO e RS consegnata     | SEND:depositata, IO:invio e consegna, SERVIZIO POSTALE:Invio e Consegna RS       | CONSEGNATA  | VERDE   |
      | IUN_MORA_PF_KO  | Invio su IO e RS fallita        | SEND:depositata, IO:invio e consegna, SERVIZIO POSTALE:Invio e KO RS             | INVIO_KO    | NESSUNO |
      | IUN_MORA_PF_VAS | Indirizzo analogico da VAS      | SEND:depositata, IO:invio e consegna, SERVIZIO POSTALE:Invio e Consegna RS       | CONSEGNATA  | VERDE   |
      | IUN_MORA_PF_UND | CF assente su IO e no analogico | SEND:depositata, IO:impossibile risalire, SERVIZIO POSTALE:Impossibile risalire | UNDELIVERABLE | ROSSO |

  @caso9.6 @messamora @pg @ignore
  Scenario Outline: [CASO_9_6] Timeline campagna Messa in Mora per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "MessaMora" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "MessaMora" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun             | descrizioneFlusso               | logCanaliAttesi                                                         | esitoFinale | tipoBox |
      | IUN_MORA_PG_PEC | Invio su PEC e consegnata       | SEND:depositata, PEC:invio e consegna                                   | CONSEGNATA  | VERDE   |
      | IUN_MORA_PG_RS  | Invio su PEC KO e RS consegnata | SEND:depositata, PEC:invio e KO, SERVIZIO POSTALE:Invio e consegna     | CONSEGNATA  | VERDE   |
      | IUN_MORA_PG_KO  | PEC KO e RS fallita             | SEND:depositata, PEC:invio e KO, SERVIZIO POSTALE:Invio e ko            | INVIO_KO    | NESSUNO |
      | IUN_MORA_PG_UND | PEC non reperita e no analogico | SEND:depositata, PEC:impossibile risalire, SERVIZIO POSTALE:Impossibile risalire | UNDELIVERABLE | ROSSO |
