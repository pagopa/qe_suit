package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath(".//div[contains(@class, 'MuiPaper-root')][.//h5[contains(text(), 'Avviso di pagamento') or contains(text(), 'Pagamenti')]]")
public interface ComboPaymentsSection extends Component {

    @XPath(".//h5 | .//h2")
    Readable<String> header();

    @XPath(".//div[contains(@class, 'MuiCard-root') or contains(@class, 'payment-item')]")
    List<Readable<String>> paymentList();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Pagamenti").isNotNull());
        softly.assertThat(paymentList()).as("Elenco bollettini PagoPA").isNotNull();
        softly.assertAll();
    }
}
