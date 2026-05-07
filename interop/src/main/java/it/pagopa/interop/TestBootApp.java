package it.pagopa.interop;

import it.pagopa.interop.infrastructure.client.auth.SessionTokenProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"it.pagopa.interop"})
@EnableConfigurationProperties(SessionTokenProperties.class)
public class TestBootApp {
}


