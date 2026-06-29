package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EServiceApiClient extends RestService {

    private final EservicesApi api;
    private final EServiceMapper mapper;

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices, EService> getEServicesCatalog(Integer offset, Integer limit, it.pagopa.interop.generated.openapi.clients.bff.model.PersonalDataFilter personalData, String q, List<UUID> producersIds, List<UUID> attributesIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState> states, List<it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState> agreementStates, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode mode, Boolean isConsumerDelegable) {
        return super.readAll(
                () -> api.getEServicesCatalogWithHttpInfo(offset, limit, personalData, q, producersIds, attributesIds, states, agreementStates, mode, isConsumerDelegable),
                mapper::toDomainList
        );
    }
}