package it.pagopa.interop.new_arch.common.infrastructure.async;

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

    public static <T> T pollUntil(
            Supplier<T> supplier,
            Predicate<T> condition,
            Duration timeout,
            Duration interval
    ) {
        Objects.requireNonNull(supplier, "supplier non può essere null");
        Objects.requireNonNull(condition, "condition non può essere null");
        Objects.requireNonNull(timeout, "timeout non può essere null");
        Objects.requireNonNull(interval, "interval non può essere null");

        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException(
                    "timeout deve essere maggiore di zero"
            );
        }

        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException(
                    "interval deve essere maggiore di zero"
            );
        }

        try {
            return Awaitility.await()
                    .pollInSameThread()
                    .pollInterval(interval)
                    .atMost(timeout)
                    .until(
                            () -> {
                                T result = supplier.get();

                                if (result == null) {
                                    throw new IllegalStateException(
                                            "Il supplier del polling ha restituito null"
                                    );
                                }

                                return result;
                            },
                            condition
                    );

        } catch (ConditionTimeoutException e) {
            throw new IllegalStateException(
                    "Polling in timeout dopo "
                            + timeout.toSeconds()
                            + " secondi.",
                    e
            );
        }
    }

    public static <T> T pollUntil(
            Supplier<T> supplier,
            Predicate<T> condition
    ) {
        return pollUntil(
                supplier,
                condition,
                DEFAULT_TIMEOUT,
                DEFAULT_INTERVAL
        );
    }
}