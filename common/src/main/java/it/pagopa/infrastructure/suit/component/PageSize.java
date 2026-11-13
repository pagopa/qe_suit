package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;
import java.util.NoSuchElementException;

@XPath("//*[@id=\"rows-per-page-select\"]")
public interface PageSize extends Component, Clickable {

    @XPath("//ul[@role='listbox' and @aria-labelledby='rows-per-page-select']/li[@role='option']")
    List<Button> sizes();

    default void setSize(int rowPerPage) {
        if (isNotExpanded())
            this.click();
        sizes().stream()
                .filter(btn -> btn.read().trim().equals(String.valueOf(rowPerPage)))
                .findFirst()
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Opzione '" + rowPerPage + "' non trovata nel menu delle righe per pagina."
                ))
                .click();
    }

    default int getSelectedSize() {
        if (isNotExpanded())
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

    default List<Integer> getSizeValues() {
        if (isNotExpanded())
            this.click();
        return sizes().stream()
                .map(Button::read)
                .map(String::trim)
                .map(Integer::valueOf)
                .toList();
    }

    private boolean isNotExpanded() {
        return !this.get()
                .orElseThrow(() -> new NoSuchElementException("No optional found for PageSize element"))
                .getAttributes()
                .get("aria-expanded")
                .equals("true");
    }
}
