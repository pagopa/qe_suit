package it.pagopa.interop.web.infrastructure.config.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//nav[contains(@class, 'MuiBreadcrumbs-root')]")
public interface Breadcrumbs extends Component {
    @XPath(".//li[contains(@class, 'MuiBreadcrumbs-li')]")
    List<Readable<String>> items();

    default String getLastItemText() {
        List<Readable<String>> breadcrumbItems = items();
        if (breadcrumbItems.isEmpty()) {
            return null;
        }
        return breadcrumbItems.get(breadcrumbItems.size() - 1).read();
    }
}
