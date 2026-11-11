@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante il download del documento firmato e il caricamento degli allegati di fruizione

  Come erogatore o fruitore
  Voglio sapere perché il download del documento firmato o il caricamento di un allegato sono stati rifiutati
  Per capire se il documento è disponibile o se devo correggere il file da allegare.

  Background:
    Given i seguenti e-service pubblicati:
      | alias | erogatore        |
      | ES-1  | Comune di Milano |
    And un admin appartenente al fruitore PagoPA

  Scenario: [ERROR_168] - Impossibilità di scaricare un documento firmato non ancora disponibile
  Data una richiesta di fruizione di PagoPA attiva con un contratto generato e il documento firmato non ancora disponibile
  Quando l'erogatore tenta di scaricare il documento firmato della richiesta
  Allora il sistema rifiuta il download e segnala che il documento firmato non è ancora disponibile.

    Given l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione attiva
    # contractException richiede ACTIVE/SUSPENDED/ARCHIVED e signedContract assente.
    # Predisporre separatamente contratto presente e documento firmato assente, senza affidarsi a ritardi di generazione.
    And il contratto della richiesta di fruizione è disponibile
    And il documento firmato della richiesta di fruizione non è ancora disponibile
    And un admin appartenente all'erogatore Comune di Milano
    # Operazione BFF e binding mancanti per il download del documento firmato; evitare retry nel tentativo.
    When l'erogatore tenta di scaricare il documento firmato della richiesta di fruizione
    Then il messaggio di errore corrisponde al caso "BFF_CONTRACT_EXCEPTION"

  Scenario: [ERROR_197] - Rifiuto di un allegato PDF con contenuto non valido
  Data una richiesta di fruizione di PagoPA in bozza
  Quando il fruitore tenta di allegare un file identificato come PDF ma con contenuto incompatibile con il formato dichiarato
  Allora il sistema rifiuta il caricamento e invita il fruitore a correggere il file prima di riprovare.

    Given l'e-service "ES-1" del Comune di Milano sottoscritto da PagoPA con una richiesta di fruizione bozza
    # Fixture da predisporre: PDF con tipo ammesso e contenuto non valido.
    # Operazione BFF e binding mancanti per il caricamento degli allegati alla richiesta.
    When il fruitore tenta di allegare alla richiesta di fruizione il file "pdf-con-contenuto-non-valido.pdf"
    Then il messaggio di errore corrisponde al caso "AGREEMENT_INVALID_FILE_UPLOAD_ERROR"
