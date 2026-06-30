package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.service.mapper.AgreementMapper;
import it.pagopa.interop.bff.support.AgreementSeedFactory;
import it.pagopa.interop.common.contract.model.agreement.Agreement;
import it.pagopa.interop.common.contract.service.IAgreementTestService;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AgreementTestService extends RestService implements IAgreementTestService {

    private final AgreementsApi agreementsApi;
    private final AgreementSeedFactory seedFactory;
    private final AgreementMapper mapper;

    @Override
    public TestChain<?, Agreement> createAgreement(UUID eserviceId, UUID descriptorId, UUID delegationId) {
        AgreementPayload payload = seedFactory.fullCreationRequest(eserviceId, descriptorId, Optional.ofNullable(delegationId));

        return super.create(
                () -> agreementsApi.createAgreementWithHttpInfo(payload),
                created -> read(created.getId())
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
                        .getModel()
        );
    }

    @Override
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> read(UUID agreementId) {
        return super.read(
                () -> agreementsApi.getAgreementByIdWithHttpInfo(agreementId),
                mapper::toAgreement
        );
    }

    @Override
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> submitAgreement(UUID agreementId) {
        return super.update(
                () -> agreementsApi.submitAgreementWithHttpInfo(
                        agreementId,
                        new AgreementSubmissionPayload()
                ),
                mapper::toAgreement
        );
    }

    @Override
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> activateAgreement(UUID agreementId) {
        return super.update(
                () -> agreementsApi.activateAgreementWithHttpInfo(agreementId, null),
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