package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta il wizard di onboarding "Il meglio di SEND" per il cittadino.
 * La pagina richiede un utente che abbia già una PEC configurata: in questo caso il wizard si apre sul passo di conferma della PEC.
 */
@Url("${url.notifiche.cittadino.onboarding-domicilio-digitale}")
public interface OnboardingDigitalDomicilePFPage extends Page {

    @XPath("//*[@data-testid=\"exit-button\"]")
    Button exitButton();

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> wizardTitle();

    @XPath("//*[@id=\"default_email-typography\"]")
    Readable<String> courtesyEmail();

    @XPath("//*[@id=\"modifyContact-default_email\"]")
    Button modifyCourtesyEmailButton();

    @XPath("//*[@data-testid=\"prev-button\"]")
    Button backButton();

    @XPath("//*[@data-testid=\"next-button\"]")
    Button continueButton();

    // labels

    @XPath("//*[@data-testid=\"pec-step\"]/p[1]")
    Readable<String> pecStepTitle();

    @XPath("//*[@data-testid=\"pec-step\"]/p[2]")
    Readable<String> pecStepDescription();

    @XPath("//*[@data-testid=\"pec-step\"]//p[normalize-space()=\"Indirizzo PEC\"]")
    Readable<String> pecLabel();

    @XPath("//*[@data-testid=\"pec-step\"]//p[normalize-space()=\"Indirizzo PEC\"]/following-sibling::p[1]")
    Readable<String> pecValue();

    @XPath("//*[@data-testid=\"default_emailContact\"]/preceding-sibling::p[1]")
    Readable<String> courtesyEmailLabel();

    @Override
    default void assertLoaded() {
        exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Esci"));
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il meglio di SEND"));
        courtesyEmail().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        modifyCourtesyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Modifica"));
        backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));
        continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Continua"));

        // labels
        pecStepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("La tua PEC come domicilio digitale SEND"));
        pecStepDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith("Riceverai le comunicazioni a valore legale sulla tua PEC"));
        pecLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indirizzo PEC"));
        pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        courtesyEmailLabel().readAndAssert(h -> Assertions.assertThat(h).startsWith("Alla ricezione di una notifica SEND, riceverai anche un avviso"));
    }
}
