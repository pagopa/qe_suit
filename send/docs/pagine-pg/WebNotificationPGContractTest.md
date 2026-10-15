# In arrivo per l'impresa

Pagina `{baseUrl}/notifiche`: si apre dopo il login e dalla voce "In arrivo per &lt;impresa&gt;" del menu laterale.
`{baseUrl}` è la proprietà `url.notifiche.persona-giuridica.base` del profilo attivo, ad esempio
`https://imprese.test.notifichedigitali.it` sul profilo `test`.

- Ticket: QA-18349
- Page object: [`NotificationPage`](../../src/main/java/it/pagopa/send/web/destinatario_pg/infrastructure/page/NotificationPage.java),
  con gli elementi comuni al cittadino in [`RecipientNotificationsPage`](../../src/main/java/it/pagopa/send/web/notification_search/infrastructure/suit/RecipientNotificationsPage.java)
- Test: [`WebNotificationPGContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pg/WebNotificationPGContractTest.java)
- Utente: Francesco Petrarca, amministratore di Le Epistolae srl
- Navigazione: `WebRecipientPgNavigationContractTest#shouldReachNotifications`
- Componente in pn-frontend: `packages/pn-personagiuridica-webapp/src/pages/Notifiche.page.tsx`, versione
  `afedb936a9db15abe40c394056b3516c6e416fcb`

## La pagina

`assertLoaded()` controlla il titolo, che inizia con "In arrivo per ". Lo usa anche il login PG degli step Cucumber.

## Cosa verifica il contract test

Solo quello che c'è per tutti gli utenti, cioè il titolo: il resto della pagina cambia in base alle notifiche ricevute,
ai recapiti e al ruolo (vedi sotto).

| Elemento | Proprietà | Atteso | Fonte |
|---|---|---|---|
| Titolo | Testo | "In arrivo per &lt;impresa&gt;", con Petrarca "In arrivo per Le Epistolae srl" | pn-frontend, `locales/it/notifiche.json`, chiave `title` |

## Perché viene validato solo il titolo

Nel codice della pagina (`Notifiche.page.tsx` e `components/Notifications/FilterNotifications.tsx`) solo il titolo è
sempre presente:

| Elemento | Quando c'è |
|---|---|
| Titolo "In arrivo per &lt;impresa&gt;" | sempre |
| Filtri (Tipologia, Codice IUN, Dal, Al, Filtra) | solo se l'impresa ha almeno una notifica (`showFilters = notifications.length > 0 \|\| filtersApplied`) |
| Tabella delle notifiche e paginazione | solo se l'impresa ha almeno una notifica |
| Messaggio "Qui vedrai le notifiche a valore legale e le altre comunicazioni" con "Vai ai recapiti" | solo se l'impresa non ha notifiche |
| Banner "Niente più documenti cartacei" | solo agli amministratori senza gruppo, e solo se l'impresa non ha un domicilio digitale |

Sul portale di test:

- Le Epistolae srl (Francesco Petrarca), che ha notifiche, vede filtri e tabella;
- Infinito srl (Giacomo Leopardi), che non ne ha, vede al loro posto il messaggio con "Vai ai recapiti".

Nessuna delle due imprese ha un domicilio digitale, quindi entrambe vedono il banner.
