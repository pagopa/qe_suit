package it.pagopa.send.bff.dashboard.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.bff.ApiClient;
import it.pagopa.send.generated.openapi.clients.bff.model.BffSenderDashboardDataResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffDashboardRestClient extends RestClient {

    private static final String PA = "PA";

    private final ApiClient bffApiClient;

    public BffDashboardRestClient(TestChainFactory testChainFactory, ApiClient bffApiClient) {
        super(testChainFactory);
        this.bffApiClient = bffApiClient;
    }

    /** Dati della dashboard Statistiche dell'ente, con il token dell'utenza in sessione. */
    public TestChain<BffSenderDashboardDataResponse> getDashboardData(String organizationId) {
        return execute(
                () -> bffApiClient.senderDashboard()
                        .getDashboardDataV1()
                        .cxTypePath(PA)
                        .cxIdPath(organizationId)
                        .execute(Function.identity()),
                BffSenderDashboardDataResponse.class
        );
    }
}
