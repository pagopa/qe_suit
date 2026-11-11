package it.pagopa.send.web.mittente.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Checkbox;

import java.util.List;

@XPath("//main//div[contains(@class, 'MuiPaper-root')][.//*[@data-testid='graph-type-select']]")
public interface StatisticsGraphCardComponent extends Component {

    @XPath(".//h3")
    Readable<String> title();

    @XPath(".//*[@data-testid='graph-type-select']//label")
    Readable<String> graphTypeLabel();

    @XPath(".//*[@data-testid='graph-type-select']//*[@role='combobox']")
    Button graphType();

    @XPath(".//*[@data-testid='legendItem']//span[contains(@class, 'MuiCheckbox-root')]")
    List<Checkbox> legendItems();
}
