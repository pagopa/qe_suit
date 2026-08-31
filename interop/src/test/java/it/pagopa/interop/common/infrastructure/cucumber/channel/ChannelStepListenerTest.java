package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.plugin.event.*;
import it.pagopa.interop.common.infrastructure.context.cucumber.ChannelContext;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChannelStepListenerTest {

    private ScenarioChannelContext scenarioChannelContext;
    private ChannelContext channelContext;
    private ChannelStepListener listener;
    private CapturingEventPublisher publisher;

    @BeforeEach
    void setUp() {
        scenarioChannelContext = new ScenarioChannelContext();
        channelContext = new ChannelContext();
        listener = new ChannelStepListener(
                () -> scenarioChannelContext,
                () -> channelContext);
        publisher = new CapturingEventPublisher();
        listener.setEventPublisher(publisher);

        // Default config: Given=BFF, When=WEB, Then=WEB
        scenarioChannelContext.setConfig(
                new ChannelConfig(Channel.BFF, Channel.WEB_BROWSER, Channel.WEB_BROWSER));
    }

    // ------------------------------------------------------------------
    // Given / When / Then routing
    // ------------------------------------------------------------------

    @Test
    void givenStep_setsBff() {
        fireStep("Given ", "step text");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    @Test
    void whenStep_setsWebBrowser() {
        fireStep("When ", "step text");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void thenStep_setsWebBrowser() {
        fireStep("Then ", "step text");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    // ------------------------------------------------------------------
    // And / But inherit previous semantic type
    // ------------------------------------------------------------------

    @Test
    void andAfterGiven_keepsBff() {
        TestCase tc = mockTestCase(UUID.randomUUID());
        fireStep(tc, "Given ", "first");
        fireStep(tc, "And ", "second");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    @Test
    void andAfterWhen_keepsWebBrowser() {
        TestCase tc = mockTestCase(UUID.randomUUID());
        fireStep(tc, "When ", "first");
        fireStep(tc, "And ", "second");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    @Test
    void sequence_givenAndWhenAndThenAnd() {
        UUID id = UUID.randomUUID();
        TestCase tc = mockTestCase(id);

        fireStep(tc, "Given ", "g1");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);

        fireStep(tc, "And ", "g2");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);

        fireStep(tc, "When ", "w1");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        fireStep(tc, "And ", "w2");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        fireStep(tc, "Then ", "t1");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        fireStep(tc, "And ", "t2");
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    // ------------------------------------------------------------------
    // No config (legacy) – channel unchanged
    // ------------------------------------------------------------------

    @Test
    void noConfig_channelNotChanged() {
        scenarioChannelContext.setConfig(null);
        channelContext.setCurrentChannel(Channel.BFF);

        fireStep("Given ", "step");

        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    // ------------------------------------------------------------------
    // Hooks are ignored
    // ------------------------------------------------------------------

    @Test
    void hookStep_doesNotChangeChannel() {
        channelContext.setCurrentChannel(Channel.BFF);
        fireHook(HookType.BEFORE);
        assertThat(channelContext.getCurrentChannel()).isEqualTo(Channel.BFF);
    }

    // ------------------------------------------------------------------
    // Scenario isolation: two separate test cases
    // ------------------------------------------------------------------

    @Test
    void twoScenarios_independentSemanticState() {
        ScenarioChannelContext ctx1 = new ScenarioChannelContext();
        ctx1.setConfig(new ChannelConfig(Channel.BFF, Channel.WEB_BROWSER, Channel.WEB_BROWSER));
        ChannelContext ch1 = new ChannelContext();

        ScenarioChannelContext ctx2 = new ScenarioChannelContext();
        ctx2.setConfig(new ChannelConfig(Channel.WEB_BROWSER, Channel.BFF, Channel.BFF));
        ChannelContext ch2 = new ChannelContext();

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        TestCase tc1 = mockTestCase(id1);
        TestCase tc2 = mockTestCase(id2);

        // Two listeners simulating two concurrent scenarios
        ChannelStepListener l1 = new ChannelStepListener(() -> ctx1, () -> ch1);
        ChannelStepListener l2 = new ChannelStepListener(() -> ctx2, () -> ch2);
        CapturingEventPublisher p1 = new CapturingEventPublisher();
        CapturingEventPublisher p2 = new CapturingEventPublisher();
        l1.setEventPublisher(p1);
        l2.setEventPublisher(p2);

        // Scenario 1: Given step → BFF
        p1.fire(new TestStepStarted(Instant.now(), tc1, pickleStep(tc1, "When ")));
        assertThat(ch1.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);

        // Scenario 2: Then step → BFF (config2.then = BFF)
        p2.fire(new TestStepStarted(Instant.now(), tc2, pickleStep(tc2, "Then ")));
        assertThat(ch2.getCurrentChannel()).isEqualTo(Channel.BFF);

        // Scenario 1 channel unaffected by scenario 2
        assertThat(ch1.getCurrentChannel()).isEqualTo(Channel.WEB_BROWSER);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void fireStep(String keyword, String text) {
        TestCase tc = mockTestCase(UUID.randomUUID());
        fireStep(tc, keyword, text);
    }

    private void fireStep(TestCase tc, String keyword, String text) {
        publisher.fire(new TestStepStarted(Instant.now(), tc, pickleStep(tc, keyword, text)));
    }

    private void fireHook(HookType hookType) {
        TestCase tc = mockTestCase(UUID.randomUUID());
        HookTestStep hookStep = mock(HookTestStep.class);
        when(hookStep.getHookType()).thenReturn(hookType);
        publisher.fire(new TestStepStarted(Instant.now(), tc, hookStep));
    }

    private TestCase mockTestCase(UUID id) {
        TestCase tc = mock(TestCase.class);
        when(tc.getId()).thenReturn(id);
        return tc;
    }

    private PickleStepTestStep pickleStep(TestCase tc, String keyword) {
        return pickleStep(tc, keyword, "step");
    }

    private PickleStepTestStep pickleStep(TestCase tc, String keyword, String text) {
        Step step = mock(Step.class);
        when(step.getKeyword()).thenReturn(keyword);
        when(step.getText()).thenReturn(text);

        PickleStepTestStep testStep = mock(PickleStepTestStep.class);
        when(testStep.getStep()).thenReturn(step);
        return testStep;
    }

    // -------------------------------------------------------------------------
    // Minimal event publisher for testing
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static class CapturingEventPublisher implements EventPublisher {
        private final java.util.Map<Class<?>, EventHandler<?>> handlers = new java.util.HashMap<>();

        @Override
        public <T> void registerHandlerFor(Class<T> eventType, EventHandler<T> handler) {
            handlers.put(eventType, handler);
        }

        @Override
        public <T> void removeHandlerFor(Class<T> eventType, EventHandler<T> handler) {
            handlers.remove(eventType);
        }

        @SuppressWarnings("unchecked")
        <T> void fire(T event) {
            EventHandler<T> handler = (EventHandler<T>) handlers.get(event.getClass());
            if (handler != null) handler.receive(event);
        }
    }
}
