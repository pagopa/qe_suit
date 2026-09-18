package it.pagopa.interop;

import it.pagopa.interop.bff.infrastructure.security.bearer.BearerTokenProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(BearerTokenProperties.class)
public class TestBootApp {
}

