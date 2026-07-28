package it.pagopa.interop.web.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//nav[contains(@class, 'MuiBreadcrumbs-root')]")
public interface Breadcrumb extends Component {
    @XPath(".//li[not(contains(concat(' ', normalize-space(@class), ' '), ' MuiBreadcrumbs-separator '))]")
    List<Readable<String>> items();
}
