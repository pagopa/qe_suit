package it.pagopa.send.web.campaigns.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//table[contains(@class,'MuiTable-root')]")
public interface Table extends Component {
    List<TableRow> rows();
}
