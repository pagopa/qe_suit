package it.pagopa.interop.web.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.web.eservice.application.WebEServiceCreationCommand;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceArchivingDialog;
import it.pagopa.interop.web.infrastructure.WebCommonGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class WebEServiceGateway implements EServiceGateway {

    private static final String DEFAULT_ARCHIVING_REASON = "Archiviazione per sostituzione dell'e-service.";

    private final EServiceCreationPage eServiceCreationPage;
    private final WebEServiceGeneralDataGateway generalDataGateway;
    private final ProducerEServiceDetailPage producerEServiceDetailPage;
    private final EntityStore entityStore;
    // The shared snackbar reader is only registered in the Cucumber profile.
    private final ObjectProvider<WebCommonGateway> webCommonGatewayProvider;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof WebEServiceCreationCommand creationCommand))
            throw new IllegalArgumentException("Invalid command type");

        eServiceCreationPage.navigateTo();
        eServiceCreationPage.assertLoaded();

        generalDataGateway.fillEServiceGeneralData(creationCommand.getWebEServiceGeneralData());

        return generalDataGateway.readEServiceGeneralData();
    }

    @Override
    public void deleteEService(EService eService) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EService getEService(EServiceRef eServiceRef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays) {
        throw new UnsupportedOperationException("Archiving an EService is not supported on the WEB_BROWSER channel");
    }

    @Override
    public void shouldFailToArchiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        String expectedMessage = switch (reason) {
            case ARCHIVING_BEFORE_DESCRIPTOR ->
                    "Non puoi archiviare l\u2019e-service prima di una versione gi\u00e0 in fase di archiviazione. Seleziona una durata pi\u00f9 lunga.";
        };
        EService eService = entityStore.getById(eServiceRef.id(), EService.class)
                .orElseThrow(() -> new IllegalStateException("EService not found in scenario context: " + eServiceRef.id()));
        WebCommonGateway webCommonGateway = webCommonGatewayProvider.getObject();

        submitArchivingRequest(eService, gracePeriodDays);

        assertThat(webCommonGateway.getSnackbarErrorMessage())
                .as("EService archiving rejection: %s", reason)
                .contains(expectedMessage);
    }

    private void submitArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        producerEServiceDetailPage.navigateTo(
                eService.getId().toString(), eService.getActiveDescriptor().getId().toString()
        );
        producerEServiceDetailPage.assertLoaded();
        producerEServiceDetailPage.getActionsMenuButton().click();
        producerEServiceDetailPage.getArchiveEServiceButton().click();

        EServiceArchivingDialog dialog = producerEServiceDetailPage.getArchivingDialog();
        dialog.setGracePeriodDays(gracePeriodDays);
        dialog.getContinueButton().click();
        dialog.setArchivingReason(DEFAULT_ARCHIVING_REASON);
        dialog.getArchiveButton().click();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}
