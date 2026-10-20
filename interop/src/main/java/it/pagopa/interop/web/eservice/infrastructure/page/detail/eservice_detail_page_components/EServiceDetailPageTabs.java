package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPageTabs extends Component {
    @XPath(".//*[@role='tab'][normalize-space()='Dettaglio e-service']")
    Label eserviceDetailTab();
    @XPath(".//*[@role='tab'][normalize-space()='Template finalità collegati']")
    Label purposeTemplatesTab();
    // MUI lab TabPanel: a not selected panel stays in the DOM with the hidden attribute and no children
    @XPath("(.//*[@role='tabpanel'])[1]")
    Label eserviceDetailTabPanel();
    @XPath("(.//*[@role='tabpanel'])[2]")
    Label purposeTemplatesTabPanel();
}
