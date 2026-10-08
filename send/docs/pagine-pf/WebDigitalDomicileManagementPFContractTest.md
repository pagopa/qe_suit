# Gestisci il tuo domicilio digitale

Pagina `{baseUrl}/recapiti/domicilio-digitale/gestione`, dal pulsante "Gestisci" della card domicilio digitale in
"I tuoi recapiti".

- Page object: [`DigitalDomicileManagementPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DigitalDomicileManagementPFPage.java)
- Test: [`WebDigitalDomicileManagementPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebDigitalDomicileManagementPFContractTest.java)
- Utente: Lucrezia Borgia (domicilio digitale attivo su PEC)
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachDigitalDomicileManagementPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#gestisci-il-tuo-domicilio-digitale)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo, "Personalizza per ente" e "Indietro".

![Gestisci il tuo domicilio digitale](img/DigitalDomicileManagementPFPage.png)

## Cosa verifica il contract test

Il titolo, lo stato e l'indirizzo del domicilio, le opzioni "Trasferisci su SEND" e "Personalizza per ente" con quello
che aprono, e il form "Personalizza per ente" con i suoi messaggi. In rosso gli elementi controllati, esclusi i
messaggi, la tendina "Tipologia" aperta e il wizard di trasferimento.

Le opzioni:

![Gestisci il tuo domicilio digitale, elementi verificati](img/WebDigitalDomicileManagementPFContractTest/pagina.png)

Il form "Personalizza per ente" con la tipologia "Indirizzo PEC":

![Personalizza per ente](img/WebDigitalDomicileManagementPFContractTest/personalizza-per-ente.png)

## Validazioni

| Caso | Messaggi |
|---|---|
| form vuoto | "Campo obbligatorio" su ente mittente e tipologia |
| tipologia PEC con PEC `abc` o vuota | "Indirizzo PEC non valido" sulla PEC, "Campo obbligatorio" su ente e accettazione |

## Note

- La pagina c'è solo per chi ha un domicilio digitale attivo. "Trasferisci su SEND" compare solo con il domicilio su
  PEC; nessun utente di test ha il domicilio su SEND.
- "Trasferisci su SEND" apre nella stessa pagina il wizard di attivazione: il test ne controlla titolo e passi ma non lo
  conferma.
- Nel form il test lascia sempre vuoto "Ente mittente", quindi "Conferma" mostra solo i messaggi e non salva nulla.
- "Indietro" torna alla pagina precedente nella cronologia del browser: del pulsante si controlla solo il testo.
