package it.pagopa.interop;

import it.pagopa.interop.common.infrastructure.client.auth.bearer.BearerTokenProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"it.pagopa.interop"})
@EnableConfigurationProperties(BearerTokenProperties.class)
public class TestBootApp {
}


