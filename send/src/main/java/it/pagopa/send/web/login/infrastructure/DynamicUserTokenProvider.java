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

        // Case variations lookup (e.g., pa1 -> PA1, user1 -> User1, pg1 -> PG1)
        for (String candidate : List.of(alias.toUpperCase(), alias.toLowerCase())) {
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
        return token != null && !token.isBlank() && !token.contains("REPLACE_ME");
    }
}
