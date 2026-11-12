package it.pagopa.send.bff.partner_tecnologico.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.bff.api.PtInstitutionsApi;
import it.pagopa.send.generated.openapi.clients.bff.model.BffPtInstitutionsResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/** Chiamata agli enti deleganti del BFF ({@code /bff/v1/pt/institutions}), con il session token del PT. */
@Component
public class BffPtInstitutionsRestClient extends RestClient {

    private final PtInstitutionsApi ptInstitutionsApi;

    public BffPtInstitutionsRestClient(TestChainFactory testChainFactory, PtInstitutionsApi ptInstitutionsApi) {
        super(testChainFactory);
        this.ptInstitutionsApi = ptInstitutionsApi;
    }

    /** Prima pagina degli enti deleganti del PT, come la carica la home del BackOffice PT. */
    public TestChain<BffPtInstitutionsResponse> getInstitutions(String sessionToken, int limit) {
        return execute(
                () -> ptInstitutionsApi.getPtInstitutionsV1()
                        .limitQuery(limit)
                        .reqSpec(spec -> spec.addHeader("Authorization", "Bearer " + sessionToken))
                        .execute(Function.identity()),
                BffPtInstitutionsResponse.class
        );
    }
}
