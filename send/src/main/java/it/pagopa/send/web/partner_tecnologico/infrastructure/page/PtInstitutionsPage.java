package it.pagopa.send.web.partner_tecnologico.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.component.PtFooter;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.component.PtHeader;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}}
 * Pagina "I tuoi enti" del BackOffice dei Partner Tecnologici: è la home del portale, su cui atterra un ente PT dopo
 * l'accesso a SEND da Area Riservata.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo "I tuoi enti".
 * È usata da più test:
 * <ul>
 *     <li>{@code accesso-backoffice-pt.feature}: atterraggio del PT dopo il login da Area Riservata;</li>
 *     <li>{@code WebPtHeaderFooterContractTest}: link dell'header e del footer del portale;</li>
 *     <li>{@code WebInstitutionsPtContractTest}: testi e stato iniziale della pagina.</li>
 * </ul>
 */
@Url("${url.notifiche.partner-tecnologico.base}")
public interface PtInstitutionsPage extends Page {

    String TITLE = "I tuoi enti";

    // il titolo dentro il contenitore della pagina "I tuoi enti"
    @XPath("//*[@data-testid=\"institutionsPage\"]//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@data-testid=\"institutionsPage\"]//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // avviso sullo stato del portale, sotto il titolo
    @XPath("//*[@data-testid=\"work-in-progress-alert\"]")
    Readable<String> workInProgressAlert();

    @XPath("//*[@data-testid=\"institutionsSearch\"]//label")
    Readable<String> searchLabel();

    @XPath("//*[@data-testid=\"institutionsSearch\"]//input[@id=\"search\"]")
    TextField searchInput();

    @XPath("//*[@data-testid=\"searchButton\"]")
    Button searchButton();

    // enti deleganti: nome, codice fiscale e stato della delega. Solo a caricamento concluso, prima le righe sono scheletri vuoti
    @XPath("//*[@data-testid=\"institutionsList\" and not(@aria-busy)]//*[@data-testid=\"institutionsTableDesktop\"]")
    Table institutionsTable();

    // numero di righe per pagina, sotto la tabella
    @XPath("//*[@data-testid=\"itemsPerPageSelector\"]//button")
    Button rowsPerPage();

    PtHeader header();

    PtFooter footer();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
    }
}
