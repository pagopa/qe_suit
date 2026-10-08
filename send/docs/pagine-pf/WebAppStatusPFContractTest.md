# Stato della piattaforma

Pagina `{baseUrl}/app-status`, dalla voce "Stato della piattaforma" del menu laterale.

- Page object: [`AppStatusPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/AppStatusPFPage.java)
- Test: [`WebAppStatusPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebAppStatusPFContractTest.java)
- Utente: Lucrezia Borgia (la pagina è uguale per tutti i cittadini)
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachAppStatusPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#stato-della-piattaforma)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo, lo stato attuale e l'ultimo aggiornamento.

![Stato della piattaforma](img/AppStatusPFPage.png)

## Cosa verifica il contract test

I testi della pagina, lo stato attuale dei servizi con l'ora dell'ultimo aggiornamento e lo storico dei disservizi con
la paginazione. In rosso gli elementi controllati.

![Stato della piattaforma, elementi verificati](img/WebAppStatusPFContractTest/pagina.png)

## Varianti

Stato e storico dipendono dai disservizi della piattaforma, non dall'utente. Il test controlla i testi fissi e il
formato dei dati, non i valori.

| Parte | Cosa si controlla |
|---|---|
| Stato attuale | con tutti i servizi operativi, "Tutti i servizi di SEND sono operativi." e l'icona verde; durante un disservizio solo che ci sia un messaggio |
| Ultimo aggiornamento | "Ultimo aggiornamento - &lt;giorno&gt;, ore hh:mm" |
| Storico | se c'è: le colonne e, per ogni riga, inizio `gg/mm/aaaa, ore hh:mm`, servizio e stato; per i disservizi risolti anche la fine e "Scarica l'attestazione" |
| Paginazione | se c'è: 10 righe per pagina, prima pagina selezionata, "Indietro" disabilitato |

Il 07/10/2026 i servizi erano tutti operativi e lo storico aveva più di 10 disservizi, quindi sono stati provati
storico e paginazione. Un disservizio in corso non si può provocare dal test.

## Note

- Il test non scarica le attestazioni.
