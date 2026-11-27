package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Link;

@XPath(".//div[contains(@class, 'MuiCard-root')]")
public interface EServiceTemplateCatalogCard extends Component {

    @XPath(".//span[contains(@class, 'MuiTypography-caption')]")
    Readable<String> producerName();

    @XPath(".//h6")
    Readable<String> title();

    @XPath(".//div[contains(@class, 'MuiCardContent-root')]//p")
    Readable<String> description();

    @XPath(".//div[contains(@class, 'MuiCardActions-root')]//a[normalize-space()='Visualizza']")
    Link inspectLink();

    default boolean isInspectLinkDisabled() {
        return inspectLink().get(FindPolicy.PRESENT)
                .map(we -> we.getClasses().contains("Mui-disabled")
                        || we.getAttributes().containsKey("disabled")
                        || "true".equals(we.getAttributes().get("aria-disabled")))
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Impossibile verificare lo stato del link Visualizza: l'elemento UI non è presente nella card."
                ));
    }
}

