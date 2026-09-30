package it.pagopa.send.web.mittente.combo.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

@XPath(".//li[contains(@class, 'MuiTimelineItem-root')]")
public interface ComboTimelineItemComponent extends Component, Readable<String> {

    @XPath(".//div[contains(@class, 'MuiTimelineContent-root')]//p[contains(@class, 'MuiTypography-root')][1]")
    Readable<String> title();

    @XPath(".//*[@data-testid='dateItem'] | .//span[contains(@class, 'MuiTimelineOppositeContent-root')]")
    Readable<String> timestamp();

    @XPath(".//div[contains(@class, 'MuiTimelineContent-root')]")
    Readable<String> content();



    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo Card Timeline").isNotNull());
        softly.assertAll();
    }
}


