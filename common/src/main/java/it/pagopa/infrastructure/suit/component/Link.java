package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//a")
public interface Link extends Component, Clickable {

    default String getHref() {
        return this.get()
                .map(we -> we.getAttributes().get("href"))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile ottenere l'attributo href del link"
                ));
    }

}
