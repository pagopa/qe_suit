package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Menu;
import it.pagopa.infrastructure.suit.component.PageSize;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Url("${interop.web.base-url}/erogazione/richieste")
public interface AgreementProducerPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following::p[1]")
    Label pageDescription();

    @XPath(".//label[normalize-space()='Cerca per e-service']")
    Label eServiceFilterLabel();

    @XPath(".//label[normalize-space()='Cerca per e-service']/following::input[@role='combobox'][1]")
    TextField eServiceFilterInput();

    @XPath(".//label[normalize-space()='Cerca per fruitore']")
    Label consumerFilterLabel();

    @XPath(".//label[normalize-space()='Cerca per fruitore']/following::input[@role='combobox'][1]")
    TextField consumerFilterInput();

    @XPath(".//label[normalize-space()='Stato della richiesta']")
    Label statusFilterLabel();

    @XPath(".//label[normalize-space()='Stato della richiesta']/following::input[@role='combobox'][1]")
    TextField statusFilterInput();

    @XPath(".//table[contains(@class, 'MuiTable-root')]")
    Table agreementsTable();

    @XPath(".//tbody[contains(@class, 'MuiTableBody-root')]//tr[contains(@class, 'MuiTableRow-root')]")
    List<AgreementRow> agreementRows();

    @XPath(".//nav[contains(@class, 'MuiPagination-root')]")
    Pagination pagination();

    PageSize rowsPerPageSelector();

    @XPath("//ul[@role='listbox']/li[@role='option']")
    List<Label> autocompleteOptions();

    default AgreementRow findAgreementRow(String eserviceName, String version) {
        String expectedLabel = eserviceName + ", v. " + version;
        eServiceFilterInput().write(eserviceName);
        DelayUtils.waitFor(Duration.ofSeconds(5));
        eServiceFilterInput().write("" + Keys.ARROW_DOWN + Keys.ENTER);
        return PollingUtils.pollUntil(
                () -> agreementRows().stream()
                        .filter(row -> !row.cells().isEmpty()
                                && row.cells().get(0).read().trim().equals(expectedLabel))
                        .findFirst(),
                Optional::isPresent,
                Duration.ofSeconds(15),
                Duration.ofMillis(500),
                "Agreement row not found for e-service: " + expectedLabel
        ).orElseThrow();
    }

    @Override
    default void assertLoaded() {
        List<String> breadcrumbItems = PollingUtils.pollUntil(
                () -> breadcrumbs().items().stream().map(Readable::read).toList(),
                items -> items.equals(List.of("Erogazione", "Richieste ricevute")),
                Duration.ofSeconds(15),
                Duration.ofMillis(500),
                "Breadcrumbs not loaded for AgreementProducerPage"
        );
        Assertions.assertThat(breadcrumbItems)
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "Richieste ricevute");

        pageTitle().readAndAssert("Richieste di fruizione ricevute");

        Assertions.assertThat(agreementsTable().get())
                .as("Agreements table should be present")
                .isPresent();
    }

    @XPath(".//tr[contains(@class, 'MuiTableRow-root')]")
    interface AgreementRow extends TableRow {

        Chip statusChip();

        @XPath(".//a[normalize-space()='Visualizza']")
        Link inspectLink();

        @XPath(".//button[@aria-haspopup='true' or @aria-label]")
        Button actionMenuButton();

        @XPath("//ul[@role='menu']")
        Menu actionMenu();
    }
}

