<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/RadioButton.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/RadioButton.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//label[.//input[@type='radio']]")
public interface RadioButton extends Component {
    String MUI_ACTIVE_CLASS = "Mui-checked";
    String MUI_DISABLED_CLASS = "Mui-disabled";

    @XPath(".//span[contains(@class, 'MuiRadio-root')]")
    Button radio();

    @XPath(".//span[contains(@class, 'MuiFormControlLabel-label')]")
    Label label();

    default boolean isSelected() {
        return radio().get()
                .map(element -> element.getClasses().contains(MUI_ACTIVE_CLASS))
                .orElseThrow(() -> new IllegalStateException("Radio button not found"));
    }

    default boolean isDisabled() {
        return radio().get()
                .map(element -> element.getClasses().contains(MUI_DISABLED_CLASS))
                .orElseThrow(() -> new IllegalStateException("Radio button not found"));
    }

    default void select() {
        radio().click();
        if (!isSelected()) throw new IllegalStateException("Radio button is not selected");
    }

    default String getLabel() {
        return label().read();
    }
}
