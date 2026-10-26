package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface ThresholdsAttributesCard extends EServiceSummaryCard {

    String API_CALL_THRESHOLDS = "Soglie di chiamate API";
    String PER_CONSUMER = "Per fruitore";
    String TOTAL = "Totali";
    String CERTIFIED_ATTRIBUTES = "Attributi Certificati";
    String VERIFIED_ATTRIBUTES = "Attributi Verificati";
    String DECLARED_ATTRIBUTES = "Attributi Dichiarati";

    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    ThresholdsAttributesContent content();

    interface ThresholdsAttributesContent extends Content {

        @XPath(".//p[normalize-space(.)='" + API_CALL_THRESHOLDS + "']")
        Label apiCallThresholdsTitle();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + PER_CONSUMER + "']]")
        Field dailyCallsPerConsumer();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + TOTAL + "']]")
        Field dailyCallsTotal();

        @XPath(".//section[.//h3[normalize-space(.)='" + CERTIFIED_ATTRIBUTES + "']]")
        AttributeSection certifiedAttributes();

        @XPath(".//section[.//h3[normalize-space(.)='" + VERIFIED_ATTRIBUTES + "']]")
        AttributeSection verifiedAttributes();

        @XPath(".//section[.//h3[normalize-space(.)='" + DECLARED_ATTRIBUTES + "']]")
        AttributeSection declaredAttributes();
    }

    interface AttributeSection extends Component {

        @XPath(".//h3")
        Label title();

        @XPath("./div[contains(@class,'MuiStack-root')]/p")
        Label description();

        @XPath(".//*[contains(@class,'MuiCardHeader-title')]")
        Label emptyStateMessage();
    }
}
