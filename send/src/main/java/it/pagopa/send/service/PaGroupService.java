package it.pagopa.send.service;

import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.externalregistries.api.InfoPaApi;
import it.pagopa.send.generated.openapi.clients.externalregistries.model.PaGroup;
import it.pagopa.send.generated.openapi.clients.externalregistries.model.PaGroupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PaGroupService {

    private final InfoPaApi infoPaApi;
    private final Map<String, String> paGroupsApiKeys;

    /**
     * Ritorna l'id del primo gruppo attivo per il {@link Tenant} indicato, se esiste.
     * L'api-key per la chiamata è unica per PA e per ambiente (vedi {@code pa.groups-api-key} negli
     * application-{profilo}.yaml), quindi va risolta per tenant a ogni chiamata, non può essere un
     * unico valore condiviso a livello di bean del client.
     */
    public Optional<String> findActiveGroupId(Tenant tenant) {
        String apiKey = paGroupsApiKeys.get(tenant.name());
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Nessuna api-key configurata per il tenant " + tenant.name()
                            + " (property pa.groups-api-key." + tenant.name() + ")");
        }

        List<PaGroup> groups = infoPaApi.getGroupsB2B()
                .statusFilterQuery(PaGroupStatus.ACTIVE)
                .reqSpec(reqSpec -> reqSpec.addHeader("x-api-key", apiKey))
                .executeAs(Function.identity());

        return groups.stream().findFirst().map(PaGroup::getId);
    }
}
