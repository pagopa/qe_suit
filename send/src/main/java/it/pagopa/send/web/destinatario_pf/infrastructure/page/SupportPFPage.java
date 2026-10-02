package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta il form di richiesta assistenza del cittadino, accessibile dal link Assistenza in alto nella pagina.
 * La pagina permette di indicare l'email su cui ricevere le risposte dell'assistenza.
 */
@Url("${url.notifiche.cittadino.assistenza}")
public interface SupportPFPage extends Page {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

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

    @XPath("//*[@id=\"confirmMail-label\"]")
    Readable<String> confirmMailLabel();

    @XPath("//*[@id=\"confirmMail\"]")
    TextField confirmMailInput();

    @XPath("//*[@data-testid=\"continueButton\"]")
    Button continueButton();

    @XPath("//*[@data-testid=\"backButton\"]")
    Button backButton();

    // labels

    @XPath("//main//a[contains(@href,\"privacy-policy-assistenza\")]")
    Readable<String> privacyPolicyLink();

    @XPath("//main//a[contains(@href,\"privacy-policy-assistenza\")]/parent::p")
    Readable<String> privacyPolicyText();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Come possiamo aiutarti?"));
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Come possiamo aiutarti?"));
        subtitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("Indica l").endsWith("ricevere le risposte dell’assistenza."));
        pecDisclaimer().readAndAssert(h -> Assertions.assertThat(h).startsWith("Inserisci un indirizzo di posta elettronica ordinaria"));
        mailLabel().readAndAssert(h -> Assertions.assertThat(h).contains("(no PEC)"));
        Assertions.assertThat(mailInput().get(FindPolicy.PRESENT)).isPresent();
        confirmMailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Conferma l'indirizzo email"));
        Assertions.assertThat(confirmMailInput().get(FindPolicy.PRESENT)).isPresent();
        Assertions.assertThat(continueButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue("Avanti");
        Assertions.assertThat(continueButton().isDisabled()).isTrue();
        backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));

        // labels
        privacyPolicyLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Privacy Policy Assistenza"));
        privacyPolicyText().readAndAssert(h -> Assertions.assertThat(h).startsWith("Proseguendo dichiari di aver letto"));
    }
}
