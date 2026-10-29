package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_card;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Card;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceCreatePageCard extends Card {
    @XPath(".//h2")
    Label sectionTitle();
}
