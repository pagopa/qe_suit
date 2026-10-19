package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.WsdlInterfaceVerifier;
import it.pagopa.interop.common.eservice_template.domain.DownloadedInterfaceContent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoapUseCase {
    private final WsdlInterfaceVerifier verifier = new WsdlInterfaceVerifier();

    public void shouldContainAddresses(EServiceDescriptor descriptor, List<SoapAddressConfiguration> expected) {
        verifier.assertAddresses(DownloadedInterfaceContent.descriptorBytes(descriptor), expected);
    }

    public void shouldPreserveBytes(EServiceTemplateVersion original, EServiceDescriptor customized) {
        verifier.assertOnlyConfiguredLocationsDiffer(
                DownloadedInterfaceContent.templateBytes(original),
                DownloadedInterfaceContent.descriptorBytes(customized));
    }

    public void shouldBeWellFormed(EServiceDescriptor descriptor) {
        verifier.assertWellFormed(DownloadedInterfaceContent.descriptorBytes(descriptor));
    }
}
