package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPagePurposeTemplateSection extends Component {
    @XPath("./div[1]//h2")
    Label purposeTemplatesTitle();

    @XPath("./div[1]/p")
    Label purposeTemplatesDescription();

    @XPath(".//button[normalize-space()='Visualizza i template collegati']")
    Button viewLinkedPurposeTemplatesButton();
}
