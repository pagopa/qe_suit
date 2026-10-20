package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPagePurposeTemplateSection extends Component {
    String PURPOSE_TEMPLATES_SECTION = ".//section[./div[1]//h2[normalize-space()='Compilazione agevolata della finalità']]";

    @XPath(PURPOSE_TEMPLATES_SECTION + "/div[1]//h2")
    Label purposeTemplatesTitle();

    @XPath(PURPOSE_TEMPLATES_SECTION + "/div[1]/p")
    Label purposeTemplatesDescription();

    @XPath(PURPOSE_TEMPLATES_SECTION + "//button[normalize-space()='Visualizza i template collegati']")
    Button viewLinkedPurposeTemplatesButton();
}
