package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

@XPath(".//*[contains(@class, 'MuiTimelineItem-root') or contains(@class, 'timeline-item')]")
public interface ComboTimelineItemComponent extends Component {

    @XPath(".//span[contains(@class, 'MuiTimelineContent') or contains(@class, 'item-title')] | .//div[contains(@class, 'MuiStack-root')]/span[1] | .//span[@data-testid='timeline-item-title']")
    Readable<String> title();

    @XPath(".//span[contains(@class, 'MuiTimelineOppositeContent-root') or contains(@class, 'timestamp')] | .//p[contains(@class, 'timestamp')] | .//p[@data-testid='timeline-item-timestamp']")
    Readable<String> timestamp();

    @XPath(".//div[contains(@class, 'box-green') or contains(@class, 'status-success') or contains(@class, 'MuiAlert-colorSuccess') or contains(@class, 'MuiChip-colorSuccess')]")
    Readable<String> greenBoxSuccess();

    @XPath(".//div[contains(@class, 'box-red') or contains(@class, 'status-error') or contains(@class, 'MuiAlert-colorError') or contains(@class, 'MuiChip-colorError')]")
    Readable<String> redBoxFailure();

    default boolean isGreenBoxPresent() {
        return greenBoxSuccess().read() != null;
    }

    default boolean isRedBoxPresent() {
        return redBoxFailure().read() != null;
    }

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo Card Timeline").isNotNull());
        softly.assertAll();
    }
}
