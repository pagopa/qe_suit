package it.pagopa.send.suite.contract.partner_tecnologico;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.TableCell;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.partner_tecnologico.application.PtDelegationUseCase;
import it.pagopa.send.common.partner_tecnologico.domain.PtDelegationStatus;
import it.pagopa.send.common.partner_tecnologico.domain.PtInstitution;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.PtInstitutionsPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.TestConstructor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina "I tuoi enti" del BackOffice dei Partner Tecnologici ({@code {baseUrl}}), la home su cui
 * atterra un ente PT, aperta con la sessione del PT {@code PT_1}.
 * <p>
 * Si verificano titolo, sottotitolo, avviso sullo stato del portale, ente e ruolo nell'header, filtro e tabella degli
 * enti deleganti, confrontata con il BFF, e numero di righe per pagina.
 */
@EnabledIf(expression = "${partner-tecnologico.enabled}", loadContext = true)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebInstitutionsPtContractTest {

    // testi attesi

    private static final String SUBTITLE = "Visualizza gli enti gestiti per il prodotto SEND.";
    // etichetta del ruolo nell'header, dal ruolo dell'utenza su Selfcare
    private static final Map<String, String> ROLE_LABELS = Map.of(
            "admin", "Amministratore",
            "operator", "Operatore");
    private static final String WORK_IN_PROGRESS = "Il portale è in fase di sviluppo, al momento non è possibile gestire SEND per conto dei tuoi enti da qui.";
    private static final String SEARCH_LABEL = "Cerca per Nome o Codice Fiscale";
    private static final String SEARCH = "Cerca";
    private static final String[] TABLE_HEADERS = {"Nome dell’ente", "Codice Fiscale", "Delega"};
    // stato della delega, seguito dalla data dell'ultimo cambio di stato in ora italiana
    private static final Map<PtDelegationStatus, String> DELEGATION_STATUS = Map.of(
            PtDelegationStatus.ACTIVE, "Attiva dal",
            PtDelegationStatus.REVOKED, "Revocata il");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ZoneId ROME = ZoneId.of("Europe/Rome");
    private static final int ROWS_PER_PAGE = 10;

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;
    private final SendJourney sendJourney;
    private final PtDelegationUseCase ptDelegationUseCase;

    /**
     * Testi della pagina "I tuoi enti":
     * <ul>
     *     <li>titolo "I tuoi enti" e sottotitolo;</li>
     *     <li>nell'header, l'ente PT e il ruolo con cui si è fatto l'accesso;</li>
     *     <li>avviso informativo sullo stato del portale.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowInstitutionsPageTexts() {
        User partner = userProvider.resolve(UserAlias.PT_1);

        return webContractValidator.as(partner)
                .on(PtInstitutionsPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo i tuoi enti e sottotitolo",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PtInstitutionsPage.TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                }
                        ),

                        new WebScenario<>(
                                "ente e ruolo nell'header",
                                page -> {},
                                page -> {
                                    page.header().partyName().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(partner.getOrganization()));
                                    page.header().partyRole().readAndAssert(h -> Assertions.assertThat(h)
                                            .isEqualTo(ROLE_LABELS.get(partner.getRoles().get(0).role())));
                                }
                        ),

                        new WebScenario<>(
                                "avviso sullo stato del portale",
                                page -> {},
                                page -> page.workInProgressAlert().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(WORK_IN_PROGRESS))
                        )
                ));
    }

    /**
     * Filtro e tabella degli enti deleganti, con il PT {@code PT_1} che ha almeno una delega:
     * <ul>
     *     <li>etichetta del campo di ricerca, campo vuoto e pulsante "Cerca" abilitato;</li>
     *     <li>intestazioni della tabella;</li>
     *     <li>per ogni ente, nome, codice fiscale e stato della delega con la data, nello stesso ordine del BFF;</li>
     *     <li>10 righe per pagina.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowSearchAndInstitutionsTable() {
        User partner = userProvider.resolve(UserAlias.PT_1);
        // prima di aprire la pagina: se il PT non ha enti deleganti si crea la delega di COMUNE_1
        sendJourney.withPtDelegation(userProvider.resolve(UserAlias.COMUNE_1), partner);
        return webContractValidator.as(partner)
                .on(PtInstitutionsPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "filtro vuoto e cerca abilitato",
                                page -> {},
                                page -> {
                                    page.searchLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SEARCH_LABEL));
                                    page.searchInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.searchButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SEARCH));
                                    Assertions.assertThat(page.searchButton().isDisabled()).isFalse();
                                }
                        ),

                        new WebScenario<>(
                                "intestazioni della tabella",
                                page -> {},
                                page -> Assertions.assertThat(page.institutionsTable().getColumnHeaders()).containsExactly(TABLE_HEADERS)
                        ),

                        new WebScenario<>(
                                "righe degli enti: nome, codice fiscale e stato della delega come nel BFF",
                                page -> {},
                                page -> {
                                    List<InstitutionRow> expected = ptDelegationUseCase.findInstitutions(partner, ROWS_PER_PAGE).stream()
                                            .map(InstitutionRow::of).toList();
                                    List<InstitutionRow> rows = page.institutionsTable().tBody().rows().stream()
                                            .map(InstitutionRow::of).toList();
                                    Assertions.assertThat(rows).as("enti deleganti").containsExactlyElementsOf(expected);
                                }
                        ),

                        new WebScenario<>(
                                "paginazione: 10 righe per pagina",
                                page -> {},
                                page -> {
                                    page.rowsPerPage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(String.valueOf(ROWS_PER_PAGE)));
                                    Assertions.assertThat(page.institutionsTable().tBody().rows()).as("righe della prima pagina")
                                            .hasSizeLessThanOrEqualTo(ROWS_PER_PAGE);
                                }
                        )
                ));
    }

    // riga della tabella degli enti: nome, codice fiscale, stato della delega con la data
    private record InstitutionRow(String name, String taxCode, String delegation) {

        static InstitutionRow of(TableRow row) {
            List<TableCell> cells = row.cells();
            return new InstitutionRow(cells.get(0).read(), cells.get(1).read(), cells.get(2).read());
        }

        // la data è quella dell'ultimo cambio di stato, in ora italiana
        static InstitutionRow of(PtInstitution institution) {
            String date = institution.statusChangedAt().atZoneSameInstant(ROME).format(DATE);
            return new InstitutionRow(institution.name(), institution.taxCode(),
                    DELEGATION_STATUS.get(institution.status()) + " " + date);
        }
    }
}
