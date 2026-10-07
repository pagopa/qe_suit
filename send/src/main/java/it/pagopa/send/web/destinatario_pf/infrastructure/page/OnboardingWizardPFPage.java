package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
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

    // titolo e pulsante avanti insieme: il titolo c'è sempre, così si sa se c'è il pulsante avanti senza attenderlo
    @XPath("//*[@data-testid=\"wizard-title\"] | //*[@data-testid=\"next-button\"]")
    Button titleAndNextButton();

    @XPath("//*[@data-testid=\"io-step\"]")
    IoSection ioSection();

    interface IoSection extends Component {
        @XPath("//*[@data-testid=\"io-primary-button\"]")
        Button downloadIoAppButton();

        @XPath("//*[@data-testid=\"io-refresh-link\"]")
        Button ioAlreadyInstalledButton();

        // labels

        @XPath("(//*[@data-testid=\"io-step\"]//p)[1]")
        Readable<String> title();

        @XPath("(//*[@data-testid=\"io-step\"]//p)[2]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
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
                && hasNextButton()
                && !CONFIRM_LABEL.equals(nextButton().read());
    }

    /**
     * Indica se il passo corrente ha il pulsante avanti; non c'è, per esempio, sui passi che richiedono una scelta.
     */
    default boolean hasNextButton() {
        return titleAndNextButton().getAll().orElse(List.of()).stream()
                .anyMatch(element -> "next-button".equals(element.getAttributes().get("data-testid")));
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
        waitForSectionChange(current);
    }

    /**
     * Torna al passo precedente con "Indietro" e attende che l'indicatore di avanzamento si aggiorni; sul primo passo non
     * fa nulla. Non modifica i dati: le scelte fatte nei passi non sono salvate finché non si preme "Conferma".
     */
    default void back() {
        if (currentSectionIndex() <= 0) {
            return;
        }
        String current = currentProgressItem().read();
        backButton().click();
        waitForSectionChange(current);
    }

    /**
     * Posizione del passo corrente nell'indicatore di avanzamento, a partire da 0.
     */
    default int currentSectionIndex() {
        return progressItems().readAll().indexOf(currentProgressItem().read());
    }

    /**
     * Va al passo il cui nome contiene {@code sectionName}, tornando indietro o andando avanti senza confermare nulla.
     * Restituisce {@code false} se il passo non è raggiungibile, per esempio perché un passo precedente richiede una scelta.
     */
    default boolean goToSection(String sectionName) {
        List<String> items = progressItems().readAll();
        int target = -1;
        for (int i = 0; i < items.size() && target < 0; i++) {
            if (items.get(i).contains(sectionName)) {
                target = i;
            }
        }
        for (int i = 0; i < items.size() && currentSectionIndex() > target; i++) {
            back();
        }
        for (int i = 0; i < items.size() && currentSectionIndex() < target && canGoNext(); i++) {
            next();
        }
        return target >= 0 && currentSectionIndex() == target;
    }

    private void waitForSectionChange(String previous) {
        for (int i = 0; i < 20 && previous.equals(currentProgressItem().read()); i++) {
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
