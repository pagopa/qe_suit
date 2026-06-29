package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurposeApiClient extends RestService {

    private final PurposesApi api;
    private final PurposeMapper mapper;


}