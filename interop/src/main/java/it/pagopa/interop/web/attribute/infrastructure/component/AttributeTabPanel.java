package it.pagopa.interop.web.attribute.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

public interface AttributeTabPanel extends Component {
    @XPath(".//*[contains(@class, 'MuiButton') and contains(text(), 'Crea requisito')]")      
    Button createRequirementButton();
}
