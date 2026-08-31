package it.pagopa.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiSnackbar-root')][1]")
public interface Snackbar extends Component {
    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert alert();
}
