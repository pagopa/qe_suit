package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.pagopa.send.web.address.infrastructure.suit.RecipientAddressPage;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/recapiti}
 * Pagina "Recapiti" dell'impresa.
 * Si apre dalla voce "Recapiti" del menu laterale.
 * Contiene le card del domicilio digitale e dell'email aziendale, più quella del cellulare se l'impresa ne ha uno; le
 * card sono uguali a quelle del cittadino ({@link RecipientAddressPage}), con testi propri dell'impresa e senza la card
 * dell'app IO.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e le card del domicilio digitale e dell'email;
 * testi e validazioni sono verificati da {@code WebAddressPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.recapiti}")
public interface AddressPGPage extends AddressPage, RecipientAddressPage {

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Recapiti"));
        legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
