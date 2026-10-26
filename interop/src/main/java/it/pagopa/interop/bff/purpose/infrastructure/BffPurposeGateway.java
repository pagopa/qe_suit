package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.purpose.application.BffPurposeCreateCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffPurposeGateway implements PurposeGateway {
    private final BffPurposeRestClient restClient;
    private final BffPurposeMapper mapper;
    private final BffPurposeCommandFactory commandFactory;

    @Override
    public Purpose getPurpose(PurposeRef purposeRef) {
        return restClient.getPurpose(purposeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toPurpose)
                .updateContext()
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
                .updateContext()
                .get();
    }

    @Override
    public Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef) {
        return restClient.createReversePurpose(commandFactory.reverseCreationRequest(eServiceRef, riskAnalysisRef))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef) {
        return restClient.createPurposeFromTemplate(purposeTemplateRef.id(), commandFactory.creationFromTemplateRequest(eServiceRef))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(createdResource -> getPurpose(PurposeRef.of(createdResource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        restClient.activatePurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.getLastActiveVersion() != null && purpose.getLastActiveVersion().getRef().equals(purposeVersionRef)
        );
    }

    @Override
    public Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef) {
        it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegation = getDelegation(delegationRef);

        restClient.suspendPurposeVersion(purposeRef.id(), purposeVersionRef.id(), delegation)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getPurpose(purposeRef),
                purpose -> purpose.findVersionById(purposeVersionRef.id())
                                .orElseThrow(() -> new IllegalStateException("Purpose version not found"))
                                .getRef().equals(purposeVersionRef)
        );
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
