package it.pagopa.interop.web.infrastructure.config.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

import java.util.List;

@XPath(".//span[contains(@class, 'MuiCheckbox-root')]")
public interface Checkbox extends Component, Clickable {
    String ERROR_CLASS = "Mui-error";
    String CHECKED_CLASS = "Mui-checked";
    String DISABLED_CLASS = "Mui-disabled";

    @XPath(".//following::span[contains(@class, 'MuiFormControlLabel-label')]")
    Readable<String> description();

    @XPath(".//following::span[contains(@class, 'MuiFormHelperText-root')]")
    FormHelperText helperText();

    default void setChecked(boolean targetState) {
        if (isChecked() != targetState) {
            click();
        }
    }

    default void check() {
        setChecked(true);
    }

    default void uncheck() {
        setChecked(false);
    }

    default String getHelperText(String labelId) {
        return helperText().getAll()
                .orElse(List.of()).stream()
                .filter(we -> !we.getClasses().contains(ERROR_CLASS))
                .filter(we -> labelId.equals(we.getAttributes().get("id")))
                .findFirst()
                .map(WebPresentationElement::getText)
                .orElse("");
    }

    default String getErrorMessage(String labelId) {
        return helperText().getAll()
                .orElse(List.of()).stream()
                .filter(we -> we.getClasses().contains(ERROR_CLASS))
                .filter(we -> labelId.equals(we.getAttributes().get("id")))
                .findFirst()
                .map(WebPresentationElement::getText)
                .orElse("");
    }

    default boolean isChecked() {
        return this.get()
                .map(we -> we.getClasses().contains(CHECKED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato checked: l'elemento UI non è presente nella pagina."
                ));
    }

    default boolean isDisabled() {
        return this.get()
                .map(we -> we.getClasses().contains(DISABLED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato checked: l'elemento UI non è presente nella pagina."
                ));
    }
}
