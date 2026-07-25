package it.pagopa.interop.common.infrastructure.security.jwt;

import java.util.List;

public final class JwtBuilder {

    public record JwtClaimOverride(String claim, String value) { }


    public static void applyOverrides(io.jsonwebtoken.JwtBuilder builder, List<JwtClaimOverride> overrides) {
        for (JwtClaimOverride ov : overrides) {
            String claim = ov.claim();
            String raw = ov.value();

            switch (claim) {
                // HEADER
                case "header.alg" -> setHeader(builder, "alg", raw);
                case "header.kid" -> setHeader(builder, "kid", raw);

                // CLAIM STANDARD
                case "iss" -> setClaim(builder, "iss", raw);
                case "sub" -> setClaim(builder, "sub", raw);
                case "aud" -> setClaim(builder, "aud", parseAud(raw));
                case "jti" -> setClaim(builder, "jti", raw);
                case "iat" -> setClaim(builder, "iat", parseEpoch(raw));
                case "exp" -> setClaim(builder, "exp", parseEpoch(raw));
                case "nbf" -> setClaim(builder, "nbf", parseEpoch(raw));

                // CLAIM DPOP
                case "htm" -> setClaim(builder, "htm", raw);
                case "htu" -> setClaim(builder, "htu", raw);

                // CLAIM CUSTOM
                case "purposeId" -> setClaim(builder, "purposeId", parseMaybeUuid(raw));
                case "digest" -> setClaim(builder, "digest", raw);
                case "algorithm" -> setClaim(builder, "algorithm", raw);
                case "assertionType" -> setClaim(builder, "client_assertion_type", raw);
                case "grantType" -> setClaim(builder, "grant_type", raw);

                // Comandi speciali utili per test negativi
                case "__remove" -> removeClaim(builder, raw); // raw = nome claim da rimuovere
                case "__removeHeader" -> removeHeader(builder, raw); // raw = nome header da rimuovere
                case "__rawPayload" -> setRawPayload(builder, raw);

                default -> throw new IllegalArgumentException("Claim non supportato: " + claim);
            }
        }
    }

    private static void setClaim(io.jsonwebtoken.JwtBuilder builder, String name, Object value) {
        // 1. Gestione del "not found": se il valore è null o stringa vuota, non aggiungiamo il claim
        if (value == null || (value instanceof String s && s.isBlank())) {
            return;
        }

        // 2. Trasformazione del valore: passiamo il valore attraverso parseMaybeUuid
        // Questo permette di mappare la stringa "INVALID_UUID" (dalla tabella) a "not-a-uuid"
        // o di lasciare "not-a-uuid" se passato direttamente.
        Object finalValue = value;
        if (value instanceof String s) {
            finalValue = parseMaybeUuid(s);
        }

        // 3. Impostazione del claim sul builder
        builder.claim(name, finalValue);
    }

    private static void setHeader(io.jsonwebtoken.JwtBuilder builder, String name, String value) {
        if (value == null || value.isBlank()) return;
        builder.header().add(name, value).and();
    }

    private static Object parseAud(String raw) {
        if (raw == null || raw.isBlank()) return null;
        // supporta singolo o multiplo separato da |
        if (raw.contains("|")) return List.of(raw.split("\\|"));
        return raw;
    }

    private static Long parseEpoch(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String value = raw.trim();
        java.time.Instant now = java.time.Instant.now();

        if ("now".equals(value)) {
            return now.getEpochSecond();
        }

        if (value.startsWith("now+") || value.startsWith("now-")) {
            char operator = value.charAt(3);
            String secondsPart = value.substring(4).trim();

            if (secondsPart.isEmpty()) {
                throw new IllegalArgumentException("Offset temporale mancante in: " + raw);
            }

            long seconds;
            try {
                seconds = Long.parseLong(secondsPart);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Offset temporale non valido in '" + raw + "'. Atteso formato now+<secondi> o now-<secondi>", e
                );
            }

            if (operator == '+') {
                return now.plusSeconds(seconds).getEpochSecond();
            } else {
                return now.minusSeconds(seconds).getEpochSecond();
            }
        }

        return Long.parseLong(value);
    }

    private static Object parseMaybeUuid(String raw) {
        if (raw == null || raw.isBlank()) return null;
        if ("INVALID_UUID".equals(raw)) return "not-a-uuid";
        return raw;
    }

    private static void removeClaim(io.jsonwebtoken.JwtBuilder builder, String claimName) {
        builder.claim(claimName, null);
    }

    private static void removeHeader(io.jsonwebtoken.JwtBuilder builder, String headerName) {
        builder.header().delete(headerName);
    }

    private static void setRawPayload(io.jsonwebtoken.JwtBuilder builder, String raw) {
        if (raw == null) return;

        // Rimuove tutti i claims impostati finora per evitare l'IllegalStateException
        // Nelle versioni recenti, passare una mappa nulla o vuota resetta i claims interni
        builder.setClaims(new java.util.HashMap<>());

        // Ora puoi impostare il contenuto grezzo senza conflitti
        builder.content(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
