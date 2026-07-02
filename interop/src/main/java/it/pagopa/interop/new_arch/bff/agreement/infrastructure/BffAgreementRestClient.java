package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.infrastructure.template.RestGateway;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BffAgreementRestClient extends RestGateway {

    private final AgreementsApi agreementsApi;
    private final BffAgreementRestClientMapper mapper;

    public TestChain<CreatedResource, Agreement> create(@Nonnull AgreementPayload payload) {
        return super.create(
                () -> agreementsApi.createAgreementWithHttpInfo(payload),
                created -> read(created.getId())
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .getModel()
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> read(@Nonnull UUID agreementId) {
        return super.read(
                () -> agreementsApi.getAgreementByIdWithHttpInfo(agreementId),
                mapper::toAgreement
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> submit(@Nonnull UUID agreementId, @Nonnull AgreementSubmissionPayload payload) {
        return super.update(
                () -> agreementsApi.submitAgreementWithHttpInfo(agreementId, payload),
                mapper::toAgreement
        );
    }

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> activate(@Nonnull UUID agreementId, @Nullable UUID delegationId) {
        return super.update(
                () -> agreementsApi.activateAgreementWithHttpInfo(agreementId, new DelegationRef().delegationId(delegationId)),
                activated -> readUntilActive(agreementId)
        );
    }

    private Agreement readUntilActive(@Nonnull UUID agreementId) {
        return read(agreementId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS_WHERE(
                        agreement -> agreement.getState() == AgreementState.ACTIVE
                ))
                .getModel();
    }
}
