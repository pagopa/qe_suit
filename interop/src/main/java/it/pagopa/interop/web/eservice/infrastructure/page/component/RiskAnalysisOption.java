package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

/**
 * Opzione del menu a tendina dell'Autocomplete (MuiAutocomplete-option, li[role=option]).
 */
public interface RiskAnalysisOption extends Component, Clickable, Readable<String> {

    default boolean isSelected() {
        return this.get(FindPolicy.PRESENT)
                .map(element -> "true".equals(element.getAttributes().get("aria-selected")))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato dell'opzione: l'elemento UI non è presente nella pagina."
                ));
    }
}

