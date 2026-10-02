package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Questa pagina rappresenta la gestione del domicilio digitale del cittadino, accessibile da "Gestisci" nella sezione I tuoi recapiti.
 * La pagina richiede un utente con una PEC attiva come domicilio digitale.
 */
@Url("${url.notifiche.cittadino.recapiti-domicilio-digitale-gestione}")
public interface DigitalDomicileManagementPFPage extends Page {

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> title();

    @XPath("//*[@data-testid=\"legalContactsTitle\"]")
    Readable<String> optionsTitle();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button[normalize-space()=\"Trasferisci su SEND\"]")
    Button transferToSendButton();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button[normalize-space()=\"Personalizza per ente\"]")
    Button customizeBySenderButton();

    @XPath("//main//button[normalize-space()=\"Indietro\"]")
    Button backButton();

    // labels

    @XPath("//*[@data-testid=\"legalContactManager\"]/div[1]//span")
    Readable<String> status();

    @XPath("//*[@data-testid=\"legalContactManager\"]/div[1]/p")
    Readable<String> pecValue();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button/preceding-sibling::p[2]")
    Readable<String> optionTitles();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button/preceding-sibling::p[1]")
    Readable<String> optionDescriptions();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Gestisci il tuo domicilio digitale"));
        optionsTitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("Scegli un").endsWith("opzione"));
        transferToSendButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Trasferisci su SEND"));
        customizeBySenderButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Personalizza per ente"));
        backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));

        // labels
        status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attivo"));
        pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        optionTitles().readAllAndAssert(List.of("Trasferisci il domicilio digitale sulla piattaforma SEND", "Personalizza il tuo domicilio digitale per ente mittente"));
        optionDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(2).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
    }
}
