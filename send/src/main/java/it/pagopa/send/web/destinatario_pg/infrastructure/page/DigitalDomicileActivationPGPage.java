package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.address.infrastructure.suit.DigitalDomicileActivationPage;

/**
 * {@code {baseUrl}/recapiti/domicilio-digitale/attivazione}
 * Pagina del wizard "Attiva domicilio digitale su SEND" dell'impresa.
 * Si apre con "Inizia" dalla card "Il domicilio digitale della tua impresa" di "Recapiti".
 * Il wizard ha la stessa struttura di quello del cittadino ({@link DigitalDomicileActivationPage}); solo l'impresa vede
 * al primo passo la sezione per usare una PEC aziendale al posto di SEND.
 * Testi e contenuto dei passi sono verificati da {@code WebDigitalDomicileActivationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.recapiti-domicilio-digitale-attivazione}")
public interface DigitalDomicileActivationPGPage extends DigitalDomicileActivationPage {

    // sezione "Preferisci usare la PEC aziendale?" del primo passo

    @XPath("//*[@data-testid=\"pec-section\"]/p[1]")
    Readable<String> pecSectionTitle();

    @XPath("//*[@data-testid=\"pec-section\"]/p[2]")
    Readable<String> pecSectionDescription();

    @XPath("//*[@data-testid=\"pec-section\"]/button")
    Button insertPecButton();
}
