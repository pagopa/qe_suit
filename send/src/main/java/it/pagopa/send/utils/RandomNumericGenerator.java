package it.pagopa.send.utils;

import java.util.Random;

/**
 * Genera stringhe numeriche casuali a lunghezza fissa (padding di zeri a sinistra), nello stesso
 * formato dei codici usati da pagoPA (es. IUV, noticeCode). Versione semplificata dell'analogo
 * generatore lato pn-b2b-client.
 */
public final class RandomNumericGenerator {

    private RandomNumericGenerator() {
    }

    public static String generate(int length) {
        long max = (long) Math.pow(10, length);
        long value = Math.abs(new Random().nextLong() % max);
        return String.format("%0" + length + "d", value);
    }
}
