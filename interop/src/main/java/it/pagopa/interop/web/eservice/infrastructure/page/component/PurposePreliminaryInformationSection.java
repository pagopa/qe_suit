package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;

public interface PurposePreliminaryInformationSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//label[starts-with(normalize-space(),'Fruitore')]")
    Label consumerLabel();

    @XPath(".//label[starts-with(normalize-space(),'Fruitore')]/following::input[@role='combobox'][1]")
    TextField consumerInput();

    @XPath(".//*[@id='consumerId-infoLabel']")
    Label consumerHelpText();

    @XPath(".//label[starts-with(normalize-space(),'E-service da associare')]")
    Label eServiceLabel();

    @XPath(".//label[starts-with(normalize-space(),'E-service da associare')]/following::input[@role='combobox'][1]")
    TextField eServiceInput();

    @XPath(".//*[@id='eservice-infoLabel']")
    Label eServiceHelpText();
}
