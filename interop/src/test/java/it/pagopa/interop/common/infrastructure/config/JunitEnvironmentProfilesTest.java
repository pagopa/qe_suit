package it.pagopa.interop.common.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JunitEnvironmentProfilesTest.Config.class)
@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
class JunitEnvironmentProfilesTest {
    @Autowired
    Environment environment;

    @Test
    void activatesJunitAlongsideConfiguredEnvironment() {
        assertThat(environment.getActiveProfiles()).contains("junit");
        String configured = System.getProperty("spring.profiles.active");
        if (configured == null || configured.isBlank()) configured = System.getenv("SPRING_PROFILES_ACTIVE");
        if (configured == null || configured.isBlank()) configured = "qa";
        assertThat(environment.getActiveProfiles()).contains(configured.split("\\s*,\\s*"));
    }

    @ParameterizedTest
    @MethodSource("profileInputs")
    void resolvesEnvironmentAndJunitProfiles(String systemProfile, String environmentProfile, String[] expected) {
        assertThat(JunitEnvironmentProfiles.resolveProfiles(systemProfile, environmentProfile))
                .containsExactly(expected);
    }

    private static Stream<Arguments> profileInputs() {
        return Stream.of(
                Arguments.of(null, null, new String[]{"qa", "junit"}),
                Arguments.of("  ", "  ", new String[]{"qa", "junit"}),
                Arguments.of(null, "dev", new String[]{"dev", "junit"}),
                Arguments.of("  ", "dev", new String[]{"dev", "junit"}),
                Arguments.of("dev", "qa", new String[]{"dev", "junit"}),
                Arguments.of("qa", "dev", new String[]{"qa", "junit"}),
                Arguments.of("dev, junit,dev", null, new String[]{"dev", "junit"})
        );
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {}
}
