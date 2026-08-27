package it.pagopa.interop.bff.purpose.infratructure;

import it.pagopa.interop.common.infrastructure.template.RestClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import org.springframework.stereotype.Component;

@Component
public class BffPurposeRestClient extends RestClient {

    private final PurposesApi purposesApi;

    public BffPurposeRestClient(ApiClient apiClient) {
        this.purposesApi = apiClient.purposes();
    }
}
