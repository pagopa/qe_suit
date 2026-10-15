@eservice-archiving
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Coerenza dell'archiviazione di un e-service e delle sue versioni

  Come erogatore proprietario di un e-service o ente delegato alla sua erogazione
  Voglio che l'archiviazione dell'intero e-service rispetti quella già pianificata per le sue versioni
  Affinché l'e-service non venga archiviato prima di una sua versione.

  Scenario: [ESERVICE_ARCHIVING_GRACE_PERIOD_1] - Impossibilità di archiviare un e-service prima di una sua versione
  Dato un e-service con una richiesta di fruizione attiva sulla prima versione e una seconda versione pubblicata
  e la prima versione in fase di archiviazione con un periodo di preavviso di 120 giorni
  quando l'erogatore proprietario tenta di archiviare l'intero e-service con un periodo di preavviso di 60 giorni
  allora il sistema rifiuta l'operazione e mostra il messaggio specifico di errore

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di 120 giorni
    When l'erogatore tenta di archiviare l'e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then viene mostrata la snackbar con un messaggio di errore contenente "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."

  Scenario: [ESERVICE_ARCHIVING_GRACE_PERIOD_2] - Impossibilità per il delegato di richiedere l'archiviazione di un e-service prima di una sua versione
  Dato un e-service con una richiesta di fruizione attiva sulla prima versione e una seconda versione pubblicata
  e la prima versione in fase di archiviazione avviata dall'erogatore proprietario con un periodo di preavviso di 120 giorni
  e una delega in erogazione creata e accettata solo dopo l'avvio dell'archiviazione della prima versione
  quando il delegato tenta di richiedere al delegante l'archiviazione dell'intero e-service con un periodo di preavviso di 60 giorni
  allora il sistema rifiuta la richiesta e mostra il messaggio specifico di errore

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    And un admin appartenente all'erogatore Comune di Milano
    And la versione 1 dell'e-service è in fase di archiviazione con un periodo di preavviso di 120 giorni
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega attiva
    And un admin appartenente al delegato Comune di Pozzallo
    When il delegato tenta di richiedere al delegante l'archiviazione dell'intero e-service con un periodo di preavviso di 60 giorni e una motivazione valida
    Then viene mostrata la snackbar con un messaggio di errore contenente "Non puoi archiviare l’e-service prima di una versione già in fase di archiviazione. Seleziona una durata più lunga."
