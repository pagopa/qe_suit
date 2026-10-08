# Configura SEND

Pagina di onboarding `{baseUrl}/onboarding`. Compare al primo accesso finché l'utente non configura SEND o salta la
configurazione; si raggiunge sempre dall'indirizzo e dal pulsante "Esci" delle pagine di onboarding.

- Page object: [`ConfigureAddressSendPage`](../../src/main/java/it/pagopa/send/web/infrastructure/page/ConfigureAddressSendPage.java)
- Test: [`WebConfigureAddressSendPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebConfigureAddressSendPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachOnboardingPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#configura-send-onboarding)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo, i pulsanti delle due card mostrate a tutti e "Salta e vai
alle tue notifiche". Lo usa anche lo step Cucumber "se presente, viene saltata la configurazione del prodotto SEND" per
capire se la pagina è mostrata.

![Configura SEND](img/ConfigureAddressSendPage.png)

## Cosa verifica il contract test

I testi della pagina e delle card, i pulsanti e la pagina che apre ogni card. In rosso gli elementi controllati.

![Configura SEND, elementi verificati](img/WebConfigureAddressSendPFContractTest/pagina.png)

## Varianti

| Card | Pulsante | Apre | Chi la vede |
|---|---|---|---|
| Scelgo il meglio di SEND | Attiva il meglio di SEND | wizard "Il meglio di SEND" (`/onboarding/domicilio-digitale`) | tutti |
| Voglio solo gli avvisi | Attiva solo gli avvisi | wizard "Attivazione avvisi" (`/onboarding/avvisi`) | tutti |
| Preferisco attivare solo SEND sull'app IO | Attiva SEND su IO | "Tutto, sull'app IO" (`/onboarding/io`) | solo chi non ha recapiti di cortesia (email, SMS o app IO) |

Lucrezia ha email e SMS, quindi non vede la terza card. La terza card è stata provata il 07/10/2026 con un utente senza
recapiti di cortesia:

![Configura SEND con la terza card](img/WebConfigureAddressSendPFContractTest/card-io.png)

## Note

- Il test non preme "Salta e vai alle tue notifiche", che segna la configurazione come fatta, e non conferma le pagine
  aperte dalle card.
- La terza card è l'unico modo per arrivare a "Tutto, sull'app IO" dal portale.
