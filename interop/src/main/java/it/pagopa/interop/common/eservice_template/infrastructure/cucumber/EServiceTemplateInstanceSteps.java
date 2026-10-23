package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceTemplateInstanceSteps {
    private final EServiceTemplateInstanceUseCase instanceUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;

    @Given("una istanza dell'e-service template in bozza creato dal {tenant} con un attributo certificato e una soglia personalizzata di {int}")
    public void createDraftTemplateInstanceWithCertifiedAttributeAndCustomThreshold(Tenant producer, int customThreshold) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createCertifiedAttribute()
                .createEServiceTemplate(
                        config -> config
                                .dailyCallsPerConsumer(50)
                                .dailyCallsTotal(1000),
                        EServiceTemplateVersionState.DRAFT)
                .updateTemplateVersionWithContextAttributes(
                        config -> config
                                .dailyCallsPerConsumer(customThreshold - 10)
                                .dailyCallsTotal(customThreshold + 100),
                        EServiceTemplateVersionState.PUBLISHED
                )
                .waitUntilEServiceTemplate(template -> template.getLastPublishedVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                .createEServiceInstanceTemplate()
                .updateEServiceInstanceDescriptorWithContextAttributes(
                        config -> config
                                .audience(List.of("QA"))
                                .dailyCallsPerConsumer(customThreshold - 10)
                                .dailyCallsTotal(customThreshold + 100),
                        customThreshold,
                        EServiceDescriptorState.DRAFT
                );
    }

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
