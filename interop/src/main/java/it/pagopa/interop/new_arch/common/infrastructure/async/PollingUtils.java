package it.pagopa.interop.new_arch.common.infrastructure.async;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class PollingUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(1);

    private PollingUtils() {
    }

    public static <T> T pollUntil(
            Supplier<T> supplier,
            Predicate<T> condition,
            Duration timeout,
            Duration interval
    ) {
        try {
            return Awaitility.await()
                    .atMost(timeout)
                    .with()
                    .pollInSameThread()
                    .pollInterval(interval)
                    .ignoreExceptions()
                    .until(supplier::get, condition);
        } catch (ConditionTimeoutException e) {
            throw new IllegalStateException("Polling in timeout dopo " + timeout.toSeconds() + " secondi.", e);
        }
    }

    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> condition) {
        return pollUntil(supplier, condition, DEFAULT_TIMEOUT, DEFAULT_INTERVAL);
    }
}