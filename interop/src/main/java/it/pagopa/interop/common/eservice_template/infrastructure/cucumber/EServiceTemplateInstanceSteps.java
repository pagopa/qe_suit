package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateSOAPInterfaceFailureReason;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceTemplateInstanceSteps {
    private final EServiceTemplateInstanceUseCase instanceUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;

    @When("l'erogatore crea/istanzia un nuovo e-service dal {eServiceTemplate}")
    public void createTemplateInstance(EServiceTemplate template) {
        instanceUseCase.createEServiceInstance(template);
    }

    @When("l'erogatore configura con successo l'interfaccia SOAP del {eServiceDescriptor} dell'{currentEService} con i seguenti indirizzi:")
    public void configureSoapInterface(
            EServiceDescriptor descriptor,
            EService eService,
            List<SoapAddressConfiguration> addresses
    ) {
        descriptorUseCase.configureTemplateInstanceSOAPInterface(
                eService, descriptor, addresses
        );
    }

    @When("l'erogatore tenta di configurare l'interfaccia SOAP del {eServiceDescriptor} dell'{currentEService} ottenendo un rifiuto per \"{soapInterfaceFailureReason}\" con i seguenti indirizzi:")
    public void rejectSoapInterface(
            EServiceDescriptor descriptor,
            EService eService,
            EServiceTemplateSOAPInterfaceFailureReason reason,
            List<SoapAddressConfiguration> addresses
    ) {
        descriptorUseCase.shouldFailToConfigureSOAPInterface(
                eService,
                descriptor,
                addresses,
                reason
        );
    }

    @When("l'erogatore configura con successo l'interfaccia REST del {eServiceDescriptor} dell'{currentEService} con i seguenti dati:")
    public void configureRestInterface(
            EServiceDescriptor descriptor,
            EService eService,
            RestInterfaceConfiguration configuration
    ) {
        descriptorUseCase.configureTemplateInstanceRESTInterface(
                eService, descriptor, configuration
        );
    }

}
