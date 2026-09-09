@agreement
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Gestione ambito sicurezza relativo alle pagine delle richieste di fruizione di un eservice

  Scenario: [AGREEMENT_SECURITY_1] - Bloccare accesso non autorizzato da parte di ente Fruitore alla pagina di dettaglio in sezione Erogazione, se Fruitore ed Erogatore non sono lo stesso ente
  Dato un EService con richiesta di fruizione attiva
  quando un Fruitore (diverso da ente Erogatore) tenta di accedere alla pagina di dettaglio in sezione Erogazione
  allora il sistema blocca l'accesso al Fruitore alla pagina in sezione Erogazione da esso richiesta in quanto non autorizzato

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    Then il sistema impedisce l'accesso a PagoPA alla pagina indicata
