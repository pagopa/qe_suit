package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Drawer;
import it.pagopa.infrastructure.suit.component.Label;

import java.util.List;

@XPath("//div[contains(@class, 'MuiDrawer-root')][1]")
public interface CertifiedAttributesDrawer extends Drawer {

    @XPath(".//h6")
    Label title();

    @XPath(".//h6/following-sibling::p[1]")
    Label subtitle();

    @XPath(".//span[contains(@class,'MuiCardHeader-title')]")
    Label infoCard();

    @XPath(".//*[@role='alert']")
    List<Alert> alerts();
}

