package it.pagopa.send.web.campagne.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

@XPath("//div[contains(@class,'MuiListItem-root')]")
public interface CampagneElement extends Component {
    Button apriCampagna();
}
