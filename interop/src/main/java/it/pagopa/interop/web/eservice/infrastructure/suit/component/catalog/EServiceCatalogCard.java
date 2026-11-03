package it.pagopa.interop.web.eservice.infrastructure.suit.component.catalog;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Card;
import it.pagopa.infrastructure.suit.component.Label;


@XPath(".//*[contains(@class, 'MuiCard-root')]")
public interface EServiceCatalogCard extends Card {

    Button goToEServiceButton();

    Header header();

    @XPath(".//h2")
    Label eServiceName();

    @XPath(".//p")
    Label eServiceDescription();

    public interface Header extends Component {
        @XPath(".//div[contains(@class, 'MuiAvatar-root')]/following-sibling::div//span[contains(@class, 'MuiTypography-root')]")
        Label title();
    }

}
