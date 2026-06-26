package it.pagopa.send.domain.web.pages.destinatario;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.cittadino.onboarding}")
public interface DigitalDomicileOnboardPage extends Page {

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/div/div[3]/div/button")
    Clickable startOnboardingButton();

    @XPath("//*[@id=\"default_email\"]")
    Writable<String> emailInput();

    @XPath("//*[@id=\"default_email-button\"]")
    Clickable emailSubmitButton();

    @XPath("//*[@id=\":rh:\"]|//*[@id=\":re:\"]")
    Writable<String> otpInput();

    @XPath("//*[@id=\"code-confirm-button\"]")
    Clickable otpSubmitButton();

    @XPath("//button[normalize-space()='Continue']|//button[normalize-space()='Continua']")
    Clickable continueToSummaryButton();

    @XPath("//div[@data-testid='emailSmsContactWizard']/p[1]")
    Readable<String> legalMailSmsTitleSection2();

    @XPath("//div[@data-testid='sercqSendContactWizard']//div[@role='alert']//div[contains(@class,'MuiAlert-message')]/div/div")
    Readable<String> summarySubtitle();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/div/div[3]/div/button")
    Clickable activateDigitalDomicileButton();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/div/h4")
    Readable<String> activationConfirmationMessage();

    default void startOnboarding(String email) {
        startOnboardingButton().click();
        emailInput().write(email);
        emailSubmitButton().click();
    }

    default void submitOtp(String otp) {
        otpInput().write(otp);
        otpSubmitButton().click();
    }

    default void continueToSummary() {
        legalMailSmsTitleSection2().readAndAssert((h -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("The email to receive alerts about SEND notifications",
                    "La tua email per ricevere avvisi sulle notifiche SEND",
                    "L’email per ricevere avvisi sulle notifiche SEND");
        }));
        continueToSummaryButton().click();
        summarySubtitle().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Monitor the contact details you have chosen: a SEND digital notification begins to produce legal effects even if you have not viewed it.",
                    "Monitora i recapiti che hai scelto: una notifica digitale SEND inizia a produrre effetti giuridici anche se non l'hai consultata.");
        });
    }

    default void activateDigitalDomicile() {
        activateDigitalDomicileButton().click();
        activationConfirmationMessage().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("You have activated your digital domicile on SEND", "Hai attivato il tuo domicilio digitale su SEND",
                    "You have activated the Digital Domicile on SEND", "Hai attivato il Domicilio Digitale su SEND");
        });

    }

}
