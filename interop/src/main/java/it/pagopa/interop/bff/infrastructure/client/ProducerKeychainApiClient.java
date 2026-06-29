package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerKeychainApiClient extends RestService {

    private final ProducerKeychainApi api;
    private final ProducerKeychainMapper mapper;

}