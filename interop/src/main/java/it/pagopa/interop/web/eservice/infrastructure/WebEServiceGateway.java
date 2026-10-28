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
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceArchivingDialog;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;
import it.pagopa.interop.web.infrastructure.WebCommonGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class WebEServiceGateway implements EServiceGateway {

    private static final String DEFAULT_ARCHIVING_REASON = "Archiviazione per sostituzione dell'e-service.";

    private final EServiceCreatePage eServiceCreatePage;
    private final WebEServiceGeneralDataGateway generalDataGateway;
    private final ProducerEServiceDetailPage producerEServiceDetailPage;
    private final EntityStore entityStore;
    // The shared snackbar reader is only registered in the Cucumber profile.
    private final ObjectProvider<WebCommonGateway> webCommonGatewayProvider;

    @Override
    public EService createEService(EServiceCreationCommand command) {
        if (!(command instanceof WebEServiceCreationCommand creationCommand))
            throw new IllegalArgumentException("Invalid command type");

        eServiceCreatePage.navigateTo();
        eServiceCreatePage.assertLoaded();

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
        openEServiceArchivingDialog(eServiceRef);
        EServiceArchivingDialog dialog = producerEServiceDetailPage.getArchivingDialog();
        fillArchivingDialog(dialog, gracePeriodDays);
        dialog.getArchiveButton().click();
    }

    @Override
    public void shouldFailToArchiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        String expectedMessage = expectedArchivingFailureMessage(reason);
        WebCommonGateway webCommonGateway = webCommonGatewayProvider.getObject();

        openEServiceArchivingDialog(eServiceRef);
        EServiceArchivingDialog dialog = producerEServiceDetailPage.getArchivingDialog();
        fillArchivingDialog(dialog, gracePeriodDays);
        dialog.getArchiveButton().click();

        assertArchivingRejection(webCommonGateway, expectedMessage, reason);
    }

    @Override
    public void shouldFailToRequestEServiceArchiving(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        String expectedMessage = expectedArchivingFailureMessage(reason);
        WebCommonGateway webCommonGateway = webCommonGatewayProvider.getObject();

        openEServiceArchivingDialog(eServiceRef);
        EServiceArchivingDialog dialog = producerEServiceDetailPage.getArchivingRequestDialog();
        fillArchivingDialog(dialog, gracePeriodDays);
        dialog.getRequestArchivingButton().click();

        assertArchivingRejection(webCommonGateway, expectedMessage, reason);
    }

    private void openEServiceArchivingDialog(EServiceRef eServiceRef) {
        EService eService = entityStore.getById(eServiceRef.id(), EService.class)
                .orElseThrow(() -> new IllegalStateException("EService not found in scenario context: " + eServiceRef.id()));
        producerEServiceDetailPage.navigateTo(
                eService.getId().toString(), eService.getActiveDescriptor().getId().toString()
        );
        producerEServiceDetailPage.assertLoaded();
        producerEServiceDetailPage.getActionsMenuButton().click();
        producerEServiceDetailPage.getArchiveEServiceButton().click();
    }

    private void fillArchivingDialog(EServiceArchivingDialog dialog, GracePeriodDays gracePeriodDays) {
        dialog.setGracePeriodDays(gracePeriodDays);
        dialog.getContinueButton().click();
        dialog.setArchivingReason(DEFAULT_ARCHIVING_REASON);
    }

    private String expectedArchivingFailureMessage(EServiceArchivingFailureReason reason) {
        return switch (reason) {
            case ARCHIVING_BEFORE_DESCRIPTOR ->
                    "Non puoi archiviare l\u2019e-service prima di una versione gi\u00e0 in fase di archiviazione. Seleziona una durata pi\u00f9 lunga.";
        };
    }

    private void assertArchivingRejection(WebCommonGateway webCommonGateway, String expectedMessage, EServiceArchivingFailureReason reason) {
        assertThat(webCommonGateway.getSnackbarErrorMessage())
                .as("EService archiving rejection: %s", reason)
                .isEqualTo(expectedMessage);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}