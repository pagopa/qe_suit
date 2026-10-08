# WebNotificationDetailsPFContractTest

Contract test del **dettaglio di una notifica** del cittadino e della pagina **"Stato della notifica"** (timeline).

- **Indirizzi:**
  - `{baseUrl}/notifiche/<IUN>/dettaglio`, notifica a valore legale, da "Apri" in "In arrivo"
  - `{baseUrl}/comunicazione/<IUN>/dettaglio`, comunicazione, da "Apri" in "In arrivo"
  - `{baseUrl}/notifiche/<IUN>/dettaglio/timeline`, da "Vai al dettaglio" nella sezione "Stato della notifica"
- **Page Object:** [`NotificationDetailsPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/NotificationDetailsPFPage.java)
  e [`NotificationTimelinePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/NotificationTimelinePFPage.java)
- **Test:** [`WebNotificationDetailsPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/WebNotificationDetailsPFContractTest.java)
- **Utente:** Lucrezia Borgia
- **Test di navigazione delle pagine:** `WebRecipientPfNavigationContractTest#shouldReachNotificationDetailsPF` e
  `#shouldReachNotificationTimelinePF`, descritti in
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#dettaglio-notifica)

## Cosa verifica

L'`assertLoaded()` del dettaglio verifica solo che sia caricato (oggetto, breadcrumb e IUN), quello della timeline il
titolo e il breadcrumb. Questo contract test verifica:

- il **dettaglio di una notifica a valore legale**: intestazione, documenti, stato, avviso di avvenuta ricezione e
  disservizi;
- il **dettaglio di una comunicazione**: intestazione, messaggio, documenti, pagamenti e contatti del mittente;
- la **timeline** della notifica a valore legale.

Le sezioni del dettaglio che implementano `NotificationDetailsPage` sono condivise con gli step Cucumber e non sono
cambiate: il contract test usa campi nuovi.

## Solo notifiche già lette

Ogni scenario parte da "In arrivo", filtra per tipologia e apre la **prima notifica già letta**, cioè senza il pallino
di notifica nuova: aprire una notifica a valore legale non letta la segnerebbe come letta, con valore di presa visione.
Se in prima pagina non c'è una notifica già letta della tipologia lo scenario termina senza aprirne una.

Nessuno scenario scarica documenti o attestazioni né preme "Paga".

Il contenuto dipende dalla notifica aperta: mittente, oggetto, IUN, stato ed eventi si verificano nel formato; avviso di
avvenuta ricezione, disservizi, pagamenti e contatti del mittente si verificano se presenti. Gli scenari sono stati
eseguiti anche con un utente senza recapiti l'08/10/2026: la sua notifica a valore legale non ha l'avviso di avvenuta
ricezione e il pulsante "Paga" mostra l'importo.

## Elementi verificati

In rosso gli elementi che il contract test legge e verifica.

Notifica a valore legale (Lucrezia):

![Dettaglio di una notifica a valore legale](img/WebNotificationDetailsPFContractTest/notifica-valore-legale.png)

Comunicazione (Lucrezia):

![Dettaglio di una comunicazione](img/WebNotificationDetailsPFContractTest/comunicazione.png)

Stato della notifica (Lucrezia):

![Stato della notifica](img/WebNotificationDetailsPFContractTest/timeline.png)

## Scenari

### Notifica a valore legale (`shouldShowLegalNotificationDetails`)

| Scenario | Cosa verifica |
|---|---|
| se presente, intestazione con breadcrumb, oggetto, mittente, data di deposito e IUN | breadcrumb "In arrivo" e oggetto, mittente, "Notifica depositata il giorno gg/mm/aaaa", "Codice IUN" e IUN nel formato `XXXX-XXXX-XXXX-AAAAMM-X-X` |
| se presente, documenti allegati con messaggio sulla disponibilità | "Documenti allegati", il messaggio sui 120 giorni e i nomi dei documenti |
| se presente, stato della notifica con stato corrente e vai al dettaglio | "Stato della notifica", stato corrente e "Vai al dettaglio" |
| se presenti, avviso di avvenuta ricezione e disservizi | "Dettaglio della notifica" con "Avviso di avvenuta ricezione" e il titolo "Disservizi", se presenti |

### Comunicazione (`shouldShowCommunicationDetails`)

| Scenario | Cosa verifica |
|---|---|
| se presente, intestazione con breadcrumb, oggetto, mittente, data di deposito e IUN | come per la notifica a valore legale, con "Comunicazione depositata il giorno gg/mm/aaaa" |
| se presente, messaggio del mittente e nota sugli effetti giuridici | il messaggio è presente (il testo è del mittente) e la nota "Questa comunicazione potrebbe produrre effetti giuridici..." |
| se presente, documenti allegati con messaggio sulla disponibilità | "Documenti allegati", il messaggio sui 180 giorni e i nomi dei documenti |
| se presenti, pagamenti con paga non premuto e contatti del mittente | "Pagamenti" e "Paga", con l'importo se recuperato; "Contatta il mittente" con le etichette dei contatti |

### Timeline (`shouldShowNotificationTimeline`)

| Scenario | Cosa verifica |
|---|---|
| se presente, vai al dettaglio apre la timeline con breadcrumb, titolo ed eventi | titolo "Stato della notifica", titolo di ogni evento, date nel formato "02 OTT, 12:52" e breadcrumb "In arrivo", oggetto e "Stato della notifica" |

Non tutti gli eventi hanno una data (es. "Invio della notifica in corso"). Il breadcrumb dell'oggetto mostra "Dettaglio
notifica" finché non arrivano i dati, per cui il test lo legge dopo gli eventi.
