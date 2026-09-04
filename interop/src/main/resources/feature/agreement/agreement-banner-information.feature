@agreement
@channel:Given=BFF,When=WEB,Then=WEB
Feature: La consultazione della pagina di fruizione di un eservice deve restituire messaggi idonei caso per caso

  Come Aderente interessato alla fruizione di un EService in catalogo
  Voglio inoltrare una richiesta di fruizione verso l'erogatore del servizio
  Al fine di instaurare un Agreement che abiliti alle fasi successive necessarie a raggiungere la fruizione dell'EService.


  Scenario: [AGREEMENT_BANNER_INFORMATION_1] - Presenza banner per v1 obsoleta con richiesta di fruizione aggiornabile
  Dato un EService con una versione in stato DEPRECATED,
  quando il Fruitore tenta di consultare la pagina relativa alla sua richiesta di fruizione,
  allora il Fruitore vede un banner di informazione con il messaggio "Questa versione dell’e-service è obsoleta, ma è ancora attiva. È disponibile una nuova versione."

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    Then il sistema mostra a PagoPA un banner di informazioni che denota la versione obsoleta dell'EService con possibilità di aggiornare ad una nuova versione


  Scenario: [AGREEMENT_BANNER_INFORMATION_2] - Presenza banner per v1 obsoleta con eservice in archiviazione e richiesta di fruizione **non** aggiornabile
  Dato un EService in fase di archiviazione con una versione in stato DEPRECATED,
  quando il Fruitore tenta di consultare la pagina relativa alla sua richiesta di fruizione,
  allora il Fruitore vede un banner di informazione con il messaggio "Questa versione dell’e-service è obsoleta, ma è ancora attiva."

    Given un EService creato dal Comune di Milano con una versione divenuta deprecata dopo la fruizione di PagoPA
    Then il sistema mostra a PagoPA un banner di informazioni che denota la versione obsoleta dell'EService


  Scenario: [AGREEMENT_BANNER_INFORMATION_3] - Assenza banner per agreement verso v2 di un eservice in archiviazione con richiesta di fruizione **non** aggiornabile
  Dato un EService in fase di archiviazione con la sua v2 avente una richiesta di fruizione attiva non aggiornabile,
  quando il Fruitore tenta di consultare la pagina relativa alla sua richiesta di fruizione,
  allora il Fruitore non vede alcun banner

    Given un EService creato dal Comune di Milano con versione v2 attiva e con fruizione attiva di PagoPA
    Then il sistema non mostra alcun banner al Comune di Pozzallo


  Scenario: [AGREEMENT_BANNER_INFORMATION_4] - Assenza banner per v1 in archiviazione con eservice in archiviazione con richiesta di fruizione **non** aggiornabile
  Dato un EService in fase di archiviazione con la sua v1 anch'essa in archiviazione avente una richiesta di fruizione attiva non aggiornabile,
  quando il Fruitore tenta di consultare la pagina relativa alla sua richiesta di fruizione,
  allora il Fruitore non vede alcun banner

    Given un EService in archiviazione creato dal Comune di Milano con una versione in archiviazione dopo la fruizione di PagoPA
    Then il sistema non mostra alcun banner al Comune di Pozzallo

