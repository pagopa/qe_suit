package it.pagopa.send.bff.infrastructure;

import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.infrastructure.config.UsersProperties;
import it.pagopa.send.generated.openapi.clients.externalregistries.api.InfoPaApi;
import it.pagopa.send.generated.openapi.clients.externalregistries.model.PaGroup;
import it.pagopa.send.generated.openapi.clients.externalregistries.model.PaGroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PaGroupService {

    private final InfoPaApi infoPaApi;
    private final UsersProperties usersProperties;

    /**
     * Ritorna l'id del primo gruppo attivo per l'ente mittente indicato, se esiste.
     * L'api-key per la chiamata è unica per PA e per ambiente (campo {@code groups-api-key} della voce dell'ente in
     * users/application-users-{profilo}.yaml), quindi va risolta per ente a ogni chiamata, non può essere un
     * unico valore condiviso a livello di bean del client.
     */
    public Optional<String> findActiveGroupId(User sender) {
        String apiKey = usersProperties.findByUsername(sender.getUsername())
                .map(UsersProperties.UserEntry::groupsApiKey)
                .orElse(null);
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Nessuna api-key dei gruppi configurata per l'utenza " + sender.getUsername()
                            + " (campo groups-api-key in users/application-users-<ambiente>.yaml)");
        }

        List<PaGroup> groups = infoPaApi.getGroupsB2B()
                .statusFilterQuery(PaGroupStatus.ACTIVE)
                .reqSpec(reqSpec -> reqSpec.addHeader("x-api-key", apiKey))
                .executeAs(Function.identity());

        return groups.stream().findFirst().map(PaGroup::getId);
    }
}
