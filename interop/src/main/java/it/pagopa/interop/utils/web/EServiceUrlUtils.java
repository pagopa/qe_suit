package it.pagopa.interop.utils.web;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EServiceUrlUtils {
    // Cattura la parte "e-service/" seguita dai due blocchi da 36 caratteri (gli UUID) separati da uno slash
    private static final Pattern URL_PATTERN = Pattern.compile("e-service/([0-9a-fA-F-]{36})/([0-9a-fA-F-]{36})");

    private EServiceUrlUtils() {}

    public record EServiceData(UUID eserviceId, UUID descriptorId) {}

    public static EServiceData extractData(String url) {
        if (url == null) {
            throw new IllegalArgumentException("L'URL non può essere nullo");
        }

        Matcher matcher = URL_PATTERN.matcher(url);

        if (matcher.find()) {
            // matcher.group(1) prende il primo UUID trovato, matcher.group(2) prende il secondo
            UUID eserviceId = UUID.fromString(matcher.group(1));
            UUID descriptorId = UUID.fromString(matcher.group(2));

            return new EServiceData(eserviceId, descriptorId);
        }

        throw new IllegalStateException("Formato URL non valido. Impossibile trovare i due UUID di e-service: " + url);
    }
}
