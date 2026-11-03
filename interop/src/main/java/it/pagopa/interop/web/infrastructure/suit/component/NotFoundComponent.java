package it.pagopa.interop.web.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.Assertions;

public interface NotFoundComponent extends Component {

    @XPath(".//h1[contains(normalize-space(.), 'Pagina inesistente')]")
    Readable<String> title();

    @XPath(".//p[contains(normalize-space(.), '404 - La pagina cercata purtroppo non esiste.')]")
    Readable<String> description();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(title().read()).as("Title should be 'Pagina inesistente'").isNotBlank();
        Assertions.assertThat(description().read()).as("Description should be '404 - La pagina cercata purtroppo non esiste.'").isNotBlank();
    }
}
