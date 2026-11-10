package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.Tabs;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.TableCell;
import it.pagopa.utils.async.PollingUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the risk-analysis workspace and its unauthorized-access view.
 */
@Url("${interop.web.base-url}/analisi-del-rischio")
public interface RiskAnalysisPage extends Page {

    String PAGE_TITLE = "Analisi del rischio";

    @XPath("//h1")
    Label title();

    @XPath("//h1/following-sibling::*[1]")
    Label subtitle();

    @XPath("//*[@role='tablist']")
    Tabs tabs();

    @XPath("//button[@role='tab' and normalize-space()='Da fare']")
    Button todoTab();

    @XPath("//button[@role='tab' and normalize-space()='Concluse']")
    Button completedTab();

    @XPath("//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Cerca per e-service']]//input[@role='combobox']")
    TextField eserviceFilter();

    @XPath("//div[contains(@class, 'MuiInputBase-root')][.//fieldset/legend/span[normalize-space()='Stato analisi del rischio']]//input[@role='combobox']")
    TextField statusFilter();

    @XPath("//table")
    Table riskAnalysisTable();

    @XPath("//nav[contains(@class, 'MuiPagination-root')]")
    Pagination pagination();

    /**
     * Reads cell snapshots across every page of the active tab, leaving the table on its last page.
     */
    default List<List<String>> readAllRows() {
        List<List<String>> rows = readCurrentRows();
        if (pagination().get(FindPolicy.PRESENT).isEmpty()) {
            return rows;
        }

        while (pagination().hasPrevious()) {
            pagination().goBack();
            rows = awaitChangedRows(rows);
        }

        List<List<String>> allRows = new ArrayList<>(rows);
        while (pagination().hasNext()) {
            pagination().goNext();
            rows = awaitChangedRows(rows);
            allRows.addAll(rows);
        }
        return List.copyOf(allRows);
    }

    private List<List<String>> readCurrentRows() {
        return riskAnalysisTable().tBody().rows().stream()
                .map(row -> row.cells().stream().map(TableCell::read).toList())
                .toList();
    }

    private List<List<String>> awaitChangedRows(List<List<String>> previousRows) {
        return PollingUtils.pollUntil(
                this::readCurrentRows,
                rows -> !rows.isEmpty() && !rows.equals(previousRows),
                "Risk analysis table did not update after changing page"
        );
    }

    /** Returns the assignment date in the first table row. */
    @XPath("//table/tbody/tr[1]/td[1]")
    Label firstRowDate();

    /** Returns the e-service name in the first table row. */
    @XPath("//table/tbody/tr[1]/td[2]")
    Label firstRowEservice();

    /** Returns the status chip text in the first table row. */
    @XPath("//table/tbody/tr[1]/td[5]//span[contains(@class, 'MuiChip-label')]")
    Label firstRowStatus();

    @XPath("//h1")
    Label unauthorizedTitle();

    @XPath("//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath("//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

    /**
     * Waits until the page title is non-blank, which is common to both the
     * authorized workspace and the unauthorized-access view.
     */
    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Risk Analysis page is not loaded or its title is absent"
        );
    }
}