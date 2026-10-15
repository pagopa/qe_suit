# Attiva domicilio digitale su SEND

Wizard `{baseUrl}/recapiti/domicilio-digitale/attivazione`, dalla card domicilio digitale di "I tuoi recapiti".

- Page object: [`DigitalDomicileActivationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DigitalDomicileActivationPFPage.java)
- Test: [`WebDigitalDomicileActivationPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebDigitalDomicileActivationPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachDigitalDomicileActivationPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md)

## La pagina

`assertLoaded()` controlla il titolo del wizard, i tre passi, "Continua" e "Annulla".

## Cosa verifica il contract test

Solo quello che c'è per tutti gli utenti, nei primi due passi del wizard.

| Parte | Cosa si controlla |
|---|---|
| Wizard | titolo e nomi dei passi "Come funziona", "Inserisci la tua email" e "Riepilogo" |
| 1. Come funziona | titolo, i tre punti con titolo e descrizione, il link "consegnata", "Continua" e "Annulla" |
| Finestra di "consegnata" | titolo, descrizione e "Ok, ho capito" |
| 2. Inserisci la tua email | titolo, descrizione, "Continua" e "Indietro" |

## Non verificato

Dipendono dai recapiti dell'utente e non vengono verificati:

- al passo 1, l'avviso "La piattaforma SEND sostituirà la PEC come tuo domicilio digitale.", che c'è solo per chi ha una
  PEC;
- al passo 2, i recapiti già presenti con "Modifica" oppure i campi per aggiungerli, con i loro messaggi di validazione;
- il passo 3, Riepilogo, che si raggiunge solo se l'utente ha un'email di cortesia.

## Note

- Il test non preme mai "Conferma", che attiva il domicilio digitale.
- "Annulla" torna alla pagina precedente nella cronologia del browser, che cambia a seconda di come si arriva qui: del
  pulsante si controlla solo il testo.
