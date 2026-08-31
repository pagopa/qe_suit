package it.pagopa.interop;

import it.pagopa.interop.bff.infrastructure.security.bearer.BearerTokenProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(BearerTokenProperties.class)
public class TestBootApp {
}


