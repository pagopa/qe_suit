package it.pagopa.send.web.mittente.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath(".//li[contains(@class, 'MuiTimelineItem-root')]")
public interface ComboTimelineItemComponent extends Component, Readable<String> {

    @XPath(".//div[contains(@class, 'MuiPaper-root')]/div[1]/p")
    Readable<String> title();

    @XPath(".//div[contains(@class, 'MuiPaper-root')]/div[2]/p")
    Label description();

    @XPath(".//div[contains(@class, 'MuiPaper-root')]/div[2]/p/*[@data-testid='dateItem']")
    Label descriptionDate();

    @XPath(".//div[contains(@class, 'MuiStack-root')][./ul[@data-testid='timeline-group-body']]")
    List<ChannelStepComponent> channelSteps();

    @XPath(".//ul[@data-testid='timeline-group-body']")
    Label channelEvents();

    // canali della card, lista vuota se non ne ha: exists() legge il DOM senza attendere il timeout
    default List<ChannelStepComponent> channelStepsOrEmpty() {
        return channelEvents().exists() ? channelSteps() : List.of();
    }

    @Override
    default void assertLoaded() {
        SoftAssertions softly = new SoftAssertions();
        title().readAndAssert(t -> softly.assertThat(t).as("Titolo Card Timeline").isNotNull());
        softly.assertAll();
    }

    @XPath(".")
    interface ChannelStepComponent extends Component {
        @XPath("./div//p")
        Readable<String> name();

        @XPath("./ul[@data-testid='timeline-group-body']/li")
        List<EventComponent> events();
    }

    @XPath(".")
    interface EventComponent extends Component {
        @XPath("./span[contains(@class, 'MuiTypography-root')]")
        Readable<String> text();

        @XPath("./span[@data-testid='dateItem']")
        Readable<String> date();

        @XPath("./span[contains(@class, 'MuiBox-root') and not(@data-testid)]")
        Label tag();
    }
}
