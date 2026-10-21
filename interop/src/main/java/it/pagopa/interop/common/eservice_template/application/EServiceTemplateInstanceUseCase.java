package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceTemplateInstanceUseCase {
    private final EServiceTemplateInstanceGateway gateway;
    private final EServiceTemplateRequestFactory requestFactory;
    private final EServiceTemplateVersionUseCase versions;
    private final EServiceDescriptorUseCase descriptors;

    public EService createEServiceInstance(EServiceTemplate template) {
        return createEServiceInstance(template, requestFactory.defaultCreationEServiceTemplateInstanceCommand());
    }

    public EService createEServiceInstance(EServiceTemplate template, EServiceTemplateInstanceCreationCommand command) {
        return gateway.createEServiceInstance(template, command);
    }

    public void publishEServiceInstance(EServiceTemplate template, EService instance) {
        var descriptor = instance.getLastDraftDescriptor();
        if (template.getTechnology() == EServiceTechnology.SOAP) {
            var document = versions.downloadInterface(template, template.getLastPublishedVersion());
            descriptor = descriptors.configureTemplateInstanceSOAPInterface(
                    instance, descriptor, document.soapAddressesAt("https://erogatore.example/soap"));
        } else {
            descriptor = descriptors.configureTemplateInstanceRESTInterface(instance, descriptor,
                    new RestInterfaceConfiguration(List.of("https://erogatore.example/api"),
                            "Erogatore", "support@erogatore.example", "https://erogatore.example",
                            "https://erogatore.example/terms"));
        }
        descriptor = descriptors.prepareDescriptorForPublication(instance, descriptor,
                (it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand) null);
        descriptors.publishDescriptor(instance, descriptor);
    }
}
