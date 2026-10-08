# Il meglio di SEND

Wizard di onboarding `{baseUrl}/onboarding/domicilio-digitale`, dalla card "Scelgo il meglio di SEND" di Configura SEND.

- Page object: [`OnboardingDigitalDomicilePFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/OnboardingDigitalDomicilePFPage.java)
- Test: [`WebOnboardingDigitalDomicilePFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebOnboardingDigitalDomicilePFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachOnboardingDigitalDomicilePF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#onboarding-il-meglio-di-send)

## La pagina

In rosso quello che controlla `assertLoaded()`: che la pagina sia caricata. Ogni passo controlla solo il proprio titolo.

![Il meglio di SEND](img/OnboardingDigitalDomicilePFPage.png)

## Cosa verifica il contract test

Il titolo, "Esci" e i nomi dei quattro passi; poi, per ogni passo, testi e pulsanti, e i messaggi di validazione
dell'email al passo 2. In rosso gli elementi controllati, esclusi i messaggi.

Passo 1, Scegli un domicilio digitale, con la PEC in attivazione (Lucrezia):

![Passo 1, PEC in attivazione](img/WebOnboardingDigitalDomicilePFContractTest/passo-1.png)

Passo 1 per un utente senza PEC:

![Passo 1, scelta tra SEND e PEC](img/WebOnboardingDigitalDomicilePFContractTest/passo-1-scelta.png)

Passo 2, Associa una casella di posta:

![Passo 2](img/WebOnboardingDigitalDomicilePFContractTest/passo-2.png)

Passo 3, Attiva gli avvisi su IO:

![Passo 3](img/WebOnboardingDigitalDomicilePFContractTest/passo-3.png)

Passo 4, riepilogo ("Conferma" non viene premuto):

![Passo 4](img/WebOnboardingDigitalDomicilePFContractTest/passo-4.png)

## Varianti

Il wizard è una sola pagina: i passi cambiano senza cambiare indirizzo. Il passo di apertura e il contenuto dipendono
dai recapiti dell'utente.

| Passo | Lucrezia (PEC in attivazione) | Utente senza PEC |
|---|---|---|
| 1. Scegli un domicilio digitale | "L'attivazione del domicilio digitale su PEC è in corso", raggiunto con Indietro | scelta tra "Attiva su SEND" e "Attiva su una PEC"; è il passo di apertura |
| 2. Associa una casella di posta | la PEC e l'email per gli avvisi; è il passo di apertura | raggiungibile solo dopo una scelta al passo 1 |
| 3. Attiva gli avvisi su IO | blocco "Attiva SEND sull'app IO" | come sopra |
| 4. Controlla le opzioni scelte | riepilogo | come sopra |

Il passo 1 con la scelta tra SEND e PEC è stato provato il 07/10/2026 con un utente senza PEC. Per lui i passi 2-4 non
si raggiungono senza scegliere, quindi il test non li controlla.

## Note

- Il test si sposta tra i passi con Indietro e Avanti, che non salvano nulla. Non fa scelte e non preme "Conferma".
- Al passo 2 "Modifica" apre il campo dell'email: con un'email valida "Conferma" invierebbe il codice di verifica, quindi
  il test usa solo un campo vuoto o `abc` e si aspetta "Indirizzo email non valido". Qui il portale toglie gli spazi
  prima di validare.
- I testi del blocco "Attiva SEND sull'app IO" sono gli stessi di "Tutto, sull'app IO" e di "Attivazione avvisi": le
  costanti sono in `OnboardingIoExpectedTexts`.
