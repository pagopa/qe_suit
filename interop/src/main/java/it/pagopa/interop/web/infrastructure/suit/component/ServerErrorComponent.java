package it.pagopa.interop.web.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.Assertions;

public interface ServerErrorComponent extends Component {

    @XPath(".//h1[contains(normalize-space(.), 'Errore del server')]")
    Readable<String> title();

    @XPath(".//p[contains(normalize-space(.), 'Si è verificato un errore nella gestione della richiesta. Per favore, riprova.')]")
    Readable<String> description();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(title().read()).as("Title should be 'Errore del Server'").isNotBlank();
        Assertions.assertThat(description().read()).as("Description should be 'Si è verificato un errore nella gestione della richiesta. Per favore, riprova.'").isNotBlank();
    }
}
