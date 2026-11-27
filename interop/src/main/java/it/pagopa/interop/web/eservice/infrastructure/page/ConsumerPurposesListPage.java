package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Url("${interop.web.base-url}/fruizione/finalita")
public interface ConsumerPurposesListPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label pageDescription();

    @XPath(".//button[normalize-space()='Crea nuovo']")
    Button createPurposeButton();

    @XPath(".//label[normalize-space()='Cerca per nome finalità']")
    Label purposeNameFilterLabel();

    @XPath(".//input[@name='q']")
    TextField purposeNameFilterInput();

    @XPath(".//button[.//*[local-name()='svg' and @data-testid='SearchIcon' and @aria-label='Filtra']]")
    Button purposeNameFilterSearchButton();

    @XPath(".//label[normalize-space()='Cerca per e-service']")
    Label eServiceFilterLabel();

    @XPath(".//label[normalize-space()='Cerca per e-service']/following::input[@role='combobox'][1]")
    TextField eServiceFilterInput();

    @XPath(".//label[normalize-space()='Cerca per erogatore']")
    Label providerFilterLabel();

    @XPath(".//label[normalize-space()='Cerca per erogatore']/following::input[@role='combobox'][1]")
    TextField providerFilterInput();

    @XPath(".//label[normalize-space()='Stato della finalità']")
    Label statusFilterLabel();

    @XPath(".//label[normalize-space()='Stato della finalità']/following::input[@role='combobox'][1]")
    TextField statusFilterInput();

    @XPath(".//table")
    Table purposesTable();

    @XPath(".//table//tbody/tr[contains(@class, 'MuiTableRow-root')]")
    List<PurposeRow> purposeRows();

    @XPath(".//table//tbody/tr[contains(@class, 'MuiTableRow-root')]")
    Component purposeRowElements();

    @XPath("//ul[@role='menu']")
    ActionsMenu actionsMenu();

    @XPath("//ul[@role='menu']//li[@role='menuitem']")
    List<Readable<String>> actionsMenuItems();

    @XPath("//ul[@role='menu']//li[@role='menuitem' and normalize-space()='Duplica']")
    Component duplicateMenuItem();

    @XPath(".//nav[@aria-label='pagination navigation']")
    Pagination pagination();

    PageSize rowsPerPageSelector();

    default PurposeRow findPurposeRow(String title, UUID purposeId) {
        purposeNameFilterInput().fill(title);
        PollingUtils.pollUntil(
                () -> purposeNameFilterSearchButton().exists(FindPolicy.CLICKABLE),
                clickable -> clickable,
                Duration.ofSeconds(30),
                Duration.ofSeconds(1),
                "Purpose search button did not become clickable"
        );
        purposeNameFilterSearchButton().click();

        List<PurposeRow> rows = PollingUtils.pollUntil(
                () -> !purposeRowElements().exists(FindPolicy.PRESENT) ? List.of() : purposeRows().stream()
                        .filter(row -> row.cells().get(0).read().equals(title))
                        .filter(row -> row.purposeLink().exists(FindPolicy.PRESENT)
                                && row.purposeLink().get(FindPolicy.PRESENT)
                                .map(link -> link.getAttributes().get("href"))
                                .map(URI::create)
                                .map(URI::getPath)
                                .map(path -> Arrays.asList(path.split("/")).contains(purposeId.toString()))
                                .orElse(false))
                        .toList(),
                matchingRows -> matchingRows.size() == 1,
                Duration.ofSeconds(30),
                Duration.ofSeconds(1),
                "Purpose row did not load for purpose " + purposeId
        );
        return rows.get(0);
    }

    interface ActionsMenu extends Component, Writable<String> {
    }

    interface PurposeRow extends TableRow {

        @XPath("./td[4]")
        Readable<String> status();

        @XPath("./td[5]//a[@aria-label='Visualizza' or @aria-label='Modifica']")
        Link purposeLink();

        @XPath("./td[5]//button[@aria-label='Apri menù delle azioni' and .//*[local-name()='svg' and @data-testid='MoreVertIcon']]")
        Button actionsButton();

        @XPath("./td[5]//a[normalize-space()='Visualizza']")
        Link viewAction();
    }

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Finalità inoltrate");

        Assertions.assertThat(purposesTable().get())
                .as("Purposes table should be present")
                .isPresent();

        pageTitle().readAndAssert("Le mie finalità inoltrate");
    }
}