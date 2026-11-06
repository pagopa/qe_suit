@eservice-import-export-error-messages
@channel:Given=BFF,When=WEB,Then=WEB
Feature: Messaggi di errore nell'importazione e nell'esportazione degli e-service

  Come amministratore di un ente erogatore o delegato alla sua erogazione
  Voglio ricevere un messaggio di errore nella lingua selezionata quando un e-service non può essere importato o esportato
  Per comprendere i vincoli dell'operazione e correggere i dati.

  # Feature in attesa dell'implementazione degli step mancanti.
  # Ogni scenario utilizza e-service, deleghe e archivi di test dedicati.
  # Il setup delle deleghe deve evitare interferenze sulla disponibilità condivisa degli enti.
  # La lingua va verificata nell'interfaccia; il Then verifica la snackbar visibile
  # e il testo esatto del catalogo, senza aggiungere SUPPORT_FOOTER.
  # Verificare la riproducibilità WEB senza aggirare controlli UI o simulare risposte.

  Scenario Outline: [ESERVICE_IMPORT_EXPORT_ERROR_MESSAGE_1] - Esportazione dal proprietario con una delega attiva
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente all'erogatore Comune di Milano
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della versione pubblicata dell'e-service
    And l'utente tenta di esportare la versione dell'e-service
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_DELEGATED_ESERVICE_NOT_EXPORTABLE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_IMPORT_EXPORT_ERROR_MESSAGE_2] - Esportazione da parte del delegato
    Given un e-service pubblicato dal Comune di Milano con approvazione manuale delle richieste di fruizione
    And l'e-service è delegato in erogazione dal Comune di Milano al Comune di Pozzallo con una delega in stato ACTIVE
    And un admin appartenente al delegato Comune di Pozzallo
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre il dettaglio della versione pubblicata dell'e-service gestito in delega
    And l'utente tenta di esportare la versione dell'e-service
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_INVALID_ESERVICE_REQUESTER__GET_EXPORT_ESERVICES_ESERVICE_ID_DESCRIPTORS_DESCRIPTOR_ID" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |

  Scenario Outline: [ESERVICE_IMPORT_EXPORT_ERROR_MESSAGE_3] - Importazione di un archivio ZIP con struttura non valida
    Given un archivio ZIP di test leggibile con struttura di importazione non valida, di dimensione consentita e identificato come "archivio da importare"
    And i dati dell'e-service nell'archivio non presentano conflitti di nome con e-service del Comune di Milano
    And un utente autenticato del Comune di Milano con ruolo admin
    When l'utente seleziona e verifica la lingua dell'interfaccia "<lingua>"
    And l'utente apre l'importazione di un e-service
    And l'utente tenta di importare l'"archivio da importare"
    Then viene mostrata la snackbar di errore con il testo esatto del messaggio di catalogo "BFF_INVALID_ZIP_STRUCTURE" in lingua "<lingua>"

    Examples:
      | lingua |
      | it     |
      | en     |
