package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.infrastructure.client_old.ProducerKeychainApiClient;
import it.pagopa.interop.common.contract.enums.Channel;
import it.pagopa.interop.common.contract.model.ProducerKeychain;
import it.pagopa.interop.common.contract.model.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainService implements it.pagopa.interop.common.contract.template.ProducerKeychainService {

    private final ProducerKeychainApiClient client;

    @Override
    public TestChain<?, ProducerKeychain> create() {
        return client.create();
    }

    @Override
    public TestChain<?, ProducerKeychain> readAll(ProducerKeychainReadAllRequest request) {
        return client.readAll(request);
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
                client.delete(keychain.getId())
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
}
