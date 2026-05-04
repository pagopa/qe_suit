package it.pagopa.send.steps.mittenti.components;

import org.openqa.selenium.Keys;

import com.google.common.base.Optional;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

public interface SearchFiltersComponent extends Component {
    //@XPath(".//*[@id=\"subject\"]")
    @XPath(".//*[@id='iunMatch']")
    Writable<String> iun();
    
    @XPath(".//*[@id='recipientId']")
    Writable<String> taxCode();
    
    @XPath(".//*[@id='startDate']")
    Writable<String> fromDate();
    
    @XPath(".//*[@id='endDate']")
    Writable<String> toDate();
        
    ////*[@id="filter-button"]
    @XPath(".//*[@id=\"filter-button\"]")
    Clickable filterButton();

    @XPath(".//*[@id='filter-button'][not(@disabled)]")
    Optional<Clickable> filterButtonEnabled();

    @XPath(".//input[@id='startDate']")
    Readable<WebPresentationElement> fromDateState();

    @XPath(".//input[@id='endDate']")
    Readable<WebPresentationElement> toDateState();

    default boolean hasDateRangeError() {
        return "true".equals(fromDateState().read().getAttributes().get("aria-invalid"))
            && "true".equals(toDateState().read().getAttributes().get("aria-invalid"));
    }

    @XPath(".//*[@id='status']")
    Clickable statusDropdown();

    default XPathSelector statusOptionSelector(String status) {
        return XPathSelector.of(
            "//*[@role='option'][normalize-space()='" + status + "']"
        );
    }

    default void filterBy(String tipoFiltro, String valore) {
        switch (tipoFiltro) {
            case "IUN" -> iun().write(valore);
            case "TAX_CODE" -> taxCode().write(valore);
        }
        filterButton().click();
    }

    default void setDateRange(String from, String to) {
        fromDate().write(Keys.chord(Keys.CONTROL, "a") + from);
        toDate().write(Keys.chord(Keys.CONTROL, "a") + to);
    }

    default void clearDateRange() {
        fromDate().write(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        toDate().write(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }
}
