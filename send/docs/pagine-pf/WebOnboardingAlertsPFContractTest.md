# Attivazione avvisi

Wizard di onboarding `{baseUrl}/onboarding/avvisi`, dalla card "Voglio solo gli avvisi" di Configura SEND.

- Page object: [`OnboardingAlertsPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/OnboardingAlertsPFPage.java)
- Test: [`WebOnboardingAlertsPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebOnboardingAlertsPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachOnboardingAlertsPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#onboarding-attivazione-avvisi)

## La pagina

In rosso quello che controlla `assertLoaded()`: che la pagina sia caricata. Ogni passo controlla solo di avere un
contenuto.

![Attivazione avvisi](img/OnboardingAlertsPFPage.png)

## Cosa verifica il contract test

Il titolo, "Esci" e i nomi dei due passi; per ogni passo testi e pulsanti, e i messaggi di validazione di email e
cellulare. In rosso gli elementi controllati, esclusi i messaggi.

Passo 1, Attiva gli avvisi su IO:

![Passo 1](img/WebOnboardingAlertsPFContractTest/passo-1.png)

Passo 2, Email e SMS, con i recapiti già attivi (Lucrezia):

![Passo 2, recapiti attivi](img/WebOnboardingAlertsPFContractTest/passo-2.png)

Passo 2 con i recapiti da inserire:

![Passo 2, recapiti da inserire](img/WebOnboardingAlertsPFContractTest/passo-2-da-inserire.png)

## Varianti

Al passo 2 email e cellulare cambiano a seconda che il recapito sia già attivo.

| Recapito | Già attivo (Lucrezia) | Da inserire |
|---|---|---|
| Email | "Avvisi via email attivi", l'indirizzo e Modifica | "Attiva gli avvisi via email", campo vuoto e "Verifica email" |
| Cellulare | "Avvisi via SMS attivi", il numero e Modifica | "Attiva gli avvisi via SMS", campo vuoto, "Verifica numero" e "Annulla attivazione SMS" |

I blocchi da inserire sono stati provati il 07/10/2026 con un utente senza recapiti di cortesia.

## Validazioni

Il test usa solo valori non validi anche togliendo gli spazi (`abc`, `123`): con un valore valido "Verifica" e
"Conferma" invierebbero il codice di verifica. I messaggi compaiono dopo il clic sul pulsante.

| Campo | Vuoto o non valido | Con spazi all'inizio o alla fine |
|---|---|---|
| Email da inserire | "Indirizzo email non valido" | "Elimina gli spazi all'inizio o alla fine" |
| Cellulare da inserire | "Numero di cellulare non valido" | "Elimina gli spazi all'inizio o alla fine" |
| Email o cellulare da modificare | "Indirizzo email non valido" / "Numero di cellulare non valido" | il portale toglie gli spazi prima di validare |

## Note

- Il test si sposta tra i passi con Indietro e Avanti, che non salvano nulla. Non compila i campi con valori validi e
  non preme "Conferma" sull'ultimo passo.
- I testi del blocco "Attiva SEND sull'app IO" sono gli stessi di "Tutto, sull'app IO" e de "Il meglio di SEND": le
  costanti sono in `OnboardingIoExpectedTexts`.
