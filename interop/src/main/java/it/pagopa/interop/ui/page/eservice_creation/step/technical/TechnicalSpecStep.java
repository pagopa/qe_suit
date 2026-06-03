package it.pagopa.interop.ui.page.eservice_creation.step.technical;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.component.InterfaceComponent;
import org.assertj.core.api.SoftAssertions;

public interface TechnicalSpecStep extends Component {

    @XPath("//section[.//h2[text()='Interfaccia']]")
    InterfaceComponent interfaceComponent();

    @XPath("//section[.//h2[text()='Voucher']]")
    VoucherComponent voucherComponent();

    @XPath("//section[.//h2[text()='Scambi asincroni e massivi']]")
    AsyncComponent asyncComponent();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            voucherComponent().assertLoaded();
            interfaceComponent().assertLoaded();
        });
    }
}
