package it.pagopa.interop.common.utils;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PollingUtils {

    private PollingUtils() {}

    /**
     * Esegue polling su un supplier finché il predicate è soddisfatto o scade il timeout.
     *
     * @param supplier   fornisce il valore ad ogni tentativo
     * @param condition  condizione di successo sul valore
     * @param timeout    durata massima totale
     * @param interval   attesa tra un tentativo e l'altro
     * @param <T>        tipo del risultato
     * @return il valore che ha soddisfatto la condizione
     * @throws IllegalStateException se il timeout scade senza successo
     */
    public static <T> T pollUntil(
            Supplier<T> supplier,
            Predicate<T> condition,
            Duration timeout,
            Duration interval
    ) {
        long deadlineMs = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadlineMs) {
            try {
                T result = supplier.get();
                if (result != null && condition.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                // tentativo fallito: riprova
            }

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Polling interrupted", ie);
            }
        }

        throw new IllegalStateException(
                String.format("Polling timed out after %s", timeout)
        );
    }

    /**
     * Overload con valori di default: timeout 10s, interval 1s.
     */
    public static <T> T pollUntil(Supplier<T> supplier, Predicate<T> condition) {
        return pollUntil(supplier, condition, Duration.ofSeconds(10), Duration.ofSeconds(1));
    }
}