@agreement
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Creazione di un template di finalità da parte di un Ente Erogatore

  Come Ente Erogatore autenticato nel portale Interop
  Voglio poter creare un template di finalità riutilizzabile
  Al fine di associarlo successivamente ai miei e-service, evitando di ridefinire manualmente le stesse informazioni di finalità per ogni fruitore

  Scenario: [TEMPLATE_PURPOSE_1] - Verificare lato UI se la pagina di "Informazioni generali"
  Dato un Fruitore,
  quando l'Erogatore tenta di creare un template di finalità,
  allora l'Erogatore visualizza la corretta pagina di Informazioni Generali

    Given un admin del Comune di Milano autenticato sul portale Interop
    When Comune di Milano tenta di creare un template di finalità
    Then Comune di Milano visualizza la corretta pagina di Informazioni Generali
