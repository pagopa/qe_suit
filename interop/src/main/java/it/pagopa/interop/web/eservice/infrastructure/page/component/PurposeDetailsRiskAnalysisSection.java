package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface PurposeDetailsRiskAnalysisSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()='Modalità']")
    Label modeLabel();

    @XPath(".//p[normalize-space()='Modalità']/ancestor::div[1]/following-sibling::div//span")
    Label modeValue();
}