package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.OrganizationRole;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Risoluzione delle utenze generiche: con il file delle utenze reale di dev per i casi di successo; per i casi di errore
 * con un ambiente inesistente o con proprietà costruite a mano (chiave assente, segnaposto, configurazione vuota), così
 * non dipendono dai dati reali delle utenze.
 */
class PropertiesUserProviderTest {

    // ambiente inesistente: i casi di errore non dipendono dai dati reali delle utenze
    private static final String FAKE_ENVIRONMENT = "fake-env";

    private static ApplicationContextRunner runnerFor(String profile) {
        return new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withSystemProperties("spring.profiles.active=" + profile)
                .withUserConfiguration(PropertiesUserProvider.class);
    }

    @Test
    void shouldResolveConfiguredUserForDev() {
        runnerFor("dev").run(context -> {
            User user = context.getBean(PropertiesUserProvider.class).resolve(UserAlias.USER_1);

            assertThat(user.getType()).isEqualTo(UserType.PF);
            assertThat(user.getUsername()).isEqualTo("lucrezia");
            assertThat(user.getTaxId()).isEqualTo("BRGLRZ80D58H501Q");
            assertThat(user.getFiscalNumber()).isEqualTo("BRGLRZ80D58H501Q");
            assertThat(user.getRoles()).isEmpty();
        });
    }

    @Test
    void shouldResolveOrganizationAndRolesForDev() {
        runnerFor("dev").run(context -> {
            User user = context.getBean(PropertiesUserProvider.class).resolve(UserAlias.IMPRESA_1);

            assertThat(user.getType()).isEqualTo(UserType.PG);
            assertThat(user.getOrganization()).isEqualTo("Vita Nova Sas");
            assertThat(user.getTaxId()).isEqualTo("12666810299");
            assertThat(user.getFiscalNumber()).isEqualTo("LGHDNT00P14D612D");
            assertThat(user.getRoles()).containsExactly(new OrganizationRole("MANAGER", "pg-admin"));
        });
    }

    @Test
    void shouldFailForUnknownEnvironment() {
        runnerFor(FAKE_ENVIRONMENT).run(context -> assertThatThrownBy(
                () -> context.getBean(PropertiesUserProvider.class).resolve(UserAlias.USER_1))
                .isInstanceOf(MissingUserConfigurationException.class)
                .hasMessage("Utenza USER_1 non configurata nel file delle utenze dell'ambiente"));
    }

    @Test
    void shouldFailWhenUserKeyIsMissing() {
        PropertiesUserProvider provider = providerWith(new UsersProperties(Map.of()));

        assertThatThrownBy(() -> provider.resolve(UserAlias.USER_2))
                .isInstanceOf(MissingUserConfigurationException.class)
                .hasMessage("Utenza USER_2 non configurata nel file delle utenze dell'ambiente");
    }

    @Test
    void shouldFailWhenUserIsPlaceholder() {
        UsersProperties.UserEntry placeholder = new UsersProperties.UserEntry("REPLACE_ME", null, null, null, null,
                null, null, null, null, null, null, false, null, null, null);
        PropertiesUserProvider provider = providerWith(new UsersProperties(Map.of(UserAlias.COMUNE_1, placeholder)));

        assertThatThrownBy(() -> provider.resolve(UserAlias.COMUNE_1))
                .isInstanceOf(MissingUserConfigurationException.class)
                .hasMessageContaining("COMUNE_1");
    }

    @Test
    void shouldFailWhenNoUsersAreConfigured() {
        PropertiesUserProvider provider = providerWith(new UsersProperties(null));

        assertThatThrownBy(() -> provider.resolve(UserAlias.COMUNE_2))
                .isInstanceOf(MissingUserConfigurationException.class)
                .hasMessageContaining("COMUNE_2");
    }

    private static PropertiesUserProvider providerWith(UsersProperties properties) {
        return new PropertiesUserProvider(properties);
    }
}
