@BFF @delete-producer-keychain
Feature: Gestione dei Producer Keychain (BFF API Headless) - Test di Flusso Funzionale

  Come utente autenticato di un Ente Erogatore (Aderente)
  Voglio gestire i Producer Keychain tramite gli endpoint dedicati del BFF
  Al fine di configurare e manutenere correttamente il materiale crittografico necessario a garantire l'integrità delle risposte degli E-Service

  ***
  OBIETTIVI DEL TEST BFF (INTEGRAZIONE API):
  1. Validare il comportamento degli endpoint BFF quando sollecitati direttamente via protocollo HTTP.
  2. Verificare che l'isolamento del tenant (Ente Erogatore/Fruitore) sia garantito dai token di sessione iniettati nella richiesta.
  3. Garantire la corretta conclusione dei flussi e la coerenza dei dati nel rispetto delle regole di business e dei vincoli di dominio.
  ***

  Scenario: [DELETE_PRODUCER_KEYCHAIN_1]
  Dato un utente di un Ente Erogatore, con i con permessi di gestione per l'Ente, correttamente autenticato in BFF e con una lista di Producer Keychain non vuota,
  quando viene invocato l'endpoint di eliminazione per ogni Producer Keychain nella lista,
  allora la successiva lettura massiva restituisce una lista vuota

    Given una sessione HTTP autenticata su BFF
    And un admin del Comune di Milano
    And una lista di 10 Producer Keychain associati al contesto dell'Ente Erogatore
    When viene inviata una richiesta di eliminazione per ogni Producer Keychain presente nella lista
    Then la lista di tutti i Producer Keychain per l'Ente risulta vuota