package it.pagopa.interop.new_arch.common.journey.infrastructure.config;

import it.pagopa.interop.new_arch.common.journey.application.Journey;
import it.pagopa.interop.new_arch.common.journey.application.AgreementJourney;
import it.pagopa.interop.new_arch.common.journey.application.UserJourney;
import it.pagopa.interop.new_arch.common.journey.infrastructure.AgreementJourneyImpl;
import it.pagopa.interop.new_arch.common.journey.infrastructure.UserJourneyImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;
import java.util.List;

@Configuration
public class JourneyProxyConfiguration {

    @Bean
    @SuppressWarnings("unchecked")
    public Journey<?> interopJourney(UserJourneyImpl userJourney, AgreementJourneyImpl agreementJourney) {

        // Lista dei componenti reali che contengono le implementazioni
        List<Object> delegates = List.of(userJourney, agreementJourney);

        // Creiamo il proxy che unisce l'interfaccia globale e le sue sotto-interfacce
        return (Journey<?>) Proxy.newProxyInstance(
                Journey.class.getClassLoader(),
                new Class<?>[]{Journey.class, UserJourney.class, AgreementJourney.class},
                new JourneyInvocationHandler(delegates)
        );
    }
}