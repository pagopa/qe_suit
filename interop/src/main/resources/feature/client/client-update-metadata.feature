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
  Scenario: [CLIENT_UPDATE_METADATA_2A] - L'amministratore aggiorna i metadati del client interop che preserva i dati non modificati, le associazioni e l'operatività
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
  Scenario: [CLIENT_UPDATE_METADATA_2B] - L'amministratore aggiorna i metadati del client interop ma la modifica risulta vietata
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
  Scenario: [CLIENT_CONSUMER_PROTECTION_1] - Rifiuto atomico della richiesta di modifica di un client e-service con un metadato valido insieme a un valore non ammesso
  Dato un client e-service modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin di PagoPA tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | ab                |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione malformata
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un admin di PagoPA tenta di modificare i metadati del client "CL-1":
      | campo       | valore     |
      | nome        | primo nome |
      | descrizione | xy         |
    Then la richiesta di modifica fallisce come operazione malformata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin di PagoPA richiede la generazione del voucher per il client e la finalità attiva

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_API_PROTECTION_1] - Rifiuto atomico della richiesta di modifica di un client interop con un metadato valido insieme a un valore non ammesso
  Dato un client interop modificabile da un ruolo autorizzato,
  quando l’attore invia una modifica con un metadato valido e un altro non valido,
  allora il sistema rifiuta tutta la modifica e mantiene invariata la risorsa.

    Given un client "CL-1" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | ab                |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione malformata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

    When un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore     |
      | nome        | primo nome |
      | descrizione | xy         |
    Then la richiesta di modifica fallisce come operazione malformata
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_CONSUMER_PROTECTION_2] - Ripetizione parziale e completa dei metadati correnti della richiesta di modifica di un client e-service senza effetti operativi
  Dato un client e-service modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con metadati già presenti,
  allora la richiesta ha successo senza modificare dati o relazioni.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin di PagoPA modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin di PagoPA modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento

    When un admin di PagoPA modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | nuova descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto la descrizione
    And l'admin di PagoPA richiede la generazione del voucher per il client e la finalità attiva

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_PROTECTION_2A] - Ripetizione parziale e completa dei metadati correnti della richiesta di modifica di un client interop senza effetti operativi
  Dato un client interop modificabile da un ruolo autorizzato,
  quando la modifica viene inviata con metadati già presenti,
  allora la richiesta ha successo senza modificare dati o relazioni.

    Given un client "CL-1" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento

    When un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | nuova descrizione |
    Then il client "CL-1" conserva le associazioni e i dati di riferimento eccetto la descrizione
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  @channel:Given=BFF,When=M2M,Then=BFF
  Scenario: [CLIENT_CONSUMER_UNIQUENESS_1] - Aggiornamento nome client e-service con stesso nome di un altro client ammesso
  Dato un client e-service con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per il client,
  allora la modifica viene applicata e vengono preservati i dati operativi.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-1" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin di PagoPA modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
    And un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate da PagoPA
    And un client "CL-2" CONSUMER creato da PagoPA, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-2" allo stato attuale appuntati come riferimento
    When un admin di PagoPA modifica i metadati del client "CL-2":
      | campo       | valore            |
      | nome        | primo nome        |
    Then il client "CL-2" conserva le associazioni e i dati di riferimento eccetto il nome
    And l'admin di PagoPA richiede la generazione del voucher per il client e la finalità attiva

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_UNIQUENESS_2A] - Aggiornamento nome client interop con stesso nome di un altro client ammesso
  Dato un client interop con metadati già valorizzati,
  quando l'utente aggiorna i metadati inserendo un nome già esistente per il client,
  allora la modifica viene applicata e vengono preservati i dati operativi.

    Given un client "CL-1" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin del Comune di Pozzallo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
    And un client "CL-2" API creato dal Comune di Pozzallo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And i dati e le associazioni del client "CL-2" allo stato attuale appuntati come riferimento
    When un admin del Comune di Pozzallo modifica i metadati del client "CL-2":
      | campo       | valore            |
      | nome        | primo nome        |
    Then il client "CL-2" conserva le associazioni e i dati di riferimento eccetto il nome
    And l'admin del Comune di Pozzallo richiede la generazione del voucher per il client interop

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_CONSUMER_METADATA_VALIDATION_1] - Aggiornamento metadati di un client e-service non ammesso ai ruoli esclusi o ad altri enti
  Dato un client e-service, se un ruolo non autorizzato (api, api,security, security, support, reviewer, viewer)
  tenta di aggiornare i metadati, o un admin di un altro ente,
  allora la richiesta di modifica risulta vietata.

    Given un EService creato dal Comune di Milano con una richiesta di fruizione e una finalità associate dal Comune di Comun Nuovo
    And un client "CL-1" CONSUMER creato dal Comune di Comun Nuovo, associato alla finalità creata, in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin del Comune di Comun Nuovo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un api del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un api,security del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un security del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un support del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un reviewer del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un viewer del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Comun Nuovo richiede la generazione del voucher per il client e la finalità attiva

  @work-item:QA-19036
  @channel:Given=BFF,When=BFF,Then=BFF
  Scenario: [CLIENT_API_METADATA_VALIDATION_1] - Aggiornamento metadati di un client interop non ammesso ai ruoli esclusi o ad altri enti
  Dato un client interop, se un ruolo non autorizzato (api, api,security, security, support, reviewer, viewer)
  tenta di aggiornare i metadati, o un admin di un altro ente,
  allora la richiesta di modifica risulta vietata.

    Given un client "CL-1" API creato dal Comune di Comun Nuovo in cui è presente un utente admin e una coppia di chiavi crittografiche
    And un admin del Comune di Comun Nuovo modifica i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    And i dati e le associazioni del client "CL-1" allo stato attuale appuntati come riferimento
    When un api del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un api,security del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un security del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un support del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un reviewer del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un viewer del Comune di Comun Nuovo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento

    When un admin del Comune di Pozzallo tenta di modificare i metadati del client "CL-1":
      | campo       | valore            |
      | nome        | primo nome        |
      | descrizione | prima descrizione |
    Then la richiesta di modifica fallisce come operazione vietata
    And un admin del Comune di Comun Nuovo
    And il client "CL-1" conserva le associazioni e i dati di riferimento
    And l'admin del Comune di Comun Nuovo richiede la generazione del voucher per il client interop
