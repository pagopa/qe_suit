package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//div[@data-testid='paymentInfoBox' or (contains(@class, 'MuiPaper-root') and (.//h2[contains(., 'Pagamenti')] or .//h5[contains(., 'Pagamenti')]))]")
public interface ComboPaymentsSection extends Component {

    @XPath(".//*[self::h2 or self::h5][contains(., 'Pagamenti')]")
    Readable<String> header();

    @XPath(".//*[@data-testid='payment-item' or contains(@class, 'payment-item') or contains(@class, 'MuiCard-root')]")
    List<Component> paymentList();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Pagamenti").isNotNull());
        softly.assertThat(paymentList()).as("Elenco bollettini PagoPA").isNotNull();
        softly.assertAll();
    }
}

