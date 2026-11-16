package it.pagopa.interop.common.journey.infrastructure.config;

import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.journey.application.JourneyModule;
import it.pagopa.interop.common.journey.application.UserJourney;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JourneyProxyConfig {

    @Bean
    public InteropJourney interopJourney(
            List<JourneyModule> modules,
            @Qualifier("routedUserJourney") UserJourney<?> userJourney
    ) {
        List<JourneyModule> delegates = new ArrayList<>();
        modules.stream().filter(module -> !(module instanceof UserJourney<?>)).forEach(delegates::add);
        delegates.add(userJourney);
        return (InteropJourney) Proxy.newProxyInstance(
                InteropJourney.class.getClassLoader(),
                new Class<?>[]{InteropJourney.class},
                new JourneyInvocationHandler(delegates)
        );
    }
}