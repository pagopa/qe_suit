@fe @mittente @comunicazioniBonarie @scenario8
Feature: [FE] [PA] Dettaglio Comunicazione Bonaria - Visualizzazione e Feedback Canali

  Background:
    Given la PA Grossini è loggata al portale SEND tramite Browser

  @caso8.2 @canali
  Scenario Outline: [CASO_8_2] Visualizzazione canali abilitati per tipologia destinatario in base alla campagna
    Given una comunicazione bonaria di campagna "<tipoCampagna>" per il destinatario "<tipoDestinatario>" con IUN "<iun>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then la sezione dettaglio invio per canale mostra esclusivamente i canali "<canaliAbilitati>"

    Examples:
      | tipoCampagna      | tipoDestinatario | campaignId | iun                        | canaliAbilitati             |
      | FattOrd           | PF               | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A  | SEND, IO, EMAIL             |
      | FattOrd           | PF               | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A  | SEND, IO, EMAIL             |

  @caso8.3 @feedback
  Scenario Outline: [CASO_8_3] Feedback e stato dei canali in base al flusso della comunicazione
    Given una comunicazione bonaria con IUN "<iun>" e flusso in stato "<statoFlusso>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then il canale "<canale>" mostra la label di stato "<labelStato>"

    Examples:
      | campaignId | iun                        | statoFlusso          | canale           | labelStato      |
      | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A  | DEPOSITATA           | SEND             | DEPOSITATA      |
      | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A  | NON_CONSEGNATA       | IO               | NON CONSEGNATA  |
      | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A  | CONSEGNATA           | EMAIL            | CONSEGNATA      |
      | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A  | DEPOSITATA           | SEND             | DEPOSITATA      |
      | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A  | INVIATA              | IO               | INVIATA         |
      | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A  | NON_DISPONIBILE      | EMAIL            | NON DISPONIBILE |
