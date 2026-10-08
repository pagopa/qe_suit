# Aggiungi una delega

Pagina `{baseUrl}/deleghe/nuova`, dal pulsante "Aggiungi una delega" della pagina Deleghe.

- Page object: [`NewDelegationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/NewDelegationPFPage.java)
- Test: [`WebNewDelegationPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebNewDelegationPFContractTest.java)
- Utente: Lucrezia Borgia (il form è uguale per tutti i cittadini)
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachNewDelegationPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#aggiungi-una-delega)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo, i campi e i pulsanti del form.

![Aggiungi una delega](img/NewDelegationPFPage.png)

## Cosa verifica il contract test

I testi della pagina e del form, il codice di verifica di 5 cifre, i valori iniziali (persona fisica, tutti gli enti,
termine della delega a domani), i campi che cambiano con le scelte e i messaggi di validazione. In rosso gli elementi
controllati, esclusi i messaggi.

All'apertura:

![Aggiungi una delega, elementi verificati](img/WebNewDelegationPFContractTest/pagina.png)

Con "Persona giuridica", la ragione sociale al posto di nome e cognome:

![Aggiungi una delega, persona giuridica](img/WebNewDelegationPFContractTest/persona-giuridica.png)

Con "Solo enti selezionati" e l'elenco degli enti aperto:

![Aggiungi una delega, solo enti selezionati](img/WebNewDelegationPFContractTest/solo-enti-selezionati.png)

## Validazioni

Il form ha due livelli di controllo:

- il browser, se un campo obbligatorio è vuoto, mostra il fumetto "Compila questo campo.": non fa parte della pagina e
  il test non lo può leggere;
- il portale, se un valore non è valido, colora il campo e mostra un messaggio sotto (`<id campo>-helper-text`).

Per vedere i messaggi del portale il test compila i campi obbligatori. Controlla: codice fiscale non valido (persona
fisica e giuridica), spazi all'inizio o alla fine in nome, cognome e ragione sociale, termine della delega vuoto, e
"Data errata" con una data passata, il 31 febbraio o una data incompleta.

## Note

- Il test non crea mai una delega: quando preme "Invia la richiesta" il codice fiscale non è valido, quindi l'invio
  viene bloccato.
- Dell'elenco degli enti si controlla solo che non sia vuoto: i nomi dipendono dagli enti attivi sulla piattaforma.
