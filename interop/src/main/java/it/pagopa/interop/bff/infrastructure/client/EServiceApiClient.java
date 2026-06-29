package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EServiceApiClient extends RestService {

    private final EservicesApi api;
    private final EServiceMapper mapper;

}