package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/onboarding/io}
 * Pagina di onboarding "Tutto, sull'app IO" del cittadino.
 * Si apre dalla card "Preferisco attivare solo SEND sull'app IO" della pagina {@code {baseUrl}/onboarding}
 * (card non mostrata a tutti gli utenti).
 * Propone di attivare SEND sull'app IO (campi mappati in questa pagina); il contenuto può cambiare se l'utente ha già attivato IO.
 * L'assertLoaded verifica solo gli elementi sempre presenti.
 */
@Url("${url.notifiche.cittadino.onboarding-domicilio-io}")
public interface OnboardingIoPFPage extends Page {

    @XPath("//*[@data-testid=\"exit-button\"]")
    Button exitButton();

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> wizardTitle();

    @XPath("//*[@data-testid=\"io-primary-button\"]")
    Button downloadIoAppButton();

    @XPath("//*[@data-testid=\"io-refresh-link\"]")
    Button ioAlreadyInstalledButton();

    // labels

    @XPath("//*[@data-testid=\"io-step\"]//p[1]")
    Readable<String> ioSectionTitle();

    @XPath("//*[@data-testid=\"io-step\"]//p[2]")
    Readable<String> ioSectionDescription();

    @Override
    default void assertLoaded() {
        exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Esci"));
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Tutto, sull'app IO"));
    }
}
