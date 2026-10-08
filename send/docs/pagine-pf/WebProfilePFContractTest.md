# I tuoi dati

Pagina `{baseUrl}/profilo`, dal menu dell'area utente in alto (il pulsante con il nome dell'utente).

- Page object: [`ProfilePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/ProfilePFPage.java)
- Test: [`WebProfilePFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebProfilePFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachProfilePF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#i-tuoi-dati)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo e le tre righe dei dati.

![I tuoi dati](img/ProfilePFPage.png)

## Cosa verifica il contract test

Titolo, sottotitolo, le etichette "Nome", "Cognome" e "Codice fiscale" e i tre valori. In rosso gli elementi controllati.

![I tuoi dati, elementi verificati](img/WebProfilePFContractTest/pagina.png)

## Note

- I valori sono confrontati con quelli di `Recipient.LUCREZIA` (`denomination`, `familyName`, `taxId`): il test controlla
  anche che la pagina mostri i dati dell'utente giusto.
- I dati arrivano da SPID o CIE e non si possono modificare: la pagina non ha form né pulsanti.
