package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.RestOpenApiInterfaceVerifier;
import it.pagopa.interop.common.eservice_template.domain.DownloadedInterfaceContent;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class OpenApiUseCase {
    private final RestOpenApiInterfaceVerifier verifier = new RestOpenApiInterfaceVerifier();

    public void shouldContainConfiguration(EServiceDescriptor descriptor, RestInterfaceConfiguration expected) {
        verifier.assertConfiguredValues(DownloadedInterfaceContent.descriptorBytes(descriptor), expected);
    }

    public void shouldPreserveStructure(EServiceTemplateVersion original, EServiceDescriptor customized) {
        byte[] baseline = DownloadedInterfaceContent.templateBytes(original);
        String name = original.getInterfaceDocument().getName().toLowerCase(Locale.ROOT);
        verifier.assertStructurePreserved(baseline, DownloadedInterfaceContent.descriptorBytes(customized),
                name.endsWith(".yaml") || name.endsWith(".yml"));
    }

    public void shouldBeValid(EServiceDescriptor descriptor) {
        verifier.assertValid(DownloadedInterfaceContent.descriptorBytes(descriptor));
    }
}
