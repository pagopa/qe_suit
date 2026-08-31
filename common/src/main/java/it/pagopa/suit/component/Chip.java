<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Chip.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Chip.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiChip-root')]")
public interface Chip extends Component {
    String SUCCESS_CLASS = "MuiChip-colorSuccess";
    String ERROR_CLASS = "MuiChip-colorError";
    String WARNING_CLASS = "MuiChip-colorWarning";

    @XPath(".//span")
    Readable<String> text();

    default boolean isSuccess() {
        return this.get()
                .stream()
                .flatMap(el -> el.getClasses().stream())
                .anyMatch(c -> c.contains(SUCCESS_CLASS));
    }

    default boolean isError() {
        return this.get()
                .stream()
                .flatMap(el -> el.getClasses().stream())
                .anyMatch(c -> c.contains(ERROR_CLASS));
    }

    default boolean isWarning() {
        return this.get()
                .stream()
                .flatMap(el -> el.getClasses().stream())
                .anyMatch(c -> c.contains(WARNING_CLASS));
    }

}
