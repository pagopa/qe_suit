# Configura SEND

Pagina di onboarding `{baseUrl}/onboarding`. Compare al primo accesso finché l'utente non configura SEND o salta la
configurazione; si raggiunge sempre dall'indirizzo e dal pulsante "Esci" delle pagine di onboarding.

- Page object: [`ConfigureAddressSendPage`](../../src/main/java/it/pagopa/send/web/infrastructure/page/ConfigureAddressSendPage.java)
- Test: [`WebConfigureAddressSendPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebConfigureAddressSendPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachOnboardingPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md)

## La pagina

`assertLoaded()` controlla il titolo, i pulsanti delle due card mostrate a tutti e "Salta e vai
alle tue notifiche". Lo usa anche lo step Cucumber "se presente, viene saltata la configurazione del prodotto SEND" per
capire se la pagina è mostrata.

## Cosa verifica il contract test

I testi della pagina e delle due card mostrate a tutti, i pulsanti e la pagina che apre ogni card.

| Card | Pulsante | Apre |
|---|---|---|
| Scelgo il meglio di SEND | Attiva il meglio di SEND | wizard "Il meglio di SEND" (`/onboarding/domicilio-digitale`) |
| Voglio solo gli avvisi | Attiva solo gli avvisi | wizard "Attivazione avvisi" (`/onboarding/avvisi`) |

## Non verificato

La terza card, "Preferisco attivare solo SEND sull'app IO" con "Attiva SEND su IO", compare solo a chi non ha recapiti
di cortesia (email, SMS o app IO) e non viene verificata.

## Note

- Il test non preme "Salta e vai alle tue notifiche", che segna la configurazione come fatta, e non conferma le pagine
  aperte dalle card.
