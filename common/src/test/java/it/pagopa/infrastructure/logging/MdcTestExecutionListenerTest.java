package it.pagopa.infrastructure.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MdcTestExecutionListenerTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void logsPassedFailedAndAbortedIndividuallyAndPreservesExistingMdc() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            MDC.put("correlationId", "kept");
            MDC.put(TestMdcKeys.SCENARIO_NAME, "outer");
            MdcTestExecutionListener listener = new MdcTestExecutionListener();

            TestIdentifier passed = identifier("[payload] VALID @ /isFreeOfCharge", "id-1");
            TestIdentifier failed = identifier("[payload] REPLACED_WITH_WRONG_TYPE_NUMBER @ /isFreeOfCharge", "id-2");
            TestIdentifier aborted = identifier("[payload] SKIPPED @ /isFreeOfCharge", "id-3");

            listener.executionFinished(passed, TestExecutionResult.successful());
            AssertionError assertion = new AssertionError("expected 200 but was 400");
            listener.executionFinished(failed, TestExecutionResult.failed(assertion));
            listener.executionFinished(aborted, TestExecutionResult.aborted(new RuntimeException("aborted")));

            assertThat(appender.list).hasSize(3);
            assertThat(appender.list.get(0).getFormattedMessage()).contains("VALID", "Status: PASSED");
            assertThat(appender.list.get(1).getFormattedMessage()).contains("REPLACED_WITH_WRONG_TYPE_NUMBER", "Status: FAILED");
            assertThat(appender.list.get(1).getThrowableProxy()).isNotNull();
            assertThat(appender.list.get(1).getThrowableProxy().getMessage()).contains("expected 200 but was 400");
            assertThat(appender.list.get(2).getFormattedMessage()).contains("Status: ABORTED");
            assertThat(appender.list.get(1).getMDCPropertyMap())
                    .containsEntry(TestMdcKeys.TEST_EXECUTION_ID, "id-2")
                    .containsEntry("correlationId", "kept");
            assertThat(MDC.get(TestMdcKeys.SCENARIO_NAME)).isEqualTo("outer");
            assertThat(MDC.get(TestMdcKeys.TEST_EXECUTION_ID)).isNull();
            assertThat(MDC.get("correlationId")).isEqualTo("kept");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void ignoresContainers() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            TestIdentifier container = mock(TestIdentifier.class);
            when(container.isTest()).thenReturn(false);
            new MdcTestExecutionListener().executionFinished(container, TestExecutionResult.successful());
            assertThat(appender.list).isEmpty();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void logsFailedFactoryContainerWithStackTraceButNotSuccessfulFactory() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            MdcTestExecutionListener listener = new MdcTestExecutionListener();
            TestIdentifier factory = mock(TestIdentifier.class);
            when(factory.isContainer()).thenReturn(true);
            when(factory.getDisplayName()).thenReturn("createAgreement()");
            when(factory.getUniqueId()).thenReturn("factory-id");

            listener.executionFinished(factory, TestExecutionResult.successful());
            assertThat(appender.list).isEmpty();

            IllegalStateException failure = new IllegalStateException("Unable to generate session token");
            listener.executionFinished(factory, TestExecutionResult.failed(failure));
            assertThat(appender.list).hasSize(1);
            ILoggingEvent event = appender.list.get(0);
            assertThat(event.getFormattedMessage()).contains("TEST FACTORY FAILED", "createAgreement()", "Status: FAILED");
            assertThat(event.getThrowableProxy().getMessage()).contains("Unable to generate session token");
            assertThat(event.getMDCPropertyMap()).containsEntry(TestMdcKeys.TEST_EXECUTION_ID, "factory-id");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void ignoresFailedNonMethodContainers() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            TestIdentifier suite = mock(TestIdentifier.class);
            when(suite.isContainer()).thenReturn(true);
            when(suite.getDisplayName()).thenReturn("ContractSuite");
            new MdcTestExecutionListener().executionFinished(suite,
                    TestExecutionResult.failed(new IllegalStateException("suite failed")));
            assertThat(appender.list).isEmpty();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void logsRuntimeExceptionEvenWhenFailureHappensBeforeHttpAssertion() {
        Logger logger = (Logger) LoggerFactory.getLogger(MdcTestExecutionListener.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            TestIdentifier id = identifier("[payload] PREPARATION_CRASH @ /x", "id-crash");
            new MdcTestExecutionListener().executionFinished(id,
                    TestExecutionResult.failed(new IllegalStateException("supplier crashed")));
            assertThat(appender.list).hasSize(1);
            assertThat(appender.list.get(0).getFormattedMessage()).contains("Status: FAILED");
            assertThat(appender.list.get(0).getThrowableProxy().getMessage()).contains("supplier crashed");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    private TestIdentifier identifier(String name, String uniqueId) {
        TestIdentifier identifier = mock(TestIdentifier.class);
        when(identifier.isTest()).thenReturn(true);
        when(identifier.getDisplayName()).thenReturn(name);
        when(identifier.getUniqueId()).thenReturn(uniqueId);
        return identifier;
    }
}
