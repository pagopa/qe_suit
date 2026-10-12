package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.address.infrastructure.suit.RecipientAddressPage;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/recapiti}
 * Pagina "I tuoi recapiti" del cittadino.
 * Si apre dalla voce "I tuoi recapiti" del menu laterale.
 * Contiene le card del domicilio digitale, di SEND sull'app IO e dell'email, più quella del cellulare se l'utente ne ha
 * uno. Le card del domicilio, dell'email e del cellulare sono uguali a quelle dell'impresa ({@link RecipientAddressPage});
 * qui c'è in più la card dell'app IO.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e le card del domicilio digitale, dell'app IO e
 * dell'email; testi, recapiti e validazioni sono verificati da {@code WebAddressPFContractTest}.
 * L'assertLoaded è usato anche dagli step Cucumber dei recapiti.
 */
@Url("${url.notifiche.cittadino.recapiti}")
public interface AddressPFPage extends AddressPage, RecipientAddressPage {

    // card di SEND sull'app IO

    @XPath("//*[@data-testid=\"ioContactTitle\"]")
    Readable<String> ioContactTitle();

    @XPath("//*[@data-testid=\"ioContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
    Readable<String> ioStatus();

    @XPath("//*[@data-testid=\"ioContactDescription\"]")
    Readable<String> ioContactDescription();

    @XPath("//*[@id=\"ioContactSection\"]//button")
    Button downloadIoAppButton();

    @XPath("//main")
    DigitalDomicileManagementPFPage digitalDomicileManagement();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi recapiti"));
        legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        ioContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
