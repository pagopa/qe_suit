package it.pagopa.interop.web.commons.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

import java.util.List;

public interface TextField extends Component, Writable<String> {
    String ERROR_CLASS = "Mui-error";

    /**
     * In caso di errore di un TextField l'helper text sono due, uno per l'errore (con classe Mui-error) e l'altro per la descrizione.
     */
    @XPath(".//following::span[contains(@class, 'MuiFormHelperText-root')]")
    FormHelperText helperText();

    default String getErrorMessage(String labelId) {
        return helperText().getAll()
                .orElse(List.of()).stream()
                .filter(we -> we.getClasses().contains(ERROR_CLASS))
                .filter(we -> labelId.equals(we.getAttributes().get("id")))
                .findFirst()
                .map(WebPresentationElement::getText)
                .orElse("");
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
}
