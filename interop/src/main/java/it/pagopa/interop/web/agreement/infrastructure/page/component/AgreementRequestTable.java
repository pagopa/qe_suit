package it.pagopa.interop.web.agreement.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.PageSize;
import it.pagopa.infrastructure.suit.component.Pagination;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.Optional;

@XPath(".//table[contains(@class, 'MuiTable-root')]")
public interface AgreementRequestTable extends Component {

    List<AgreementRequestRow> rows();

    @XPath(".//tr/td//div[contains(@class, 'MuiAlert-root')]")
    Optional<Alert> noResultsAlert();

    PageSize pageSize();

    Pagination pagination();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly ->
                softly.assertThat(!rows().isEmpty() || noResultsAlert().isPresent())
                        .as("La tabella deve mostrare righe oppure lo stato 'nessun risultato'")
                        .isTrue()
        );
    }
}

