<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Header.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Header.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

public interface Header extends Component {

    @XPath("//*[@id=\"root\"]/div/div[1]/header/div/div/div/div/button[3]")
    Button logoutButton();

    @XPath("/html/body/div[3]/div[3]/div/div/div[2]/div[2]/div[2]/button")
    Button logoutConfirmButton();

    default void logout(){
        logoutButton().click();
        logoutConfirmButton().click();
    }
}
