package it.pagopa.interop.new_arch.web.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath("//*[@id=\"rows-per-page-select\"]")
public interface PageSize extends Component, Clickable {

    @XPath("//ul[@role='listbox' and @aria-labelledby='rows-per-page-select']/li[@role='option']")
    List<Button> sizes();

    default void setSize(int rowPerPage) {
        // Clicca sul selettore stesso per aprire la tendina di Material-UI
        this.click();

        // Filtra la lista dei bottoni per trovare quello con il numero corretto
        sizes().stream()
                .filter(btn -> btn.read().trim().equals(String.valueOf(rowPerPage)))
                .findFirst()
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Opzione '" + rowPerPage + "' non trovata nel menu delle righe per pagina."
                ))
                .click();
    }

    default int getSelectedSize() {
        this.click();

        return sizes().stream()
                .filter(Button::isSelected)
                .findFirst()
                .map(Button::read)
                .map(String::trim)
                .map(Integer::valueOf)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Nessuna opzione selezionata trovata nel menu delle righe per pagina."
                ));
    }
}
