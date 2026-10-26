package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiCard-root')]")
public interface Card extends Component, Clickable, Readable<String> {

    @Override
    default void assertLoaded() {
        this.get(FindPolicy.PRESENT)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }
}
