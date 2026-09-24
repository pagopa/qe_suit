package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath(".//div[contains(@class, 'MuiTimeline-root') or contains(@class, 'NotificationTimelineBox') or @id='timeline-container']")
public interface ComboTimelineComponent extends Component {

    @XPath(".//*[contains(@class, 'MuiTimelineItem-root') or contains(@class, 'timeline-item')]")
    List<ComboTimelineItemComponent> items();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(items()).as("Nessun elemento presente nella timeline combo").isNotEmpty();
        softly.assertAll();
    }
}
