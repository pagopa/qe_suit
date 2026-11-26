package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface RiskAnalysisQuestionAnswerItem extends Component {

    @XPath(".//p[contains(@class, 'MuiTypography-body2')][1]")
    Label question();

    @XPath(".//p[contains(@class, 'MuiTypography-body2')][2]")
    Label answer();

    @XPath(".//p[contains(@class, 'MuiTypography-caption')]")
    Label infoLabel();
}

