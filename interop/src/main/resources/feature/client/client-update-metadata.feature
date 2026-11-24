Feature: Modifica dei metadati dei client
  Come amministratore dell'ente e creatore di un client e-service o interop
  Voglio aggiornare il nome e la descrizione dei client
  preservando i dati non modificati, le associazioni e l'operatività del client.

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [CLIENT_UPDATE_METADATA_1] - L'amministratore aggiorna i metadati del client e-service che preserva i dati non modificati, le associazioni e l'operatività
  Un amministratore aggiorna i metadati di un client e-service (solo nome, solo descrizione, nome e descrizione).
  I dati modificati vengono aggiornati, quelli non modificati vengono preservati,
  le associazioni di chiavi e finalità al client vengono preservate.
  Il client e-service permette di generare voucher garantendo la corretta operatività.

    Given un client "CL-1" CONSUMER creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate dal Comune di Pozzallo
    And un admin del Comune di Pozzallo ha associato la finalità al client "CL-1"
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto il nome
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client e la finalità attiva

    When i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | descrizione | prima descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto la descrizione
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client e la finalità attiva

    When i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto il nome e la descrizione
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client e la finalità attiva

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_UPDATE_METADATA_2] - L'amministratore aggiorna i metadati del client interop che preserva i dati non modificati, le associazioni e l'operatività
  Un amministratore aggiorna i metadati di un client interop (solo nome, solo descrizione, nome e descrizione).
  I dati modificati vengono aggiornati, quelli non modificati vengono preservati,
  le associazioni di chiavi vengono preservate.
  Il client interop permette di generare voucher garantendo la corretta operatività.

    Given un client "CL-1" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto il nome
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

    When i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | descrizione | prima descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto la descrizione
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

    When i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto il nome e la descrizione
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

  @work-item:QA-19036
  @channel:Given=BFF,When=M2M,Then=M2M
  Scenario: [CLIENT_UPDATE_METADATA_3] - L'amministratore aggiorna i metadati del client interop ma la modifica risulta vietata
  Un amministratore aggiorna i metadati di un client interop (solo nome, solo descrizione, nome e descrizione).
  La richiesta risulta vietata, il client interop conserva i dati e le associazioni di chiavi.
  Il client interop permette di generare voucher garantendo la corretta operatività.

    Given un client "CL-1" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
    Then la richiesta di modifica fallisce come operazione vietata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

    When i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

    When i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    And un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | nuovo nome        |
      | descrizione | nuova descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_CONSUMER_PROTECTION_1A] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin di PagoPA tenta di modificare i metadati del client "CL-1":
      | campo       | valore |
      | descrizione | ab     |
    Then la richiesta di modifica fallisce come operazione malformata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin di PagoPA richiede la generazione del voucher per il client e la finalità attiva

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_API_PROTECTION_1B] - Rifiuto atomico di un metadato valido insieme a un valore non ammesso
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un client "CL-1" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore |
      | nome        | ab     |
    Then la richiesta di modifica fallisce come operazione malformata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin di PagoPA aggiorna i metadati della risorsa "CL-1" con nuovi valori validi
    And un admin di PagoPA ripete gli stessi valori per la risorsa "CL-1"
    Then la ripetizione della modifica della risorsa "CL-1" viene accettata
    And la risorsa "CL-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_METADATA_PROTECTION_3] - Ripetizione dei metadati correnti senza effetti operativi
  Dato una risorsa i cui metadati sono modificabili,
  quando l’attore ripete una modifica già applicata,
  allora la ripetizione ha successo senza modificare versioni stati o relazioni.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato dal Comune di Milano, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano aggiorna i metadati della risorsa "CL-1" con nuovi valori validi
    And un admin del Comune di Milano ripete gli stessi valori per la risorsa "CL-1"
    Then la ripetizione della modifica della risorsa "CL-1" viene accettata
    And la risorsa "CL-1" conserva stati, versioni e relazioni

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin di PagoPA richiede la modifica di un metadato della risorsa "CL-1" confermando gli altri valori correnti
    Then la risorsa "CL-1" presenta il metadato aggiornato
    And la risorsa "CL-1" conserva stati, versioni, relazioni e metadati invariati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_METADATA_PROTECTION_4] - Valori modificati e invariati nella stessa richiesta
  Dato una risorsa con metadati già valorizzati,
  quando l’attore modifica un campo e conferma esplicitamente il valore di un altro,
  allora il sistema applica solo il valore modificato senza alterare dati operativi.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" API creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    When un admin del Comune di Milano richiede la modifica di un metadato della risorsa "CL-1" confermando gli altri valori correnti
    Then la risorsa "CL-1" presenta il metadato aggiornato
    And la risorsa "CL-1" conserva stati, versioni, relazioni e metadati invariati

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_METADATA_AUTHORIZATION_1] - Rifiuto della modifica da parte di un ruolo non autorizzato
  Dato una risorsa appartenente a un ente determinato,
  quando un utente dello stesso ente con ruolo escluso richiede l’aggiornamento,
  allora il sistema impedisce la modifica e conserva i dati iniziali.

    Given un client "CL-1" di tipo CONSUMER appartenente al Comune di Milano
    When un security del Comune di Milano modifica i metadati del client "CL-1":
      | campo | valore             |
      | nome  | Client aggiornato  |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_METADATA_AUTHORIZATION_2] - Rifiuto della modifica da parte di un altro ente
  Dato una risorsa appartenente a un ente,
  quando un amministratore di un ente diverso prova a modificarla,
  allora il sistema rifiuta l’operazione senza aggiornare la risorsa.

    Given un client "CL-1" di tipo CONSUMER appartenente al Comune di Milano
    When un admin di PagoPA modifica i metadati del client "CL-1":
      | campo | valore             |
      | nome  | Client aggiornato  |
    Then la modifica dei metadati del client "CL-1" viene rifiutata
    And il client "CL-1" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_UNIQUENESS_1] - I client possono avere lo stesso nome
  Dato due client appartenenti al medesimo ente,
  quando l’amministratore assegna a un client il nome dell’altro,
  allora la rinomina riesce senza introdurre un vincolo di unicità.

    Given due client "CL-1" e "CL-2" appartenenti al Comune di Milano
    When un admin del Comune di Milano richiede di assegnare al client "CL-1" il nome di "CL-2"
    Then la rinomina del client "CL-1" viene accettata
    And il client "CL-2" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario Outline: [CLIENT_METADATA_VALIDATION_1] - Rispetto dei limiti di lunghezza dei metadati
  Dato una risorsa modificabile con un campo soggetto a limiti di lunghezza,
  quando l’attore propone un testo alla lunghezza di confine,
  allora il sistema accetta i valori ammessi e rifiuta gli altri senza modifiche parziali.

    Given un client "CL-1" di tipo CONSUMER appartenente al Comune di Milano
    When un admin del Comune di Milano richiede un valore di <lunghezza> caratteri per il campo <campo> della risorsa "CL-1"
    Then la modifica della risorsa "CL-1" ha esito <esito>
    And in caso di rifiuto la risorsa "CL-1" conserva tutti i dati iniziali

    Examples:
      | campo       | lunghezza | esito     |
      | nome        | 61        | rifiutata |
      | descrizione | 250       | accettata |

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_VALIDATION_2] - Rifiuto di valori non ammessi nei metadati di un client
  Dato una risorsa modificabile da un attore autorizzato,
  quando l’attore richiede una modifica con un valore vietato,
  allora il sistema rifiuta la richiesta e conserva i dati originari.

    Given un client "CL-1" di tipo CONSUMER del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica del campo nome della risorsa "CL-1" con valore vuoto
    Then la modifica della risorsa "CL-1" viene rifiutata
    And la risorsa "CL-1" conserva tutti i dati iniziali

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_METADATA_VALIDATION_3] - I campi omessi di un client non vengono sovrascritti
  Dato una risorsa con nome e descrizione valorizzati,
  quando l’attore invia la modifica del solo nome,
  allora la descrizione rimane identica a quella iniziale.

    Given un client "K-1" di tipo CONSUMER del Comune di Milano modificabile nei metadati
    When un admin del Comune di Milano richiede la modifica del solo nome della risorsa "K-1"
    Then la risorsa "R-1" presenta il nome aggiornato e conserva la descrizione iniziale
