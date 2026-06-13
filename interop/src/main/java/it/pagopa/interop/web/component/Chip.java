package it.pagopa.interop.web.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiChip-root')]")
public interface Chip extends Component {
    String SUCCESS_CLASS = "MuiChip-colorSuccess";
    String ERROR_CLASS = "MuiChip-colorError";
    String WARNING_CLASS = "MuiChip-colorWarning";

    @XPath(".//span")
    Readable<String> text();

    default boolean isSuccess() {
        return this.get()
                .stream()
                .flatMap(el -> el.getClasses().stream())
                .anyMatch(c -> c.contains(SUCCESS_CLASS));
    }

    default boolean isError() {
        return this.get()
                .stream()
                .flatMap(el -> el.getClasses().stream())
                .anyMatch(c -> c.contains(ERROR_CLASS));
    }

    default boolean isWarning() {
        return this.get()
                .stream()
                .flatMap(el -> el.getClasses().stream())
                .anyMatch(c -> c.contains(WARNING_CLASS));
    }

}
