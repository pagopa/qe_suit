package it.pagopa.interop.bff.service.producer_keychain;

import it.pagopa.interop.bff.model.ProducerKeychain;
import it.pagopa.interop.bff.service.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.service.template.CanCreate;
import it.pagopa.interop.bff.service.template.CanDelete;
import it.pagopa.interop.bff.service.template.CanRead;
import it.pagopa.interop.bff.service.template.CanReadAll;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychains;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;

import java.util.List;
import java.util.UUID;

public interface IProducerKeychainService extends
        CanCreate<ProducerKeychainSeed, CreatedResource, ProducerKeychain>,
        CanRead<UUID, it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain, ProducerKeychain>,
        CanReadAll<IProducerKeychainService.GetAllRequest, CompactProducerKeychains, ProducerKeychain>,
        CanDelete<UUID, Void, ProducerKeychain> {

    record GetAllRequest(@jakarta.annotation.Nonnull Integer offset, @jakarta.annotation.Nonnull Integer limit,
                         @jakarta.annotation.Nullable String q, @jakarta.annotation.Nullable List<UUID> userIds,
                         @jakarta.annotation.Nullable UUID eserviceId) {
    }

    default void deleteAll() {
        List<ProducerKeychain> response;

        do {
            response = this.readAll(new GetAllRequest(0, 100, null, null, null))
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .getModels();

            response.forEach(keychain ->
                    this.delete(keychain.getId())
                            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            );
        } while (response.isEmpty());
    }
}
