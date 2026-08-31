<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Drawer.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Drawer.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//div[contains(@class, 'MuiDrawer-root')][1]")
public interface Drawer extends Component {
    @XPath(".//button[1]")
    Clickable closeButton();

    default void close(){
        closeButton().click();
    }
}
