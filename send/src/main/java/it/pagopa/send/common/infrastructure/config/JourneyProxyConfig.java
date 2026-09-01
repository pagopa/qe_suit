package it.pagopa.send.common.infrastructure.config;

import it.pagopa.send.common.journey.application.JourneyModule;
import it.pagopa.send.common.journey.application.SendJourney;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;
import java.util.List;

@Configuration
public class JourneyProxyConfig {

    @Bean
    public SendJourney interopJourney(List<JourneyModule> modules) {
        return (SendJourney) Proxy.newProxyInstance(
                SendJourney.class.getClassLoader(),
                new Class<?>[]{SendJourney.class},
                new JourneyInvocationHandler(modules)
        );
    }
}