package it.pagopa.send.domain.web.pages.destinatario.pf.login;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.capability.core.Readable;

import java.util.Optional;

public interface DowntimeItem extends Component {
    @XPath(".//p[1]")
    Readable<String> description();

    @XPath(".//ul[2]//span")
    Readable<String> affectedFunctionalities();

    @XPath(".//button")
    Optional<Clickable> malfunctionCertificateButton();

    @XPath(".//div[contains(@class,'MuiBox-root')]//p")
    Optional<Readable<String>> pendingCertificateMessage();

    default boolean isCertificateAvailable() {
        return malfunctionCertificateButton().isPresent();
    }
}
