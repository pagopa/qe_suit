<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/TextField.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/TextField.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

import java.util.List;

public interface TextField extends Component, Writable<String>, Readable<String> {
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

    default void fill(String value) {
        if (value == null || value.isEmpty()) {
            cleanAndAssert();
        } else {
            cleanAndWriteAndAssert(value);
        }
    }

    default void fill(Object value) {
        if (value != null) {
            cleanAndWriteAndAssert(value.toString());
        }  else {
            cleanAndAssert();
        }
    }
}
