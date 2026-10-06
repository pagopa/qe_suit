package it.pagopa.send;

import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CucumberActiveProfilesResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(Class<?> testClass) {
        String envProfile = System.getProperty("spring.profiles.active");
        if (!StringUtils.hasText(envProfile)) {
            envProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        if (!StringUtils.hasText(envProfile)) {
            envProfile = "test";
        }

        List<String> profiles = new ArrayList<>();
        profiles.add("cucumber");

        for (String p : envProfile.split(",")) {
            String trimmed = p.trim();
            if (StringUtils.hasText(trimmed) && !profiles.contains(trimmed)) {
                profiles.add(trimmed);
            }
        }

        return profiles.toArray(new String[0]);
    }
}
