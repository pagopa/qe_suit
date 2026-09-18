package it.pagopa.interop.m2m.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.m2m.infrastructure.context.CucumberCurrentApiClientSession;
import it.pagopa.interop.m2m.kernel.context.CurrentApiClientSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cucumber")
public class M2MCucumberConfig {

    @Bean
    @ScenarioScope
    @Primary
    CurrentApiClientSession currentApiClientSession() {
        return new CucumberCurrentApiClientSession();
    }
}
