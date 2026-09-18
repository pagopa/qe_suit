package it.pagopa.interop.common.infrastructure.auth;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.auth.ApiClient;
import it.pagopa.interop.generated.openapi.clients.auth.api.AuthApi;
import it.pagopa.interop.generated.openapi.clients.auth.model.ClientCredentialsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class AuthRestClient extends RestClient {

    private final AuthApi authApi;

    public AuthRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.authApi = apiClient.auth();
    }

    public TestChain<ClientCredentialsResponse> createToken(UUID clientId, String clientAssertion, @Nullable String dPoPProof) {
        return execute(
                () -> authApi.createToken()
                        .clientIdForm(clientId)
                        .clientAssertionForm(clientAssertion)
                        .clientAssertionTypeForm("urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                        .grantTypeForm("client_credentials")
                        .dpoPHeader(dPoPProof)
                        .execute(Function.identity()),
                ClientCredentialsResponse.class
        );
    }
}
