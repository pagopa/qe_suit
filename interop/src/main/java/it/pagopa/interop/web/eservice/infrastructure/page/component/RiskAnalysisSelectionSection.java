package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.TextField;

/**
 * Prima sezione (SectionContainer) della pagina: titolo, descrizione e Autocomplete di selezione
 * dell'analisi del rischio (RHFAutocompleteSingle).
 */
public interface RiskAnalysisSelectionSection extends Component {

    @XPath(".//h2")
    Label title();

    @XPath(".//h2/following::p[1]")
    Label description();

    @XPath(".//input[@role='combobox']")
    TextField combobox();

    @XPath(".//div[contains(@class, 'MuiAutocomplete-root')]//label")
    Label comboboxLabel();

    @XPath(".//button[contains(@class, 'MuiAutocomplete-popupIndicator')]")
    Button popupButton();

    @XPath(".//button[contains(@class, 'MuiAutocomplete-clearIndicator')]")
    Button clearButton();
}

