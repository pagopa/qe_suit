@login
Feature: Login pagoPA demo

Scenario: Login pagoPA mittente
  Given l'utente Grossini effettua l'accesso a SelfCare con autenticazione SPID
  When l'utente accede alla Dashboard selezionando "Comune di Palermo"
  And l'utente accede alla area riservata e seleziona il prodotto SEND
  Then la pagina Dashboard è caricata con successo
  When viene effettutato il logout
  Then la pagina LoginPage è caricata con successo


Scenario: Login mittente con token
  Given l'utente è un "admin" di "Comune di Verona"
  When naviga alla pagina Dashboard
  Then la pagina deve caricarsi correttamente
  When viene effettutato il logout
  Then la pagina LoginPage è caricata con successo