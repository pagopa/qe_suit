package it.pagopa.interop.web.tos.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.DomNode;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.tos.infrastructure.page.component.TOSSection;
import it.pagopa.interop.web.tos.infrastructure.page.component.TOSIndex;
import org.assertj.core.api.Assertions;

import java.util.List;

@Url("${interop.web.base-url}/termini-di-servizio")
public interface TOSPage extends Page {

    @XPath(".//main//h1")
    Readable<String> pageTitle();

    @XPath(".//main//*[starts-with(@id, 'otnotice-section-')]")
    List<TOSSection> sections();

    @XPath(".//main//ul[.//a[contains(@href, '#otnotice-section-')]]")
    List<TOSIndex> indexes();

    @XPath("//head/base")
    DomNode documentBase();

    default String documentBaseHref() {
        return documentBase().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Elemento base del documento TOS non disponibile"))
                .getAttributes().get("href");
    }

    @Override
    default void assertLoaded() {
        Assertions.assertThat(pageTitle().read()).as("Titolo pagina TOS disponibile").isNotBlank();
        Assertions.assertThat(sections()).as("Documento TOS caricato").isNotEmpty();
    }
}
