package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface    TechnicalSpecsCard extends EServiceSummaryCard {

    String INTERFACE = "Interfaccia";
    String AUDIENCE = "Audience";
    String VOUCHER_LIFESPAN = "Durata del voucher";

    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    TechnicalSpecsContent content();

    interface TechnicalSpecsContent extends Content {

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + INTERFACE + "']]")
        InterfaceField apiInterface();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + AUDIENCE + "']]")
        Field audience();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + VOUCHER_LIFESPAN + "']]")
        Field voucherLifespan();
    }

    interface InterfaceField extends Component {

        @XPath("./div[1]/p")
        Label label();

        @XPath("./div[2]//button")
        Button attachmentButton();
    }
}
