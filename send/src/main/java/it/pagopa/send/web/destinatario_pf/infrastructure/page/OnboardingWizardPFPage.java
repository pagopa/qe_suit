package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.AbstractPage;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;

/**
 * Elementi comuni ai wizard di onboarding del cittadino ({@link OnboardingDigitalDomicilePFPage} e {@link OnboardingAlertsPFPage}):
 * pulsante "Esci", titolo, indicatore di avanzamento in alto, pulsanti per spostarsi tra i passi e sezione dell'app IO.
 * {@link #next()} non conferma mai i dati: sull'ultimo passo il pulsante avanti diventa "Conferma" e non viene premuto.
 */
public interface OnboardingWizardPFPage extends AbstractPage {

    String CONFIRM_LABEL = "Conferma";

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"exit-button\"]")
    Button exitButton();

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> wizardTitle();

    @XPath("//*[@data-testid=\"desktopWizardStepper\"]/*[@role=\"listitem\"]")
    Readable<String> progressItems();

    @XPath("//*[@data-testid=\"desktopWizardStepper\"]/*[@aria-current=\"step\"]")
    Readable<String> currentProgressItem();

    @XPath("//*[@data-testid=\"prev-button\"]")
    Button backButton();

    @XPath("//*[@data-testid=\"next-button\"]")
    Button nextButton();

    @XPath("//*[@data-testid=\"io-step\"]")
    IoSection ioSection();

    interface IoSection extends Component {
        @XPath("//*[@data-testid=\"io-primary-button\"]")
        Button downloadIoAppButton();

        @XPath("//*[@data-testid=\"io-refresh-link\"]")
        Button ioAlreadyInstalledButton();

        // labels

        @XPath("//*[@data-testid=\"io-step\"]//p[1]")
        Readable<String> title();

        @XPath("//*[@data-testid=\"io-step\"]//p[2]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).startsWith("Attiva SEND sull").endsWith("app IO"));
            description().readAndAssert(h -> Assertions.assertThat(h).contains("Riceverai un avviso su IO"));
        }
    }

    /**
     * Indica se il passo corrente è l'ultimo del wizard, quello in cui il pulsante avanti conferma i dati.
     */
    default boolean isLastSection() {
        List<String> items = progressItems().readAll();
        return !items.isEmpty() && items.get(items.size() - 1).equals(currentProgressItem().read());
    }

    /**
     * Indica se si può passare al passo successivo senza confermare nulla.
     */
    default boolean canGoNext() {
        return !isLastSection()
                && nextButton().get(FindPolicy.PRESENT).isPresent()
                && !CONFIRM_LABEL.equals(nextButton().read());
    }

    /**
     * Passa al passo successivo e attende che l'indicatore di avanzamento si aggiorni; se non si può proseguire non fa nulla.
     */
    default void next() {
        if (!canGoNext()) {
            return;
        }
        String current = currentProgressItem().read();
        nextButton().click();
        for (int i = 0; i < 20 && current.equals(currentProgressItem().read()); i++) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Avanza fino all'ultimo passo del wizard senza confermare; si ferma prima se un passo non permette di proseguire.
     */
    default void goToLastSection() {
        int sections = progressItems().readAll().size();
        for (int i = 0; i < sections && canGoNext(); i++) {
            next();
        }
    }
}
