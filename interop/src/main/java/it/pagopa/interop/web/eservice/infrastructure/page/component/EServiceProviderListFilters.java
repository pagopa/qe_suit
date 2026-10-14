package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.DomNode;
import it.pagopa.infrastructure.suit.component.Button;

/** Search controls scoped to the page main landmark; no HTML form or generated input IDs are assumed. */
public interface EServiceProviderListFilters extends Component {
    @XPath(".//input[@name='q']")
    DomNode nameInput();

    @XPath(".//label[@for = ancestor::main//input[@name='q']/@id]")
    Readable<String> nameLabel();

    @XPath(".//input[@role='combobox']")
    DomNode consumerInput();

    @XPath(".//label[@for = ancestor::main//input[@role='combobox']/@id]")
    Readable<String> consumerLabel();

    @XPath(".//input[@name='q']/ancestor::*[.//button][1]//button")
    Button clearNameButton();
}
