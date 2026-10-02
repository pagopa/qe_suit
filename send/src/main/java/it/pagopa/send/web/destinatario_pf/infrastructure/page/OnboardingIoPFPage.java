package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta il passo di onboarding "Tutto, sull'app IO" per il cittadino,
 * in cui attivare SEND sull'app IO.
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
    Readable<String> ioStepTitle();

    @XPath("//*[@data-testid=\"io-step\"]//p[2]")
    Readable<String> ioStepDescription();

    @Override
    default void assertLoaded() {
        exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Esci"));
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Tutto, sull'app IO"));
        downloadIoAppButton().readAndAssert(h -> Assertions.assertThat(h).startsWith("Scarica l").endsWith("app IO"));
        ioAlreadyInstalledButton().readAndAssert(h -> Assertions.assertThat(h).contains("scaricato e installato"));

        // labels
        ioStepTitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("Attiva SEND sull").endsWith("app IO"));
        ioStepDescription().readAndAssert(h -> Assertions.assertThat(h).contains("Riceverai un avviso su IO"));
    }
}
