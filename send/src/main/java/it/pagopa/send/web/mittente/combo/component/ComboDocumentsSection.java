package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//div[contains(@class, 'MuiPaper-root')][.//h2[@id='notification-detail-document-attached'] or .//h2[contains(., 'Documenti')] or .//h5[contains(., 'Documenti')]]")
public interface ComboDocumentsSection extends Component {

    @XPath(".//*[self::h2 or self::h5]")
    Readable<String> header();

    @XPath(".//div[@data-testid='notificationDetailDocuments']//button | .//button[@data-testid='documentButton' or contains(@data-testid, 'document')]")
    List<Readable<String>> attachmentList();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        header().readAndAssert(h -> softly.assertThat(h).as("Header Sezione Documenti").isNotNull());
        softly.assertThat(attachmentList()).as("Elenco allegati documento").isNotNull();
        softly.assertAll();
    }
}

