package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.infrastructure.page.ConfigureAddressSendPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/onboarding/io}
 * Pagina di onboarding "Tutto, sull'app IO" del cittadino.
 * Si apre solo dalla terza card "Preferisco attivare solo SEND sull'app IO" della pagina "Configura SEND"
 * ({@code {baseUrl}/onboarding}), che il portale mostra solo agli utenti senza recapiti di cortesia (email, SMS o app IO).
 * Contiene lo stesso blocco "Attiva SEND sull'app IO" del passo IO dei wizard di onboarding. La pagina contiene anche i
 * pulsanti "Indietro" e "Conferma" del wizard, ma sono nascosti perché il wizard ha un solo passo, e non sono mappati.
 * L'assertLoaded verifica che la pagina sia caricata, cioè che il titolo sia "Tutto, sull'app IO" e che ci siano i
 * pulsanti; testi e comportamento di "Esci" sono verificati da {@code WebOnboardingIoPFContractTest}.
 */
@Url("${url.notifiche.cittadino.onboarding-domicilio-io}")
public interface OnboardingIoPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"exit-button\"]")
    Button exitButton();

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> wizardTitle();

    @XPath("//*[@data-testid=\"io-primary-button\"]")
    Button downloadIoAppButton();

    @XPath("//*[@data-testid=\"io-refresh-link\"]")
    Button ioAlreadyInstalledButton();

    // link a pagina "Configura SEND", raggiunta con "Esci"
    @XPath("//main")
    ConfigureAddressSendPage configureSend();

    // labels

    @XPath("(//*[@data-testid=\"io-step\"]//p)[1]")
    Readable<String> ioSectionTitle();

    @XPath("(//*[@data-testid=\"io-step\"]//p)[2]")
    Readable<String> ioSectionDescription();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Tutto, sull'app IO"));
        exitButton().assertLoaded();
        downloadIoAppButton().assertLoaded();
        ioAlreadyInstalledButton().assertLoaded();
    }
}
