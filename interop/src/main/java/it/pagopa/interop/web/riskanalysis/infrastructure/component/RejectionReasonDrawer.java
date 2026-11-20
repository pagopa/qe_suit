package it.pagopa.interop.web.riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Drawer;
import it.pagopa.infrastructure.suit.component.Label;

public interface RejectionReasonDrawer extends Drawer {
    @XPath(".//h6")
    Label drawerTitle();

    @XPath(".//p[contains(@class, 'MuiTypography-root')]")
    Label drawerDescription();

    @XPath(".//h6[normalize-space()='Rifiuto analisi del rischio']/parent::div/following-sibling::div/p")
    Label rejectionReason();

    @XPath("//button[@aria-label='Chiudi il drawer']")
    Button closeButton();
}
