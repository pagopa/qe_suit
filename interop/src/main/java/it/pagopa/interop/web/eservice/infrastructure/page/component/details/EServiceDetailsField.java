package it.pagopa.interop.web.eservice.infrastructure.page.component.details;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

/** Reads a label/value row in the producer details panel, without prescribing its copy. */
public interface EServiceDetailsField extends Component {
    @XPath("./div[1]//p")
    Label label();

    @XPath("./div[2]")
    Label value();
}
