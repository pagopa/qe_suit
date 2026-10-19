package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiSwitch-switchBase')]")
public interface Switch extends Component, Clickable {

    default void toggle() {
        this.click();
    }

    default boolean isChecked() {
        return get()
                .map(we -> we.getClasses().contains("Mui-checked"))
                .orElse(false);
    }
}
