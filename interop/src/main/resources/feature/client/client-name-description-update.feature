@client-name-description-update
Feature: Aggiornamento dei metadati del Client senza alterarne identità e associazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_NAME_DESCRIPTION_UPDATE_BFF_CONSUMER_1] Modifica dei metadati di un Client Consumer via BFF
    Given un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And vengono acquisite la configurazione iniziale, le chiavi e le associazioni del Client
    When l'amministratore modifica i metadati del Client applicando "<campi modificati>"
    And l'amministratore salva e consulta nuovamente il Client
    And l'amministratore verifica chiavi e associazioni confrontandole con i valori iniziali
    And viene richiesto un nuovo voucher con il Client aggiornato e la Finalità associata
    Then la piattaforma salva i metadati richiesti e conserva quelli omessi
    And identificativo, tipologia, proprietario, chiavi e associazioni del Client rimangono invariati
    And il nuovo voucher viene emesso correttamente

    Examples:
      | campi modificati |
      | solo nome        |
      | solo descrizione |
      | entrambi         |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_NAME_DESCRIPTION_UPDATE_BFF_API_1] Modifica dei metadati di un Client API via BFF
    Given un client API creato da PagoPA in cui è presente un admin e una coppia di chiavi crittografiche
    And vengono acquisite la configurazione iniziale, le chiavi e le associazioni del Client
    When l'amministratore modifica i metadati del Client applicando "<campi modificati>"
    And l'amministratore salva e consulta nuovamente il Client
    And l'amministratore verifica chiavi e associazioni confrontandole con i valori iniziali
    Then la piattaforma salva i metadati richiesti e conserva quelli omessi
    And identificativo, tipologia, proprietario, chiavi e associazioni del Client rimangono invariati

    Examples:
      | campi modificati |
      | solo nome        |
      | solo descrizione |
      | entrambi         |

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario Outline: [CLIENT_NAME_DESCRIPTION_UPDATE_M2M_CONSUMER_1] Modifica dei metadati di un Client Consumer via M2M v3
    Given un client CONSUMER creato da PagoPA, associato alla finalità, in cui è presente un admin e una coppia di chiavi crittografiche
    And vengono acquisite la configurazione iniziale, le chiavi e le associazioni del Client
    When l'amministratore modifica i metadati del Client applicando "<campi modificati>"
    And l'amministratore salva e consulta nuovamente il Client
    And l'amministratore verifica chiavi e associazioni confrontandole con i valori iniziali
    And viene richiesto un nuovo voucher con il Client aggiornato e la Finalità associata
    Then la piattaforma salva i metadati richiesti e conserva quelli omessi
    And identificativo, tipologia, proprietario, chiavi e associazioni del Client rimangono invariati
    And il nuovo voucher viene emesso correttamente

    Examples:
      | campi modificati |
      | solo nome        |
      | solo descrizione |
      | entrambi         |