package it.pagopa.interop.common.utils;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class PollingUtils {

    private PollingUtils() {
    }

    /**
     * Esegue polling su un supplier finché il predicate è soddisfatto o scade il timeout.
     * Mantiene traccia dell'ultimo errore per un debugging chiaro.
     */
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
                if (result != null && condition.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                // Cattura l'errore (es. 404 Not Found temporaneo) e lo salva per il timeout
                lastException = e;
            }

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Polling interrotto bruscamente", ie);
            }
        }

        // Timeout scaduto: lancia un'eccezione parlante includendo la causa reale
        String errorMsg = String.format("Polling in timeout dopo %s secondi.", timeout.toSeconds());
        if (lastException != null) {
            throw new IllegalStateException(errorMsg + " Ultimo errore riscontrato: " + lastException.getMessage(), lastException);
        } else {
            throw new IllegalStateException(errorMsg + " La condizione non è mai stata soddisfatta.");
        }
    }

    /**
     * Overload standard: timeout 10s, intervallo 1s.
     */
    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> condition) {
        return pollUntil(supplier, condition, Duration.ofSeconds(10), Duration.ofSeconds(1));
    }

    /**
     * Esegue il polling specifico per le chiamate HTTP (ResponseEntity).
     * Verifica automaticamente che lo status sia 2xx e che il body sia presente prima di testare la condizione.
     */
    public static <T> T pollUntilWithHttpInfo(
            Supplier<ResponseEntity<T>> supplier,
            Predicate<T> bodyCondition,
            Duration timeout,
            Duration interval
    ) {
        // Riconduce il comportamento al pollUntil generico, spacchettando la ResponseEntity
        ResponseEntity<T> finalResponse = pollUntil(
                supplier,
                response -> response != null
                        && response.getStatusCode().is2xxSuccessful()
                        && response.getBody() != null
                        && bodyCondition.test(response.getBody()),
                timeout,
                interval
        );

        return finalResponse.getBody();
    }

    /**
     * Esegue il polling specifico per le chiamate HTTP (ResponseEntity).
     * Verifica automaticamente che lo status sia 2xx e che il body sia presente prima di testare la condizione.
     */
    public static <T> ResponseEntity<T> pollUntilWithHttpInfo(
            Supplier<ResponseEntity<T>> supplier,
            BiPredicate<HttpStatusCode, T> responseCondition,
            Duration timeout,
            Duration interval
    ) {

        return pollUntil(
                supplier,
                response -> response != null
                        && responseCondition.test(response.getStatusCode(), response.getBody()),
                timeout,
                interval
        );
    }

    /**
     * Overload per ResponseEntity con valori di default (10s timeout, 1s intervallo).
     */
    public static <T> T pollUntilWithHttpInfo(Supplier<ResponseEntity<T>> supplier, Predicate<T> bodyCondition) {
        return pollUntilWithHttpInfo(supplier, bodyCondition, Duration.ofSeconds(10), Duration.ofSeconds(1));
    }

    /**
     * Overload per ResponseEntity con valori di default (10s timeout, 1s intervallo).
     */
    public static <T> ResponseEntity<T> pollUntilWithHttpInfo(Supplier<ResponseEntity<T>> supplier,  BiPredicate<HttpStatusCode, T> responseCondition) {
        return pollUntilWithHttpInfo(supplier, responseCondition, Duration.ofSeconds(10), Duration.ofSeconds(1));
    }
}