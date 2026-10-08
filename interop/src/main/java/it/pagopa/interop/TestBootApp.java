package it.pagopa.interop;

import it.pagopa.interop.bff.infrastructure.security.bearer.BearerTokenProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point dell'applicazione Spring Boot per i test del modulo Interop.
 * <p>
 * {@code @EnableCaching} abilita il supporto a {@code @Cacheable} usato da
 * {@link it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider} per
 * cacheare i session token e ridurre i round-trip verso il provider di autenticazione.
 * {@code @EnableConfigurationProperties(BearerTokenProperties.class)} abilita il binding
 * della configurazione dei bearer token dichiarata in {@code application.yaml}.
 */
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(BearerTokenProperties.class)
public class TestBootApp {
}

