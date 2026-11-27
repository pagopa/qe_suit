package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.PageSize;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Url("${interop.web.base-url}/erogazione/portachiavi")
public interface ProducerKeychainListPage extends Page {

    String EXPECTED_TITLE = "I miei portachiavi erogatore";

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Label pageDescription();

    @XPath(".//button[normalize-space()='Crea nuovo']")
    Button createNewButton();

    @XPath(".//label[normalize-space()='Cerca per nome']")
    Label nameFilterLabel();

    @XPath(".//input[@name='q']")
    TextField nameFilterInput();

    @XPath(".//table[contains(@class, 'MuiTable-root')]")
    Table keychainsTable();

    @XPath(".//table[contains(@class, 'MuiTable-root')]//tbody[contains(@class, 'MuiTableBody-root')]//tr[contains(@class, 'MuiTableRow-root')]")
    List<KeychainRow> keychainRows();

    PageSize rowsPerPageSelector();

    @XPath(".//nav[@aria-label='pagination navigation']")
    Pagination pagination();

    @XPath("//ul[@role='menu']//li[contains(@class, 'MuiMenuItem-root') and normalize-space()='Elimina']")
    Button deleteMenuItem();

    @XPath("//ul[@role='menu']")
    Writable<String> openActionMenu();

    @XPath(".//tr[contains(@class, 'MuiTableRow-root')]")
    interface KeychainRow extends TableRow {

        @XPath(".//*[(self::a or self::button) and normalize-space()='Visualizza']")
        Button inspectButton();

        @XPath(".//button[contains(@class, 'MuiIconButton-root')]")
        Button actionMenuButton();
    }

    default Optional<KeychainRow> findVisibleKeychainRowByName(String keychainName) {
        Objects.requireNonNull(keychainName, "keychainName must not be null");
        return keychainRows().stream()
                .filter(row -> !row.cells().isEmpty()
                        && keychainName.equals(row.cells().get(0).read().trim()))
                .findFirst();
    }

    default Optional<KeychainRow> findKeychainRowByName(String keychainName) {
        Objects.requireNonNull(keychainName, "keychainName must not be null");
        Optional<KeychainRow> visibleRow = findVisibleKeychainRowByName(keychainName);
        if (visibleRow.isPresent()) {
            return visibleRow;
        }

        nameFilterInput().fill(keychainName);
        nameFilterInput().write(Keys.ENTER.toString());

        try {
            return PollingUtils.pollUntil(
                    () -> findVisibleKeychainRowByName(keychainName),
                    Optional::isPresent,
                    Duration.ofSeconds(15),
                    Duration.ofMillis(500),
                    "Producer keychain row '%s' not found after filtering".formatted(keychainName)
            );
        } catch (IllegalStateException ignored) {
            return Optional.empty();
        }
    }

    default void closeActionMenu() {
        openActionMenu().write(Keys.ESCAPE.toString());
        PollingUtils.pollUntil(
                () -> !deleteMenuItem().exists(Gettable.FindPolicy.VISIBLE),
                Boolean::booleanValue,
                Duration.ofSeconds(5),
                Duration.ofMillis(250),
                "Action menu did not close after pressing Escape"
        );
    }

    @Override
    default void assertLoaded() {
        Assertions.assertThat(keychainsTable().get())
                .as("Producer keychains table should be present")
                .isPresent();

        pageTitle().readAndAssert(EXPECTED_TITLE);
    }
}

