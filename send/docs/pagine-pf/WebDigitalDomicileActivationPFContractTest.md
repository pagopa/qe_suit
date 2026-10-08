# Attiva domicilio digitale su SEND

Wizard `{baseUrl}/recapiti/domicilio-digitale/attivazione`, dalla card domicilio digitale di "I tuoi recapiti".

- Page object: [`DigitalDomicileActivationPFPage`](../../src/main/java/it/pagopa/send/web/destinatario_pf/infrastructure/page/DigitalDomicileActivationPFPage.java)
- Test: [`WebDigitalDomicileActivationPFContractTest`](../../src/test/java/it/pagopa/send/suite/contract/destinatario_pf/WebDigitalDomicileActivationPFContractTest.java)
- Utente: Lucrezia Borgia
- Navigazione: `WebRecipientPfNavigationContractTest#shouldReachDigitalDomicileActivationPF`, vedi
  [WebRecipientPfNavigationContractTest.md](WebRecipientPfNavigationContractTest.md#attiva-domicilio-digitale-su-send)

## La pagina

In rosso quello che controlla `assertLoaded()`: il titolo del wizard, i tre passi, "Continua" e "Annulla".

![Attiva domicilio digitale su SEND](img/DigitalDomicileActivationPFPage.png)

## Cosa verifica il contract test

Il titolo e i nomi dei passi; per ogni passo testi e pulsanti; la finestra che si apre da "consegnata"; i messaggi di
validazione di email e cellulare al passo 2. In rosso gli elementi controllati, esclusi i messaggi e la finestra.

Passo 1, Come funziona (Lucrezia):

![Passo 1: Come funziona](img/WebDigitalDomicileActivationPFContractTest/passo-1.png)

Passo 2, Inserisci la tua email, con email e cellulare (Lucrezia):

![Passo 2: Inserisci la tua email, recapiti presenti](img/WebDigitalDomicileActivationPFContractTest/passo-2.png)

Passo 2 senza recapiti:

![Passo 2: Inserisci la tua email, recapiti da inserire](img/WebDigitalDomicileActivationPFContractTest/passo-2-da-inserire.png)

Passo 3, Riepilogo ("Conferma" non viene premuto):

![Passo 3: Riepilogo](img/WebDigitalDomicileActivationPFContractTest/passo-3.png)

## Varianti

| Passo | Con email e cellulare (Lucrezia) | Senza recapiti |
|---|---|---|
| 1. Come funziona | avviso "La piattaforma SEND sostituirà la PEC come tuo domicilio digitale.", perché Lucrezia ha una PEC | nessun avviso |
| 2. Inserisci la tua email | email e cellulare con Modifica | campo email vuoto con "Aggiungi email" e "Aggiungi numero di cellulare" |
| 3. Riepilogo | si raggiunge con Continua | non si raggiunge: Continua resta sul passo 2 senza messaggi |

Le varianti senza recapiti sono state provate l'08/10/2026 con un utente senza recapiti.

## Note

- Il test non preme mai "Conferma", che attiva il domicilio digitale.
- Nelle validazioni usa solo valori non validi anche togliendo gli spazi (`abc`, `123`): con un valore valido il portale
  invierebbe il codice di verifica. Messaggi attesi: "Indirizzo email non valido" e "Numero di cellulare non valido".
- "Annulla" torna alla pagina precedente nella cronologia del browser, che cambia a seconda di come si arriva qui: del
  pulsante si controlla solo il testo.
- I link del riepilogo portano a `/informativa-privacy` e `/termini-di-servizio/sercq-send`.
