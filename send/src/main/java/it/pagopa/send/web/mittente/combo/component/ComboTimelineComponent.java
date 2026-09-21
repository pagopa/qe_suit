package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

// TODO: Confirm locator against FE implementation
@XPath(".//*[@data-testid='combo-timeline-container']")
public interface ComboTimelineComponent extends Component {

    @XPath(".//*[contains(@class, 'MuiTimelineItem-root') or @data-testid='combo-timeline-item']")
    List<ComboTimelineItemComponent> items();

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(items()).as("Nessun elemento presente nella timeline combo").isNotEmpty();
        softly.assertAll();
    }
}
