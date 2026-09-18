package it.pagopa.interop.bff.infrastructure.security.bearer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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
    private BearerAuthProvider proxiedBearerAuthProvider;
    private KmsClient kmsClient;

    @BeforeEach
    void setUp() {
        kmsClient = mock(KmsClient.class);

        BearerAuthProvider target = new BearerAuthProvider(
                new ObjectMapper(),
                kmsClient,
                new BearerTokenProperties(
                        "https://dev.interop.pagopa.it/.well-known/jwks.json",
                        "dev.interop.pagopa.it",
                        "dev.interop.pagopa.it/ui",
                        14400
                )
        );

        CacheManager cacheManager = new ConcurrentMapCacheManager("sessionToken");
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        CacheResolver cacheResolver = new SimpleCacheResolver(cacheManager);
        CacheOperationSource cacheOperationSource = new AnnotationCacheOperationSource();

        cacheInterceptor.setCacheOperationSources(cacheOperationSource);
        cacheInterceptor.setCacheResolver(cacheResolver);
        cacheInterceptor.setErrorHandler(new SimpleCacheErrorHandler());
        cacheInterceptor.afterPropertiesSet();

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(cacheInterceptor);
        proxiedBearerAuthProvider = (BearerAuthProvider) proxyFactory.getProxy();
    }

    @Test
    void getToken_withSameUserAndTenant_usesCacheAndSignsOnce() {
        when(kmsClient.sign(any(SignRequest.class)))
                .thenReturn(SignResponse.builder().signature(SdkBytes.fromByteArray(new byte[]{1, 2, 3})).build());

        String firstToken = proxiedBearerAuthProvider.getToken(User.S_MATTIA, Tenant.COMUNE_DI_MILANO);
        String secondToken = proxiedBearerAuthProvider.getToken(User.S_MATTIA, Tenant.COMUNE_DI_MILANO);

        assertEquals(removeJti(extractPayload(firstToken)), removeJti(extractPayload(secondToken)));
        verify(kmsClient, times(1)).sign(any(SignRequest.class));
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
}
