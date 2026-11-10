@purpose-template-metadata-update
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Aggiornamento parziale dei metadati di un Template Finalità

  @work-item:QA-19036 @old
  Scenario Outline: [PURPOSE_TEMPLATE_METADATA_UPDATE_1] Aggiornare i metadati senza alterare stato e configurazioni
    Given il creatore dispone di un Template Finalità nello stato <stato>
    When il creatore modifica i metadati applicando la variazione su "<campi modificati>"
    And il creatore salva e consulta nuovamente il template
    Then la piattaforma salva i metadati richiesti e conserva quelli omessi

    Examples:
      | stato      | campi modificati |
      | in bozza   | solo titolo      |
      | in bozza   | solo descrizione |
      | in bozza   | solo target      |
      | in bozza   | tutti            |

      | pubblicato | solo titolo      |
      | pubblicato | solo descrizione |
      | pubblicato | solo target      |
      | pubblicato | tutti            |

      | sospeso    | solo titolo      |
      | sospeso    | solo descrizione |
      | sospeso    | solo target      |
      | sospeso    | tutti            |

      | archiviato | solo titolo      |
      | archiviato | solo descrizione |
      | archiviato | solo target      |
      | archiviato | tutti            |

  Scenario: [PURPOSE_TEMPLATE_METADATA_PROPAGATION_1] Modificare i metadati senza alterare le Finalità già create
    Given il creatore dispone di un Template Finalità nello stato in bozza
    And il template è stato utilizzato per creare le seguenti Finalità:
      | stato    | ente creatore                                                 |
      | in bozza | diverso dal creatore del template                             |
      | attiva   | diverso dal creatore del template                             |
      | in bozza | stesso ente del creatore del template, se previsto dal flusso |
    When il creatore modifica tutti i metadati del Template Finalità
    And il creatore salva e consulta nuovamente il template
    And si rileggono le Finalità preesistenti
    Then il template presenta i nuovi metadati
    And le Finalità preesistenti conservano metadati, identificativi d'istanza, versioni e stati
