package it.pagopa.interop.web.agreement.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

/**
 * Tabella delle richieste ricevute. Selettori da validare sul DOM QA.
 */
@XPath(".//table[contains(@class, 'MuiTable-root')]")
public interface AgreementProducerTable extends Component {

    @XPath(".//thead//th")
    List<Readable<String>> headerLabels();

    default boolean isPresent() {
        return get(FindPolicy.PRESENT).isPresent();
    }

    default List<String> readHeaderLabels() {
        return headerLabels().stream().map(h -> h.read().trim()).toList();
    }
}

