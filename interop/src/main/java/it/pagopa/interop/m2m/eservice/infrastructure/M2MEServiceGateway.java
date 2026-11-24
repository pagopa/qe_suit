package it.pagopa.interop.m2m.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceDraftUpdateSeed;
import it.pagopa.interop.m2m.eservice.application.M2MEServiceUpdateCommand;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class M2MEServiceGateway implements EServiceGateway {

    private final M2MEServiceRestClient restClient;
    private final M2MEServiceMapper mapper;
    private final EntityStore entityStore;
    private final LastApiResponseStore lastApiResponseStore;

    @Override
    public void openEServiceCreation() {
        throw unsupportedOperation();
    }

    @Override
    public void openDraftEServiceGeneralData(EService eService) {
        throw unsupportedOperation();
    }

    @Override
    public void shouldFailToUpdateDraftEServiceDescription(String description,
        EServiceUpdateFailureReason reason) {
        throw unsupportedOperation();
    }

    @Override
    public void shouldFailToUpdateDraftEServiceName(String name,
        EServiceUpdateFailureReason reason) {
        throw unsupportedOperation();
    }

    @Override
    public void shouldFailToCreateEService(EServiceCreationCommand command,
        EServiceCreationFailureReason reason) {
        throw unsupportedOperation();
    }

    @Override
    public EService createEService(EServiceCreationCommand command) {
        throw unsupportedOperation();
    }

    @Override
    public void tryUpdateEServiceMetadata(EServiceRef eServiceRef, EServiceUpdateCommand command) {
        executeUpdate(eServiceRef, requireM2MCommand(command), false);
    }

    @Override
    public void updateEServiceMetadata(EServiceRef eServiceRef, EServiceUpdateCommand command) {
        M2MEServiceUpdateCommand m2mCommand = requireM2MCommand(command);
        executeUpdate(eServiceRef, m2mCommand, true);

        PollingUtils.pollUntil(
                () -> getEService(eServiceRef),
                actual -> matchesUpdate(actual, m2mCommand.getM2mUpdatePayload()),
                "I metadati aggiornati dell'e-service " + eServiceRef.id() + " non sono diventati visibili"
        );
    }

    @Override
    public void shouldFailToUpdateEServiceMetadata(EServiceUpdateFailureReason reason) {
        int expected = switch (reason) {
            case INVALID_DATA, NOT_ALLOWED -> 400;
            case CONFLICT, OUT_OF_DRAFT_OPERATION_FIELD -> 409;
            case UNAUTHORIZED -> 403;
            default -> throw new UnsupportedOperationException("Unsupported EServiceUpdateFailureReason: " + reason);
        };
        Assertions.assertThat(lastApiResponseStore.getLastResponse().getStatusCode()).isEqualTo(expected);
    }

    @Override
    public void deleteEService(EService eService) {
        throw unsupportedOperation();
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        return restClient.getEService(eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(remote -> mapper.toEServicePreservingDescriptors(
                        remote,
                        entityStore.getById(eServiceRef.id(), EService.class).orElse(null)
                ))
                .updateContext()
                .get();
    }

    @Override
    public void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays) {
        throw unsupportedOperation();
    }

    @Override
    public void cloneEService(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef) {
        throw unsupportedOperation();
    }

    @Override
    public void shouldFailToArchiveEService(
            EServiceRef eServiceRef,
            GracePeriodDays gracePeriodDays,
            EServiceArchivingFailureReason reason
    ) {
        throw unsupportedOperation();
    }

    @Override
    public void shouldFailToRequestEServiceArchiving(
            EServiceRef eServiceRef,
            GracePeriodDays gracePeriodDays,
            EServiceArchivingFailureReason reason
    ) {
        throw unsupportedOperation();
    }

    @Override
    public void shouldFailToImportSOAPEService(
            String eserviceName,
            String wsdlResourcePath,
            SoapInterfaceUploadFailureReason reason
    ) {
        throw unsupportedOperation();
    }

    @Override
    public void assertEServiceNotPresentInProducerCatalog(String eserviceName) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptProducerEServiceDetailsRead(EServiceRef eServiceRef) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptNameUpdate(EServiceRef eServiceRef, String name) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptDelegatedArchivingRequest(EServiceRef eServiceRef,
        GracePeriodDays gracePeriodDays) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptDescriptionUpdate(EServiceRef eServiceRef, String description) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptPartialNameUpdate(EServiceRef eServiceRef, String name) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptEServiceImportFromFile(String filePath) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private void executeUpdate(
            EServiceRef eServiceRef,
            M2MEServiceUpdateCommand command,
            boolean waitForSuccess
    ) {
        var action = restClient.updateEServiceMetadata(
                eServiceRef.id(),
                command.getM2mUpdatePayload()
        );
        if (waitForSuccess) {
            action.withPolling(PollingStrategy.UNTIL_SUCCESS).get();
        } else {
            action.withoutPolling().get();
        }
    }

    private static M2MEServiceUpdateCommand requireM2MCommand(EServiceUpdateCommand command) {
        if (!(command instanceof M2MEServiceUpdateCommand m2mCommand)) {
            throw new IllegalArgumentException(
                    "Command must be an instance of " + M2MEServiceUpdateCommand.class.getSimpleName()
            );
        }
        return m2mCommand;
    }

    private static boolean matchesUpdate(EService actual, EServiceDraftUpdateSeed requested) {
        return isApplied(actual.getName(), requested.getName())
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
                && isApplied(actual.getIsConsumerDelegable(), requested.getIsConsumerDelegable())
                && isApplied(actual.getIsClientAccessDelegable(), requested.getIsClientAccessDelegable())
                && isApplied(actual.getPersonalData(), requested.getPersonalData())
                && isApplied(actual.getAsyncExchange(), requested.getAsyncExchange());
    }

    private static <T> boolean isApplied(T actual, T requested) {
        return requested == null || Objects.equals(actual, requested);
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This e-service operation is not supported on M2M v3");
    }
}
