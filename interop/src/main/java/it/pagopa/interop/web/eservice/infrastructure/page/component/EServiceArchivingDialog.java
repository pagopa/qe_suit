package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;

public interface EServiceArchivingDialog extends Component {

    @XPath(".//*[@role='radiogroup' and .//input[@name='gracePeriodDays']]")
    RadioGroup getGracePeriodDaysField();

    @XPath(".//textarea[@id='reason']")
    TextField getArchivingReasonField();

    @XPath(".//button[normalize-space(.)='Prosegui']")
    Button getContinueButton();

    @XPath(".//button[normalize-space(.)='Archivia']")
    Button getArchiveButton();

    @XPath(".//button[normalize-space(.)='Richiedi archiviazione']")
    Button getRequestArchivingButton();

    default EServiceArchivingDialog setGracePeriodDays(GracePeriodDays gracePeriodDays) {
        getGracePeriodDaysField().selectLike(gracePeriodDays.getDays() + " giorni");
        return this;
    }

    default EServiceArchivingDialog setArchivingReason(String reason) {
        getArchivingReasonField().fill(reason);
        return this;
    }
}
