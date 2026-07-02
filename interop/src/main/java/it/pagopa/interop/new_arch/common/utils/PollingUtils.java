package it.pagopa.interop.new_arch.common.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.time.Instant;
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
        Instant deadline = Instant.now().plus(timeout);
        Throwable lastException = null;

        while (Instant.now().isBefore(deadline)) {
            try {
                T result = supplier.get();

                if (condition.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                lastException = e;

                T mappedResult = mapExceptionToResult(e);

                if (mappedResult != null && condition.test(mappedResult)) {
                    return mappedResult;
                }
            }

            executeSleep(interval);
        }

        throwTimeoutException("Polling in timeout dopo " + timeout.toSeconds() + " secondi.", lastException);
        return null;
    }

    public static <T> T pollUntil(
            Supplier<T> supplier,
            Predicate<T> condition,
            int maxAttempts,
            Duration interval
    ) {
        Throwable lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T result = supplier.get();

                if (condition.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                lastException = e;

                T mappedResult = mapExceptionToResult(e);

                if (mappedResult != null && condition.test(mappedResult)) {
                    return mappedResult;
                }
            }

            if (attempt < maxAttempts) {
                executeSleep(interval);
            }
        }

        throwTimeoutException("Polling fallito dopo " + maxAttempts + " tentativi.", lastException);
        return null;
    }

    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> condition) {
        return pollUntil(supplier, condition, DEFAULT_TIMEOUT, DEFAULT_INTERVAL);
    }

    @SuppressWarnings("unchecked")
    private static <T> T mapExceptionToResult(Exception e) {
        RestClientResponseException restException = findCause(e, RestClientResponseException.class);

        if (restException == null) {
            return null;
        }

        HttpHeaders headers = restException.getResponseHeaders() != null
                ? restException.getResponseHeaders()
                : new HttpHeaders();

        ResponseEntity<Void> responseEntity = ResponseEntity
                .status(restException.getStatusCode())
                .headers(headers)
                .body(null);

        return (T) responseEntity;
    }

    private static <E extends Throwable> E findCause(Throwable throwable, Class<E> expectedType) {
        Throwable current = throwable;

        while (current != null) {
            if (expectedType.isInstance(current)) {
                return expectedType.cast(current);
            }

            current = current.getCause();
        }

        return null;
    }

    private static void executeSleep(Duration interval) {
        try {
            Thread.sleep(interval.toMillis());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Polling interrotto bruscamente.", ie);
        }
    }

    private static void throwTimeoutException(String baseMessage, Throwable lastException) {
        if (lastException != null) {
            throw new IllegalStateException(baseMessage + " Ultimo errore: " + lastException.getMessage(), lastException);
        }

        throw new IllegalStateException(baseMessage + " La condizione non è mai stata soddisfatta.");
    }
}