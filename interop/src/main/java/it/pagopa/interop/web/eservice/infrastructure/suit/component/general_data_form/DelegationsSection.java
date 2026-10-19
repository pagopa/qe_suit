package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Deleghe']]")
public interface DelegationsSection extends Component {
    @XPath(".//h2")
    it.frontend.e2e.framework.web.capability.core.Readable<String> title();

    @XPath(".//p")
    Readable<String> description();
}
