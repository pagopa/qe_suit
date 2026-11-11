@error-copy
@channel:Given=BFF,When=BFF,Then=BFF
@lingua:it,en
Feature: Messaggi di errore durante il caricamento degli allegati alle annotazioni dei template finalità

  Come creatore di template finalità
  Voglio sapere perché il caricamento di un allegato all'annotazione è stato rifiutato
  Per scegliere un PDF valido o correggere il contenuto del file.

  Background:
    Given un admin appartenente al fruitore PagoPA
    # Binding di setup comune mancante; riusare createPurposeTemplate e predisporre una risposta annotata.
    And un template finalità in bozza con una risposta annotata

  Scenario: [ERROR_195] - Rifiuto di un allegato all'annotazione in un formato diverso da PDF
  Dato un template finalità in bozza con una risposta annotata
  Quando il creatore tenta di allegare all'annotazione un file di testo
  Allora il sistema rifiuta il caricamento e segnala che il formato non è ammesso.

    # Fixture da predisporre: file di testo valido, non identificato come PDF.
    # Operazione BFF e binding negativo comuni mancanti per gli allegati alle annotazioni.
    When l'operatore allega il file "allegato-non-pdf.txt" all'annotazione del template finalità con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_CONTENT_TYPE_DETECTED"

  Scenario: [ERROR_196] - Rifiuto di un allegato PDF con firma binaria non valida
  Dato un template finalità in bozza con una risposta annotata
  Quando il creatore tenta di allegare un file dichiarato PDF ma con firma binaria non valida
  Allora il sistema rifiuta il caricamento e invita il creatore a scegliere un PDF valido.

    # Fixture da predisporre: tipo PDF ammesso e firma binaria non valida.
    # Operazione BFF e binding negativo comuni mancanti per gli allegati alle annotazioni.
    When l'operatore allega il file "pdf-con-contenuto-non-valido.pdf" all'annotazione del template finalità con il nome "Allegato di test"
    Then il messaggio di errore corrisponde al caso "PURPOSE_TEMPLATE_INVALID_PDF_SIGNATURE_ERROR"
