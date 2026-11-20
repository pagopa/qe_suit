package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.command.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateUpdateFailureReason;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.m2m.eservice_template.application.M2MUpdateEServiceTemplateCommand;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTemplateDraftUpdateSeed;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class M2MEServiceTemplateGateway implements EServiceTemplateGateway {

    private final M2MEServiceTemplateRestClient restClient;
    private final M2MEServiceTemplateMapper mapper;
    private final EntityStore entityStore;
    private final LastApiResponseStore lastApiResponseStore;

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateCreationCommand command) {
        throw unsupportedOperation();
    }

    @Override
    public EServiceTemplate getEServiceTemplate(EServiceTemplateRef templateRef) {
        return restClient.getEServiceTemplate(templateRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(remote -> mapper.toTemplate(
                        remote,
                        entityStore.getById(templateRef.id(), EServiceTemplate.class).orElse(null)
                ))
                .updateContext()
                .get();
    }

    @Override
    public EServiceTemplate updateEServiceTemplate(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateCommand command
    ) {
        throw unsupportedOperation();
    }

    @Override
    public EServiceTemplate updateEServiceTemplateMetadata(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateCommand command
    ) {
        M2MUpdateEServiceTemplateCommand m2mCommand = requireM2MCommand(command);
        EServiceTemplateDraftUpdateSeed payload = m2mCommand.getM2mUpdatePayload();

        restClient.updateEServiceTemplateMetadata(templateRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> getEServiceTemplate(templateRef),
                actual -> matchesUpdate(actual, payload),
                "I metadati aggiornati dell'e-service template " + templateRef.id() + " non sono diventati visibili"
        );
    }

    @Override
    public EServiceTemplate tryUpdateEServiceTemplateMetadata(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateCommand command
    ) {
        M2MUpdateEServiceTemplateCommand m2mCommand = requireM2MCommand(command);
        return restClient.updateEServiceTemplateMetadata(templateRef.id(), m2mCommand.getM2mUpdatePayload())
                .withoutPolling()
                .map(remote -> mapper.toTemplate(
                        remote,
                        entityStore.getById(templateRef.id(), EServiceTemplate.class).orElse(null)
                ))
                .updateContext()
                .get();
    }

    @Override
    public void shouldFailToUpdateEServiceTemplateMetadata(EServiceTemplateUpdateFailureReason reason) {
        int expected = switch (reason) {
            case INVALID_DATA -> 400;
            case CONFLICT, OUT_OF_DRAFT_OPERATION_FIELD -> 409;
            case UNAUTHORIZED -> 403;
        };
        Assertions.assertThat(lastApiResponseStore.getLastResponse().getStatusCode()).isEqualTo(expected);
    }

    @Override
    public EServiceTemplate updateTemplateName(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateNameCommand command
    ) {
        throw unsupportedOperation();
    }

    @Override
    public EServiceTemplate updateTemplateIntendedTarget(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateIntendedTargetCommand command
    ) {
        throw unsupportedOperation();
    }

    @Override
    public EServiceTemplate updateTemplateDescription(
            EServiceTemplateRef templateRef,
            UpdateEServiceTemplateDescriptionCommand command
    ) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static M2MUpdateEServiceTemplateCommand requireM2MCommand(UpdateEServiceTemplateCommand command) {
        if (!(command instanceof M2MUpdateEServiceTemplateCommand m2mCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + M2MUpdateEServiceTemplateCommand.class.getSimpleName()
            );
        }
        return m2mCommand;
    }

    private static boolean matchesUpdate(EServiceTemplate actual, EServiceTemplateDraftUpdateSeed requested) {
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
                && isApplied(actual.getPersonalData(), requested.getPersonalData());
    }

    private static <T> boolean isApplied(T actual, T requested) {
        return requested == null || Objects.equals(actual, requested);
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This e-service template operation is not supported on M2M v3");
    }
}
