<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Button.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Button.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiButton')]")
public interface Button extends Component, Clickable, Readable<String> {
    String DISABLED_CLASS = "Mui-disabled";
    String SELECTED_CLASS = "Mui-selected";

    default boolean isDisabled() {
        return this.get(FindPolicy.PRESENT)
                .map(we -> we.getClasses().contains(DISABLED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }

    default boolean isSelected() {
<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Button.java
        return this.get(FindPolicy.PRESENT)
                .map(we -> we.getClasses().contains(SELECTED_CLASS))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }

    @Override
    default void assertLoaded() {
        this.get(FindPolicy.PRESENT)
========
        return this.get()
                .map(we -> we.getClasses().contains(SELECTED_CLASS))
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Button.java
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del button: l'elemento UI non è presente nella pagina."
                ));
    }
}
