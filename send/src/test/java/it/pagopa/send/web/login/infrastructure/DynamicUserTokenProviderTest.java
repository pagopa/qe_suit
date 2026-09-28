package it.pagopa.send.web.login.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

class DynamicUserTokenProviderTest {

    @Nested
    @SpringBootTest(classes = DynamicUserTokenProvider.class)
    @ActiveProfiles("dev")
    @DisplayName("Dev environment token resolution")
    class DevProfileTest {

        @Autowired
        private DynamicUserTokenProvider provider;

        @Test
        @DisplayName("Should resolve standard PA1 and User1 tokens in dev")
        void shouldResolveTokensInDev() {
            assertNotNull(provider.getToken("PA1"), "PA1 token should be present in dev");
            assertNotNull(provider.getToken("User1"), "User1 token should be present in dev");
            assertNotNull(provider.getToken("PG1"), "PG1 token should be present in dev");
            assertFalse(provider.getToken("PA1").isBlank());
            assertFalse(provider.getToken("User1").isBlank());
        }
    }

    @Nested
    @SpringBootTest(classes = DynamicUserTokenProvider.class)
    @ActiveProfiles("test")
    @DisplayName("Test environment token resolution")
    class TestProfileTest {

        @Autowired
        private DynamicUserTokenProvider provider;

        @Test
        @DisplayName("Should resolve standard PA1 and User1 tokens in test")
        void shouldResolveTokensInTest() {
            assertNotNull(provider.getToken("PA1"), "PA1 token should be present in test");
            assertNotNull(provider.getToken("User1"), "User1 token should be present in test");
            assertNotNull(provider.getToken("PG1"), "PG1 token should be present in test");
            assertFalse(provider.getToken("PA1").isBlank());
            assertFalse(provider.getToken("User1").isBlank());
        }
    }

    @Nested
    @SpringBootTest(classes = DynamicUserTokenProvider.class)
    @ActiveProfiles("uat")
    @DisplayName("UAT environment token resolution")
    class UatProfileTest {

        @Autowired
        private DynamicUserTokenProvider provider;

        @Test
        @DisplayName("Should resolve standard PA1 and User1 tokens in uat")
        void shouldResolveTokensInUat() {
            assertNotNull(provider.getToken("PA1"), "PA1 token should be present in uat");
            assertNotNull(provider.getToken("User1"), "User1 token should be present in uat");
            assertNotNull(provider.getToken("PG1"), "PG1 token should be present in uat");
            assertFalse(provider.getToken("PA1").isBlank());
            assertFalse(provider.getToken("User1").isBlank());
        }
    }
}
