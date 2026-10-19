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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Pratiche Sogecap                          |
      | descrizione | Consultazione delle pratiche scolastiche. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Catalogo pratiche scolastiche        |
      | descrizione | Informazioni sui servizi di Sogecap. |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto da Sogecap con la seguente configurazione:
      | nome        | Catalogo pratiche assicurative          |
      | descrizione | Consultazione dei servizi assicurativi. |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    And l'e-service "ES-4" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Archivio pratiche comunali               |
      | descrizione | Consultazione degli atti amministrativi. |
    And la versione 1 dell'e-service "ES-4" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "<parola>"
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |
      | ES-3  |
    And tra i risultati non compare l'e-service "ES-4"

    Examples:
      | parola  |
      | Sogecap |
      | SOGECAP |
      | sogecap |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_2] Normalizzazione di accenti, punteggiatura e forme della stessa radice
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Servizi di Sanità                |
      | descrizione | Consultazione dei dati sanitari. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Pagamento dei Tributi          |
      | descrizione | Gestione dei pagamenti dovuti. |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    And l'e-service "ES-3" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Archivio pratiche comunali         |
      | descrizione | Consultazione degli atti comunali. |
    And la versione 1 dell'e-service "ES-3" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "<parola>"
    Then tra i risultati compare l'e-service "<incluso>"
    And tra i risultati non compare l'e-service "ES-3"

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
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Pratiche scolastiche                  |
      | descrizione | Consultazione dei servizi scolastici. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Archivio pratiche comunali         |
      | descrizione | Consultazione degli atti comunali. |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo senza indicare alcuna parola
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_4] Ricerca con una parola composta soltanto da spazi
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Pratiche scolastiche                  |
      | descrizione | Consultazione dei servizi scolastici. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Archivio pratiche comunali         |
      | descrizione | Consultazione degli atti comunali. |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo una parola composta soltanto da spazi
    Then tra i risultati compaiono gli e-service predisposti:
      | alias |
      | ES-1  |
      | ES-2  |

  Scenario Outline: [PRIVATE_CATALOG_TEXT_SEARCH_5] Parola composta soltanto da caratteri eliminati dalla normalizzazione
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Pratiche scolastiche                  |
      | descrizione | Consultazione dei servizi scolastici. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "<parola>"
    Then tra i risultati non compare l'e-service "ES-1"

    Examples:
      | parola |
      | !!!    |
      | ...    |
      | - -    |
      | 😀     |

  Scenario: [PRIVATE_CATALOG_TEXT_SEARCH_6] Ricerca approssimata con un refuso
    Given l'e-service "ES-1" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Anagrafe                           |
      | descrizione | Consultazione dei dati anagrafici. |
    And la versione 1 dell'e-service "ES-1" nello stato "pubblicata"
    And l'e-service "ES-2" predisposto dal Comune di Milano con la seguente configurazione:
      | nome        | Archivio pratiche comunali         |
      | descrizione | Consultazione degli atti comunali. |
    And la versione 1 dell'e-service "ES-2" nello stato "pubblicata"
    When PagoPA cerca nel catalogo la parola "anagrfe"
    Then tra i risultati compare l'e-service "ES-1"
    And tra i risultati non compare l'e-service "ES-2"
