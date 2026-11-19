package it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface AssignmentCard extends Component {
    @XPath(".//h2")
    Label title();

    @XPath(".//p[normalize-space()='Data assegnazione']")
    Label assignmentDateLabel();

    @XPath(".//p[normalize-space()='Data assegnazione']/parent::div/following-sibling::div[1]//span")
    Label assignmentDateValue();

    @XPath(".//p[normalize-space()='Assegnata a']")
    Label assignedToLabel();

    @XPath(".//p[normalize-space()='Assegnata a']/parent::div/following-sibling::div[1]//span")
    Label assignedToValue();
}
