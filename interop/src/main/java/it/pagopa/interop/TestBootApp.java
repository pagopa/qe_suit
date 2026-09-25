package it.pagopa.interop;

import it.pagopa.interop.bff.infrastructure.security.bearer.BearerTokenProperties;
import it.pagopa.interop.common.infrastructure.config.CacheConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(BearerTokenProperties.class)
@Import(CacheConfig.class)
public class TestBootApp {
}

