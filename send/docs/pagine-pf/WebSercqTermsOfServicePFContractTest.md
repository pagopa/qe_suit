# Termini e condizioni d'uso SERCQ

Pagina `{baseUrl}/termini-di-servizio/sercq-send`, dal link "Termini del servizio" nel riepilogo del wizard di
attivazione del domicilio digitale.

- Page object: [`SercqTermsOfServicePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/SercqTermsOfServicePFPage.java)
- Test: [`WebSercqTermsOfServicePFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebSercqTermsOfServicePFContractTest.java)
- Utente: Lucrezia Borgia (la pagina è uguale per tutti i cittadini)
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachSercqTermsOfServicePF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#termini-di-servizio-sercq)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo e la presenza di almeno una sezione.

![Termini e condizioni d'uso SERCQ](img/SercqTermsOfServicePFPage.png)

## Cosa verifica il contract test

Il titolo, l'inizio dell'introduzione, i titoli delle 10 sezioni e l'indice: le voci, nell'ordine, e il link di ognuna
a una sezione della pagina (`#otnotice-section-…`). In rosso gli elementi controllati.

![Termini e condizioni d'uso SERCQ, elementi verificati](img/WebSercqTermsOfServicePFContractTest/pagina.png)

## Note

- Il testo è un documento legale caricato da un widget OneTrust. I titoli sono confrontati esattamente: se il documento
  cambia, il test va aggiornato.
- La pagina ha due indici, uno per desktop e uno nascosto per mobile. Il test usa quello per desktop.
