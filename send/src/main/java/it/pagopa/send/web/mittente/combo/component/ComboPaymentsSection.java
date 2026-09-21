package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

// TODO: Confirm locator against FE implementation
@XPath(".//*[@data-testid='combo-payments-section']")
public interface ComboPaymentsSection extends Component {

    @XPath(".//h2[contains(text(), 'Pagamenti')]")
    Readable<String> header();

    @XPath(".//div[@data-testid='payment-item']")
    List<Readable<String>> paymentList();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Pagamenti").isNotNull());
        softly.assertThat(paymentList()).as("Elenco bollettini PagoPA").isNotNull();
        softly.assertAll();
    }
}
