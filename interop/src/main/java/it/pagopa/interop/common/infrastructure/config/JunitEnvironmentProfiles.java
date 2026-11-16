package it.pagopa.interop.common.infrastructure.config;

import org.springframework.test.context.ActiveProfilesResolver;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class JunitEnvironmentProfiles implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> testClass) {
        String environment = System.getProperty("spring.profiles.active");
        if (environment == null || environment.isBlank()) {
            environment = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        if (environment == null || environment.isBlank()) {
            environment = "qa";
        }
        var profiles = new LinkedHashSet<>(Arrays.asList(environment.split("\\s*,\\s*")));
        profiles.add("junit");
        return profiles.toArray(String[]::new);
    }
}
