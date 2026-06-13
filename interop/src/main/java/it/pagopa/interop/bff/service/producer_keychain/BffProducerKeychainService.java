package it.pagopa.interop.bff.service.producer_keychain;

import it.pagopa.interop.bff.client.BffProducerKeychainClient;
import it.pagopa.interop.common.domain.enums.Channel;
import it.pagopa.interop.common.domain.model.ProducerKeychain;
import it.pagopa.interop.common.service.producer_keychain.request.BaseReadAllProducerKeychainRequest;
import it.pagopa.interop.common.service.template.action.TestChain;
import it.pagopa.interop.common.service.template.action.strategy.PollingStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BffProducerKeychainService implements IBffProducerKeychainService {

    private final BffProducerKeychainClient client;

    @Override
    public TestChain<?, ProducerKeychain> create() {
        return client.create();
    }

    @Override
    public TestChain<?, ProducerKeychain> readAll(BaseReadAllProducerKeychainRequest request) {
        return client.readAll(request);
    }

    @Override
    public void deleteAll() {
        List<ProducerKeychain> keychains;

        do {
            keychains = this.readAll(BaseReadAllProducerKeychainRequest.unfiltered())
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
