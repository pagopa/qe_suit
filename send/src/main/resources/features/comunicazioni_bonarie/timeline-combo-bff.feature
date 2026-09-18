@bff @comunicazioniBonarie @scenario5
Feature: Recupero Timeline Comunicazione Bonaria via BFF API

  Background:
    Given una sessione HTTP programmatica su BFF

  Scenario Outline: [SCENARIO_5_NEGATIVO] Errore per comunicazioni inesistenti, in bozza o rifiutate
    Given una comunicazione bonaria con IUN "<iun>" in stato "<statoInvalido>"
    When il mittente invoca la chiamata BFF per la timeline della campagna "<campaignId>" e IUN "<iun>"
    Then il sistema risponde con errore e non restituisce alcuna timeline per lo stato "<statoInvalido>"

    Examples:
      | campaignId | iun                 | statoInvalido |
      | CAMP_999   | IUN_DRAFT_111       | DRAFT         |
      | CAMP_999   | IUN_REFUSED_222     | REFUSED       |
      | CAMP_999   | IUN_NON_EXISTENT_33 | NOT_FOUND     |

  Scenario: [SCENARIO_5_POSITIVO] Recupero timeline e verifica ordinamento cronologico decrescente
    Given una comunicazione bonaria valida con IUN "IUN_TIMELINE_VALID_123" ed eventi registrati sui canali "SEND,IO,EMAIL,SMS"
    When il mittente invoca la chiamata BFF per la timeline della campagna "CAMP_200" e IUN "IUN_TIMELINE_VALID_123"
    Then la risposta JSON della timeline contiene iun "IUN_TIMELINE_VALID_123", destinatari e l'ultimo stato registrato
    And la lista degli eventi della timeline è ordinata rigorosamente dal più recente al meno recente
    And la timeline traccia correttamente i feedback per ciascun canale abilitato
