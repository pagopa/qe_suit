package it.pagopa.interop.web.purpose_template.infrastructure.suite.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.TextField;

public interface PurposeTemplateCatalogFilter extends Component {
    @XPath(".//label")
    Readable<String> label();

    @XPath(".//input")
    TextField input();
}
