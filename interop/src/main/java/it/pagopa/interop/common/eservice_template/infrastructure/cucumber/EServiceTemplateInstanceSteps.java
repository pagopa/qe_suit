package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInstanceCreationFailureReason;
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
    private final EntityStore entities;

    @Given("una istanza dell'e-service template in bozza creato dal {tenant} con un attributo certificato e una soglia personalizzata di {int}")
    public void createDraftTemplateInstanceWithCertifiedAttributeAndCustomThreshold(Tenant producer, int customThreshold) {
        interopJourney
                .withProducer(producer)
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

    @When("l'erogatore tenta di istanziare il {eServiceTemplate} e inserisce un link nella parola identificativa")
    public void attemptToCreateTemplateInstanceWithLinkInLabel(EServiceTemplate template) {
        instanceUseCase.shouldFailToCreateEServiceInstance(template,
                command -> command.instanceLabel("https://interopLinkExample.com"),
                EServiceTemplateInstanceCreationFailureReason.HYPERLINK_DETECTED);
    }

    @When("l'erogatore tenta di istanziare il {eServiceTemplate} con la parola identificativa già usata in precedenza")
    public void attemptToCreateTemplateInstanceWithDuplicateLabel(EServiceTemplate template) {
        EService existingInstance = entities.getLastOrThrow(EService.class);
        String label = existingInstance.getInstanceLabel();
        if (label == null || label.isBlank()) {
            throw new IllegalStateException("Existing template instance has no identifying word");
        }
        instanceUseCase.shouldFailToCreateEServiceInstance(template,
                command -> command.instanceLabel(label),
                EServiceTemplateInstanceCreationFailureReason.DUPLICATE_NAME);
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

    @When("l'erogatore tenta di aggiornare i dati dell'{currentEService} con la parola identificativa {string}")
    public void attemptGeneralDataUpdate(EService instance, String instanceLabel) {
        instanceUseCase.attemptGeneralDataUpdate(instance, instanceLabel);
    }

    @When("l'erogatore tenta di modificare la sola parola identificativa del{eServiceByAlias} in {string}")
    public void attemptInstanceLabelUpdate(EService instance, String instanceLabel) {
        instanceUseCase.attemptInstanceLabelUpdate(instance, instanceLabel);
    }

    @Given("un'istanza REST in bozza derivata da un template con una versione OpenAPI non supportata")
    public void draftRestInstanceFromTemplateWithUnsupportedOpenApiVersion() {
        throw new IllegalStateException(
                "Prerequisito ambiente mancante per ERROR_079: il caricamento BFF rifiuta già specifiche OpenAPI "
                        + "non supportate sul template; occorre una fixture QA preesistente isolata."
        );
    }

    @When("l'erogatore tenta di configurare l'interfaccia SOAP della versione {int} dell'{currentEService} con indirizzi validi")
    public void attemptConfigureSoapInterfaceWithValidAddresses(int version, EService instance) {
        List<SoapAddressConfiguration> addresses = entities.getLast(EServiceTemplate.class)
                .map(EServiceTemplate::getLastPublishedVersion)
                .map(v -> v.getInterfaceDocument())
                .filter(doc -> doc != null && doc.hasDownloadedContent())
                .map(doc -> doc.soapAddressesAt("https://erogatore.example/soap"))
                .orElseGet(() -> List.of(
                        new SoapAddressConfiguration("HelloService", "HelloPort", "https://erogatore.example/soap")
                ));
        descriptorUseCase.attemptTemplateInstanceSOAPInterfaceConfiguration(
                instance, instance.getDescriptorOrThrow(version), addresses
        );
    }

    @When("l'erogatore tenta di configurare l'interfaccia REST della versione {int} dell'{currentEService} con dati validi")
    public void attemptConfigureRestInterfaceWithValidData(int version, EService instance) {
        descriptorUseCase.attemptTemplateInstanceRESTInterfaceConfiguration(
                instance, instance.getDescriptorOrThrow(version)
        );
    }
}
