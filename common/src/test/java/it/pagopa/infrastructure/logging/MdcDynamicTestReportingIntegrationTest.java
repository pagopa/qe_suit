package it.pagopa.infrastructure.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * End-to-end regression: the factory succeeds, but two generated children fail.
 */
class MdcDynamicTestReportingIntegrationTest {

    @Test
    void reportsEachDynamicChildInsteadOfTheFactoryOutcome() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            var request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(DynamicFixture.class))
                    .build();
            var launcher = LauncherFactory.create();
            var summary = new SummaryGeneratingListener();
            // Register explicitly: do not rely on ServiceLoader behavior in a test runtime.
            launcher.registerTestExecutionListeners(new MdcTestExecutionListener(), summary);
            launcher.execute(request);

            assertThat(summary.getSummary().getTestsFoundCount()).isEqualTo(3);
            assertThat(summary.getSummary().getTestsSucceededCount()).isEqualTo(1);
            assertThat(summary.getSummary().getTestsFailedCount()).isEqualTo(2);
            String logs = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .reduce("", (a, b) -> a + "\n" + b);
            assertThat(logs).contains("[payload] VALID @ /isFreeOfCharge", "Status: PASSED");
            assertThat(logs).contains("[payload] REPLACED_WITH_WRONG_TYPE_NUMBER @ /isFreeOfCharge", "Status: FAILED");
            assertThat(logs).contains("[payload] PREPARATION_CRASH @ /isFreeOfCharge", "Status: FAILED");
            assertThat(appender.list.stream().filter(e -> e.getFormattedMessage().contains("Status: FAILED")).count())
                    .isGreaterThanOrEqualTo(2);
            assertThat(appender.list.stream().anyMatch(e -> e.getThrowableProxy() != null
                    && e.getThrowableProxy().getMessage() != null
                    && e.getThrowableProxy().getMessage().contains("expected 200 but was 400"))).isTrue();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void logsFactoryFailureDuringDiscoveryEvenWithoutDynamicChildren() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            var request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(FailingFactoryFixture.class))
                    .build();
            var launcher = LauncherFactory.create();
            var summary = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(new MdcTestExecutionListener(), summary);
            launcher.execute(request);

            assertThat(summary.getSummary().getTestsFoundCount()).isZero();
            assertThat(summary.getSummary().getContainersFailedCount()).isGreaterThanOrEqualTo(1);
            assertThat(appender.list.stream().anyMatch(e ->
                    e.getFormattedMessage().contains("TEST FACTORY FAILED")
                            && e.getFormattedMessage().contains("crashesDuringGeneration()")
                            && e.getThrowableProxy() != null)).isTrue();
            assertThat(appender.list.stream().noneMatch(e ->
                    e.getFormattedMessage().contains("Status: PASSED"))).isTrue();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    public static class FailingFactoryFixture {
        @TestFactory
        Stream<DynamicTest> crashesDuringGeneration() {
            throw new IllegalStateException("payload supplier failed during discovery");
        }
    }

    public static class DynamicFixture {
        @TestFactory
        Stream<DynamicTest> contractCases() {
            return Stream.of(
                    dynamicTest("[payload] VALID @ /isFreeOfCharge", () -> {
                    }),
                    dynamicTest("[payload] REPLACED_WITH_WRONG_TYPE_NUMBER @ /isFreeOfCharge",
                            () -> {
                                throw new AssertionError("expected 200 but was 400");
                            }),
                    dynamicTest("[payload] PREPARATION_CRASH @ /isFreeOfCharge",
                            () -> {
                                throw new IllegalStateException("supplier crashed");
                            })
            );
        }
    }
}
