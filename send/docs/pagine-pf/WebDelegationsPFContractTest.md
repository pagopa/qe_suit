# Deleghe

Pagina `{baseUrl}/deleghe`, dalla voce "Deleghe" del menu laterale.

- Page object: [`DelegationsPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DelegationsPFPage.java)
- Test: [`WebDelegationsPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebDelegationsPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachDelegationsPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md)

## La pagina

`assertLoaded()` controlla il titolo, "Aggiungi una delega" e le due sezioni.

## Cosa verifica il contract test

Solo quello che c'è per tutti gli utenti.

| Parte | Cosa si controlla |
|---|---|
| Intestazione | titolo e sottotitolo |
| Sezioni | titoli "I tuoi delegati" e "Deleghe a tuo carico" |
| Aggiungi una delega | il pulsante e la pagina "Aggiungi una delega" che apre |

## Non verificato

Il contenuto delle due sezioni dipende dalle deleghe dell'utente e non viene verificato: messaggio di sezione vuota con
il link "Aggiungi una delega" oppure tabella delle deleghe.

## Note

- Il test non crea, accetta, rifiuta o revoca deleghe.
