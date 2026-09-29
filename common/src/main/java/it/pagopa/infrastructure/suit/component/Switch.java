package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//span[contains(@class, 'MuiSwitch-root')]")
public interface Switch extends Component, Clickable {
    String CHECKED_CLASS = "Mui-checked";
    String DISABLED_CLASS = "Mui-disabled";

    default boolean isChecked() {
        return this.get(FindPolicy.PRESENT)
                .map(we -> we.getClasses().contains(CHECKED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato checked: l'elemento UI non è presente nella pagina."
                ));
    }

    default boolean isDisabled() {
        return this.get(FindPolicy.PRESENT)
                .map(we -> we.getClasses().contains(DISABLED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato disabled: l'elemento UI non è presente nella pagina."
                ));
    }

    default void setChecked(boolean targetState) {
        if (isChecked() != targetState) {
            click();
        }
    }

    @Override
    default void assertLoaded() {
        this.get(FindPolicy.PRESENT)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato dello switch: l'elemento UI non è presente nella pagina."
                ));
    }
}

