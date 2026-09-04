package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiButton')]")
public interface Button extends Component, Clickable, Readable<String> {
    String DISABLED_CLASS = "Mui-disabled";
    String SELECTED_CLASS = "Mui-selected";

    default boolean isDisabled() {
        return this.get()
                .map(we -> we.getClasses().contains(DISABLED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }

    default boolean isSelected() {
        return this.get().map(we -> we.getClasses().contains(SELECTED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }

    @Override
    default void assertLoaded() {
        this.get(FindPolicy.VISIBLE)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }
}
