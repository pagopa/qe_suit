package it.pagopa.interop.web.page.eservice.creation.wizard.technical;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.component.InterfaceComponent;
import org.assertj.core.api.SoftAssertions;

public interface TechnicalSpecWizard extends Component {

    @XPath("//section[.//h2[text()='Interfaccia']]")
    InterfaceComponent interfaceComponent();

    @XPath("//section[.//h2[text()='Voucher']]")
    TechnicalVoucherSection voucherComponent();

    @XPath("//section[.//h2[text()='Scambi asincroni e massivi']]")
    TechnicalAsyncSection asyncComponent();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            voucherComponent().assertLoaded();
            interfaceComponent().assertLoaded();
        });
    }
}
