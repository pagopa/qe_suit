package it.pagopa.interop.web.agreement.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

/**
 * Filtro autocomplete della lista richieste ricevute (MUI Autocomplete).
 * Il selettore della radice è definito dalla Page, che conosce la label del filtro.
 * <p>
 * Selettori da validare sul DOM QA.
 */
public interface AgreementProducerFilter extends Component {

    @XPath(".//label")
    Label label();

    /**
     * Input abilitato: esiste solo se l'attributo {@code disabled} è assente.
     */
    @XPath(".//input[not(@disabled)]")
    Component enabledInput();

    /**
     * Valori selezionati (chip) all'interno del filtro.
     */
    @XPath(".//*[contains(@class, 'MuiChip-root')]")
    Component selectedValue();

    /**
     * Presenza nel DOM (non implica visibilità).
     */
    default boolean isPresent() {
        return get(FindPolicy.PRESENT).isPresent();
    }

    default boolean isEnabled() {
        return enabledInput().get(FindPolicy.PRESENT).isPresent();
    }

    default boolean hasSelection() {
        return selectedValue().get(FindPolicy.PRESENT).isPresent();
    }
}

