package it.pagopa.send.web.mittente.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;

import java.util.List;

@XPath("//*[@data-testid='statistics-filter']")
public interface StatisticsFilterComponent extends Component {

    @XPath(".//button[starts-with(@data-testid, 'filter.')]")
    List<Button> periods();

    @XPath(".//input[@id='startDate']")
    TextField startDate();

    @XPath(".//input[@id='endDate']")
    TextField endDate();

    @XPath(".//*[@data-testid='filterButton']")
    Button filterButton();

    @XPath(".//*[@data-testid='cancelButton']")
    Button cancelButton();
}
