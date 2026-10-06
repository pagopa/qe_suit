# WebTermsOfServicePFContractTest

Contract test della pagina **"Termini e condizioni d'uso"** di SEND.

- **Indirizzo:** `{baseUrl}/termini-di-servizio`, dal link "Termini e Condizioni" nel footer del portale
- **Page Object:** [`TermsOfServicePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/TermsOfServicePFPage.java)
- **Test:** [`WebTermsOfServicePFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/WebTermsOfServicePFContractTest.java)
- **Utente:** Lucrezia Borgia (la pagina è la stessa per qualunque cittadino)
- **Test di navigazione della pagina:** `WebRecipientPfNavigationContractTest#shouldReachTermsOfServicePF`, descritto in
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#termini-di-servizio)

## Cosa verifica

L'`assertLoaded()` della pagina verifica solo che sia caricata: che il titolo sia "Termini e condizioni d'uso" e che ci
sia almeno una sezione. Questo contract test verifica:

- i **testi**: titolo, inizio dell'introduzione e titoli delle sezioni;
- l'**indice**: le voci e il collegamento di ognuna a una sezione della pagina.

Il testo è un documento legale caricato da un widget OneTrust. I titoli delle sezioni sono confrontati esattamente: se
il documento cambia il test fallisce e va aggiornato insieme al documento.

La pagina contiene due indici: uno per desktop e uno per mobile, nascosto. Il test usa quello per desktop.

## Scenari

### Testi della pagina (`shouldShowTermsOfServiceTexts`)

| Scenario | Cosa verifica |
|---|---|
| titolo e introduzione | titolo "Termini e condizioni d'uso" e inizio del primo paragrafo |
| titoli delle sezioni | i titoli delle 15 sezioni numerate, nell'ordine |

### Indice delle sezioni (`shouldShowTermsOfServiceIndex`)

| Scenario | Cosa verifica |
|---|---|
| voci dell'indice | "Introduzione", le 15 sezioni numerate e "1341 e 1342 c.c.", nell'ordine |
| ogni voce dell'indice porta a una sezione della pagina | una voce per ogni sezione, ciascuna con un link a una sezione della pagina (`#otnotice-section-…`) |
