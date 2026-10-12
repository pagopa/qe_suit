package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.send.web.delegation.infrastructure.suit.NewDelegationFormPage;

/**
 * {@code {baseUrl}/deleghe/nuova}
 * Pagina "Aggiungi una delega" del cittadino.
 * Si apre dal pulsante "Aggiungi una delega" della pagina {@code {baseUrl}/deleghe}.
 * Il form è uguale a quello dell'impresa ({@link NewDelegationFormPage}); qui cambiano gli id dei radio del soggetto
 * giuridico. Testi, stato iniziale e validazioni del form sono verificati da {@code WebNewDelegationPFContractTest}.
 */
@Url("${url.notifiche.cittadino.add-deleghe}")
public interface NewDelegationPFPage extends NewDelegationFormPage {

    @Override
    @XPath("//*[@id=\"recipient-pf\"]")
    RadioButton naturalPersonRadio();

    @Override
    @XPath("//*[@id=\"recipent-pg\"]")
    RadioButton legalPersonRadio();
}
