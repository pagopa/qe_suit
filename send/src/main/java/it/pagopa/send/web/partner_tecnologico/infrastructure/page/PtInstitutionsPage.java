package it.pagopa.send.web.partner_tecnologico.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}}
 * Pagina "I tuoi enti" del BackOffice dei Partner Tecnologici: è la home del portale, su cui atterra un ente PT dopo
 * l'accesso a SEND da Area Riservata.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo "I tuoi enti".
 */
@Url("${url.notifiche.partner-tecnologico.base}")
public interface PtInstitutionsPage extends Page {

    String TITLE = "I tuoi enti";

    // il titolo dentro il contenitore della pagina "I tuoi enti"
    @XPath("//*[@data-testid=\"institutionsPage\"]//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
    }
}
