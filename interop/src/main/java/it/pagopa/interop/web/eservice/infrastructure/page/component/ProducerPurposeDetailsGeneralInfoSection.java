package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

public interface ProducerPurposeDetailsGeneralInfoSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()='E-service associato']")
    Label eServiceLabel();

    @XPath(".//a[contains(@href,'/erogazione/e-service/')]")
    Link eServiceLink();

    @XPath(".//p[normalize-space()='Fruitore']")
    Label consumerLabel();

    @XPath(".//div[.//p[normalize-space()='Fruitore'] and .//span and not(.//div[.//p[normalize-space()='Fruitore'] and .//span])]//span")
    Label consumerValue();

    @XPath(".//p[normalize-space()='Descrizione della finalità']")
    Label purposeDescriptionLabel();

    @XPath(".//div[.//p[normalize-space()='Descrizione della finalità'] and .//span and not(.//div[.//p[normalize-space()='Descrizione della finalità'] and .//span])]//span")
    Label purposeDescriptionValue();

    @XPath(".//a[normalize-space()='Vai a richiesta di fruizione']")
    Link goToAgreementLink();
}

