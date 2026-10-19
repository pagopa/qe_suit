@private-catalog
@private-catalog-text-search
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Ricerca testuale nel catalogo privato degli e-service

  Come fruitore
  Voglio cercare e-service usando una parola chiave
  Per trovare quelli che la contengono nel nome, nella descrizione o nel nome dell'erogatore.

  Assunzioni:
  - gli e-service predisposti sono eliminati o archiviati al termine dello scenario da un hook;
  - nomi e parole cercate sono improbabili: il rischio che altre risorse dell'ambiente QA condiviso
  corrispondano alla ricerca (anche approssimata) è accettato;
  - nelle ricerche che non restringono il perimetro (senza parola, solo spazi, solo caratteri eliminati)
  la ricerca scorre le pagine fino a trovare gli e-service predisposti.

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_1] Ricerca nei campi dell'e-service e dell'erogatore
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                           | descrizione                               |
      | ES-1  | Comune di Milano | Pratiche Sogecap               | Consultazione delle pratiche scolastiche. |
      | ES-2  | Comune di Milano | Catalogo pratiche scolastiche  | Informazioni sui servizi di Sogecap.      |
      | ES-3  | Sogecap          | Catalogo pratiche assicurative | Consultazione dei servizi assicurativi.   |
      | ES-4  | Comune di Milano | Archivio pratiche comunali     | Consultazione degli atti amministrativi.  |
    When PagoPA cerca nel catalogo "<parola>"
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
    And l'e-service "ES-4" non è presente tra i risultati della ricerca

    Examples:
      | parola  |
      | Sogecap |
      | SOGECAP |
      | sogecap |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_2] Normalizzazione di accenti, punteggiatura e forme della stessa radice
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       | descrizione                        |
      | ES-1  | Comune di Milano | Servizi di Sanità          | Consultazione dei dati sanitari.   |
      | ES-2  | Comune di Milano | Pagamento dei Tributi      | Gestione dei pagamenti dovuti.     |
      | ES-3  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali. |
    When PagoPA cerca nel catalogo "<parola>"
    Then l'e-service "<incluso>" è presente tra i risultati della ricerca
    And l'e-service "ES-3" non è presente tra i risultati della ricerca

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
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       | descrizione                           |
      | ES-1  | Comune di Milano | Pratiche scolastiche       | Consultazione dei servizi scolastici. |
      | ES-2  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali.    |
    When PagoPA cerca nel catalogo senza indicare alcuna parola
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_4] Ricerca con una parola composta soltanto da spazi
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       | descrizione                           |
      | ES-1  | Comune di Milano | Pratiche scolastiche       | Consultazione dei servizi scolastici. |
      | ES-2  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali.    |
    When PagoPA cerca nel catalogo una parola composta soltanto da spazi
    Then tra i risultati della ricerca sono presenti i seguenti e-service:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_5] Parola composta soltanto da caratteri eliminati dalla normalizzazione
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                 | descrizione                           |
      | ES-1  | Comune di Milano | Pratiche scolastiche | Consultazione dei servizi scolastici. |
    When PagoPA cerca nel catalogo "<parola>"
    Then l'e-service "ES-1" non è presente tra i risultati della ricerca

    Examples:
      | parola |
      | !!!    |
      | ...    |
      | - -    |
      | 😀     |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_6] Ricerca approssimata con un refuso
    Given i seguenti e-service pubblicati:
      | alias | erogatore        | nome                       | descrizione                        |
      | ES-1  | Comune di Milano | Anagrafe                   | Consultazione dei dati anagrafici. |
      | ES-2  | Comune di Milano | Archivio pratiche comunali | Consultazione degli atti comunali. |
    When PagoPA cerca nel catalogo "anagrfe"
    Then l'e-service "ES-1" è presente tra i risultati della ricerca
    And l'e-service "ES-2" non è presente tra i risultati della ricerca
