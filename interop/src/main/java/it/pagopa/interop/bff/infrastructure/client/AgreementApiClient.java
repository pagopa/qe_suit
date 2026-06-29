package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementApiClient extends RestService {

    private final AgreementsApi api;
    private final AgreementMapper mapper;

}