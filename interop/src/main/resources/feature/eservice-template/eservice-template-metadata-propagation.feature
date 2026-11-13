@PIN-11319
Feature: Propagazione dei metadati del template e-service alle istanze
  Come creatore di un template e-service
  Voglio aggiornare i metadati ereditati dalle istanze
  Per mantenere coerenti le istanze preservando etichette e configurazioni proprie.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_PROPAGATION_1] - Propagazione dei metadati ereditati alle istanze
  Dato un template e-service pubblicato e istanziato da erogatori diversi con etichette distinte,
  quando il creatore ne modifica nome e descrizione,
  allora le istanze interessate ereditano i nuovi valori e conservano etichette e configurazioni proprie.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | TE-1  | Comune di Milano | Template anagrafico | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | erogatore        | suffisso |
      | EI-1  | TE-1     | Comune di Milano | Milano   |
      | EI-2  | TE-1     | PagoPA           | PagoPA   |
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service "TE-1":
      | campo       | valore                              |
      | nome        | Template anagrafico aggiornato      |
      | descrizione | Descrizione aggiornata del template |
    Then il template e-service "TE-1" presenta i metadati richiesti
    And le istanze "EI-1" e "EI-2" presentano i metadati ereditati dal template "TE-1"
    And le istanze "EI-1" e "EI-2" conservano etichette, descrittori e configurazioni proprie

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_PROPAGATION_2] - Conservazione delle istanze quando cambiano soltanto i destinatari
  Dato un template e-service pubblicato con più istanze,
  quando il creatore modifica soltanto il testo dei destinatari,
  allora le istanze mantengono nome, descrizione, etichetta e configurazioni.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | TE-1  | Comune di Milano | Template anagrafico | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso |
      | EI-1  | TE-1     | Milano   |
      | EI-2  | TE-1     | Seconda  |
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service "TE-1":
      | campo       | valore                           |
      | destinatari | Testo dei destinatari aggiornato |
    Then il template e-service "TE-1" presenta i metadati richiesti
    And le istanze "EI-1" e "EI-2" conservano nome, descrizione, etichette e configurazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_PROPAGATION_3] - Rifiuto atomico della rinomina in conflitto con un'istanza
  Dato un template e-service pubblicato con un'istanza etichettata e un e-service omonimo al nome che risulterebbe dalla rinomina,
  quando il creatore modifica nome e descrizione del template,
  allora la modifica viene rifiutata e template e istanza restano invariati.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                | tecnologia |
      | TE-1  | Comune di Milano | Template anagrafico | REST       |
    And le seguenti istanze di template e-service pubblicate:
      | alias | template | suffisso |
      | EI-1  | TE-1     | Milano   |
    And i seguenti e-service in bozza:
      | alias | erogatore        | nome                           |
      | ES-1  | Comune di Milano | Nuovo nome in conflitto Milano |
    When un admin del Comune di Milano richiede la modifica dei metadati del template e-service "TE-1":
      | campo       | valore                              |
      | nome        | Nuovo nome in conflitto             |
      | descrizione | Descrizione valida ma non applicata |
    Then la modifica del template e-service "TE-1" viene rifiutata
    And il template e-service "TE-1" conserva tutti i metadati iniziali
    And l'istanza "EI-1" conserva i propri metadati e configurazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [ESERVICE_TEMPLATE_PROPAGATION_4] - Rifiuto della rinomina in conflitto con un altro template
  Dati due template e-service pubblicati con nomi distinti,
  quando il creatore rinomina il primo con il nome del secondo e modifica anche la descrizione,
  allora l'intera modifica viene rifiutata.

    Given i seguenti template e-service pubblicati:
      | alias | erogatore        | nome                    | tecnologia |
      | TE-1  | Comune di Milano | Template anagrafico     | REST       |
      | TE-2  | Comune di Milano | Template anagrafico bis | REST       |
    When un admin del Comune di Milano richiede la modifica del template e-service "TE-1" usando il nome di "TE-2" e una descrizione diversa
    Then la modifica del template e-service "TE-1" viene rifiutata
    And il template e-service "TE-1" conserva tutti i metadati iniziali
