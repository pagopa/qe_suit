package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.service.mapper.ProducerKeychainMapper;
import it.pagopa.interop.bff.support.ProducerKeychainSeedFactory;
import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.service.IProducerKeychainTestService;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychains;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainTestService extends RestService implements IProducerKeychainTestService {

    private final ProducerKeychainSeedFactory seedFactory;
    private final ProducerKeychainMapper mapper;
    private final ScenarioContext context;
    private final ProducerKeychainApi keychainApi;

    @Override
    public TestChain<CreatedResource, ProducerKeychain> create() {
        return super.create(
                () -> keychainApi.createProducerKeychainWithHttpInfo(seedFactory.fullCreationRequest()),
                (res) -> readProducerKeychain(res.getId())
        );
    }

    @Override
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain, ProducerKeychain> read(UUID producerKeychainId) {
        Optional<ProducerKeychain> keychain = context.getById(producerKeychainId, ProducerKeychain.class);

        return super.read(
                () -> keychainApi.getProducerKeychainWithHttpInfo(producerKeychainId),
                (res) -> mapper.toDomainWithUpsert(res, keychain.orElse(null))
        );
    }

    @Override
    public TestChain<CompactProducerKeychains, ProducerKeychain> readAll(ProducerKeychainReadAllRequest seed) {
        return super.readAll(
                () -> keychainApi.getProducerKeychainsWithHttpInfo(seed.getOffset(), seed.getLimit(), seed.getQ(), seed.getUserIds(), seed.getEserviceId()),
                mapper::toProducerKeychains
        );
    }

    @Override
    public TestChain<?, ProducerKeychain> delete(UUID producerKeychainId) {
        return super.delete(
                () -> keychainApi.deleteProducerKeychainWithHttpInfo(producerKeychainId),
                null
        );
    }

    @Override
    public void deleteAll() {
        List<ProducerKeychain> keychains;

        do {
            keychains = this.readAll(ProducerKeychainReadAllRequest.unfiltered())
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .getModels();

            if (keychains == null || keychains.isEmpty()) {
                return;
            }

            for (ProducerKeychain keychain : keychains) {
                delete(keychain.getId())
                        .withPolling(((statusCode, body) ->
                                statusCode.is2xxSuccessful() || statusCode.equals(HttpStatus.NOT_FOUND))
                        );
            }

        } while (true);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    public ProducerKeychain readProducerKeychain(UUID producerKeychainId) {
        return read(producerKeychainId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModel();
    }
}
