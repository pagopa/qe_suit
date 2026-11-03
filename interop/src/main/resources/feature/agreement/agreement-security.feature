@agreement-security
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Gestione ambito sicurezza relativo alle pagine delle richieste di fruizione di un eservice

  Scenario: [AGREEMENT_SECURITY_1] - Accesso negato alla sezione Erogazione per un ente Fruitore
  Dato un EService con una richiesta di fruizione attiva
  quando l'ente Fruitore tenta di accedere alla pagina di dettaglio nella sezione Erogazione
  allora il sistema nega l'accesso alla pagina poiché riservato all'ente Erogatore

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    Then il sistema nega al fruitore PagoPA di accedere alla pagina di dettaglio nella sezione Erogazione per la richiesta di fruizione