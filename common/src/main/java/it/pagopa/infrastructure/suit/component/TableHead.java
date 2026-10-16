package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//*[contains(@class, 'MuiTableHead-root')]")
public interface TableHead extends Component {

    @XPath(".//tr")
    List<TableRow> rows();
}