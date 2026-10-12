package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.send.web.delegation.infrastructure.suit.NewDelegationFormPage;

/**
 * {@code {baseUrl}/deleghe/delegati/nuova}
 * Pagina "Aggiungi una delega" dell'impresa.
 * Si apre da "Aggiungi una delega" nella scheda "Delegati dall'impresa" della pagina "Deleghe".
 * Il form è uguale a quello del cittadino ({@link NewDelegationFormPage}); qui cambiano gli id dei radio del soggetto
 * giuridico e il sottotitolo, che nomina l'impresa. Testi, stato iniziale e validazioni del form sono verificati da
 * {@code WebNewDelegationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.add-delegati}")
public interface NewDelegationPage extends NewDelegationFormPage {

    @Override
    @XPath("//*[@id=\"select-pf\"]")
    RadioButton naturalPersonRadio();

    @Override
    @XPath("//*[@id=\"select-pg\"]")
    RadioButton legalPersonRadio();
}
