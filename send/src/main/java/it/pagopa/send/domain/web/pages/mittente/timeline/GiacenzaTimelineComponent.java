package it.pagopa.send.domain.web.pages.mittente.timeline;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;

import java.util.List;

@XPath(".//span[normalize-space()='Consegnata o in giacenza']/ancestor::div[contains(@class,'MuiPaper-root')][1]")
public interface GiacenzaTimelineComponent extends Component {

    @XPath(".//span[normalize-space()='Consegnata o in giacenza']")
    List<Readable<String>> title();

    @XPath(".//p[1]")
    Readable<String> subtitle();

    @XPath(".//span[normalize-space()='Consegnata o in giacenza']/ancestor::div[contains(@class,'MuiPaper-root')]//p[1]/following::p")
    List<Readable<String>> recipients();

    List<Button> legalFactUrls();








    @Override
    default void assertLoaded() {
    }

    default void verificyRecipients(List<String> expectedRecipients) {
        List<String> actualRecipients = recipients().stream()
                .map(Readable::read)
                .toList();

        if (!actualRecipients.equals(expectedRecipients)) {
            throw new AssertionError("Expected recipients: " + expectedRecipients + ", but found: " + actualRecipients);
        }
    }
}
