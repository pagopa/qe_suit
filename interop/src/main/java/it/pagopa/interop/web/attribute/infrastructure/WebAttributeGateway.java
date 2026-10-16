package it.pagopa.interop.web.attribute.infrastructure;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
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
    public Attribute createCertifiedAttribute(Attribute attribute) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Attribute getAttribute(AttributeRef attributeRef) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void removeCertifiedAttributeThresholdFromDraftEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex) {

        eServiceEditPage.navigateTo(eServiceRef.id().toString(), eServiceDescriptorRef.id().toString());
        eServiceEditPage.assertLoaded();
        eServiceEditPage.saveDraftAndProceed();

        if (!eServiceEditPage.thresholdAndAttributeWizard().attributesTabs().certifiedAttributesTab().isActive()) {
            eServiceEditPage.thresholdAndAttributeWizard().attributesTabs().certifiedAttributesTab().activate();
        }

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

    private void removeCertifiedAttributeThresholdFromPublishedEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex) {

        eServiceViewPage.navigateTo(eServiceRef.id().toString(), eServiceDescriptorRef.id().toString());
        eServiceViewPage.assertLoaded();

        eServiceViewPage.certifiedAttributeGroups().get(groupIndex-1)
                .certifiedAttributes().get(attributeIndex-1)
                .removeThresholdValue()
                .confirmThesholdValueDeletion();
    }

    @Override
    public void removeCertifiedAttributeThreshold(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, EServiceDescriptorState state, int groupIndex, int attributeIndex) {
        switch (state) {
            case DRAFT:
                removeCertifiedAttributeThresholdFromDraftEService(eServiceRef, eServiceDescriptorRef, groupIndex, attributeIndex);
                break;
            case PUBLISHED:
                removeCertifiedAttributeThresholdFromPublishedEService(eServiceRef, eServiceDescriptorRef, groupIndex, attributeIndex);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported state: " + state);
        }
    }

    @Override
    public void certifiedAttributeThresholdShouldBeRemoved() {
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
