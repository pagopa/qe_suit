package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

@XPath(".//li[contains(@class, 'MuiTimelineItem-root')] | .//div[contains(@class, 'MuiTimelineItem-root')]")
public interface ComboTimelineItemComponent extends Component {

    @XPath(".//div[contains(@class, 'MuiStack-root')][1]/span[last()] | .//span[contains(@class, 'MuiTimelineContent') or contains(@class, 'item-title')] | .//span[@data-testid='timeline-item-title'] | .//p[contains(@class, 'MuiTypography-root')]")
    Readable<String> title();

    @XPath(".//span[contains(@class, 'MuiTimelineOppositeContent-root') or contains(@class, 'timestamp')] | .//p[contains(@class, 'timestamp')] | .//p[@data-testid='timeline-item-timestamp'] | .//span[contains(@class, 'MuiTypography-caption')]")
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
