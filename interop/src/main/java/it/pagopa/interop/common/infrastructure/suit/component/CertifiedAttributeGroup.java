package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

import java.util.List;

@XPath(".//*[contains(@class, 'MuiCard-root')")
public interface CertifiedAttributeGroup extends Component, Clickable {

    @XPath(".//following::span[contains(@class, 'MuiCardHeader-title')]")
    Readable<String> title();

    @XPath(".//following::p")
    Readable<String> description();

    Button createAttributeButton();

    Button deleteAttributeButton();

    List<CertifiedAttribute> certifiedAttributes();
}
