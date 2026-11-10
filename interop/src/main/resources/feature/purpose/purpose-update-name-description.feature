@purpose-metadata-update
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Aggiornamento dei metadati di una Finalità senza alterarne i dati operativi

  Scenario Outline: [PURPOSE_NAME_DESCRIPTION_UPDATE_1] Modifica parziale dei metadati di una Finalità
    Given il fruitore dispone di una Finalità <tipo> non derivata da template in stato <configurazione>
    When il fruitore modifica i metadati applicando la variazione su "<campi modificati>"
    And il fruitore salva e consulta nuovamente la Finalità
    Then i dati operativi della Finalità corrispondono a quelli iniziali
    And la piattaforma salva i metadati richiesti e conserva quelli omessi

    Examples:
      | tipo     | campi modificati | configurazione                        |
      | ordinaria | solo titolo      | bozza                                  |
      | ordinaria | solo descrizione | bozza                                  |
      | ordinaria | entrambi         | bozza                                  |
      | inversa   | solo titolo      | bozza                                  |
      | inversa   | solo descrizione | bozza                                  |
      | inversa   | entrambi         | bozza                                  |
      | ordinaria | solo titolo      | attiva                                 |
      | ordinaria | solo descrizione | attiva                                 |
      | ordinaria | entrambi         | attiva                                 |
      | inversa   | solo titolo      | attiva                                 |
      | inversa   | solo descrizione | attiva                                 |
      | inversa   | entrambi         | attiva                                 |
      | ordinaria | solo titolo      | sospesa                                |
      | ordinaria | solo descrizione | sospesa                                |
      | ordinaria | entrambi         | sospesa                                |
      | inversa   | solo titolo      | sospesa                                |
      | inversa   | solo descrizione | sospesa                                |
      | inversa   | entrambi         | sospesa                                |
      | ordinaria | solo titolo      | archiviata                             |
      | ordinaria | solo descrizione | archiviata                             |
      | ordinaria | entrambi         | archiviata                             |
      | inversa   | solo titolo      | archiviata                             |
      | inversa   | solo descrizione | archiviata                             |
      | inversa   | entrambi         | archiviata                             |
      | ordinaria | solo titolo      | con versione in attesa di approvazione |
      | ordinaria | solo descrizione | con versione in attesa di approvazione |
      | ordinaria | entrambi         | con versione in attesa di approvazione |
      | inversa   | solo titolo      | con versione in attesa di approvazione |
      | inversa   | solo descrizione | con versione in attesa di approvazione |
      | inversa   | entrambi         | con versione in attesa di approvazione |
      | ordinaria | solo titolo      | con versione rifiutata                 |
      | ordinaria | solo descrizione | con versione rifiutata                 |
      | ordinaria | entrambi         | con versione rifiutata                 |
      | inversa   | solo titolo      | con versione rifiutata                 |
      | inversa   | solo descrizione | con versione rifiutata                 |
      | inversa   | entrambi         | con versione rifiutata                 |
