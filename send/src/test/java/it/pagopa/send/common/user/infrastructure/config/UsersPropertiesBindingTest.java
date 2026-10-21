package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.UserAlias;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica, per ogni ambiente, che {@code application-<ambiente>.yaml} importi il file delle utenze e che ogni
 * {@link UserAlias} vi abbia la sua voce (chiave in kebab-case, es. {@code user-1} per {@code USER_1}).
 */
class UsersPropertiesBindingTest {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(UsersProperties.class)
    static class UsersPropertiesConfig {
    }

    @ParameterizedTest
    @ValueSource(strings = {"dev", "test", "uat", "hotfix"})
    void shouldBindEveryUserAliasForProfile(String profile) {
        new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withSystemProperties("spring.profiles.active=" + profile)
                .withUserConfiguration(UsersPropertiesConfig.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getEnvironment().getActiveProfiles()).containsExactly(profile);

                    Map<UserAlias, UsersProperties.UserEntry> users = context.getBean(UsersProperties.class).users();
                    for (UserAlias alias : UserAlias.values()) {
                        assertThat(users).as("utenza %s nel profilo %s", alias, profile).containsKey(alias);
                    }
                });
    }
}
