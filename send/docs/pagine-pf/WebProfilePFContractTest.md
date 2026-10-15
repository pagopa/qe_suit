# I tuoi dati

Pagina `{baseUrl}/profilo`, dal menu dell'area utente in alto (il pulsante con il nome dell'utente).

- Page object: [`ProfilePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/ProfilePFPage.java)
- Test: [`WebProfilePFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebProfilePFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachProfilePF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md)

## La pagina

`assertLoaded()` controlla il titolo e le tre righe dei dati.

## Cosa verifica il contract test

Titolo, sottotitolo, le etichette "Nome", "Cognome" e "Codice fiscale" e i tre valori.
