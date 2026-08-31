<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Alert.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Alert.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiAlert-root')]")
public interface Alert extends Component {

    String[] ERROR_CLASSES = {"MuiAlert-standardError", "MuiAlert-outlinedError"};
    String[] SUCCESS_CLASSES = {"MuiAlert-standardSuccess", "MuiAlert-outlinedSuccess"};
    String[] WARNING_CLASSES = {"MuiAlert-standardWarning", "MuiAlert-outlinedWarning"};
    String[] INFO_CLASSES = {"MuiAlert-standardInfo", "MuiAlert-outlinedInfo"};

    @XPath(".//div[contains(@class, 'MuiAlert-message')]/div[1]")
    Readable<String> title();

    @XPath(".//div[contains(@class, 'MuiAlert-message')]")
    Readable<String> message();

    default boolean isError() {
        return get()
                .map(we -> java.util.Arrays.stream(ERROR_CLASSES).anyMatch(cls -> we.getClasses().contains(cls)))
                .orElse(false);
    }

    default boolean isSuccess() {
        return get()
                .map(we -> java.util.Arrays.stream(SUCCESS_CLASSES).anyMatch(cls -> we.getClasses().contains(cls)))
                .orElse(false);
    }

    default boolean isWarning() {
        return get()
                .map(we -> java.util.Arrays.stream(WARNING_CLASSES).anyMatch(cls -> we.getClasses().contains(cls)))
                .orElse(false);
    }

    default boolean isInfo() {
        return get()
                .map(we -> java.util.Arrays.stream(INFO_CLASSES).anyMatch(cls -> we.getClasses().contains(cls)))
                .orElse(false);
    }
}
