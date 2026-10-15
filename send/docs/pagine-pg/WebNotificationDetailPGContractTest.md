# Dettaglio della notifica

Pagina `{baseUrl}/notifiche/<IUN>/dettaglio`: il dettaglio di una notifica a valore legale ricevuta dall'impresa. Si
apre dal pulsante "Apri" di una riga di [In arrivo per l'impresa](WebNotificationPGContractTest.md). `{baseUrl}` è la
proprietà `url.notifiche.persona-giuridica.base` del profilo attivo, ad esempio
`https://imprese.test.notifichedigitali.it` sul profilo `test`.

- Ticket: QA-18351
- Page object: [`NotificationDetailPGPage`](../../src/main/java/it/pagopa/send/web/destinatario_pg/infrastructure/page/NotificationDetailPGPage.java),
  raggiunta da [`NotificationPage`](../../src/main/java/it/pagopa/send/web/destinatario_pg/infrastructure/page/NotificationPage.java)
- Test: [`WebNotificationDetailPGContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pg/WebNotificationDetailPGContractTest.java)
- Utente: Francesco Petrarca, amministratore di Le Epistolae srl
- Notifica: l'ultima notifica a valore legale della lista "In arrivo per Le Epistolae srl"
- Componente in pn-frontend: `packages/pn-personagiuridica-webapp/src/pages/NotificationDetail.page.tsx`, versione
  `afedb936a9db15abe40c394056b3516c6e416fcb`

## La pagina

`assertLoaded()` controlla il titolo, cioè l'oggetto della notifica, il breadcrumb e il codice IUN.

## Cosa verifica il contract test

Ogni scenario apre "In arrivo per Le Epistolae srl" e preme "Apri" sull'ultima notifica a valore legale. Si verifica
solo quello che c'è in ogni notifica a valore legale:

| Elemento | Proprietà | Atteso | Fonte |
|---|---|---|---|
| Breadcrumb, prima voce | Testo | "In arrivo per Le Epistolae srl" | pn-frontend, `locales/it/common.json`, chiave `menu.notifiche-impresa` |
| Breadcrumb, voce corrente | Testo | uguale al titolo della notifica | `NotificationDetail.page.tsx`, `notification.subject` |
| Etichetta sopra il titolo | Testo | "Notifica a valore legale" | `locales/it/notifiche.json`, chiave `detail.legal-value` |
| Logo del mittente | Presenza nel DOM | presente | `AbstractPaper.tsx`, `InstitutionLogo` |
| Mittente | Testo | non vuoto | `AbstractPaper.tsx`, `senderDenomination` |
| Data di deposito | Testo | "Notifica depositata il giorno " seguito da una data `gg/mm/aaaa` | `locales/it/notifiche.json`, chiave `detail.legal-date` |
| Codice IUN | Testo | "Codice IUN" | `locales/it/common.json`, chiave `iun` |
| Valore dello IUN | Testo | formato IUN, es. `XYDV-UNLJ-TVND-202305-M-1` | `AbstractPaper.tsx`, `iun` |
| Informativa | Testo | "Notifica inviata tramite SEND, la piattaforma disciplinata dall’art. 26 del D.L. 76/2020" | `locales/it/notifiche.json`, chiave `detail.legal-disclaimer` |
| Documenti | Testo | "Documenti allegati" | `locales/it/notifiche.json`, chiave `detail.acts` |
| Stato della notifica | Testo | "Stato della notifica" | `locales/it/notifiche.json`, chiave `detail.notification-timeline-section.title` |
| Stato corrente | Testo | non vuoto | `NotificationTimelineBox.tsx`, stato più recente della notifica |
| "Vai al dettaglio" | Testo e stato iniziale | "Vai al dettaglio", abilitato | `locales/it/notifiche.json`, chiave `go-to-detail` |
| Dettaglio della notifica | Testo | "Dettaglio della notifica" | `locales/it/notifiche.json`, chiave `detail.notification-detail-section.title` |
