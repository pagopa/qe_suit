package it.pagopa.send.web.login.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

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
}
