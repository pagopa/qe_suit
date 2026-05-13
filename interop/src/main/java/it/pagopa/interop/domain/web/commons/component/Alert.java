package it.pagopa.interop.domain.web.commons.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiAlert-root')]")
public interface Alert extends Component, Gettable {

    String ERROR_CLASS = "MuiAlert-standardError";
    String SUCCESS_CLASS = "MuiAlert-standardSuccess";

    @XPath(".//div[contains(@class, 'MuiAlert-message')]/div[1]")
    Readable<String> title();

    @XPath(".//div[contains(@class, 'MuiAlert-message')]/p[1]")
    Readable<String> message();

    default boolean isError() {
        return get().map(we -> we.getClasses().contains(ERROR_CLASS)).orElse(false);
    }

    default boolean isSuccess() {
        return get().map(we -> we.getClasses().contains(SUCCESS_CLASS)).orElse(false);
    }
}
