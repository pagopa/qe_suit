package it.pagopa.send.common.infrastructure;

import java.util.Random;

/**
 * Genera stringhe numeriche casuali a lunghezza fissa (padding di zeri a sinistra), nello stesso
 * formato dei codici usati da pagoPA (es. IUV, noticeCode). Versione semplificata dell'analogo
 * generatore lato pn-b2b-client.
 */
public final class RandomNumericGenerator {

    private static final Random RANDOM = new Random();

    private RandomNumericGenerator() {
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
