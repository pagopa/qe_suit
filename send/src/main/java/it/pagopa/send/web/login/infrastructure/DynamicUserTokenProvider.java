package it.pagopa.send.web.login.infrastructure;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Provides bearer tokens based on a logical alias (e.g., {@code PA1}, {@code User1}).
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
     * @param alias one of {@code PA1}, {@code PA2}, {@code User1}, {@code User2}
     * @return the token string, or {@code null} if none could be found
     */
    public String getToken(String alias) {
        String token = env.getProperty("token.session." + alias);
        if (token != null && !token.isBlank() && !token.contains("REPLACE_ME")) {
            return token;
        }
        return null;
    }
}
