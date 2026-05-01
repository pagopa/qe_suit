package it.pagopa.send.steps.mittenti.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;

public interface SearchFiltersComponent extends Component {
    //@XPath(".//*[@id=\"subject\"]")
    @XPath(".//*[@id='iunMatch']")
    Writable<String> iun();
    
    @XPath(".//*[@id='recipientId']")
    Writable<String> taxCode();
    
    @XPath(".//input[@id='fromDate']")
    Clickable fromDate();
    
    @XPath(".//input[@id='toDate']")
    Clickable toDate();
    
    @XPath(".//select[@id='status']")
    Clickable statusDropdown();
    
    ////*[@id="filter-button"]
    @XPath(".//*[@id=\"filter-button\"]")
    Clickable filterButton();

    default void filterBy(String tipoFiltro, String valore) {
        switch (tipoFiltro) {
            case "IUN" -> iun().write(valore);
            case "TAX_CODE" -> taxCode().write(valore);
        }
        filterButton().click();
    }
}
