package it.pagopa.interop.bff.debug_client_assertion.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ToolsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffDebugClientAssertionRestClient extends RestClient {

    private final ToolsApi toolsApi;
    private final String grantType;
    private final String assertionType;

    public BffDebugClientAssertionRestClient(
            TestChainFactory chainFactory,
            ApiClient apiClient,
            @Value("${interop.auth.client-assertion.grant_type}") String grantType,
            @Value("${interop.auth.client-assertion.assertion_type}") String assertionType
    ) {
        super(chainFactory);
        this.toolsApi = apiClient.tools();
        this.grantType = grantType;
        this.assertionType = assertionType;
    }

    public TestChain<Void> submitClientAssertionValidationRequest(
            String clientAssertion, String dpopProof, String clientId
    ) {
        return execute(
                () -> {
                    var operation = toolsApi.validateTokenGeneration()
                            .clientAssertionForm(clientAssertion)
                            .clientAssertionTypeForm(assertionType)
                            .grantTypeForm(grantType)
                            .reqSpec(spec -> spec.setContentType("application/x-www-form-urlencoded; charset=UTF-8"));
                    if (clientId != null) {
                        operation.clientIdForm(clientId);
                    }
                    if (dpopProof != null) {
                        operation.dpopProofForm(dpopProof);
                    }
                    return operation.execute(Function.identity());
                },
                Void.class
        );
    }
}
