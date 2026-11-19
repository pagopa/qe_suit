package it.pagopa.interop.web.eservice_template.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TextField;

public interface ProducerEServiceTemplateTenantsTab extends Component {

    @XPath(".//*[@role='alert']")
    Alert emptyStateAlert();

    @XPath(".//label[normalize-space()='Cerca per erogatore']/following::input[1]")
    TextField searchProducerInput();

    @XPath(".//label[normalize-space()='Stato istanza']")
    Label instanceStateFilterLabel();

    @XPath(".//table")
    Table instancesTable();
}

