package it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Tabs;
import it.pagopa.interop.web.attribute.infrastructure.component.CertifiedAttributeTabPanel;
import it.pagopa.interop.web.attribute.infrastructure.component.DeclaredAttributeTabPanel;
import it.pagopa.interop.web.attribute.infrastructure.component.VerifiedAttributeTabPanel;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Requisiti di accesso']]")
public interface RequiredAttributeSection extends Component {
    @XPath(".//h2")
    it.frontend.e2e.framework.web.capability.core.Readable<String> title();

    @XPath(".//p")
    Readable<String> description();

    @XPath(".//a[contains(normalize-space(.), 'Scopri di più sugli attributi')]")
    Link attributeLink();

    Tabs attributesTabs();

    CertifiedAttributeTabPanel certifiedAttributeTabPanel();

    VerifiedAttributeTabPanel verifiedAttributeTabPanel();

    DeclaredAttributeTabPanel declaredAttributeTabPanel();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Requisiti di accesso");
    }

    default CertifiedAttributeTabPanel selectCertifiedAttributeTab() {
        openTabByLabel("Attributi certificati");
        return certifiedAttributeTabPanel();
    }

    default VerifiedAttributeTabPanel selectVerifiedAttributeTab() {
        openTabByLabel("Attributi verificati");
        return verifiedAttributeTabPanel();
    }

    default DeclaredAttributeTabPanel selectDeclaredAttributeTab() {
        openTabByLabel("Attributi dichiarati");
        return declaredAttributeTabPanel();
    }

    private void openTabByLabel(String label) {
        attributesTabs().tabs().stream()
                .filter(tab -> tab.getLabel().equals(label))
                .findFirst()
                .ifPresentOrElse(Tabs.Tab::click, () -> {
                    throw new IllegalArgumentException("Tab non trovato: " + label);
                });
    }
}
