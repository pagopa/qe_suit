package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/assistenza}
 * Pagina "Come possiamo aiutarti?" del cittadino.
 * Si apre dal link "Assistenza" (icona con il punto interrogativo) in alto nella pagina.
 * Contiene il form per indicare l'email su cui ricevere le risposte dell'assistenza; "Avanti" resta disabilitato finché le
 * due email non sono valide e uguali.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e la presenza dei campi e dei pulsanti del form;
 * testi, stato iniziale e validazioni del form sono verificati da {@code WebSupportPFContractTest}.
 */
@Url("${url.notifiche.cittadino.assistenza}")
public interface SupportPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"pecDisclaimer\"]")
    Readable<String> pecDisclaimer();

    @XPath("//*[@id=\"mail-label\"]")
    Readable<String> mailLabel();

    @XPath("//*[@id=\"mail\"]")
    TextField mailInput();

    @XPath("//*[@id=\"mail-helper-text\"]")
    Readable<String> mailHelperText();

    @XPath("//*[@id=\"confirmMail-label\"]")
    Readable<String> confirmMailLabel();

    @XPath("//*[@id=\"confirmMail\"]")
    TextField confirmMailInput();

    @XPath("//*[@id=\"confirmMail-helper-text\"]")
    Readable<String> confirmMailHelperText();

    @XPath("//*[@data-testid=\"continueButton\"]")
    Button continueButton();

    @XPath("//*[@data-testid=\"backButton\"]")
    Button backButton();

    // labels

    @XPath("//main//a[contains(@href,\"privacy-policy-assistenza\")]")
    Button privacyPolicyLink();

    @XPath("//main//a[contains(@href,\"privacy-policy-assistenza\")]/parent::p")
    Readable<String> privacyPolicyText();

    default String getMailErrorMessage() {
        return mailHelperText().read();
    }

    default String getConfirmMailErrorMessage() {
        return confirmMailHelperText().read();
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Come possiamo aiutarti?"));
        Assertions.assertThat(mailInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(confirmMailInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(continueButton().get(FindPolicy.PRESENT)).isPresent();
        backButton().assertLoaded();
    }
}
