package it.pagopa.interop.web.tos.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

public interface TOSSection extends Component {

    @XPath(".//h2")
    Readable<String> heading();

    @XPath(".//p")
    Readable<String> content();

    default String id() {
        return get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Sezione TOS non disponibile nel DOM"))
                .getAttributes().get("id");
    }
}
