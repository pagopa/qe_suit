package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;

public interface ThresholdsAttributesTemplateEServiceCard extends EServiceSummaryCard {

    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    ThresholdsAttributesTemplateEServiceCard.ThresholdsAttributesContent content();

    interface ThresholdsAttributesContent extends Content {

        @XPath(".//p[contains(@class, 'MuiTypography-body2') and contains(normalize-space(), 'Soglie suggerite di chiamate API')]")
        Label apiCallThresholdsTitle();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='Per fruitore']]")
        Field dailyCallsPerConsumer();

        @XPath(".//div[contains(@class, 'MuiStack-root') and ./div/p[contains(normalize-space(), 'Totale')]]")
        Field dailyCallsTotal();

        @XPath(".//section[.//h3[normalize-space(.)='Attributi Certificati']]")
        ThresholdsAttributesTemplateEServiceCard.AttributeSection certifiedAttributes();

        @XPath(".//section[.//h3[normalize-space(.)='Attributi Verificati']]")
        ThresholdsAttributesTemplateEServiceCard.AttributeSection verifiedAttributes();

        @XPath(".//section[.//h3[normalize-space(.)='Attributi Dichiarati']]")
        ThresholdsAttributesTemplateEServiceCard.AttributeSection declaredAttributes();
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
