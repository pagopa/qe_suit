@fe @mittente @comunicazioniBonarie @scenario8
Feature: [FE] [PA] Dettaglio Comunicazione Bonaria - Visualizzazione e Feedback Canali

  Background:
    Given la PA Grossini è loggata al portale SEND tramite Browser

  @caso8.1 @layout
  Scenario Outline: [CASO_8_1] Layout della pagina di dettaglio comunicazione con 5 sezioni dinamiche
    Given una comunicazione bonaria di campagna "<tipoCampagna>" per il destinatario "<tipoDestinatario>" con IUN "<iun>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then la pagina di dettaglio della comunicazione mostra le sezioni principali "Overview, Stato, Canali"
    And è presente il pulsante per accedere alla timeline

    Examples:
      | tipoCampagna | tipoDestinatario | campaignId | iun                       |
      | FattOrd      | PF               | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A |
      | FattOrd      | PG               | FattOrd    | MUZL-GJHU-DEHX-202609-N-A |

  @caso8.2 @canali
  Scenario Outline: [CASO_8_2] Visualizzazione canali abilitati per tipologia destinatario in base alla campagna
    Given una comunicazione bonaria di campagna "<tipoCampagna>" per il destinatario "<tipoDestinatario>" con IUN "<iun>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then la sezione dettaglio invio per canale mostra esclusivamente i canali "<canaliAbilitati>"

    Examples:
      | tipoCampagna | tipoDestinatario | campaignId | iun                       | canaliAbilitati             |
      | FattOrd      | PF               | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A | SEND, IO, EMAIL             |
      | FattOrd      | PG               | FattOrd    | MUZL-GJHU-DEHX-202609-N-A | SEND, PEC                   |
      | Reminder     | PF               | Reminder   | DPKG-ENTQ-KHZA-202609-V-A | SEND, IO, EMAIL, SMS        |
      | Reminder     | PG               | Reminder   | PXUQ-UTJE-QMGW-202609-V-A | SEND, PEC, SMS              |
      | MessaMora    | PF               | MessaMora  | EXVA-TVPL-QPVJ-202609-Q-A | SEND, IO, SERVIZIO POSTALE  |
      | MessaMora    | PG               | MessaMora  | HWUJ-TGYU-MEXW-202609-D-A | SEND, PEC, SERVIZIO POSTALE |

  @caso8.3 @feedback
  Scenario Outline: [CASO_8_3] Feedback e stato dei canali in base al flusso della comunicazione
    Given una comunicazione bonaria con IUN "<iun>" e flusso in stato "<statoFlusso>"
    When il mittente naviga sulla pagina di dettaglio della comunicazione con campagna "<campaignId>" e IUN "<iun>"
    Then il canale "<canale>" mostra la label di stato "<labelStato>"

    Examples:
      | campaignId | iun                       | statoFlusso     | canale | labelStato      |
      | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A | DEPOSITATA      | SEND   | DEPOSITATA      |
      | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A | NON_CONSEGNATA  | IO     | NON CONSEGNATA  |
      | FattOrd    | XKDZ-PXRU-ARWP-202609-J-A | CONSEGNATA      | EMAIL  | CONSEGNATA      |
      | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A | DEPOSITATA      | SEND   | DEPOSITATA      |
      | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A | INVIATA         | IO     | INVIATA         |
      | FattOrd    | GLAM-ZTPT-NZQG-202609-K-A | NON_DISPONIBILE | EMAIL  | NON DISPONIBILE |
