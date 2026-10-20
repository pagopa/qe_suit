package it.pagopa.interop.web.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.Assertions;

public interface UnauthorizedComponent extends Component {

    @XPath(".//h1[contains(normalize-space(.), 'Non autorizzato')]")
    Readable<String> title();

    @XPath(".//p[contains(normalize-space(.), 'Non possiedi le autorizzazioni necessarie per visualizzare la pagina.')]")
    Readable<String> description();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(title().read()).as("Title should be 'Non autorizzato'").isNotBlank();
        Assertions.assertThat(description().read()).as("Description should be 'Non possiedi le autorizzazioni necessarie per visualizzare la pagina.'").isNotBlank();
    }
}
