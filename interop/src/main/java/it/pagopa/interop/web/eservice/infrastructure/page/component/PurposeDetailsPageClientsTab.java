package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Table;

public interface PurposeDetailsPageClientsTab extends Component {

    @XPath(".//button[normalize-space()='Aggiungi']")
    Button addButton();

    @XPath(".//table")
    Table clientsTable();
}
