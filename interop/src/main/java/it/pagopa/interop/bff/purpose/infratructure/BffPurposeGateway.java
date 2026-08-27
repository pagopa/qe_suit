package it.pagopa.interop.bff.purpose.infratructure;

import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffPurposeGateway implements PurposeGateway {
    private final BffPurposeRestClient restClient;
    private final BffPurposeMapper mapper;

    @Override
    public Purpose getPurpose(PurposeRef purposeRef) {
        return restClient.getPurpose(purposeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toPurpose)
                .get();
    }

    @Override
    public Purpose createPurpose(PurposeCreateCommand createCommand) {
        if (!(createCommand instanceof BffPurposeCreateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffPurposeCreateCommand");

        PurposeSeed bffCreationPayload = bffCommand.getBffCreationPayload();
        return restClient.createPurpose(bffCreationPayload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .get();
    }

    @Override
    public Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        return restClient.activatePurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(activatedResource -> getPurpose(purposeRef))
                .get();
    }

    @Override
    public Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        return restClient.suspendPurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(suspendedResource -> getPurpose(purposeRef))
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private static it.pagopa.interop.generated.openapi.clients.bff.model.@NonNull DelegationRef getDelegation(@org.jspecify.annotations.Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = new it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef();
        delegation.setDelegationId(delegationRef != null ? delegationRef.id() : null);
        return delegation;
    }
}
