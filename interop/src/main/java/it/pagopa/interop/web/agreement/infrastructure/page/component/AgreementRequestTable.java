package it.pagopa.interop.web.agreement.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.PageSize;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.Optional;

@XPath(".//table[contains(@class, 'MuiTable-root')]")
public interface AgreementRequestTable extends Table {

    @XPath(".//tr/td//div[contains(@class, 'MuiAlert-root')]")
    Optional<Alert> noResultsAlert();

    PageSize pageSize();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(getColumnHeaders())
                        .as("Intestazioni della tabella")
                        .contains("E-service", "Erogatore", "Stato richiesta");
    }
}

