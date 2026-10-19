package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;

/** Search controls scoped to the page main landmark; no HTML form or generated input IDs are assumed. */
public interface EServiceProviderListFilters extends Component {
    @XPath(".//input[@name='q']")
    TextField nameInput();

    @XPath(".//label[@for = ancestor::main//input[@name='q']/@id]")
    Readable<String> nameLabel();

    @XPath(".//input[@role='combobox']")
    TextField consumerInput();

    @XPath(".//label[@for = ancestor::main//input[@role='combobox']/@id]")
    Readable<String> consumerLabel();

    @XPath(".//input[@name='q']/ancestor::*[.//button][1]//button")
    Button clearNameButton();
}
