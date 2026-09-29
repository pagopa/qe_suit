package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//*[@data-testid='NotificationEventsTimeline'] | //ul[contains(@class, 'MuiTimeline-root')] | //div[contains(@class, 'MuiTimeline-root')]")
public interface ComboTimelineComponent extends Component {

    @XPath(".//li[contains(@class, 'MuiTimelineItem-root')] | .//div[contains(@class, 'MuiTimelineItem-root')]")
    List<ComboTimelineItemComponent> items();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(items()).as("Nessun elemento presente nella timeline combo").isNotEmpty();
        softly.assertAll();
    }
}
