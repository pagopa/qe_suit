package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.java.Scenario;
import io.cucumber.java.Step;
import it.pagopa.interop.common.infrastructure.context.cucumber.CucumberCurrentChannel;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChannelStepHookTest {

    private ScenarioChannelContext scenarioChannelContext;
    private CucumberCurrentChannel channelContext;
    private ChannelStepHook hook;
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        scenarioChannelContext = new ScenarioChannelContext();
        channelContext = new CucumberCurrentChannel();
        hook = new ChannelStepHook(scenarioChannelContext, channelContext);
        scenario = mock(Scenario.class);

        // Default config: Given=BFF, When=WEB, Then=WEB
        scenarioChannelContext.setConfig(
                new ChannelConfig(Channel.BFF, Channel.WEB_BROWSER, Channel.WEB_BROWSER));
    }

    // ------------------------------------------------------------------
    // Given / When / Then routing
    // ------------------------------------------------------------------

    @Test
    void givenStep_setsBff() {
        hook.beforeStep(scenario, step("Given "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    @Test
    void whenStep_setsWebBrowser() {
        hook.beforeStep(scenario, step("When "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void thenStep_setsWebBrowser() {
        hook.beforeStep(scenario, step("Then "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    // ------------------------------------------------------------------
    // And / But inherit previous semantic type
    // ------------------------------------------------------------------

    @Test
    void andAfterGiven_keepsBff() {
        hook.beforeStep(scenario, step("Given "));
        hook.beforeStep(scenario, step("And "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    @Test
    void andAfterWhen_keepsWebBrowser() {
        hook.beforeStep(scenario, step("When "));
        hook.beforeStep(scenario, step("And "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void andAfterThen_keepsWebBrowser() {
        hook.beforeStep(scenario, step("Then "));
        hook.beforeStep(scenario, step("And "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void sequence_givenAndWhenAndThenAnd() {
        hook.beforeStep(scenario, step("Given "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);

        hook.beforeStep(scenario, step("And "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);

        hook.beforeStep(scenario, step("When "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        hook.beforeStep(scenario, step("And "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        hook.beforeStep(scenario, step("Then "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        hook.beforeStep(scenario, step("And "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    // ------------------------------------------------------------------
    // No config (legacy) – channel unchanged
    // ------------------------------------------------------------------

    @Test
    void noConfig_channelNotChanged() {
        scenarioChannelContext.setConfig(null);
        channelContext.setCurrentChannel(Channel.BFF);

        hook.beforeStep(scenario, step("When "));

        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    // ------------------------------------------------------------------
    // Localized keywords
    // ------------------------------------------------------------------

    @Test
    void italianAllora_setsThenChannel() {
        scenarioChannelContext.setConfig(
                new ChannelConfig(Channel.BFF, Channel.BFF, Channel.WEB_BROWSER));

        hook.beforeStep(scenario, step("Allora "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void italianQuando_setsWhenChannel() {
        hook.beforeStep(scenario, step("Quando "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void italianDato_setsGivenChannel() {
        hook.beforeStep(scenario, step("Dato "));
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Step step(String keyword) {
        Step s = mock(Step.class);
        when(s.getKeyword()).thenReturn(keyword);
        when(s.getText()).thenReturn("step text");
        return s;
    }
}
