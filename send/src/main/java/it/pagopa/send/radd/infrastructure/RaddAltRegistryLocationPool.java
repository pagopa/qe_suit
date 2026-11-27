package it.pagopa.send.radd.infrastructure;

import it.pagopa.send.generated.openapi.clients.raddregistry.ApiClient;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.CreateRegistryRequestV2;
import it.pagopa.send.generated.openapi.clients.raddregistry.model.RegistryV2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Precondizioni dei contract test del registry RADD: sedi create con {@code addRegistry}.
 * <ul>
 *   <li>{@link #shared()}: una sola sede per contesto, usata dalle modifiche (PATCH/PUT). Ogni richiesta baseline
 *       riporta la sede in uno stato valido e l'indirizzo non cambia mai, quindi i casi concorrenti non
 *       interferiscono tra loro;</li>
 *   <li>{@link #create()}: una sede nuova a ogni chiamata, per {@code deleteRegistry} (una sede si cancella una volta
 *       sola).</li>
 * </ul>
 */
@Component
public class RaddAltRegistryLocationPool {

    private static final int SUCCESS_STATUS_CODE = 200;

    private final ApiClient apiClient;
    private final RaddAltRegistryRequestFactory requestFactory;
    private volatile RaddRegistryLocation shared;

    public RaddAltRegistryLocationPool(
            @Qualifier("raddRegistryApiClient") ApiClient apiClient,
            RaddAltRegistryRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
    }

    public RaddRegistryLocation shared() {
        RaddRegistryLocation current = shared;
        if (current == null) {
            synchronized (this) {
                if (shared == null) {
                    shared = create();
                }
                current = shared;
            }
        }
        return current;
    }

    public RaddRegistryLocation create() {
        CreateRegistryRequestV2 request = requestFactory.createRequest();
        RegistryV2 registry = apiClient.registryV2().addRegistry()
                .body(request)
                .execute(response -> response.then()
                        .statusCode(SUCCESS_STATUS_CODE)
                        .extract()
                        .as(RegistryV2.class));
        if (registry.getLocationId() == null || registry.getLocationId().isBlank()) {
            throw new IllegalStateException("addRegistry non ha restituito un locationId: " + registry);
        }
        return new RaddRegistryLocation(registry.getLocationId(), request);
    }

    /**
     * Sede creata e richiesta usata per crearla (serve alla PUT, che deve reinviare lo stesso indirizzo).
     */
    public record RaddRegistryLocation(String locationId, CreateRegistryRequestV2 request) {
    }
}
