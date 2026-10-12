package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.address.infrastructure.suit.RecipientAddressPage;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Keys;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Scenari comuni ai contract test dei recapiti del cittadino ({@code WebAddressPFContractTest}) e dell'impresa
 * ({@code WebAddressPGContractTest}): i messaggi di validazione dell'email e del cellulare, da modificare o da
 * aggiungere, che sono uguali nei due portali. Ogni test passa le descrizioni delle card, che cambiano tra i portali e
 * servono a riconoscere se il recapito è attivo.
 * <p>
 * Usano solo valori non validi anche togliendo gli spazi, così "Conferma", "Aggiungi email" e "Aggiungi numero" non
 * avviano mai la verifica del recapito.
 */
public final class RecipientAddressScenarios {

    // testi attesi
    public static final String SMS_QUESTION = "Vuoi ricevere gli avvisi anche via SMS?";

    // messaggi di validazione
    public static final String INVALID_EMAIL_MESSAGE = "Indirizzo email non valido";
    public static final String INVALID_SMS_MESSAGE = "Numero di cellulare non valido";
    public static final String SPACES_AT_EDGES_MESSAGE = "Elimina gli spazi all'inizio o alla fine";

    // dati di prova: non validi anche senza spazi, perché un valore valido avvierebbe la verifica del recapito
    public static final String INVALID_ADDRESS = "abc";
    public static final String INVALID_SMS = "123";

    private RecipientAddressScenarios() {
    }

    /**
     * Modifica dell'email e del cellulare attivi: ogni test passa le descrizioni delle due card attive, che cambiano tra i
     * portali e servono a riconoscere che il recapito è attivo.
     */
    public static <P extends RecipientAddressPage> Stream<WebScenario<P>> editContactValidations(String emailDescription, String smsDescription) {
        return Stream.of(
                RecipientAddressScenarios.<P>editScenario("se attiva, modifica email non valida", INVALID_ADDRESS, INVALID_EMAIL_MESSAGE,
                        page -> page.emailContactContent().read().contains(emailDescription),
                        page -> page.emailContact().modifyEmailButton(), page -> page.emailContact().editEmailInput(),
                        page -> page.emailContact().saveEmailButton(), page -> page.emailContact().editEmailErrorMessage()),
                RecipientAddressScenarios.<P>editScenario("se attiva, modifica email vuota", "", INVALID_EMAIL_MESSAGE,
                        page -> page.emailContactContent().read().contains(emailDescription),
                        page -> page.emailContact().modifyEmailButton(), page -> page.emailContact().editEmailInput(),
                        page -> page.emailContact().saveEmailButton(), page -> page.emailContact().editEmailErrorMessage()),
                RecipientAddressScenarios.<P>editScenario("se attivo, modifica cellulare non valido", INVALID_SMS, INVALID_SMS_MESSAGE,
                        page -> page.content().read().contains(smsDescription),
                        page -> page.smsContact().modifySmsButton(), page -> page.smsContact().editSmsInput(),
                        page -> page.smsContact().saveSmsButton(), page -> page.smsContact().editSmsErrorMessage()),
                RecipientAddressScenarios.<P>editScenario("se attivo, modifica cellulare vuoto", "", INVALID_SMS_MESSAGE,
                        page -> page.content().read().contains(smsDescription),
                        page -> page.smsContact().modifySmsButton(), page -> page.smsContact().editSmsInput(),
                        page -> page.smsContact().saveSmsButton(), page -> page.smsContact().editSmsErrorMessage())
        );
    }

    /**
     * Recapito attivo: preme "Modifica", sostituisce il valore, preme "Conferma" e controlla il messaggio. Se il recapito
     * non è attivo lo scenario termina senza verificarlo.
     */
    public static <P extends RecipientAddressPage> WebScenario<P> editScenario(String name, String value, String expectedMessage,
                                                                               Predicate<P> present,
                                                                               Function<P, Button> modify,
                                                                               Function<P, TextField> input,
                                                                               Function<P, Button> save,
                                                                               Function<P, Readable<String>> error) {
        return new WebScenario<>(
                name,
                page -> {},
                page -> {
                    if (!present.test(page)) {
                        // Il recapito non è attivo: "Modifica" non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    modify.apply(page).click();
                    input.apply(page).write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + value);
                    save.apply(page).click();
                    error.apply(page).readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }

    public static <P extends RecipientAddressPage> Stream<WebScenario<P>> addContactValidations(String emailToAddDescription) {
        return Stream.of(
                addEmailScenario("se da aggiungere, email non valida", INVALID_ADDRESS, INVALID_EMAIL_MESSAGE, emailToAddDescription),
                addEmailScenario("se da aggiungere, email vuota", "", INVALID_EMAIL_MESSAGE, emailToAddDescription),
                // nel campo per aggiungere l'email gli spazi non hanno un messaggio dedicato
                addEmailScenario("se da aggiungere, email non valida con spazi all'inizio o alla fine", " " + INVALID_ADDRESS + " ", INVALID_EMAIL_MESSAGE, emailToAddDescription),
                addSmsScenario("se da aggiungere, cellulare non valido", INVALID_SMS, INVALID_SMS_MESSAGE),
                addSmsScenario("se da aggiungere, cellulare vuoto", "", INVALID_SMS_MESSAGE),
                addSmsScenario("se da aggiungere, cellulare con spazi all'inizio o alla fine", " " + INVALID_SMS + " ", SPACES_AT_EDGES_MESSAGE)
        );
    }

    /**
     * Email da aggiungere: scrive il valore, preme "Aggiungi email" e controlla il messaggio. Se l'email è già attiva lo
     * scenario termina senza verificarlo.
     */
    private static <P extends RecipientAddressPage> WebScenario<P> addEmailScenario(String name, String value, String expectedMessage, String emailToAddDescription) {
        return new WebScenario<>(
                name,
                page -> {},
                page -> {
                    if (!page.emailContactContent().read().contains(emailToAddDescription)) {
                        // L'email è già attiva: il campo per aggiungerla non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    RecipientAddressPage.EmailToAdd toAdd = page.emailToAdd();
                    toAdd.emailInput().write(value);
                    toAdd.addEmailButton().click();
                    toAdd.emailErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }

    /**
     * Cellulare da aggiungere: preme "Aggiungi numero di cellulare", scrive il valore, preme "Aggiungi numero" e controlla
     * il messaggio. Se il pulsante per aggiungere il cellulare non c'è lo scenario termina senza verificarlo.
     */
    private static <P extends RecipientAddressPage> WebScenario<P> addSmsScenario(String name, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> {},
                page -> {
                    if (!page.emailContactContent().read().contains(SMS_QUESTION)) {
                        // Il pulsante per aggiungere il cellulare non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    RecipientAddressPage.EmailToAdd toAdd = page.emailToAdd();
                    toAdd.addSmsButton().click();
                    toAdd.smsInput().write(value);
                    toAdd.saveSmsButton().click();
                    toAdd.smsErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }
}
