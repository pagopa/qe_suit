@eservice-template-name-description-update
@channel:Given=BFF,When=WEB,Then=WEB
Feature: La modifica dei metadati non deve creare o modificare versioni funzionali né alterare le configurazioni operative del template.

  @work-item:QA-19036
  Scenario Outline: [ESERVICE_TEMPLATE_NAME_DESCRIPTION_UPDATE_1] Salvare i metadati senza modificare versioni e configurazioni

    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.yaml |
    When il creatore modifica "<ambito_modifica>" dei metadati
    Then i metadati modificati corrispondono ai valori richiesti e i metadati omessi conservano i valori iniziali

    Examples:
      | situazione_versioni                       | ambito_modifica            |
      | nessuna versione                          | solo nome                  |
      | nessuna versione                          | solo descrizione           |
      | nessuna versione                          | solo target                |
      | nessuna versione                          | tutti i campi modificabili |

      | una versione in bozza                     | solo nome                  |
      | una versione in bozza                     | solo descrizione           |
      | una versione in bozza                     | solo target                |
      | una versione in bozza                     | tutti i campi modificabili |

      | una versione pubblicata                   | solo nome                  |
      | una versione pubblicata                   | solo descrizione           |
      | una versione pubblicata                   | solo target                |
      | una versione pubblicata                   | tutti i campi modificabili |

      | una versione sospesa                      | solo nome                  |
      | una versione sospesa                      | solo descrizione           |
      | una versione sospesa                      | solo target                |
      | una versione sospesa                      | tutti i campi modificabili |

      | solo versioni deprecate                   | solo nome                  |
      | solo versioni deprecate                   | solo descrizione           |
      | solo versioni deprecate                   | solo target                |
      | solo versioni deprecate                   | tutti i campi modificabili |

      | una versione pubblicata e una nuova bozza | solo nome                  |
      | una versione pubblicata e una nuova bozza | solo descrizione           |
      | una versione pubblicata e una nuova bozza | solo target                |
      | una versione pubblicata e una nuova bozza | tutti i campi modificabili |


  Scenario Outline: [ESERVICE_TEMPLATE_METADATA_PROPAGATION_1] Propagare i metadati alle istanze senza alterare etichette e descrittori
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.yaml |
    And il template dispone di istanze create da erogatori distinti con etichette distinguibili
    When il creatore modifica i metadati del template applicando "<modifica>"
    Then il salvataggio del template conserva i metadati previsti
    And si attende l'allineamento delle istanze interessate
    And testi, etichette e configurazioni delle istanze corrispondono ai valori attesi

    Examples:
      | modifica                   |
      | solo nome                  |
      | solo descrizione           |
      | solo target                |
      | nome e descrizione insieme |

  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [ESERVICE_TEMPLATE_NAME_COLLISION_1] Rifiutare la rinomina del template che genera una collisione
    Given un admin appartenente all'erogatore Comune di Milano
    And un template e-service preparato con la seguente configurazione:
      | tecnologia | stato     | interfaccia               |
      | REST       | PUBLISHED | openapi3-riferimenti.yaml |
    And il template dispone di istanze create da erogatori distinti con etichette distinguibili
    And esiste una risorsa che determina la collisione "<collisione>"
    When il creatore richiede la rinomina del template includendo una descrizione valida e differente
    Then la piattaforma rifiuta l'intera modifica
    And rileggendo il template nome e descrizione restano invariati
    And le istanze rilette non ricevono aggiornamenti derivanti dalla richiesta rifiutata

    Examples:
      | collisione                                                                       |
      | nome coincidente con un altro Template E-service                                  |
      | nome composto dell'istanza coincidente con un E-service dello stesso erogatore   |
      | nome coincidente con differenze soltanto di maiuscole e minuscole                |
