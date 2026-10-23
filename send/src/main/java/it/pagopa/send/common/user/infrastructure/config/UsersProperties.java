package it.pagopa.send.common.user.infrastructure.config;

import it.pagopa.send.common.user.domain.UserAlias;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utenze generiche di test dell'ambiente attivo, lette da {@code users/application-users-<ambiente>.yaml} (importato da
 * {@code application-<ambiente>.yaml}). Nel file la chiave è l'alias in kebab-case ({@code user-1}), che Spring converte
 * nell'enum {@link UserAlias} ({@code USER_1}).
 */
@ConfigurationProperties
public record UsersProperties(Map<UserAlias, UserEntry> users) {

    /**
     * Voce dell'utenza con lo username indicato, se configurata nell'ambiente attivo.
     */
    public Optional<UserEntry> findByUsername(String username) {
        if (users == null || username == null) {
            return Optional.empty();
        }
        return users.values().stream()
                .filter(entry -> username.equals(entry.username()))
                .findFirst();
    }

    /**
     * Dati di un'utenza: {@code taxId} è il codice fiscale dell'ente o dell'impresa (della persona per una PF),
     * {@code personalTaxId} quello della persona che accede. Per gli enti, {@code groupsApiKey} e
     * {@code deliveryApiKey} sono le api-key (header {@code x-api-key}) delle chiamate B2B sui gruppi e sulle notifiche.
     */
    public record UserEntry(String username, String password, String taxId, String organization,
                            String organizationId, String uid, String denomination, String familyName,
                            String personalTaxId, List<RoleEntry> roles, String ipaCode, boolean hasGroups,
                            String email, String groupsApiKey, String deliveryApiKey) {
    }

    public record RoleEntry(String partyRole, String role) {
    }
}
