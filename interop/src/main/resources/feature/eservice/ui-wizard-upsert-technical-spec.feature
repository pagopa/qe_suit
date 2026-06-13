Feature: Creazione di un e-service asincrono - Validazione dello step Specifiche Tecniche (Frontend) - Test Comportamentale

  Come Ente Erogatore autenticato nel portale Interop
  Voglio che la piattaforma Interop prevenga inconsistenze nella compilazione dello step delle Specifiche Tecniche durante la creazione di un e-service asincrono
  E che evidenzi eventuali errori di compilazione della form in modo chiaro e comprensibile
  Al fine di poterli comprendere e correggere e proseguire con la creazione dell'e-service

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  ***

  Scenario: [CREATE_ASYNC_ESERVICE_TECHNICAL_SPECIFICATION_1]
  Dato un utente impegnato nella creazione di un e-service asincrono,
  quando cancella i valori da tutti i campi dello step delle Specifiche Tecniche e tenta di proseguire,
  allora il sistema blocca il flusso evidenziando ogni campo obbligatorio con l'errore 'Campo richiesto'

    Given un admin del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    And compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | asyncExchange | mode |
      | true          |      |
    And clicca sul button 'Salva bozza e prosegui'
    And si trova allo step Soglie e attributi
    And clicca sul button 'Salva bozza e prosegui'
    And si trova allo step Specifiche tecniche
    When cancella i valori da tutti gli input delle specifiche tecniche
    And clicca sul button 'Salva bozza e prosegui'
    Then la creazione non prosegue ed il campo Durata validità dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Audience dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Tempo massimo di risposta dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Numero massimo di risultati per risposta dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Durata di disponibilità del dato dello step Specifiche tecniche è evidenziato come errore mostrando il messaggio "Campo richiesto"

  Scenario: [CREATE_ASYNC_ESERVICE_TECHNICAL_SPECIFICATION_2]
  Dato un utente impegnato nella creazione di un e-service asincrono con tecnologia SOAP,
  quando raggiunge lo step delle Specifiche Tecniche,
  allora il sistema disabilita la checkbox relativa al download a blocchi in conformità con i vincoli tecnologici

    Given un admin del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    And compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | asyncExchange | mode | technology |
      | true          |      | SOAP       |
    And clicca sul button 'Salva bozza e prosegui'
    And si trova allo step Soglie e attributi
    And clicca sul button 'Salva bozza e prosegui'
    And si trova allo step Specifiche tecniche
    Then la checkbox Consenti download a blocchi è disabilitata
