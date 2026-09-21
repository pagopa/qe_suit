package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

// TODO: Confirm locator against FE implementation
@XPath(".//*[contains(@class, 'MuiTimelineItem-root') or @data-testid='combo-timeline-item']")
public interface ComboTimelineItemComponent extends Component {

    @XPath(".//span[@data-testid='timeline-item-title'] | .//div[contains(@class,'MuiStack-root')][1]/span[last()]")
    Readable<String> title();

    @XPath(".//p[@data-testid='timeline-item-timestamp'] | .//span[@data-testid='timeline-item-timestamp']")
    Readable<String> timestamp();

    @XPath(".//div[contains(@class, 'box-green') or contains(@class, 'status-success') or contains(@class, 'MuiChip-colorSuccess')]")
    Readable<String> greenBoxSuccess();

    @XPath(".//div[contains(@class, 'box-red') or contains(@class, 'status-error') or contains(@class, 'MuiChip-colorError')]")
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
