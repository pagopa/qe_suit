package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//*[contains(@class, 'MuiTableRow-root')]")
public interface TableRow extends Component {

    @XPath("./*[self::th or self::td]")
    List<TableCell> cells();
}
