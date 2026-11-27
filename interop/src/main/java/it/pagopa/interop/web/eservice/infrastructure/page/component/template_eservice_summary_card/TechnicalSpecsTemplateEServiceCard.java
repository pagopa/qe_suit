package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;

public interface TechnicalSpecsTemplateEServiceCard extends EServiceSummaryCard {

    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    TechnicalSpecsTemplateEServiceCard.TechnicalSpecsContent content();

    interface TechnicalSpecsContent extends Content {

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='Interfaccia']]")
        TechnicalSpecsTemplateEServiceCard.InterfaceField apiInterface();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='Durata del voucher']]")
        Field voucherLifespan();
    }

    interface InterfaceField extends Component {

        @XPath("./div[1]/p")
        Label label();

        @XPath("./div[2]//button")
        Button attachmentButton();
    }

}
