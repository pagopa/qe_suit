# WebDelegationsPFContractTest

Contract test della pagina **"Deleghe"** del cittadino.

- **Indirizzo:** `{baseUrl}/deleghe`, dalla voce "Deleghe" del menu laterale
- **Page Object:** [`DelegationsPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DelegationsPFPage.java)
- **Test:** [`WebDelegationsPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/WebDelegationsPFContractTest.java)
- **Utente:** Lucrezia Borgia
- **Test di navigazione della pagina:** `WebRecipientPfNavigationContractTest#shouldReachDelegationsPF`, descritto in
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#deleghe)

## Cosa verifica

L'`assertLoaded()` della pagina verifica solo che sia caricata: il titolo, il pulsante "Aggiungi una delega" e la
presenza delle due sezioni. Questo contract test verifica tutto il resto:

- i **testi** della pagina e delle sezioni;
- i **pulsanti** e i link, compresa la pagina che aprono;
- il **contenuto delle sezioni** "I tuoi delegati" e "Deleghe a tuo carico".

## Il contenuto delle sezioni dipende dall'utente

Ogni sezione mostra un messaggio se è vuota, altrimenti una tabella con le deleghe. Gli scenari delle sezioni leggono
prima il testo della sezione, che c'è sempre, e verificano lo stato presente:

| Sezione | Sezione vuota | Sezione con deleghe |
|---|---|---|
| I tuoi delegati | messaggio "Non hai delegato nessuno…" e link "Aggiungi una delega" | intestazioni e, per ogni riga, nome, date, permessi, stato e menu |
| Deleghe a tuo carico | messaggio "Non hai deleghe a tuo carico." | intestazioni e, per ogni riga, nome, date, permessi, stato e menu |

Le due tabelle hanno la stessa struttura e sono mappate con lo stesso componente (`DelegationsTable`). Lucrezia non ha
deleghe, quindi con lei vengono verificati gli stati vuoti; i rami con le tabelle sono stati eseguiti il 06/10/2026 con
un utente che ha sia delegati sia deleghe a carico.

Nessuno scenario crea, accetta, rifiuta o revoca deleghe.

## Elementi verificati

In rosso gli elementi che il contract test legge e verifica, esclusi i messaggi di validazione.

Utente senza deleghe (Lucrezia):

![Deleghe, utente senza deleghe](img/WebDelegationsPFContractTest/pagina.png)

Utente con delegati e deleghe a carico:

![Deleghe, utente con deleghe](img/WebDelegationsPFContractTest/con-deleghe.png)

## Scenari

Il test è diviso in tre gruppi, uno per aspetto della pagina. I nomi sono quelli che compaiono nel report dei test; i
testi attesi sono costanti in cima alla classe.

### Testi della pagina (`shouldShowDelegationsTexts`)

| Scenario | Cosa verifica |
|---|---|
| intestazione della pagina | titolo e sottotitolo |
| titoli delle sezioni | "I tuoi delegati" e "Deleghe a tuo carico" |

### Pulsanti (`shouldShowDelegationsButtons`)

| Scenario | Cosa verifica |
|---|---|
| pulsante aggiungi una delega | testo del pulsante "Aggiungi una delega" |
| il pulsante aggiungi una delega apre la pagina aggiungi una delega | il clic apre la pagina "Aggiungi una delega" (verificata con il suo `assertLoaded()`) |

### Contenuto delle sezioni (`shouldShowDelegationsSections`)

| Scenario | Cosa verifica |
|---|---|
| i tuoi delegati: messaggio di sezione vuota oppure tabella delle deleghe | il messaggio di sezione vuota e il link "Aggiungi una delega", oppure la tabella come per le deleghe a carico |
| se vuota, il link della sezione i tuoi delegati apre la pagina aggiungi una delega | il clic sul link apre la pagina "Aggiungi una delega"; se l'utente ha delegati il link non c'è e lo scenario termina senza verificarlo |
| deleghe a tuo carico: messaggio di sezione vuota oppure tabella delle deleghe | il messaggio di sezione vuota, oppure le intestazioni della tabella (Nome, Inizio delega, Fine delega, Permessi, Stato) e su ogni riga nome non vuoto, date `gg/mm/aaaa`, permessi, stato ("Accetta" o lo stato della delega) e menu delle azioni |
