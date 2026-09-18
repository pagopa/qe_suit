package it.pagopa.interop.bff.infrastructure.security.bearer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.model.SignResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BearerAuthProviderCachingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getToken_withSameUserAndTenant_usesCacheAndSignsOnce() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            KmsClient kmsClient = context.getBean(KmsClient.class);
            BearerAuthProvider bearerAuthProvider = context.getBean(BearerAuthProvider.class);

            when(kmsClient.sign(any(SignRequest.class)))
                    .thenReturn(SignResponse.builder().signature(SdkBytes.fromByteArray(new byte[]{1, 2, 3})).build());

            String firstToken = bearerAuthProvider.getToken(User.S_MATTIA, Tenant.COMUNE_DI_MILANO);
            String secondToken = bearerAuthProvider.getToken(User.S_MATTIA, Tenant.COMUNE_DI_MILANO);

            assertEquals(removeJti(extractPayload(firstToken)), removeJti(extractPayload(secondToken)));
            verify(kmsClient, times(1)).sign(any(SignRequest.class));
        }
    }

    private static String extractPayload(String token) {
        String[] parts = token.split("\\.");
        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

    private Map<String, Object> removeJti(String payload) {
        try {
            Map<String, Object> claims = objectMapper.readValue(payload, new TypeReference<>() {});
            claims.remove("jti");
            return claims;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse token payload", e);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        KmsClient kmsClient() {
            return mock(KmsClient.class);
        }

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("sessionToken");
        }

        @Bean
        BearerTokenProperties bearerTokenProperties() {
            return new BearerTokenProperties(
                    "https://dev.interop.pagopa.it/.well-known/jwks.json",
                    "dev.interop.pagopa.it",
                    "dev.interop.pagopa.it/ui",
                    14400
            );
        }

        @Bean
        BearerAuthProvider bearerAuthProvider(
                ObjectMapper objectMapper,
                KmsClient kmsClient,
                BearerTokenProperties bearerTokenProperties
        ) {
            return new BearerAuthProvider(objectMapper, kmsClient, bearerTokenProperties);
        }
    }
}
