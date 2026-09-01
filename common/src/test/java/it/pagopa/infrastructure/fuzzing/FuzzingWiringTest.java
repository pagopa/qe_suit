package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class FuzzingWiringTest {

    @Test
    void package_components_are_wired_without_dedicated_configuration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            assertNotNull(context.getBean(FuzzEngine.class));
            assertNotNull(context.getBean(FuzzMutationApplier.class));
            List<FuzzRule> rules = context.getBeanProvider(FuzzRule.class).orderedStream().toList();
            assertEquals(2, rules.size());
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = FuzzEngine.class)
    static class TestConfig {
        @Bean
        ObjectMapper testObjectMapper() {
            return new ObjectMapper();
        }

        @Bean
        ObjectGraphDecomposer testObjectGraphDecomposer() {
            return mock(ObjectGraphDecomposer.class);
        }
    }
}
