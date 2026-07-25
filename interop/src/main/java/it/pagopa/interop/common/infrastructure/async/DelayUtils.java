package it.pagopa.interop.common.infrastructure.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

/**
 * Utility class per gestire i ritardi nei test in modo pulito.
 */
public final class DelayUtils {

    private static final Logger log = LoggerFactory.getLogger(DelayUtils.class);

    private DelayUtils() {
        throw new UnsupportedOperationException("Questa è una classe di utility e non può essere istanziata");
    }

    /**
     * Attende per il numero di millisecondi specificato.
     *
     * @param millis millisecondi di attesa
     */
    public static void waitForMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Attesa interrotta inaspettatamente", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Attende per il numero di secondi specificato.
     *
     * @param seconds secondi di attesa
     */
    public static void waitForSeconds(long seconds) {
        waitForMillis(seconds * 1000);
    }

    /**
     * Attende per la durata specificata (es. Duration.ofMinutes(1)).
     *
     * @param duration oggetto Duration che rappresenta il tempo di attesa
     */
    public static void waitFor(Duration duration) {
        if (duration != null) {
            waitForMillis(duration.toMillis());
        }
    }
}