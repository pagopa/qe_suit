package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Informazioni sull’e-service']]")
public interface AboutEServiceSection extends Component {
    @XPath(".//h2")
    it.frontend.e2e.framework.web.capability.core.Readable<String> title();

    @XPath(".//p")
    Readable<String> description();

    @XPath(".//*[@id='name']")
    TextField eServiceNameInput();

    @XPath(".//*[@id='description']")
    TextField eServiceDescriptionInput();
}
