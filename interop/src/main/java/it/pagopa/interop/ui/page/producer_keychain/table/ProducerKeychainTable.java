package it.pagopa.interop.ui.page.producer_keychain.table;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.component.Alert;
import it.pagopa.interop.ui.component.PageSize;
import it.pagopa.interop.ui.component.Pagination;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.Optional;

@XPath(".//table[contains(@class, 'MuiTable-root')]")
public interface ProducerKeychainTable extends Component {

    @XPath(".//th[text()='Portachiavi']")
    Readable<String> header();

    List<ProducerKeychainRow> rows();

    @XPath(".//tr/td//div[contains(@class, 'MuiAlert-root')]")
    Optional<Alert> noKeychainAlert();

    PageSize pageSize();

    Pagination pagination();

    default void deleteAll() {
        if(noKeychainAlert().isPresent())
            return;

        while (!rows().isEmpty())
            rows().get(0).delete();
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            // Verifica la presenza dell'intestazione della tabella
            softly.assertThat(header().read())
                    .isEqualTo("Portachiavi");

            // Verifica la presenza del componente di page size
            softly.assertThat(pageSize().get())
                    .isPresent();

            // Verifica condizionale del body della table
            if (rows().isEmpty()) {
                softly.assertThat(noKeychainAlert().isPresent());

                softly.assertThat(noKeychainAlert().get().isInfo())
                        .isTrue();

                softly.assertThat(noKeychainAlert().get().message().read())
                        .isEqualTo("Non ci sono portachiavi disponibili");
            }
        });
    }
}
