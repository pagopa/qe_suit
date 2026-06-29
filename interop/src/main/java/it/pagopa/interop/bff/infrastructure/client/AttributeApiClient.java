package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttributeApiClient extends RestService {

    private final AttributesApi api;
    private final AttributeMapper mapper;


}