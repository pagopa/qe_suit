@ignore
Feature: Creazione di un e-service asincrono - Validazione dello step Informazioni Generali (Frontend) - Test Comportamentale

  Come Ente Erogatore autenticato nel portale Interop
  Voglio che la piattaforma Interop prevenga inconsistenze nella compilazione dello step Informazioni Generali durante la creazione di un e-service asincrono
  E che evidenzi eventuali errori di compilazione della form in modo chiaro e comprensibile
  Al fine di poterli comprendere e correggere e proseguire con la creazione dell'e-service

  ***
  OBIETTIVI DEL TEST FRONTEND:
  1. Verificare il corretto comportamento dei componenti grafici (es. text field, messaggi di errore) in risposta ai diversi input dell'utente.
  ***

  Scenario Outline: [VALIDATE_ASYNC_ESERVICE_GENERAL_INFO_WIZARD_1]
  Dato un utente che omette i campi obbligatori nello step delle Informazioni Generali,
  quando richiede di salvare la bozza e proseguire nel wizard,
  allora il sistema impedisce l'avanzamento e mostra il messaggio 'Campo richiesto' per ciascun campo vuoto

    Given un <userRole> del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | name     | description | technology   | asyncExchange | personalData | mode |
      | $blank() | $blank()    | <technology> | true          |              |      |
    And clicca sul button 'Salva bozza e prosegui'
    Then la creazione non prosegue ed il campo Nome dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo Descrizione dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"
    And la creazione non prosegue ed il campo L’e-service eroga dati personali? dello step Dati Generali è evidenziato come errore mostrando il messaggio "Campo richiesto"

    Examples:
      | userRole | technology |
      | admin    | REST       |
      | api      | SOAP       |

  Scenario Outline: [VALIDATE_ASYNC_ESERVICE_GENERAL_INFO_WIZARD_1]
  Dato un utente che imposta lo scambio dati in modalità asincrona,
  quando visualizza lo step delle Informazioni Generali,
  allora il radio group relativo alla direzione dei dati risulta disabilitato per vincolo di architettura

    Given un <userRole> del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | technology   | asyncExchange | mode |
      | <technology> | true          |      |
    Then il radio group L'e-service eroga o riceve dati? è disabilitato

    Examples:
      | userRole | technology |
      | admin    | REST       |
      | api      | SOAP       |

  Scenario Outline: [VALIDATE_ASYNC_ESERVICE_GENERAL_INFO_WIZARD_2]
  Dato un utente con profilo operativo API,
  quando attiva l'opzione di scambio asincrono,
  allora l'interfaccia mostra un alert di warning che segnala la necessità di un utente Amministratore per la configurazione del portachiavi

    Given un utente api del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | technology   | asyncExchange | mode |
      | <technology> | true          |      |
    And viene mostrato l'alert relativo al keychain in stile warning "Per gli scambi asincroni è necessario collegare un portachiavi all’e-service. Solo chi ha il ruolo di amministratore può farlo: chiedi di collegarlo prima o dopo la pubblicazione per abilitare lo scambio dei dati."

    Examples:
      | technology |
      | REST       |
      | SOAP       |

  Scenario Outline: [VALIDATE_ASYNC_ESERVICE_GENERAL_INFO_WIZARD_3]
  Dato un utente che seleziona la combinazione di tecnologia SOAP e scambio asincrono,
  quando si trova nello step iniziale,
  allora la UI mostra un alert di warning per evidenziare l'indisponibilità del download a blocchi per questo specifico stack

    Given un <userRole> del Comune di Milano si trova alla pagina Creazione EService del portale Interop
    When compila lo step 'Informazioni generali' con i valori di default ma specificando:
      | technology | asyncExchange | mode |
      | SOAP       | true          |      |
    And viene mostrato l'alert relativo al SOAP in stile warning "La tecnologia SOAP non permette di abilitare il download a blocchi durante lo scambio asincrono dei dati."

    Examples:
      | userRole |
      | admin    |
      | api      |
