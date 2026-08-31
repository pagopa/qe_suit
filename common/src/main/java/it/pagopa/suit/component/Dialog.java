<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Dialog.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Dialog.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiDialog-paper')]")
public interface Dialog extends Component {
    @XPath(".//button[normalize-space()='Conferma']")
    Button confirmBtn();
}
