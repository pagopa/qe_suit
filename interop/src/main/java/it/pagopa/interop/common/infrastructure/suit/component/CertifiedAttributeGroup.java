package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

import java.util.List;

@XPath("./*[contains(@class, 'MuiCard-root')]")
public interface CertifiedAttributeGroup extends Component, Clickable {

    @XPath(".//*[contains(@class, 'MuiCardHeader-title')]")
    Readable<String> title();

    @XPath(".//following::p")
    Readable<String> description();

    @XPath(".//button[contains(@class, 'MuiButton-root') and contains(normalize-space(text()), 'Aggiungi un altro attributo')]")
    Button createAttributeButton();

    @XPath(".//button[contains(@class, 'MuiIconButton-root') and contains(@aria-label, 'Rimuovi gruppo di attributi')]")
    Button deleteAttributeButton();

    List<CertifiedAttribute> certifiedAttributes();
}
