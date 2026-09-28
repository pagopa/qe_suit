package it.pagopa.send.web.campaigns.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//tr[contains(@class,'MuiTableRow-root')]")
public interface TableRow extends Component {
    List<TableCell> cells();
}
