package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//*[contains(@class, 'MuiTable-root')]")
public interface Table extends Component {

    @XPath(".//thead[contains(@class,'MuiTableHead-root')]")
    TableHead tHead();

    @XPath(".//tbody[contains(@class,'MuiTableBody-root')]")
    TableBody tBody();

    default int getTotalRows() {
        return tBody().rows().size();
    }

    default List<String> getColumnHeaders() {
        return tHead().rows().stream()
                .map(TableRow::cells)
                .flatMap(List::stream)
                .map(TableCell::read)
                .toList();
    }
}
