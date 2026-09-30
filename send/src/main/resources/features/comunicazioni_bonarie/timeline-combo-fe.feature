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
      | iun                        | descrizioneFlusso                                   | logCanaliAttesi                                                                                                                                           | esitoFinale    | tipoBox |
      | XKDZ-PXRU-ARWP-202609-J-A  | Invio IO fallito ed invio EMAIL andato a buon fine | SEND:Depositata sulla piattaforma SEND., IO:Impossibile effettuare l'invio su IO., EMAIL:Consegna via email riuscita.                                    | Consegnata     | VERDE   |

  @caso9.2 @fattord @pg
  Scenario Outline: [CASO_9_2] Timeline campagna FattOrd per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "FattOrd" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "FattOrd" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                        | descrizioneFlusso                 | logCanaliAttesi                                                         | esitoFinale   | tipoBox |
      | MUZL-GJHU-DEHX-202609-N-A  | Invio e consegna via PEC riuscita | SEND:Depositata sulla piattaforma SEND., PEC:Consegna via PEC riuscita. | Letta         | VERDE   |

  @caso9.3 @reminder @pf
  Scenario Outline: [CASO_9_3] Timeline campagna Reminder per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "Reminder" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "Reminder" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                        | descrizioneFlusso                                   | logCanaliAttesi                                                                                                                           | esitoFinale   | tipoBox |
      | VGMV-XTEV-YZLQ-202609-U-A  | Invio IO fallito ed invio EMAIL andato a buon fine | SEND:Depositata sulla piattaforma SEND., IO:Impossibile effettuare l'invio su IO., EMAIL:Consegna via email riuscita.                    | Consegnata    | VERDE   |
      | DPKG-ENTQ-KHZA-202609-V-A  | Invio IO fallito ed email consegnata                | SEND:Depositata sulla piattaforma SEND., IO:Impossibile effettuare l'invio su IO., EMAIL:Consegna via email riuscita.                   | Consegnata    | VERDE   |
      | AWLP-DTLX-RNTH-202609-A-A  | Invio IO fallito e canali esclusi                   | SEND:Depositata sulla piattaforma SEND., IO:Impossibile effettuare l'invio su IO.                                                        | Invio fallito | ROSSO   |

  @caso9.4 @reminder @pg
  Scenario Outline: [CASO_9_4] Timeline campagna Reminder per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "Reminder" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "Reminder" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                        | descrizioneFlusso            | logCanaliAttesi                         | esitoFinale   | tipoBox |
      | PXUQ-UTJE-QMGW-202609-V-A  | Notifica visionata su SEND   | SEND:Depositata sulla piattaforma SEND. | Letta         | VERDE   |

  @caso9.5 @messamora @pf
  Scenario Outline: [CASO_9_5] Timeline campagna Messa in Mora per persona fisica (PF)
    Given una comunicazione bonaria inviata a PF per la campagna "MessaMora" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "MessaMora" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                        | descrizioneFlusso                     | logCanaliAttesi                                                                                                                           | esitoFinale   | tipoBox |
      | EXVA-TVPL-QPVJ-202609-Q-A  | Invio IO fallito e consegna analogica | SEND:Depositata sulla piattaforma SEND., IO:Impossibile effettuare l'invio su IO., SERVIZIO POSTALE:Consegna via raccomandata semplice riuscita. | Consegnata    | VERDE   |
      | TEUE-ZDGY-VETY-202609-Q-A  | Invio IO riuscito e consegna analogica | SEND:Depositata sulla piattaforma SEND., IO:Consegna su IO riuscita., SERVIZIO POSTALE:Consegna via raccomandata semplice riuscita.       | Consegnata    | VERDE   |

  @caso9.6 @messamora @pg
  Scenario Outline: [CASO_9_6] Timeline campagna Messa in Mora per persona giuridica (PG)
    Given una comunicazione bonaria inviata a PG per la campagna "MessaMora" con IUN "<iun>" e flusso "<descrizioneFlusso>"
    When il mittente naviga sulla pagina della timeline per la campagna "MessaMora" e IUN "<iun>"
    Then la timeline mostra gli eventi nell'ordine cronologico corretto
    And la timeline include i log dei canali "<logCanaliAttesi>"
    And la timeline mostra l'esito finale "<esitoFinale>" con box visivo "<tipoBox>"

    Examples:
      | iun                        | descrizioneFlusso         | logCanaliAttesi                                                         | esitoFinale   | tipoBox |
      | HWUJ-TGYU-MEXW-202609-D-A  | Invio su PEC e consegnata | SEND:Depositata sulla piattaforma SEND., PEC:Consegna via PEC riuscita. | Consegnata    | VERDE   |
