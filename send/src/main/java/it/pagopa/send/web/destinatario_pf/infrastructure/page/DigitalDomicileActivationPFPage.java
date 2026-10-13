package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.send.web.address.infrastructure.suit.DigitalDomicileActivationPage;

/**
 * {@code {baseUrl}/recapiti/domicilio-digitale/attivazione}
 * Pagina del wizard "Attiva domicilio digitale su SEND" del cittadino.
 * Si apre dalla card domicilio digitale di "I tuoi recapiti".
 * Il wizard ha la stessa struttura di quello dell'impresa ({@link DigitalDomicileActivationPage}); solo il cittadino che
 * ha già una PEC vede al primo passo l'avviso che SEND la sostituirà.
 * Testi e contenuto dei passi sono verificati da {@code WebDigitalDomicileActivationPFContractTest}.
 */
@Url("${url.notifiche.cittadino.recapiti-domicilio-digitale-attivazione}")
public interface DigitalDomicileActivationPFPage extends DigitalDomicileActivationPage {

    // avviso mostrato solo a chi ha già una PEC come domicilio digitale
    @XPath("//*[@data-testid=\"default-pec-info\"]")
    Readable<String> pecReplacementInfo();
}
