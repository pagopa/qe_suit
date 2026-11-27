package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface ProducerPurposeDetailsTechnicalInfoSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()='In questa sezione potrai verificare le specifiche della finalità.']")
    Label sectionDescription();

    @XPath(".//p[normalize-space()='ID finalità']")
    Label purposeIdLabel();

    @XPath(".//div[.//p[normalize-space()='ID finalità'] and .//span and not(.//div[.//p[normalize-space()='ID finalità'] and .//span])]//span")
    Label purposeIdValue();

    @XPath(".//div[.//p[normalize-space()='ID finalità'] and .//button and not(.//div[.//p[normalize-space()='ID finalità'] and .//button])]//button")
    Button copyPurposeIdButton();

    @XPath(".//p[normalize-space()='ID fruitore']")
    Label consumerIdLabel();

    @XPath(".//div[.//p[normalize-space()='ID fruitore'] and .//span and not(.//div[.//p[normalize-space()='ID fruitore'] and .//span])]//span")
    Label consumerIdValue();

    @XPath(".//div[.//p[normalize-space()='ID fruitore'] and .//button and not(.//div[.//p[normalize-space()='ID fruitore'] and .//button])]//button")
    Button copyConsumerIdButton();
}

