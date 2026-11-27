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

    /**
     * Reads all cell values from the rows on the currently selected tab and page.
     *
     * @return table rows, each represented by its cell values
     */
    default List<List<String>> readCurrentRows() {
        return tBody().rows().stream()
                .map(row -> row.cells().stream().map(TableCell::read).toList())
                .toList();
    }
}
