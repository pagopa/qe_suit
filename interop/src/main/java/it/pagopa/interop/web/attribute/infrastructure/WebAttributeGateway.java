package it.pagopa.interop.web.attribute.infrastructure;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.web.eservice.infrastructure.WebEServiceGeneralDataGateway;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceEditPage;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceViewPage;
import it.pagopa.interop.web.infrastructure.WebCommonGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class WebAttributeGateway implements AttributeGateway {

    private final EServiceEditPage eServiceEditPage;
    private final EServiceViewPage eServiceViewPage;
    private final WebCommonGateway webCommonGateway;

    @Override
    public void removeCertifiedAttributeThresholdFromDraftEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex) {

        eServiceEditPage.navigateTo(eServiceRef.id().toString(), eServiceDescriptorRef.id().toString());
        eServiceEditPage.assertLoaded();
        eServiceEditPage.saveDraftAndProceed();

        if (!eServiceEditPage.thresholdAndAttributeWizard().attributesTabs().certifiedAttributesTab().isActive()) {
            eServiceEditPage.thresholdAndAttributeWizard().attributesTabs().certifiedAttributesTab().activate();
        }

        /*var description = eServiceEditPage.thresholdAndAttributeWizard().attributesTabs().certifiedAttributesTab().description().read();
        var groups = eServiceEditPage.thresholdAndAttributeWizard().attributesTabs().certifiedAttributesTab().certifiedAttributeGroups();
        var group = groups.get(groupIndex - 1);
        var deleteAttributeButton = group.deleteAttributeButton();
        // deleteAttributeButton.click();

        var attribute = group.certifiedAttributes().get(attributeIndex - 1);
        var attributeName = attribute.name().read();
        var attributeThresholdInfo = attribute.thresholdInfo().read();

        attribute.removeThresholdValue();
        // attribute.remove();

        SuitUtils.debug(attribute.get().orElseThrow());*/

        var attribute = eServiceEditPage.thresholdAndAttributeWizard().attributesTabs()
                .certifiedAttributesTab()
                .certifiedAttributeGroups().get(groupIndex-1)
                .certifiedAttributes().get(attributeIndex-1);
        attribute.removeThresholdValue();
        assertThat(attribute.isThresholdNotSet())
                .as(
                        "Validation result for step remove certified attribute threshold"
                )
                .isTrue();
    }

    @Override
    public void removeCertifiedAttributeThresholdFromPublishedEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex) {

        eServiceViewPage.navigateTo(eServiceRef.id().toString(), eServiceDescriptorRef.id().toString());
        eServiceViewPage.assertLoaded();

        eServiceViewPage.certifiedAttributeGroups().get(groupIndex-1)
                .certifiedAttributes().get(attributeIndex-1)
                .removeThresholdValue()
                .confirmThesholdValueDeletion();
    }

    @Override
    public void certifiedAttributeThresholdRemoved() {
        String actualSuccessMessage = webCommonGateway.getSnackbarSuccessMessage();
        String successMessage = "Hai rimosso la soglia di chiamate API dell’attributo certificato.";

        assertThat(actualSuccessMessage)
                .as("Messaggio di successo visualizzato nella snackbar")
                .contains(successMessage);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}
