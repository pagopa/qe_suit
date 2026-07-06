package it.pagopa.interop.new_arch.common.journey.infrastructure.config;

import it.pagopa.interop.new_arch.common.journey.application.InteropJourney;
import it.pagopa.interop.new_arch.common.journey.application.JourneyModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;
import java.util.List;

@Configuration
public class JourneyProxyConfiguration {

    @Bean
    public InteropJourney interopJourney(List<JourneyModule> modules) {
        return (InteropJourney) Proxy.newProxyInstance(
                InteropJourney.class.getClassLoader(),
                new Class<?>[]{InteropJourney.class},
                new JourneyInvocationHandler(modules)
        );
    }
}