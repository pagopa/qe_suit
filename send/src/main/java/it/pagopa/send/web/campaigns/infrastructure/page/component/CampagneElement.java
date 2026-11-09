package it.pagopa.send.web.campaigns.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.frontend.e2e.framework.web.capability.core.Readable;

import java.util.List;

@XPath(".//li[contains(@class,'MuiListItem-root')]")
public interface CampagneElement extends Component {
    Button apriCampagna();
    @XPath(".//span[contains(@class, 'MuiTypography-root')]")
    List<Readable<String>> fields();
}
