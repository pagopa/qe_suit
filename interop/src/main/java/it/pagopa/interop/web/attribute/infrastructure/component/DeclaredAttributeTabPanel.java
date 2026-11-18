package it.pagopa.interop.web.attribute.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;

@XPath(".//*[contains(@class, 'MuiTabPanel')]")
public interface DeclaredAttributeTabPanel extends AttributeTabPanel {
    @XPath(".//*[contains(@class, 'MuiButton') and contains(normalize-space(.), 'Crea nuovo attributo')]")
    Button createNewAttributeButton();
}
