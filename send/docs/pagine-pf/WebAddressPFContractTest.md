# I tuoi recapiti

Pagina `{baseUrl}/recapiti`, dalla voce "I tuoi recapiti" del menu laterale.

- Page object: [`AddressPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/AddressPFPage.java)
- Test: [`WebAddressPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebAddressPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachAddressPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#i-tuoi-recapiti)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo e le card del domicilio digitale, dell'app IO e dell'email.
Lo usano anche gli step Cucumber dei recapiti.

![I tuoi recapiti](img/AddressPFPage.png)

## Cosa verifica il contract test

I testi della pagina, il contenuto di ogni card e i messaggi di validazione. In rosso gli elementi controllati, esclusi
i messaggi.

Con recapiti attivi (Lucrezia):

![I tuoi recapiti, recapiti attivi](img/WebAddressPFContractTest/pagina.png)

Senza recapiti:

![I tuoi recapiti, recapiti da attivare](img/WebAddressPFContractTest/senza-recapiti.png)

## Varianti

Ogni card cambia con i recapiti dell'utente e il test controlla quella che trova.

| Card | Recapito attivo | Da attivare |
|---|---|---|
| Domicilio digitale | PEC con Modifica, Gestisci e Disattiva; se ci sono, le PEC per ente con Modifica ed Elimina | Perché è utile?, i tre vantaggi e Inizia |
| SEND sull'app IO | nessun utente di test ce l'ha attivo | descrizione e Scarica l'app IO |
| Email | email con Modifica e Disattiva | campo vuoto e Aggiungi email |
| Cellulare | card "Il tuo cellulare" con Modifica e Disattiva | Aggiungi numero di cellulare, che apre il campo con Aggiungi numero e Annulla |

Le varianti "da attivare" sono state provate l'08/10/2026 con un utente senza recapiti.

## Note

- Il test non preme mai Disattiva, Elimina, Gestisci o Inizia.
- Nelle validazioni usa solo valori non validi anche togliendo gli spazi (`abc`, `123`): con un valore valido il portale
  invierebbe il codice di verifica.
- Messaggi attesi: "Indirizzo PEC non valido", "Indirizzo email non valido" e "Numero di cellulare non valido".
- Gli spazi all'inizio o alla fine hanno un messaggio dedicato solo per il cellulare ("Elimina gli spazi all'inizio o
  alla fine"); per l'email il portale mostra quello generico.
