package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfiguration {

    @Bean
    TestChainFactory testChainFactory(
            LastApiResponseStore lastApiResponseStore,
            EntityStore entityStore
    ) {
        return new TestChainFactory(lastApiResponseStore, entityStore);
    }
}
