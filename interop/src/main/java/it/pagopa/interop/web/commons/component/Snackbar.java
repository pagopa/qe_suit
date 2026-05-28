package it.pagopa.interop.web.commons.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiSnackbar-root')][1]")
public interface Snackbar extends Component, Gettable {
    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert alert();
}
