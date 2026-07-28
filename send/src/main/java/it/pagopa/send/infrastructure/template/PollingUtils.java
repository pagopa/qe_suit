package it.pagopa.send.infrastructure.template;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class PollingUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(1);

    private PollingUtils() {
    }

    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> condition, Duration timeout, Duration interval) {
        Objects.requireNonNull(supplier, "supplier non può essere null");
        Objects.requireNonNull(condition, "condition non può essere null");

        Duration effectiveTimeout = timeout != null ? timeout : DEFAULT_TIMEOUT;
        Duration effectiveInterval = interval != null ? interval : DEFAULT_INTERVAL;

        if (effectiveTimeout.isZero() || effectiveTimeout.isNegative()) {
            throw new IllegalArgumentException("timeout deve essere maggiore di zero");
        }
        if (effectiveInterval.isZero() || effectiveInterval.isNegative()) {
            throw new IllegalArgumentException("interval deve essere maggiore di zero");
        }

        try {
            return Awaitility.await()
                    .pollInSameThread()
                    .pollInterval(effectiveInterval)
                    .atMost(effectiveTimeout)
                    .until(supplier::get, result -> result != null && condition.test(result));
        } catch (ConditionTimeoutException e) {
            throw new IllegalStateException("Polling in timeout dopo " + effectiveTimeout.toSeconds() + " secondi.", e);
        }
    }

    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> condition) {
        return pollUntil(supplier, condition, DEFAULT_TIMEOUT, DEFAULT_INTERVAL);
    }
}
