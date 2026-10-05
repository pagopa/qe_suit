package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/recapiti/domicilio-digitale/gestione}
 * Pagina "Gestisci il tuo domicilio digitale" del cittadino.
 * Si apre dal pulsante "Gestisci" della card domicilio digitale in "I tuoi recapiti".
 * Mostra il domicilio digitale attivo e le opzioni per modificarlo; la pagina è disponibile solo a un utente con un domicilio digitale attivo.
 * L'assertLoaded verifica solo gli elementi presenti per qualunque domicilio; stato, indirizzo e "Trasferisci su SEND"
 * dipendono dal tipo di domicilio (PEC o SEND) e non vengono verificati.
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
        customizeBySenderButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Personalizza per ente"));
        backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));
    }
}
