package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogFilterPayload;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffEServiceCatalogRestClient extends RestClient {
    private final EservicesApi eservicesApi;

    public BffEServiceCatalogRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        eservicesApi = apiClient.eservices();
    }

    public TestChain<CatalogEServices> queryEServicesCatalog(CatalogFilterPayload payload) {
        return execute(
                () -> eservicesApi
                        .queryEServicesCatalog()
                        .body(payload)
                        .execute(Function.identity()),
                CatalogEServices.class
        );
    }
}
