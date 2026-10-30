package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class, 'MuiTable-root')]")
public interface Table extends Component {

    @XPath(".//thead[contains(@class,'MuiTableHead-root')]")
    TableHead tHead();

    @XPath(".//tbody[contains(@class,'MuiTableBody-root')]")
    TableBody tBody();
}
