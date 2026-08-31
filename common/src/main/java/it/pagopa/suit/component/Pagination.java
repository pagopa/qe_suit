<<<<<<<< HEAD:common/src/main/java/it/pagopa/infrastructure/suit/component/Pagination.java
package it.pagopa.infrastructure.suit.component;
========
package it.pagopa.suit.component;
>>>>>>>> 9ca1b9d5 (refactor: [QA-15573] ristrutturazione dei pacchetti mediante l'aggiunta di componenti comuni al modulo common in modo da migliorare l'organizzazione del codice e aggiornamento dei riferimenti negli altri moduli):common/src/main/java/it/pagopa/suit/component/Pagination.java

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//nav[contains(@class, 'MuiPagination-root')]")
public interface Pagination extends Component {

    String SELECTED_CLASS = "Mui-selected";

    @XPath("(.//li)[last()]")
    Button nextBtn();

    @XPath("(.//li)[1]")
    Button prevBtn();

    default boolean hasNext(){
        return !nextBtn().isDisabled();
    }

    default boolean hasPrevious(){
        return !prevBtn().isDisabled();
    }
}
