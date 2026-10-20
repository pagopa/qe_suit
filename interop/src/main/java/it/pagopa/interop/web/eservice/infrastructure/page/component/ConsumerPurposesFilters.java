package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;

public interface ConsumerPurposesFilters extends Component {

    @XPath(".//input[@name='q']")
    TextField purposeNameFilter();

    @XPath(".//label[normalize-space()='Cerca per e-service']/following::*[@role='combobox'][1]")
    Button eServiceFilter();

    @XPath(".//label[normalize-space()='Cerca per erogatore']/following::*[@role='combobox'][1]")
    Button providerFilter();

    @XPath(".//label[normalize-space()='Stato della finalità']/following::*[@role='combobox'][1]")
    Button purposeStateFilter();
}