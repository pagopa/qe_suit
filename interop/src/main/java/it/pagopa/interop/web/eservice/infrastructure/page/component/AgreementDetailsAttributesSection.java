package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface AgreementDetailsAttributesSection extends Component {

    @XPath(".//h2")
    Label title();

    @XPath(".//p")
    Label description();

    @XPath(".//span[contains(@class,'MuiCardHeader-title')]")
    Label infoCard();
}