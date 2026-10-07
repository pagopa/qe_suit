# WebOnboardingIoPFContractTest

Contract test della pagina di onboarding **"Tutto, sull'app IO"** del cittadino.

- **Indirizzo:** `{baseUrl}/onboarding/io`
- **Page Object:** [`OnboardingIoPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/OnboardingIoPFPage.java)
- **Test:** [`WebOnboardingIoPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/WebOnboardingIoPFContractTest.java)
- **Utente:** Lucrezia Borgia
- **Test di navigazione della pagina:** `WebRecipientPfNavigationContractTest#shouldReachOnboardingIoPF`, descritto in
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#onboarding-tutto-sullapp-io)

## La pagina

![Tutto, sull'app IO](img/OnboardingIoPFPage.png)

In rosso gli elementi verificati dall'`assertLoaded()`.

## Da dove si arriva

Dal portale la pagina si apre **solo dalla terza card** di "Configura SEND" (`{baseUrl}/onboarding`), "Preferisco
attivare solo SEND sull'app IO", che è mostrata solo agli utenti **senza recapiti di cortesia** (email, SMS o app IO).
Agli altri utenti la card non compare e la pagina non è raggiungibile dal portale.

![Configura SEND con la terza card](img/WebConfigureAddressSendPFContractTest/card-io.png)

Il collegamento tra la card e la pagina è verificato da
[`WebConfigureAddressSendPFContractTest`](WebConfigureAddressSendPFContractTest.md#terza-card-shouldshowconfiguresendiocard).
Questo test apre la pagina dall'indirizzo, così il contenuto è verificato con qualunque utente.

Non va confusa con il passo "Attiva gli avvisi su IO" dei wizard "Il meglio di SEND" e "Attivazione avvisi": il blocco
"Attiva SEND sull'app IO" è lo stesso, ma lì è un passo del wizard e l'indirizzo resta quello del wizard.

## Cosa verifica

L'`assertLoaded()` della pagina verifica solo che sia caricata: che il titolo sia "Tutto, sull'app IO" e che ci siano
i pulsanti "Esci", "Scarica l'app IO" e "Ho già scaricato e installato l'app". Questo contract test verifica:

- i **testi** della pagina e della sezione dell'app IO;
- i **pulsanti** e dove porta "Esci".

Nessuno scenario preme i pulsanti della sezione dell'app IO.

La pagina contiene anche i pulsanti "Indietro" e "Conferma" del wizard, ma sono nascosti perché il wizard ha un solo
passo: non sono mappati né verificati.

## Scenari

### Testi della pagina (`shouldShowOnboardingIoTexts`)

| Scenario | Cosa verifica |
|---|---|
| titolo della pagina | titolo "Tutto, sull'app IO" |
| sezione dell'app IO | titolo e descrizione della sezione |

### Pulsanti (`shouldShowOnboardingIoButtons`)

| Scenario | Cosa verifica |
|---|---|
| pulsante per uscire | testo del pulsante "Esci" |
| pulsanti della sezione dell'app IO | testo di "Scarica l'app IO" e "Ho già scaricato e installato l'app" |
| esci riporta alla pagina configura SEND | il clic su "Esci" apre la pagina "Configura SEND" (verificata con il suo `assertLoaded()`) |
