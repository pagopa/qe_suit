package it.pagopa.interop.common.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JunitEnvironmentProfilesBootTest.Config.class)
@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
class JunitEnvironmentProfilesBootTest {
    @Autowired
    Environment environment;

    @Autowired
    JunitMarker junitMarker;

    @Test
    void activatesJunitBeanAndEnvironmentConfiguration() {
        assertThat(junitMarker).isNotNull();
        assertThat(environment.getActiveProfiles()).contains("junit");
        String configured = System.getProperty("spring.profiles.active");
        if (configured == null || configured.isBlank()) configured = System.getenv("SPRING_PROFILES_ACTIVE");
        if (configured == null || configured.isBlank()) configured = "qa";
        assertThat(environment.getActiveProfiles()).contains(configured.split("\\s*,\\s*"));
        if (configured.equals("qa") || configured.equals("dev")) {
            assertThat(environment.getRequiredProperty("interop.auth.bearer.issuer"))
                    .isEqualTo(configured + ".interop.pagopa.it");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {
        @Bean
        @Profile("junit")
        JunitMarker junitMarker() {
            return new JunitMarker();
        }
    }

    static class JunitMarker {}
}
