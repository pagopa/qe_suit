package it.pagopa.interop.common.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione del cache manager per il modulo Interop.
 * <p>
 * Questo bean è necessario perché {@link it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider}
 * usa {@code @Cacheable(cacheNames = "sessionToken")} per memoizzare i token di sessione
 * e ridurre le chiamate al provider di autenticazione durante i test paralleli.
 * Il nome della cache ({@code "sessionToken"}) deve corrispondere a quello dichiarato in {@code BearerAuthProvider}.
 */
@Configuration(proxyBeanMethods = false)
public class CacheConfig {

    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("sessionToken");
    }
}
