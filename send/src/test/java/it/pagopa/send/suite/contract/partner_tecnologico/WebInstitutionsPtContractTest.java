package it.pagopa.send.suite.contract.partner_tecnologico;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.TableCell;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
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

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina "I tuoi enti" del BackOffice dei Partner Tecnologici ({@code {baseUrl}}), la home su cui
 * atterra un ente PT, aperta con la sessione del PT {@code PT_1}.
 * <p>
 * Si verificano titolo, sottotitolo, avviso sullo stato del portale, ente e ruolo nell'header, filtro e tabella degli
 * enti deleganti. I testi di filtro e tabella vengono dal sorgente di pn-pt-webapp (PN-21981); la paginazione non è
 * verificata.
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
    private static final String ADMIN_ROLE = "Amministratore";
    private static final String WORK_IN_PROGRESS = "Il portale è in fase di sviluppo, al momento non è possibile gestire SEND per conto dei tuoi enti da qui.";
    private static final String SEARCH_LABEL = "Cerca per Nome o Codice Fiscale";
    private static final String SEARCH = "Cerca";
    private static final String[] TABLE_HEADERS = {"Nome dell’ente", "Codice Fiscale", "Delega"};
    // "Attiva dal" o "Revocata il" seguito dalla data dell'ultimo cambio di stato
    private static final String DELEGATION_STATUS = "(Attiva dal|Revocata il) \\d{2}/\\d{2}/\\d{4}";

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

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
                                    page.header().partyRole().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADMIN_ROLE));
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
     *     <li>per ogni ente, nome, codice fiscale e stato della delega con la data.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowSearchAndInstitutionsTable() {
        return webContractValidator.as(userProvider.resolve(UserAlias.PT_1))
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
                                "righe degli enti: nome, codice fiscale e stato della delega",
                                page -> {},
                                page -> {
                                    List<TableRow> rows = page.institutionsTable().tBody().rows();
                                    Assertions.assertThat(rows).as("enti deleganti").isNotEmpty();
                                    rows.forEach(row -> {
                                        List<TableCell> cells = row.cells();
                                        Assertions.assertThat(cells.get(0).read()).as("nome dell'ente").isNotBlank();
                                        Assertions.assertThat(cells.get(1).read()).as("codice fiscale").isNotBlank();
                                        Assertions.assertThat(cells.get(2).read()).as("delega").matches(DELEGATION_STATUS);
                                    });
                                }
                        )
                ));
    }
}
