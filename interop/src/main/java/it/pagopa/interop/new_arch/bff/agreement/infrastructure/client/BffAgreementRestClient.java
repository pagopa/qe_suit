package it.pagopa.interop.new_arch.bff.agreement.infrastructure.client;

import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.infrastructure.template.RestGateway;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BffAgreementRestClient extends RestGateway {

    private final AgreementsApi agreementsApi;
    private final BffAgreementMapper mapper;

    public TestChain<CreatedResource, Agreement> create(AgreementPayload payload) {
        return super.create(
                () -> agreementsApi.createAgreementWithHttpInfo(payload),
                created -> read(created.getId())
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .getModel()
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> read(UUID agreementId) {
        return super.read(
                () -> agreementsApi.getAgreementByIdWithHttpInfo(agreementId),
                mapper::toAgreement
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> submit(UUID agreementId, AgreementSubmissionPayload payload) {
        return super.update(
                () -> agreementsApi.submitAgreementWithHttpInfo(agreementId, new AgreementSubmissionPayload()),
                mapper::toAgreement
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> activate(UUID agreementId, UUID delegationId) {
        return super.update(
                () -> agreementsApi.activateAgreementWithHttpInfo(agreementId, new DelegationRef().delegationId(delegationId)),
                activated -> readUntilActive(agreementId)
        );
    }

    private Agreement readUntilActive(UUID agreementId) {
        return read(agreementId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS_WHERE(
                        agreement -> agreement.getState() == AgreementState.ACTIVE
                ))
                .getModel();
    }
}
