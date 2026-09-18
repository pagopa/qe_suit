package it.pagopa.interop.m2m.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.m2m.infrastructure.context.CucumberCurrentM2MSession;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
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
    CurrentM2MSession currentApiClientSession() {
        return new CucumberCurrentM2MSession();
    }
}
