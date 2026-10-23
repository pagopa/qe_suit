package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.utils.async.PollingUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BffAgreementGateway implements AgreementGateway {

    private final BffAgreementRestClient restClient;
    private final BffAgreementMapper mapper;
    private final BffAgreementRequestFactory agreementRequestFactory;
    private final BffAgreementPrerequisiteGateway prerequisiteGateway;

    @Override
    public void ensureActiveAgreement(EService eService, EServiceDescriptor descriptor) {
        prerequisiteGateway.ensureActiveAgreement(eService, descriptor, this);
    }

    @Override
    public Agreement createAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        AgreementPayload payload = agreementRequestFactory.creationRequest(eService, descriptor, delegation);

        return restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getAgreement(new AgreementRef(createdResource.getId())))
                .get();
    }

    @Override
    public void shouldFailToCreateAgreement(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation, AgreementCreationFailureReason reason) {
        AgreementPayload payload = agreementRequestFactory.creationRequest(eService, descriptor, delegation);

        int expectedStatus = switch (reason) {
            case ESERVICE_INVALID_STATE, DEPRECATED_VERSION, ARCHIVED_STATE -> 400;
        };

        restClient.create(payload)
                .withPolling(PollingStrategy.UNTIL_ERROR)
                .assertStatusCode(expectedStatus);
    }

    @Override
    public Agreement getAgreement(AgreementRef ref) {
        return restClient.read(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }


    @Override
    public Agreement submitAgreement(Agreement agreement) {
        return restClient.submit(agreement.getId(), new AgreementSubmissionPayload().consumerNotes("consumerNotes"))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement activateAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return restClient.activate(
                        agreement.getId(), Optional.ofNullable(delegation).map(Delegation::getId).orElse(null)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .assertThat( a -> a.getState().getValue().equals(AgreementState.ACTIVE.getValue()))
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement suspendAgreement(Agreement agreement, @Nullable Delegation delegation) {
        return restClient.suspend(
                        agreement.getId(), Optional.ofNullable(delegation).map(Delegation::getId).orElse(null)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAgreement)
                .updateContext()
                .get();
    }

    @Override
    public Agreement archiveAgreement(Agreement agreement) {
        restClient.archive(agreement.getId())
                .withoutPolling()
                .assertStatusCode(204);
        return awaitState(agreement, Set.of(AgreementState.ARCHIVED));
    }

    @Override
    public Agreement awaitState(Agreement agreement, Set<AgreementState> expectedStates) {
        if (expectedStates.isEmpty()) {
            throw new IllegalArgumentException("At least one expected agreement state is required");
        }
        return PollingUtils.pollUntil(
                () -> getAgreement(agreement.getRef()),
                current -> {
                    AgreementState state = current.getState();
                    if (expectedStates.contains(state)) {
                        return true;
                    }
                    if (state == null || state == AgreementState.REJECTED
                            || state == AgreementState.MISSING_CERTIFIED_ATTRIBUTES
                            || state == AgreementState.ARCHIVED) {
                        throw new IllegalStateException(
                                "Agreement reached state " + state + " while awaiting " + expectedStates
                        );
                    }
                    return false;
                }
        );
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
