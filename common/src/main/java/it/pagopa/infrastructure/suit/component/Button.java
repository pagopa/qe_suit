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
        return this.get(FindPolicy.PRESENT)
                .map(we -> we.getAttributes().containsKey(DISABLED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }

    default boolean isSelected() {
        return this.get(FindPolicy.PRESENT).map(we -> we.getClasses().contains(SELECTED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }

    @Override
    default void assertLoaded() {
        this.get(FindPolicy.PRESENT)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }
}
