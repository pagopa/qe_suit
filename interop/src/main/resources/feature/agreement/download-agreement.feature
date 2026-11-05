@agreement
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Download del documento di una richiesta di fruizione

  Scenario: [CONSUMER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per il fruitore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    When il fruitore accede ai dettagli della richiesta di fruizione inoltrata
    Then il fruitore può scaricare il documento della richiesta di fruizione solo quanto è pronto

  Scenario: [PRODUCER_AGREEMENT_DOWNLOAD_1] Il link per scaricare il documento della richiesta di fruizione si abilita quando pronto per l'erogatore
    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    When l'erogatore accede ai dettagli della richiesta di fruizione ricevuta
    Then l'erogatore può scaricare il documento della richiesta di fruizione solo quanto è pronto
