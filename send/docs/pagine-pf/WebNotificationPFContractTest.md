# WebNotificationPFContractTest

Contract test della pagina **"In arrivo"** del cittadino.

- **Indirizzo:** `{baseUrl}/notifiche`, pagina iniziale dopo il login e voce "In arrivo" del menu laterale
- **Page Object:** [`NotificationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/NotificationPFPage.java)
- **Test:** [`WebNotificationPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/WebNotificationPFContractTest.java)
- **Utente:** Lucrezia Borgia
- **Test di navigazione della pagina:** `WebRecipientPfNavigationContractTest#shouldReachNotificationListPF`, descritto in
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#in-arrivo)

## Cosa verifica

L'`assertLoaded()` della pagina verifica solo che sia caricata: il titolo e i campi dei filtri. È usato anche dagli step
Cucumber. Questo contract test verifica:

- i **testi** della pagina e dei filtri e le opzioni di "Tipologia";
- la **tabella delle notifiche**, la paginazione e il filtro per tipologia;
- il **banner** per attivare il domicilio digitale, mostrato solo a chi non ce l'ha;
- i **messaggi di validazione** dei filtri.

Gli scenari non aprono le notifiche, perché aprirle le segna come lette, e non chiudono il banner.

## La pagina dipende dall'utente

| Elemento | Lucrezia (domicilio digitale attivo) | Utente senza domicilio digitale |
|---|---|---|
| Banner "Niente più documenti cartacei" | assente | titolo, descrizione, "Attiva domicilio digitale" e "Chiudi" |
| Tabella | presente | presente |

Se l'utente non ha notifiche la tabella e la paginazione non ci sono e gli scenari della tabella terminano senza
verificarle; nessun utente di test è in questa situazione. Con Lucrezia sono verificati la tabella e i filtri; il banner
è stato verificato l'08/10/2026 con un utente senza domicilio digitale.

## Elementi verificati

In rosso gli elementi che il contract test legge e verifica, esclusi i messaggi di validazione e i menu aperti
("Tipologia", "Righe per pagina").

Utente con domicilio digitale (Lucrezia):

![In arrivo](img/WebNotificationPFContractTest/pagina.png)

Utente senza domicilio digitale:

![In arrivo, banner per attivare il domicilio digitale](img/WebNotificationPFContractTest/senza-domicilio.png)

## Scenari

### Testi della pagina (`shouldShowNotificationListTexts`)

| Scenario | Cosa verifica |
|---|---|
| titolo, etichette dei filtri e filtra disabilitato | "In arrivo", "Tipologia", "Codice IUN", "Dal", "Al" e "Filtra" disabilitato |
| tipologia propone notifiche a valore legale e comunicazioni | le due opzioni della tendina, in quest'ordine |

### Tabella delle notifiche (`shouldShowNotificationsTable`)

| Scenario | Cosa verifica |
|---|---|
| se presenti, intestazioni e righe con data, mittente, oggetto, IUN e apri | intestazioni "Data", "Mittente", "Oggetto", "Codice IUN" e una vuota; per ogni riga data ("Oggi", "Ieri" o gg/mm/aaaa), mittente, oggetto, IUN nel formato `XXXX-XXXX-XXXX-AAAAMM-X-X` e "Apri" |
| se presenti, paginazione da 10 righe sulla prima pagina | "Righe per pagina" a 10, al massimo 10 righe, pagina precedente disabilitata, pagina "1" e pagina successiva |
| se presenti, righe per pagina propone 10, 20 e 50 | le opzioni del menu |
| se presenti, filtro notifiche a valore legale mostra solo righe con l'etichetta | dopo il filtro ogni oggetto ha l'etichetta "Notifica a valore legale" |
| se presenti, filtro comunicazioni mostra solo righe senza l'etichetta | dopo il filtro nessun oggetto ha l'etichetta |

Dopo "Filtra" la tabella si aggiorna con un breve ritardo: gli scenari dei filtri rileggono le righe fino a 10 secondi.

### Banner per attivare il domicilio digitale (`shouldShowAddDomicileBanner`)

| Scenario | Cosa verifica |
|---|---|
| se l'utente non ha un domicilio digitale, banner con titolo, descrizione e pulsante | "Niente più documenti cartacei", descrizione, "Attiva domicilio digitale" e "Chiudi" |
| se presente, attiva domicilio digitale apre il wizard di attivazione | il wizard "Attiva domicilio digitale su SEND" è caricato |

### Validazioni (`shouldValidateNotificationFilters`)

| Scenario | Cosa verifica |
|---|---|
| IUN non valido | "Inserisci un codice IUN valido" dopo "Filtra" con IUN `abc` |
| data dal precedente a 10 anni fa | "Inserisci una data compresa tra *10 anni fa* e *oggi*" con `01/01/2000` |
| data al nel futuro | lo stesso messaggio con `01/01/2099` |

Le date del messaggio sono calcolate dal test sulla data del giorno.
