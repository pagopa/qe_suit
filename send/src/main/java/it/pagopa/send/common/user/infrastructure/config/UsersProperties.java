package it.pagopa.send.common.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utenze generiche di test dell'ambiente attivo, lette da {@code users/application-users-<ambiente>.yaml} (importato da
 * {@code application-<ambiente>.yaml}). La chiave della mappa è l'alias dell'utenza in kebab-case ({@code USER_1} →
 * {@code user-1}).
 * <p>
 * Il prefisso è {@code qe} (Quality Engineering, namespace delle proprietà della suite) e il componente {@code users}
 * corrisponde a {@code qe.users}: le altre chiavi {@code qe.*} non vengono lette da questa classe.
 */
@ConfigurationProperties(prefix = "qe")
public record UsersProperties(Map<String, UserEntry> users) {

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
