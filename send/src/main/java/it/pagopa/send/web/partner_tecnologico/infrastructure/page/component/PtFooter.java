package it.pagopa.send.web.partner_tecnologico.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

/**
 * Footer del BackOffice dei Partner Tecnologici: link a informativa privacy, termini e condizioni, service practice
 * statement SERCQ e accessibilità. I link si cercano per testo, non per posizione.
 */
@XPath("//footer")
public interface PtFooter extends Component {

    @XPath(".//a[normalize-space()=\"Informativa Privacy\"]")
    Button privacyPolicyLink();

    @XPath(".//a[normalize-space()=\"Termini e Condizioni\"]")
    Button termsOfServiceLink();

    @XPath(".//a[normalize-space()=\"Service practice statement - SERCQ\"]")
    Button sercqStatementLink();

    @XPath(".//a[normalize-space()=\"Accessibilità\"]")
    Button accessibilityLink();

    @Override
    default void assertLoaded() {}
}
