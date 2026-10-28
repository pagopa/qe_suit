package it.pagopa.send.bff.api_key.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.bff.apikeys.api.ApiKeysApi;
import it.pagopa.send.generated.openapi.clients.bff.apikeys.model.BffRequestNewApiKey;
import it.pagopa.send.generated.openapi.clients.bff.apikeys.model.BffResponseNewApiKey;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ApiKeyRestClient extends RestClient {

    private final ApiKeysApi apiKeysApi;

    public ApiKeyRestClient(TestChainFactory chainFactory, ApiKeysApi apiKeysApi) {
        super(chainFactory);
        this.apiKeysApi = apiKeysApi;
    }

    public TestChain<BffResponseNewApiKey> create(BffRequestNewApiKey payload) {
        return execute(
                () -> apiKeysApi.newApiKeyV1().body(payload).execute(Function.identity()),
                BffResponseNewApiKey.class
        );
    }
}
