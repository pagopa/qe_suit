package it.pagopa.interop.web.riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface APIEstimateCard extends Component {
    @XPath(".//h2")
    Label title();

    @XPath(".//p[normalize-space()='Stima numero chiamate API/giorno']")
    Label apiEstimateLabel();

    @XPath(".//p[normalize-space()='Stima numero chiamate API/giorno']/parent::div/following-sibling::div[1]//span")
    Label apiEstimateValue();

}
