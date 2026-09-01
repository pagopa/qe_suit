package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class FuzzingWiringTest {

    @Test
    void fuzzing_components_are_wired() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            assertNotNull(context.getBean(FuzzEngine.class));
            assertNotNull(context.getBean(FuzzMutationApplier.class));

            List<FuzzRule> rules = context.getBeanProvider(FuzzRule.class)
                    .orderedStream()
                    .toList();

            assertEquals(2, rules.size());
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        ObjectGraphDecomposer objectGraphDecomposer() {
            return mock(ObjectGraphDecomposer.class);
        }

        @Bean
        FuzzMutationApplier fuzzMutationApplier(ObjectMapper objectMapper) {
            return new JacksonFuzzMutationApplier(objectMapper);
        }

        @Bean
        FuzzRule firstRule() {
            return mock(FuzzRule.class);
        }

        @Bean
        FuzzRule secondRule() {
            return mock(FuzzRule.class);
        }

        @Bean
        FuzzEngine fuzzEngine(
                ObjectGraphDecomposer decomposer,
                ObjectMapper objectMapper,
                FuzzMutationApplier mutationApplier,
                List<FuzzRule> rules
        ) {
            return new DefaultFuzzEngine(
                    decomposer,
                    objectMapper,
                    mutationApplier,
                    rules
            );
        }
    }
}