package it.pagopa.interop.web.eservice_template.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;

import java.util.List;

public interface TemplateDocumentSummaryCard extends EServiceSummaryCard {
    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    DocumentContent content();

    interface DocumentContent extends Content {
        @XPath(".//button")
        List<Button> documentDownloadButtons();
    }
}
