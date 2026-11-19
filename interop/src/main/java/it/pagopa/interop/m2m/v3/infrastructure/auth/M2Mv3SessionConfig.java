package it.pagopa.interop.m2m.v3.infrastructure.auth;

import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class M2Mv3SessionConfig {
    @Bean("m2mV3AuthSession")
    @ScenarioScope
    @Profile("cucumber")
    CucumberM2Mv3Session cucumberM2Mv3Session(ObjectProvider<M2Mv3ApiClientProvider> provider) {
        return new CucumberM2Mv3Session(provider);
    }

    @Bean("m2mV3AuthSession")
    @Profile("junit")
    InMemoryM2Mv3Session inMemoryM2Mv3Session(ObjectProvider<M2Mv3ApiClientProvider> provider) {
        return new InMemoryM2Mv3Session(provider);
    }
}
