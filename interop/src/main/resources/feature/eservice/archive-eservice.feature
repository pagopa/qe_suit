@eservice-archiving
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Coerenza dell'archiviazione di un e-service e delle sue versioni

  Come erogatore proprietario di un e-service o ente delegato alla sua erogazione
  Voglio che l'archiviazione dell'intero e-service rispetti quella già pianificata per le sue versioni
  Affinché l'e-service non venga archiviato prima di una sua versione.

  Scenario: [ESERVICE_ARCHIVING_GRACE_PERIOD_1] - Impossibilità di archiviare un e-service prima di una sua versione
  Dato un e-service con due versioni: la prima, meno recente, in stato ARCHIVING e con una richiesta di fruizione attiva
  e la seconda, più recente, in stato PUBLISHED
  Quando l'erogatore proprietario tenta di archiviare l'intero e-service
  scegliendo un preavviso che ne prevede l'archiviazione prima della data già pianificata per la prima versione,
  allora il sistema rifiuta l'operazione e mostra il messaggio di errore che invita a selezionare una durata più lunga

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di 120 giorni
    When l'erogatore tenta di archiviare l'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then viene mostrata la snackbar con un messaggio di errore "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."

  Scenario: [ESERVICE_ARCHIVING_GRACE_PERIOD_2] - Impossibilità per il delegato di richiedere l'archiviazione di un e-service prima di una sua versione
  Dato un e-service con una delega in erogazione attiva e due versioni:
  la prima, meno recente, in stato ARCHIVING e con una richiesta di fruizione attiva,
  e la seconda, più recente, in stato PUBLISHED.
  Quando il delegato in erogazione richiede al delegante l'archiviazione dell'intero e-service,
  scegliendo un preavviso che comporta una data di archiviazione antecedente a quella già pianificata per la prima versione,
  allora il sistema rifiuta la richiesta e mostra il messaggio di errore che invita a selezionare una durata più lunga.

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di 120 giorni
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When il delegato tenta di richiedere al delegante l'archiviazione dell'intero e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then viene mostrata la snackbar con un messaggio di errore "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."

  Scenario Outline: [ESERVICE_ARCHIVING_GRACE_PERIOD_3] - Archiviazione di un e-service con un preavviso compatibile con quello di una sua versione
  Dato un e-service con due versioni:
  la prima, meno recente, in stato ARCHIVING e con una richiesta di fruizione attiva,
  e la seconda, più recente, in stato PUBLISHED.
  Quando l'erogatore proprietario richiede l'archiviazione dell'intero e-service,
  scegliendo un preavviso che comporta una data di archiviazione uguale o successiva a quella già pianificata per la prima versione,
  allora il sistema avvia l'archiviazione dell'intero e-service e mostra il messaggio di successo.

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di <descriptor_grace_period> giorni
    When l'erogatore archivia l'e-service con un periodo di preavviso di <eservice_grace_period> giorni e una motivazione valida
    Then viene mostrata la snackbar con un messaggio di successo "Questo e-service sarà archiviato tra <eservice_grace_period> giorni"

    Examples:
      | descriptor_grace_period | eservice_grace_period |
      | 60                      | 90                    |
      | 120                     | 120                   |
