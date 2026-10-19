package it.pagopa.send.web.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingAlertsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingDigitalDomicilePFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingIoPFPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code {baseUrl}/onboarding}
 * Pagina "Configura SEND" del cittadino (onboarding).
 * Mostrata al primo accesso al portale.
 * Contiene le card per scegliere come ricevere le notifiche, che aprono le pagine di onboarding, e il pulsante per
 * saltare la configurazione; "Salta" segna la configurazione come fatta e nei test non va mai premuto.
 * Le card "Scelgo il meglio di SEND" e "Voglio solo gli avvisi" sono mostrate a tutti; la terza card "Preferisco attivare
 * solo SEND sull'app IO" solo agli utenti senza recapiti di cortesia (email, SMS o app IO).
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e la presenza dei pulsanti delle due card mostrate a
 * tutti e di "Salta"; testi e pagine aperte dalle card sono verificati da {@code WebConfigureAddressSendPFContractTest}.
 * Lo step Cucumber "se presente, viene saltata la configurazione del prodotto SEND" usa l'assertLoaded per capire se la
 * pagina è mostrata.
 */
@Url("${url.notifiche.cittadino.base}/onboarding")
public interface ConfigureAddressSendPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div[2]/button")
    Button skipConfigButton();

    @XPath(".//h1")
    Readable<String> header();

    @XPath("//main//h1/following-sibling::p[1]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"onboarding-card-cta-digital_domicile\"]")
    Button digitalDomicileButton();

    @XPath("//*[@data-testid=\"onboarding-card-cta-courtesy\"]")
    Button courtesyButton();

    // terza card: mostrata solo agli utenti senza recapiti di cortesia
    @XPath("//*[@data-testid=\"onboarding-card-cta-io\"]")
    Button ioButton();

    // pulsanti di tutte le card mostrate, per sapere quali card ci sono senza attendere quelle assenti
    @XPath("//*[starts-with(@data-testid,\"onboarding-card-cta-\")]")
    Button cardButtons();

    // link alla pagina del wizard "Il meglio di SEND", aperta da "Attiva il meglio di SEND"
    @XPath("//main")
    OnboardingDigitalDomicilePFPage digitalDomicileWizard();

    // link alla pagina del wizard "Attivazione avvisi", aperta da "Attiva solo gli avvisi"
    @XPath("//main")
    OnboardingAlertsPFPage alertsWizard();

    // link alla pagina "Tutto, sull'app IO", aperta da "Attiva SEND su IO"
    @XPath("//main")
    OnboardingIoPFPage ioPage();

    // labels

    @XPath("//main//h2")
    Readable<String> cardTitles();

    @XPath("//main//span[normalize-space()=\"Consigliato\"]")
    Readable<String> recommendedLabel();

    @XPath("//*[@data-testid=\"onboarding-card-cta-digital_domicile\"]/preceding-sibling::div[1]//li")
    Readable<String> digitalDomicileBullets();

    @XPath("//*[@data-testid=\"onboarding-card-cta-courtesy\"]/preceding-sibling::div[1]//li")
    Readable<String> courtesyBullets();

    @XPath("//*[@data-testid=\"onboarding-card-cta-io\"]/preceding-sibling::div[1]//li")
    Readable<String> ioBullets();

    /**
     * Indica se è mostrata la terza card "Preferisco attivare solo SEND sull'app IO", leggendo i pulsanti delle card
     * presenti invece di attendere quello della card IO.
     */
    default boolean isIoCardShown() {
        return cardButtons().getAll().orElse(List.of()).stream()
                .anyMatch(button -> "onboarding-card-cta-io".equals(button.getAttributes().get("data-testid")));
    }

    interface SkipConfigDialog extends Component {
        @XPath(".//button[1]")
        Clickable cancelSkipButton();

        @XPath(".//button[2]")
        Clickable confirmSkipButton();
    }

    @XPath("/html/body/div[3]/div[3]/div")
    SkipConfigDialog skipConfigDialog();

    default void clickSkipConfigButton() {
        skipConfigButton().click();
        skipConfigDialog().confirmSkipButton().click();
    }

    @Override
    default void assertLoaded() {
        header().readAndAssert((h) -> {
            assertThat(h).isNotNull();
            assertThat(h).isIn("Configure SEND", "Configura SEND");
        });
        digitalDomicileButton().assertLoaded();
        courtesyButton().assertLoaded();
        skipConfigButton().assertLoaded();
    }
}
