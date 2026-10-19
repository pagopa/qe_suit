@private-catalog
@private-catalog-text-search
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Ricerca testuale nel catalogo privato degli e-service

  Come fruitore
  Voglio cercare e-service usando una parola chiave
  Per trovare quelli che la contengono nel nome, nella descrizione o nel nome dell'erogatore.

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_1] Ricerca nei campi dell'e-service e dell'erogatore
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                           | descrizione                               | stato della versione |
      | ES-1  | Comune di Milano | Pratiche Sogecap               | Consultazione delle pratiche scolastiche. | pubblicata           |
      | ES-2  | Comune di Milano | Catalogo pratiche scolastiche  | Informazioni sui servizi di Sogecap.      | pubblicata           |
      | ES-3  | Sogecap          | Catalogo pratiche assicurative | Consultazione dei servizi assicurativi.   | pubblicata           |
      | ES-4  | Comune di Milano | Archivio pratiche comunali     | Consultazione degli atti amministrativi.  | pubblicata           |
    When PagoPA cerca nel catalogo la parola "<parola>"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service predisposto "ES-4"

    Examples:
      | parola  |
      | Sogecap |
      | SOGECAP |
      | sogecap |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_2] Normalizzazione di accenti, punteggiatura e forme della stessa radice
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                       | descrizione                        | stato della versione |
      | ES-1  | Comune di Milano | Servizi di Sanità          | Consultazione dei dati sanitari.   | pubblicata           |
      | ES-2  | Comune di Milano | Pagamento dei Tributi      | Gestione dei pagamenti dovuti.     | pubblicata           |
      | ES-3  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali. | pubblicata           |
    When PagoPA cerca nel catalogo la parola "<parola>"
    Then tra i risultati compare l'e-service predisposto "<incluso>"
    And tra i risultati non compare l'e-service predisposto "ES-3"

    Examples: Accenti
      | parola | incluso |
      | sanita | ES-1    |
      | SANITÀ | ES-1    |

    Examples: Forme della stessa radice
      | parola  | incluso |
      | tributo | ES-2    |

    Examples: Punteggiatura insieme a testo significativo
      | parola   | incluso |
      | tributi. | ES-2    |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_3] Ricerca senza indicare alcuna parola
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                       | descrizione                           | stato della versione |
      | ES-1  | Comune di Milano | Pratiche scolastiche       | Consultazione dei servizi scolastici. | pubblicata           |
      | ES-2  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali.    | pubblicata           |
    When PagoPA cerca nel catalogo senza indicare alcuna parola
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_4] Ricerca con una parola composta soltanto da spazi
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                       | descrizione                           | stato della versione |
      | ES-1  | Comune di Milano | Pratiche scolastiche       | Consultazione dei servizi scolastici. | pubblicata           |
      | ES-2  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali.    | pubblicata           |
    When PagoPA cerca nel catalogo una parola composta soltanto da spazi
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_5] Parola composta soltanto da caratteri eliminati dalla normalizzazione
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                 | descrizione                           | stato della versione |
      | ES-1  | Comune di Milano | Pratiche scolastiche | Consultazione dei servizi scolastici. | pubblicata           |
    When PagoPA cerca nel catalogo la parola "<parola>"
    Then tra i risultati non compare l'e-service predisposto "ES-1"

    Examples:
      | parola |
      | !!!    |
      | ...    |
      | - -    |
      | 😀     |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_6] Ricerca approssimata con un refuso
    Given un admin appartenente al fruitore PagoPA
    And sono predisposti a catalogo i seguenti e-service:
      | alias | erogatore        | nome                       | descrizione                        | stato della versione |
      | ES-1  | Comune di Milano | Anagrafe                   | Consultazione dei dati anagrafici. | pubblicata           |
      | ES-2  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali. | pubblicata           |
    When PagoPA cerca nel catalogo la parola "anagrfe"
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"
