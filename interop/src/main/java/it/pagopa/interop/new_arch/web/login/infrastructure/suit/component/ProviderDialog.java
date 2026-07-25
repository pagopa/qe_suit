package it.pagopa.interop.new_arch.web.login.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.new_arch.web.infrastructure.component.Button;

public interface ProviderDialog extends Component {
    @XPath("//*[@id=\"https://idp.uat.oneid.pagopa.it\"]|//*[@id=\"xx_testenv2\"]|//*[@id=\"spid-select-xx_testenv2\"]")
    Button providerButton();

    default void selectFakeProvider() {
        providerButton().click();
    }
}
