package it.pagopa.interop.web.attribute.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Link;

public interface CertifiedAttributeTabPanel extends AttributeTabPanel {
    @XPath(".//p")
    Readable<String> description();

    @XPath(".//a[contains(normalize-space(.), 'Scopri di più su quali attributi usare')]")
    Link findOutMoreAboutAttributesToUseLink();
}
