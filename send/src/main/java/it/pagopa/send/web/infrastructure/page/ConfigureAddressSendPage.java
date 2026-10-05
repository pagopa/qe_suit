package it.pagopa.send.web.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code {baseUrl}/onboarding}
 * Pagina "Configura SEND" del cittadino (onboarding).
 * Mostrata al primo accesso al portale.
 * Contiene le card per scegliere come ricevere le notifiche e il pulsante per saltare la configurazione.
 * L'assertLoaded verifica solo gli elementi presenti per qualunque utente: la card "Preferisco attivare solo SEND sull'app IO"
 * non è mostrata a tutti e non viene verificata.
 */
@Url("${url.notifiche.cittadino.base}/onboarding")
public interface ConfigureAddressSendPage extends Page {

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

    // labels

    @XPath("//main//h2")
    Readable<String> cardTitles();

    @XPath("//main//span[normalize-space()=\"Consigliato\"]")
    Readable<String> recommendedLabel();

    @XPath("//*[@data-testid=\"onboarding-card-cta-digital_domicile\"]/preceding-sibling::div[1]//li")
    Readable<String> digitalDomicileBullets();

    @XPath("//*[@data-testid=\"onboarding-card-cta-courtesy\"]/preceding-sibling::div[1]//li")
    Readable<String> courtesyBullets();

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
        subtitle().readAndAssert(h -> assertThat(h).isEqualTo("Ottieni il massimo e risparmi!"));
        digitalDomicileButton().readAndAssert(h -> assertThat(h).isEqualTo("Attiva il meglio di SEND"));
        courtesyButton().readAndAssert(h -> assertThat(h).isEqualTo("Attiva solo gli avvisi"));
        skipConfigButton().readAndAssert(h -> assertThat(h).isEqualTo("Salta e vai alle tue notifiche"));

        // labels
        cardTitles().readAllAndAssert(h -> assertThat(h).contains("Scelgo il meglio di SEND", "Voglio solo gli avvisi"));
        recommendedLabel().readAndAssert(h -> assertThat(h).isEqualTo("Consigliato"));
        digitalDomicileBullets().readAllAndAssert(h -> assertThat(h).hasSize(2).allSatisfy(v -> assertThat(v).isNotBlank()));
        courtesyBullets().readAllAndAssert(h -> assertThat(h).hasSize(2).allSatisfy(v -> assertThat(v).isNotBlank()));
    }
}
