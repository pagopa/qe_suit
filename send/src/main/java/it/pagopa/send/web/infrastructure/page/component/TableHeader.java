package it.pagopa.send.web.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//tr[contains(@class,'MuiTableRow-head')]")
public interface TableHeader extends Component {
    @XPath(".//th[contains(@class,'MuiTableCell-root')]")
    List<TableCell> cells();
}
