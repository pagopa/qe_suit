package it.pagopa.send.bff.delegation.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.mandate.api.MandateApi;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffAcceptRequest;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffNewMandateRequest;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffSearchMandateRequest;
import it.pagopa.send.generated.openapi.clients.mandate.model.BffSearchMandateResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Chiamate alle API delle deleghe del BFF ({@code /bff/v1/mandate}): ognuna riceve il session token dell'utente che
 * la fa.
 */
@Component
public class BffDelegationRestClient extends RestClient {

    // le deleghe di un'utenza di test sono poche: basta la prima pagina
    private static final int SEARCH_PAGE_SIZE = 50;

    private final MandateApi mandateApi;

    public BffDelegationRestClient(TestChainFactory chainFactory, MandateApi mandateApi) {
        super(chainFactory);
        this.mandateApi = mandateApi;
    }

    public TestChain<BffSearchMandateResponse> searchByDelegate(String sessionToken, BffSearchMandateRequest payload) {
        return execute(
                () -> mandateApi.searchMandatesByDelegateV1()
                        .sizeQuery(SEARCH_PAGE_SIZE)
                        .body(payload)
                        .reqSpec(spec -> spec.addHeader("Authorization", "Bearer " + sessionToken))
                        .execute(Function.identity()),
                BffSearchMandateResponse.class
        );
    }

    public TestChain<Void> create(String sessionToken, BffNewMandateRequest payload) {
        return execute(
                () -> mandateApi.createMandateV1()
                        .body(payload)
                        .reqSpec(spec -> spec.addHeader("Authorization", "Bearer " + sessionToken))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> accept(String sessionToken, String mandateId, BffAcceptRequest payload) {
        return execute(
                () -> mandateApi.acceptMandateV1()
                        .mandateIdPath(mandateId)
                        .body(payload)
                        .reqSpec(spec -> spec.addHeader("Authorization", "Bearer " + sessionToken))
                        .execute(Function.identity()),
                Void.class
        );
    }
}
