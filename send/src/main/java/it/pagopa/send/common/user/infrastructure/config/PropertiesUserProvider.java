package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.OrganizationRole;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.domain.UserType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Risolve le utenze generiche dal file delle utenze dell'ambiente attivo ({@link UsersProperties}).
 * <p>
 * Fail-fast: se l'utenza manca, o è ancora un segnaposto {@code REPLACE_ME}, lancia
 * {@link MissingUserConfigurationException} con alias e ambiente, invece di restituire dati vuoti o finti.
 */
@Component
@EnableConfigurationProperties(UsersProperties.class)
public class PropertiesUserProvider implements UserProvider {

    private static final String PLACEHOLDER = "REPLACE_ME";

    private final UsersProperties properties;
    private final Environment environment;

    public PropertiesUserProvider(UsersProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Override
    public User resolve(UserAlias alias) {
        Map<String, UsersProperties.UserEntry> users = properties.users();
        UsersProperties.UserEntry entry = users != null ? users.get(alias.key()) : null;
        if (entry == null || isPlaceholder(entry.username())) {
            throw new MissingUserConfigurationException(alias, activeEnvironment());
        }
        return toUser(entry, alias.getType());
    }

    private static boolean isPlaceholder(String username) {
        return username == null || username.isBlank() || username.contains(PLACEHOLDER);
    }

    private String activeEnvironment() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length > 0 ? String.join(",", profiles) : "default";
    }

    private static User toUser(UsersProperties.UserEntry entry, UserType type) {
        List<OrganizationRole> roles = entry.roles() == null ? List.of() : entry.roles().stream()
                .map(role -> new OrganizationRole(role.partyRole(), role.role()))
                .toList();
        return new ConfiguredUser(type, entry.username(), entry.password(), entry.taxId(), entry.organization(),
                entry.organizationId(), entry.uid(), entry.denomination(), entry.familyName(),
                entry.personalTaxId(), roles, entry.ipaCode(), entry.hasGroups(), entry.email());
    }
}
