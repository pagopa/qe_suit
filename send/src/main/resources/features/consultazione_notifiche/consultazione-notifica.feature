@consultNotification
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Consultazione delle notifiche inviate e download dei documenti probatori
  In qualità di PA mittente
  voglio consultare stato, cronologia e documenti delle notifiche inviate
  così da verificarne la consegna e il perfezionamento e disporre delle attestazioni opponibili a terzi

  Scenario Outline: [CONSULTAZIONE_NOTIFICA_RICERCA] Una notifica in lavorazione cercata per <criterio> mostra stato e cronologia coerenti
    Given una notifica monodestinatario creata dalla PA Comune_1 con destinatario di tipo PF
    When la PA Comune_1 cerca la notifica per <criterio>
    Then la notifica compare tra i risultati della ricerca con il suo stato attuale
    And la cronologia della notifica è coerente con lo stato attuale

    Examples:
      | criterio      |
      | IUN           |
      | data di invio |

  # Il portale della PA mostra una sola data di perfezionamento per l'intera notifica, non una per destinatario:
  # con più destinatari la notifica risulta perfezionata quando lo è per tutti, ed è questa la data verificata.
  Scenario: [CONSULTAZIONE_NOTIFICA_PERFEZIONAMENTO] Una notifica giunta a conclusione riporta la data di perfezionamento
    Given una notifica multidestinatario mista creata dalla PA Comune_1
    And la notifica raggiunge lo stato "Perfezionata per decorrenza termini"
    When la PA Comune_1 consulta la cronologia della notifica
    Then la cronologia riporta la data di perfezionamento della notifica

  Scenario: [CONSULTAZIONE_NOTIFICA_DOWNLOAD_ATTI] Download dell'atto notificato e dell'avviso di avvenuta ricezione
    Given una notifica monodestinatario creata dalla PA Comune_1 con destinatario di tipo PF
    When la PA Comune_1 scarica l'atto della notifica
    And la PA Comune_1 scarica l'avviso di avvenuta ricezione della notifica
    Then i documenti vengono scaricati

  Scenario: [CONSULTAZIONE_NOTIFICA_DOWNLOAD_ATTESTAZIONI] Download delle attestazioni opponibili a terzi di una notifica perfezionata
    Given una notifica monodestinatario creata dalla PA Comune_1 con destinatario di tipo PG con domicilio digitale
    And la notifica raggiunge lo stato "Perfezionata per decorrenza termini"
    When la PA Comune_1 scarica le attestazioni opponibili a terzi della notifica
    Then le attestazioni vengono scaricate

  # Destinatari raggiunti entrambi in digitale: i loro eventi sono già in cronologia quando la notifica si perfeziona,
  # mentre l'invio cartaceo di un destinatario parte alcuni minuti dopo.
  Scenario: [CONSULTAZIONE_NOTIFICA_MULTI_DESTINATARIO] Cronologia e documenti sono distinti per ciascun destinatario
    Given una notifica multidestinatario creata dalla PA Comune_1 con destinatario di tipo PF con domicilio digitale
    And la notifica raggiunge lo stato "Perfezionata per decorrenza termini"
    When la PA Comune_1 consulta la cronologia della notifica
    Then la cronologia riporta gli eventi distinti per ciascun destinatario
    And per ogni destinatario è disponibile l'avviso di avvenuta ricezione
