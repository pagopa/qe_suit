package it.pagopa.send.web.login.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.env.MockEnvironment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DynamicUserTokenProviderTest {

    @Test
    @DisplayName("Should resolve direct alias token")
    void shouldResolveDirectAliasToken() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("token.session.PA1", "mock-token-pa1");
        DynamicUserTokenProvider provider = new DynamicUserTokenProvider(env);

        assertEquals("mock-token-pa1", provider.getToken("PA1"));
    }

    @Test
    @DisplayName("Should resolve case variations of alias including PascalCase (User1/user1/USER1)")
    void shouldResolveCaseVariations() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("token.session.PA1", "mock-token-pa1");
        env.setProperty("token.session.User1", "mock-token-user1");
        DynamicUserTokenProvider provider = new DynamicUserTokenProvider(env);

        assertEquals("mock-token-pa1", provider.getToken("pa1"));
        assertEquals("mock-token-pa1", provider.getToken("PA1"));
        assertEquals("mock-token-user1", provider.getToken("user1"));
        assertEquals("mock-token-user1", provider.getToken("USER1"));
        assertEquals("mock-token-user1", provider.getToken("User1"));
    }

    @Test
    @DisplayName("Should handle JWT expiration correctly")
    void shouldHandleJwtExpiration() {
        MockEnvironment env = new MockEnvironment();
        // Valid JWT with future exp (year 2030 -> 1893456000)
        String futureJwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4OTM0NTYwMDB9.signature";
        // Expired JWT with past exp (year 2020 -> 1577836800)
        String expiredJwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1Nzc4MzY4MDB9.signature";

        env.setProperty("token.session.PA1", futureJwt);
        env.setProperty("token.session.PA2", expiredJwt);
        DynamicUserTokenProvider provider = new DynamicUserTokenProvider(env);

        assertEquals(futureJwt, provider.getToken("PA1"));
        assertNull(provider.getToken("PA2"), "Expired JWT should be filtered out");
    }

    @Test
    @DisplayName("Should resolve legacy user aliases")
    void shouldResolveLegacyAliases() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("token.session.PA1", "mock-token-pa1");
        env.setProperty("token.session.User1", "mock-token-user1");
        env.setProperty("token.session.PG1", "mock-token-pg1");
        DynamicUserTokenProvider provider = new DynamicUserTokenProvider(env);

        assertEquals("mock-token-pa1", provider.getToken("grossini"));
        assertEquals("mock-token-user1", provider.getToken("lucrezia"));
        assertEquals("mock-token-pg1", provider.getToken("francescopetrarca"));
        assertEquals("mock-token-pg1", provider.getToken("petrarca"));
    }

    @Test
    @DisplayName("Should ignore REPLACE_ME placeholders and return null")
    void shouldIgnoreReplaceMe() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("token.session.PA1", "REPLACE_ME");
        env.setProperty("token.session.User1", "");
        DynamicUserTokenProvider provider = new DynamicUserTokenProvider(env);

        assertNull(provider.getToken("PA1"));
        assertNull(provider.getToken("User1"));
        assertNull(provider.getToken("non_existent"));
        assertNull(provider.getToken(null));
        assertNull(provider.getToken("   "));
    }

    @Test
    @DisplayName("Should fail fast with a clear message when no valid token is configured")
    void shouldFailFastWhenTokenIsMissing() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("token.session.garibaldi", "REPLACE_ME");
        DynamicUserTokenProvider provider = new DynamicUserTokenProvider(env);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> provider.requireToken("garibaldi"));
        assertEquals("Nessun session token valido per l'utenza garibaldi: aggiungere"
                + " pn.external.session-token.garibaldi nei segreti dell'ambiente (o rinnovarlo se è scaduto)",
                exception.getMessage());
    }

    /**
     * Ogni utenza dei file delle utenze ha il suo {@code token.session.<username>} in ogni ambiente, letto da
     * {@code pn.external.session-token.<username>}: i token qui sono finti e passati come proprietà di sistema.
     */
    @ParameterizedTest
    @ValueSource(strings = {"dev", "test", "uat", "hotfix"})
    @DisplayName("Should resolve the session token of every configured username in every profile")
    void shouldResolveSessionTokenOfEveryConfiguredUsername(String profile) {
        List<String> usernames = List.of("lucrezia", "garibaldi", "DanteAlighieri", "FrancescoPetrarca", "grossini",
                "albino63");
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("spring.profiles.active", profile);
        usernames.forEach(username -> properties.put("pn.external.session-token." + username.toLowerCase(),
                "token-" + username.toLowerCase()));

        new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withSystemProperties(properties.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .toArray(String[]::new))
                .withUserConfiguration(DynamicUserTokenProvider.class)
                .run(context -> {
                    DynamicUserTokenProvider provider = context.getBean(DynamicUserTokenProvider.class);
                    for (String username : usernames) {
                        assertEquals("token-" + username.toLowerCase(), provider.requireToken(username),
                                "token di " + username + " nel profilo " + profile);
                    }
                });
    }
}
