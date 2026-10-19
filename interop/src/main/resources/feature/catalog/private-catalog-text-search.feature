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
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                          |
      | nome                 | Pratiche Sogecap                          |
      | descrizione          | Consultazione delle pratiche scolastiche. |
      | stato della versione | pubblicata                                |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                     |
      | nome                 | Catalogo pratiche scolastiche        |
      | descrizione          | Informazioni sui servizi di Sogecap. |
      | stato della versione | pubblicata                           |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Sogecap                                 |
      | nome                 | Catalogo pratiche assicurative          |
      | descrizione          | Consultazione dei servizi assicurativi. |
      | stato della versione | pubblicata                              |
    And l'e-service "ES-4" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                         |
      | nome                 | Archivio pratiche comunali               |
      | descrizione          | Consultazione degli atti amministrativi. |
      | stato della versione | pubblicata                               |
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
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                 |
      | nome                 | Servizi di Sanità                |
      | descrizione          | Consultazione dei dati sanitari. |
      | stato della versione | pubblicata                       |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano               |
      | nome                 | Pagamento dei Tributi          |
      | descrizione          | Gestione dei pagamenti dovuti. |
      | stato della versione | pubblicata                     |
    And l'e-service "ES-3" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                   |
      | nome                 | Archivio pratiche comunali         |
      | descrizione          | Consultazione degli atti comunali. |
      | stato della versione | pubblicata                         |
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
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                      |
      | nome                 | Pratiche scolastiche                  |
      | descrizione          | Consultazione dei servizi scolastici. |
      | stato della versione | pubblicata                            |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                   |
      | nome                 | Archivio pratiche comunali         |
      | descrizione          | Consultazione degli atti comunali. |
      | stato della versione | pubblicata                         |
    When PagoPA cerca nel catalogo senza indicare alcuna parola
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_4] Ricerca con una parola composta soltanto da spazi
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                      |
      | nome                 | Pratiche scolastiche                  |
      | descrizione          | Consultazione dei servizi scolastici. |
      | stato della versione | pubblicata                            |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                   |
      | nome                 | Archivio pratiche comunali         |
      | descrizione          | Consultazione degli atti comunali. |
      | stato della versione | pubblicata                         |
    When PagoPA cerca nel catalogo una parola composta soltanto da spazi
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_5] Parola composta soltanto da caratteri eliminati dalla normalizzazione
    Given un admin appartenente al fruitore PagoPA
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                      |
      | nome                 | Pratiche scolastiche                  |
      | descrizione          | Consultazione dei servizi scolastici. |
      | stato della versione | pubblicata                            |
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
    And l'e-service "ES-1" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                   |
      | nome                 | Anagrafe                           |
      | descrizione          | Consultazione dei dati anagrafici. |
      | stato della versione | pubblicata                         |
    And l'e-service "ES-2" è predisposto a catalogo con la seguente configurazione:
      | erogatore            | Comune di Milano                   |
      | nome                 | Archivio pratiche comunali         |
      | descrizione          | Consultazione degli atti comunali. |
      | stato della versione | pubblicata                         |
    When PagoPA cerca nel catalogo la parola "anagrfe"
    Then tra i risultati compare l'e-service predisposto "ES-1"
    And tra i risultati non compare l'e-service predisposto "ES-2"
