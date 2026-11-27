@createNotification
Feature: Creazione e validazione di una notifica legale
  In qualità di PA mittente
  voglio inviare una notifica legale ai destinatari, persone fisiche o giuridiche
  così che venga accettata e recapitata, dal portale come tramite API

  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=WEB,When=WEB,Then=WEB
  Scenario: [CREAZIONE_NOTIFICA_MONO_PF] Una notifica a una persona fisica con indirizzo fisico viene accettata
    Given una notifica monodestinatario creata dalla PA Comune_1 con destinatario di tipo PF
    Then la notifica risulta accettata

  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=WEB,When=WEB,Then=WEB
  Scenario: [CREAZIONE_NOTIFICA_MONO_PG] Una notifica a una persona giuridica con domicilio digitale viene accettata
    Given una notifica monodestinatario creata dalla PA Comune_1 con destinatario di tipo PG con domicilio digitale
    Then la notifica risulta accettata

  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=WEB,When=WEB,Then=WEB
  Scenario: [CREAZIONE_NOTIFICA_MULTI_MISTA] Una notifica a una persona fisica e a una persona giuridica viene accettata
    Given una notifica multidestinatario mista creata dalla PA Comune_1
    Then la notifica risulta accettata

  # Solo BFF: dal portale la notifica si può inviare, ma una richiesta rifiutata non compare tra le notifiche della
  # PA e il portale non ne mostra né il rifiuto né la causa (il codice d'errore della validazione della richiesta).
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CREAZIONE_NOTIFICA_RIFIUTATA] Una notifica con l'indirizzo fisico non valido viene rifiutata (<variante>)
    Given la PA Comune_1 predispone una nuova notifica con i seguenti dati:
      | subject | Notifica con indirizzo fisico non valido |
    And è stato aggiunto User_1 come destinatario:
      | <campo>       | <valore> |
      | pagoPA_number | 0        |
      | F24_number    | 0        |
    When la notifica non conforme viene sottomessa
    Then la richiesta di notifica risulta rifiutata con l'errore "<errore>"

    Examples:
      | variante           | campo                        | valore            | errore            |
      | comune inesistente | physicalAddress_municipality | Comuneinesistente | NOT_VALID_ADDRESS |
      | stato inesistente  | physicalAddress_foreignState | Narnia            | NOT_VALID_ADDRESS |
