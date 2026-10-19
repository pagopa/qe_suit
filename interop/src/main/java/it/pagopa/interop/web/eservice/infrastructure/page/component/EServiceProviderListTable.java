package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

/** Semantic table headings; row content and counts are environment data, not fixed expectations. */
public interface EServiceProviderListTable extends Component {
    @XPath(".//thead//th")
    Readable<String> columnHeadings();
}
