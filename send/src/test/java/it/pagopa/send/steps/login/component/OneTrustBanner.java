package it.pagopa.send.steps.login.component;

import java.util.Optional;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//*[@id=\"onetrust-banner-sdk\"]")
public interface OneTrustBanner extends Component {

    @XPath(".//*[@id=\"onetrust-accept-btn-handler\"]")
    Optional<Clickable> acceptButton();

    @XPath(".//*[@id=\"onetrust-reject-all-handler\"]")
    Clickable rejectButton();

    default void accept(){
        acceptButton().ifPresent(Clickable::click);
    }

    default void reject(){
        rejectButton().click();
    }
}
