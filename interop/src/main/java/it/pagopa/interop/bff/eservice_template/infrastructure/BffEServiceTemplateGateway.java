package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice_template.application.*;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateUpdateFailureReason;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchUpdateEServiceTemplateSeed;
import it.pagopa.utils.async.PollingUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateGateway implements EServiceTemplateGateway {

    private final BffEServiceTemplateRestClient restClient;
    private final BffEServiceTemplateVersionGateway versionGateway;
    private final EntityStore entityStore;
    private final BffEServiceTemplateMappingService assembler;
    private final LastApiResponseStore lastApiResponseStore;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffEServiceTemplateCreationCommand.class.getSimpleName());

        var created = restClient.createEServiceTemplate(bffCommand.getBffCreationPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return refresh(new EServiceTemplateRef(created.getId()), new EServiceTemplateVersionRef(created.getVersionId()));
    }

    @Override
    public EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef) {
        return restClient.getEServiceTemplate(templateRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(templateDetails -> {
                    Optional<EServiceTemplate> maybeTemplate = entityStore.getById(templateRef.id(), EServiceTemplate.class);
                    return assembler.toEServiceTemplatePreservingVersions(templateDetails, maybeTemplate.orElse(null));
                })
                .updateContext()
                .get();
    }

    @Override
    public EServiceTemplate updateEServiceTemplate(EServiceTemplateRef templateRef, UpdateEServiceTemplateCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateCommand.class.getSimpleName());

        return restClient.updateEServiceTemplate(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateEServiceTemplateMetadata(EServiceTemplateRef templateRef, UpdateEServiceTemplateCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateCommand.class.getSimpleName());

        var payload = bffCommand.asPatchEServiceTemplateSeed();
        restClient.patchEServiceTemplate(templateRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
            () -> getEServiceTemplate(templateRef),
            eServiceTemplate -> matchesUpdate(eServiceTemplate, payload),
            "I metadati aggiornati dell'e-service template " + templateRef.id() + " non sono diventati visibili"
        );
    }

    @Override
    public EServiceTemplate tryUpdateEServiceTemplateMetadata(EServiceTemplateRef templateRef, UpdateEServiceTemplateCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateCommand.class.getSimpleName());

        var payload = bffCommand.asPatchEServiceTemplateSeed();
        return restClient.patchEServiceTemplate(templateRef.id(), payload)
                .withoutPolling()
                .map(ignored -> entityStore.getById(templateRef.id(), EServiceTemplate.class)
                        .orElseThrow(() -> new IllegalStateException(
                                "E-service template " + templateRef.id() + " not found in entity store after patch attempt"
                        )))
                .get();
    }

    @Override
    public void shouldFailToUpdateEServiceTemplateMetadata(EServiceTemplateUpdateFailureReason reason) {
        var expected = switch (reason) {
            case INVALID_DATA -> 400;
            case CONFLICT, OUT_OF_DRAFT_OPERATION_FIELD -> 409;
            case UNAUTHORIZED -> 403;
        };
        var actual = lastApiResponseStore.getLastResponse().getStatusCode();
        assertThat(actual).isEqualTo(expected);
    }

    private static boolean matchesUpdate(EServiceTemplate actual, PatchUpdateEServiceTemplateSeed requested) {
        return isApplied(actual.getName(), requested.getName())
                && isApplied(actual.getIntendedTarget(), requested.getIntendedTarget())
                && isApplied(actual.getDescription(), requested.getDescription())
                && isApplied(
                        actual.getTechnology() == null ? null : actual.getTechnology().getValue(),
                        requested.getTechnology() == null ? null : requested.getTechnology().getValue()
                )
                && isApplied(
                        actual.getMode() == null ? null : actual.getMode().getValue(),
                        requested.getMode() == null ? null : requested.getMode().getValue()
                )
                && isApplied(actual.getIsSignalHubEnabled(), requested.getIsSignalHubEnabled())
                && isApplied(actual.getPersonalData(), requested.getPersonalData())
                && isApplied(actual.getAsyncExchange(), requested.getAsyncExchange());
    }

    private static <T> boolean isApplied(T actual, T requested) {
        return requested == null || Objects.equals(actual, requested);
    }

    @Override
    public EServiceTemplate updateTemplateName(EServiceTemplateRef templateRef, UpdateEServiceTemplateNameCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateNameCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateNameCommand.class.getSimpleName());

        return restClient.updateTemplateName(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateTemplateIntendedTarget(EServiceTemplateRef templateRef, UpdateEServiceTemplateIntendedTargetCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateIntendedTargetCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateIntendedTargetCommand.class.getSimpleName());

        return restClient.updateTemplateIntendedTarget(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceTemplate updateTemplateDescription(EServiceTemplateRef templateRef, UpdateEServiceTemplateDescriptionCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateDescriptionCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateDescriptionCommand.class.getSimpleName());

        return restClient.updateTemplateDescription(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getEServiceTemplate(templateRef))
                .get();
    }

    @Override
    public EServiceRiskAnalysis addRiskAnalysis(
            EServiceTemplateRef templateRef,
            CreateEServiceTemplateRiskAnalysisCommand command
    ) {
        if (!(command instanceof BffCreateEServiceTemplateRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffCreateEServiceTemplateRiskAnalysisCommand.class.getSimpleName()
            );
        }

        restClient.addRiskAnalysis(templateRef.id(), bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        EServiceRiskAnalysis eServiceRiskAnalysis = PollingUtils.pollUntil(
            () -> getEServiceTemplate(templateRef).getRiskAnalyses().stream()
                .filter(analysis -> bffCommand.getName().equals(analysis.getName()))
                .findFirst(),
            Optional::isPresent
        ).orElseThrow();
        entityStore.upsert(eServiceRiskAnalysis);
        return eServiceRiskAnalysis;
    }

    private EServiceTemplate refresh(EServiceTemplateRef templateRef, EServiceTemplateVersionRef versionRef) {
        EServiceTemplateVersion createdVersion = versionGateway.getEServiceTemplateVersion(templateRef, versionRef);
        EServiceTemplate savedTemplate = getEServiceTemplate(templateRef);
        savedTemplate.addVersion(createdVersion);

        return savedTemplate;
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    @Override
    public void attemptTemplateNameUpdate(EServiceTemplateRef templateRef, UpdateEServiceTemplateNameCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateNameCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateNameCommand.class.getSimpleName());
        restClient.updateTemplateName(templateRef.id(), bffCommand.getBffPayload()).withoutPolling();
    }

    @Override
    public void attemptTemplateDescriptionUpdate(EServiceTemplateRef templateRef, UpdateEServiceTemplateDescriptionCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateDescriptionCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateDescriptionCommand.class.getSimpleName());
        restClient.updateTemplateDescription(templateRef.id(), bffCommand.getBffPayload()).withoutPolling();
    }

    @Override
    public void attemptTemplateIntendedTargetUpdate(EServiceTemplateRef templateRef, UpdateEServiceTemplateIntendedTargetCommand command) {
        if (!(command instanceof BffUpdateEServiceTemplateIntendedTargetCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffUpdateEServiceTemplateIntendedTargetCommand.class.getSimpleName());
        restClient.updateTemplateIntendedTarget(templateRef.id(), bffCommand.getBffPayload()).withoutPolling();
    }

    @Override
    public void attemptTemplateCreation(EServiceTemplateCreationCommand command) {
        if (!(command instanceof BffEServiceTemplateCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of " + BffEServiceTemplateCreationCommand.class.getSimpleName());
        restClient.createEServiceTemplate(bffCommand.getBffCreationPayload()).withoutPolling();
    }

    @Override
    public void attemptAddRiskAnalysis(
            EServiceTemplateRef templateRef,
            CreateEServiceTemplateRiskAnalysisCommand command
    ) {
        if (!(command instanceof BffCreateEServiceTemplateRiskAnalysisCommand bffCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + BffCreateEServiceTemplateRiskAnalysisCommand.class.getSimpleName()
            );
        }
        restClient.addRiskAnalysis(templateRef.id(), bffCommand.getBffPayload()).withoutPolling();
    }
}
