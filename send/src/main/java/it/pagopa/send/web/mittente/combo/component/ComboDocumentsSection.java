package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

// TODO: Confirm locator against FE implementation
@XPath(".//*[@data-testid='combo-documents-section']")
public interface ComboDocumentsSection extends Component {

    @XPath(".//h2[contains(text(), 'Documenti')]")
    Readable<String> header();

    @XPath(".//div[@data-testid='attachment-item']")
    List<Readable<String>> attachmentList();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Documenti").isNotNull());
        softly.assertThat(attachmentList()).as("Elenco allegati documento").isNotNull();
        softly.assertAll();
    }
}
