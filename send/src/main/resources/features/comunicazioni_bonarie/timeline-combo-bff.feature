@bff @comunicazioniBonarie @scenario5
Feature: [BFF] [PA] Recupero Timeline Comunicazione Bonaria via BFF API

  # Gli scenari di errore per stato invalido (DRAFT, REFUSED, NOT_FOUND) sono test di contratto dell'API
  # e si trovano in BffComboContractTest.java.
  # Questo file descrive solo flussi di business: come la PA mittente recupera la timeline di una comunicazione.

  Background:
    Given una sessione HTTP programmatica su BFF

  @positivo @ordinamento
  Scenario: [SCENARIO_5_POSITIVO_ORDINAMENTO] Recupero timeline e verifica ordinamento cronologico decrescente
    Given una comunicazione bonaria valida con IUN "${combo.iun.fattord.pf}" ed eventi registrati sui canali "SEND,IO,EMAIL,SMS"
    When viene recuperata la timeline per la campagna "FattOrd" e IUN "${combo.iun.fattord.pf}"
    Then la risposta JSON della timeline contiene iun "${combo.iun.fattord.pf}", destinatari e l'ultimo stato registrato
    And la lista degli eventi della timeline è ordinata rigorosamente dal più recente al meno recente
    And la timeline traccia correttamente i feedback per ciascun canale abilitato

  @positivo @esiti
  Scenario Outline: [SCENARIO_5_POSITIVO_ESITI] Recupero timeline per specifici esiti e ramificazioni del workflow
    Given una comunicazione bonaria valida con IUN "<iun>" per esito "<esitoFlusso>"
    When viene recuperata la timeline per la campagna "<campaignId>" e IUN "<iun>"
    Then la risposta JSON della timeline contiene iun "<iun>", destinatari e l'ultimo stato registrato
    And la timeline riflette l'esito di flusso "<esitoFlusso>"
    And la timeline traccia correttamente i feedback per ciascun canale abilitato

    Examples:
      | campaignId | iun                                    | esitoFlusso                   |
      | FattOrd    | ${combo.iun.fattord.pf}                | INVIO_RIUSCITO                |
      | FattOrd    | ${combo.iun.fattord.viewed}            | INTERROTTO_PER_LETTURA        |
      | Reminder   | ${combo.iun.reminder.flow-io-fail-ok}  | INTERROTTO_PER_CONSEGNA       |
      | Reminder   | ${combo.iun.reminder.flow-io-fail-excl}| INVIO_FALLITO                 |
      | MessaMora  | ${combo.iun.messamora.pf}              | INVIO_COMPLETATO_CON_KO       |
