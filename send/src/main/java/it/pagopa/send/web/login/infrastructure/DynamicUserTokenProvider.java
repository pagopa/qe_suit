package it.pagopa.send.web.login.infrastructure;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Provides bearer tokens based on a logical alias (e.g., {@code PA1}, {@code User1}, {@code PG1}).
 * Each environment defines a concrete token in {@code token.session.<alias>} (e.g.
 * {@code token.session.PA1}) in the corresponding {@code application-*.yaml}.
 * The provider returns that token, ignoring the {@code REPLACE_ME} placeholder.
 */
@Component
public class DynamicUserTokenProvider {

    private final Environment env;

    public DynamicUserTokenProvider(Environment env) {
        this.env = env;
    }

    /**
     * Resolve the bearer token for the given alias.
     *
     * @param alias one of {@code PA1}, {@code PA2}, {@code User1}, {@code User2}, {@code PG1}, {@code PG2} (or legacy username)
     * @return the token string, or {@code null} if none could be found
     */
    public String getToken(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }

        // Direct lookup
        String token = env.getProperty("token.session." + alias);
        if (isValid(token)) {
            return token;
        }

        // Case variations lookup (e.g., pa1 -> PA1, user1 -> User1, USER1 -> User1, pg1 -> PG1)
        String capitalized = alias.substring(0, 1).toUpperCase() + alias.substring(1).toLowerCase();
        for (String candidate : List.of(alias.toUpperCase(), alias.toLowerCase(), capitalized)) {
            token = env.getProperty("token.session." + candidate);
            if (isValid(token)) {
                return token;
            }
        }

        // Fallback for legacy user names mapped to standard aliases
        String mappedAlias = switch (alias.toLowerCase()) {
            case "grossini" -> "PA1";
            case "lucrezia" -> "User1";
            case "francescopetrarca", "petrarca" -> "PG1";
            default -> null;
        };

        if (mappedAlias != null) {
            token = env.getProperty("token.session." + mappedAlias);
            if (isValid(token)) {
                return token;
            }
        }

        return null;
    }

    private boolean isValid(String token) {
        if (token == null || token.isBlank() || token.contains("REPLACE_ME")) {
            return false;
        }
        return !isJwtExpired(token);
    }

    private boolean isJwtExpired(String token) {
        String[] parts = token.trim().split("\\.");
        if (parts.length != 3) {
            // Not a standard 3-part JWT (could be an opaque token or mock token in tests)
            return false;
        }

        try {
            byte[] decoded = java.util.Base64.getUrlDecoder().decode(parts[1]);
            String payload = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\"exp\"\\s*:\\s*(\\d+)").matcher(payload);
            if (matcher.find()) {
                long expSeconds = Long.parseLong(matcher.group(1));
                long nowSeconds = java.time.Instant.now().getEpochSecond();
                return expSeconds < nowSeconds;
            }
        } catch (Exception ignored) {
            // If parsing fails, fall back to treating token as non-expired so we don't break non-standard tokens
        }
        return false;
    }
}
