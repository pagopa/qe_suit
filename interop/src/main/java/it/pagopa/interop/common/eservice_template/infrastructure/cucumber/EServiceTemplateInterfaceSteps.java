package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTemplateInterfaceFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.RestOpenApiInterfaceVerifier;
import it.pagopa.interop.common.eservice_template.domain.WsdlInterfaceVerifier;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class EServiceTemplateInterfaceSteps {
    private final InteropJourney journey;
    private final EServiceTemplateUseCase templateUseCase;
    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final EntityStore entityStore;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WsdlInterfaceVerifier wsdlVerifier = new WsdlInterfaceVerifier();
    private final RestOpenApiInterfaceVerifier restVerifier = new RestOpenApiInterfaceVerifier();

    private List<SoapAddressConfiguration> expectedAddresses;
    private RestInterfaceConfiguration expectedRestConfiguration;
    private String interfaceFixture;
    private EService createdEService;

    @After
    public void cleanupCreatedEServiceInstance() {
        if (createdEService != null) {
            log.info("PIN-10024 cleanup draft instance: eServiceId={}", createdEService.getId());
            eServiceUseCase.deleteEService(createdEService);
        }
    }

    public EServiceTemplateInterfaceSteps(
            InteropJourney journey,
            EServiceTemplateUseCase templateUseCase,
            EServiceUseCase eServiceUseCase,
            EServiceDescriptorUseCase descriptorUseCase,
            EntityStore entityStore
    ) {
        this.journey = journey;
        this.templateUseCase = templateUseCase;
        this.eServiceUseCase = eServiceUseCase;
        this.descriptorUseCase = descriptorUseCase;
        this.entityStore = entityStore;
    }

    @Given("un template e-service SOAP pubblicato con l'interfaccia {string}")
    public void givenPublishedSoapTemplate(String fixture) {
        interfaceFixture = fixture;
        journey.createPublishedEServiceTemplate(EServiceTechnology.SOAP, "assets/eservice-template/" + fixture);
    }

    @Given("un template e-service REST pubblicato con l'interfaccia {string}")
    public void givenPublishedRestTemplate(String fixture) {
        interfaceFixture = fixture;
        journey.createPublishedEServiceTemplate(EServiceTechnology.REST, "assets/eservice-template/" + fixture);
    }

    @When("l'erogatore configura con successo l'interfaccia SOAP di un nuovo e-service dal template con i seguenti indirizzi:")
    public void configureSoapInterface(DataTable table) {
        expectedAddresses = List.copyOf(table.asMaps(String.class, String.class).stream()
                .map(row -> new SoapAddressConfiguration(
                        row.get("servizio"),
                        row.get("porta"),
                        row.get("indirizzo")
                ))
                .toList());
        EService eService = createTemplateInstance();
        log.info("PIN-10024 SOAP success: fixture={}, eServiceId={}, descriptorId={}, addresses={}",
                interfaceFixture, eService.getId(), eService.getLastDraftDescriptor().getId(), expectedAddresses);
        descriptorUseCase.configureTemplateInstanceSOAPInterface(
                eService, eService.getLastDraftDescriptor(), expectedAddresses
        );
    }

    @When("l'erogatore tenta di configurare l'interfaccia SOAP di un nuovo e-service dal template ottenendo un rifiuto per {string} con i seguenti indirizzi:")
    public void shouldFailToConfigureSoapInterface(String reason, DataTable table) {
        expectedAddresses = List.copyOf(table.asMaps(String.class, String.class).stream()
                .map(row -> new SoapAddressConfiguration(
                        row.get("servizio"),
                        row.get("porta"),
                        row.get("indirizzo")
                ))
                .toList());
        EService eService = createTemplateInstance();
        log.info("PIN-10024 SOAP rejection: fixture={}, eServiceId={}, descriptorId={}, reason={}, addresses={}",
                interfaceFixture, eService.getId(), eService.getLastDraftDescriptor().getId(), reason, expectedAddresses);
        descriptorUseCase.shouldFailToConfigureSOAPInterface(
                eService,
                eService.getLastDraftDescriptor(),
                expectedAddresses,
                failureReason(reason)
        );
    }

    @When("l'erogatore configura con successo l'interfaccia REST di un nuovo e-service dal template con i seguenti dati:")
    public void configureRestInterface(DataTable table) throws IOException {
        Map<String, String> rows = table.asMaps(String.class, String.class).stream()
                .collect(java.util.stream.Collectors.toMap(row -> row.get("campo"), row -> row.get("valore")));
        expectedRestConfiguration = new RestInterfaceConfiguration(
                objectMapper.readValue(rows.get("serverUrls"), new TypeReference<>() {}),
                rows.get("contactName"),
                rows.get("contactEmail"),
                rows.get("contactUrl"),
                rows.get("termsAndConditionsUrl")
        );
        EService eService = createTemplateInstance();
        log.info("PIN-10024 REST success: fixture={}, eServiceId={}, descriptorId={}, configuration={}",
                interfaceFixture, eService.getId(), eService.getLastDraftDescriptor().getId(), expectedRestConfiguration);
        descriptorUseCase.configureTemplateInstanceRESTInterface(
                eService, eService.getLastDraftDescriptor(), expectedRestConfiguration
        );
    }

    @And("l'erogatore recupera l'interfaccia dell'e-service")
    public void retrieveInstanceInterface() {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor descriptor = eService.getLastDraftDescriptor();
        Assertions.assertThat(descriptor.getInterfaceDocument())
                .as("Interfaccia alla radice del descriptor dell'istanza")
                .isNotNull();
        descriptorUseCase.downloadInterfaceBytes(eService, descriptor);
    }

    @Then("l'e-service ha la prima versione in stato bozza")
    public void assertDraftDescriptor() {
        Assertions.assertThat(entityStore.getLastOrThrow(EService.class).getLastDraftDescriptor().getState())
                .isEqualTo(EServiceDescriptorState.DRAFT);
    }

    @And("ogni porta del WSDL contiene l'indirizzo configurato per quella porta")
    public void assertConfiguredWsdlAddresses() {
        wsdlVerifier.assertAddresses(instanceInterfaceBytes(), expectedAddresses);
    }

    @And("il WSDL è identico byte per byte all'originale salvo i valori degli indirizzi configurati")
    public void assertWsdlBytesPreserved() {
        log.info("PIN-10024 WSDL byte comparison: fixture={}, templateId={}, documentId={}, eServiceId={}, descriptorId={}",
                interfaceFixture, entityStore.getLastOrThrow(EServiceTemplate.class).getId(),
                templateInterface().getId(), entityStore.getLastOrThrow(EService.class).getId(),
                entityStore.getLastOrThrow(EService.class).getLastDraftDescriptor().getId());
        wsdlVerifier.assertOnlyConfiguredLocationsDiffer(templateInterface().getContent(), instanceInterfaceBytes());
    }

    @And("il WSDL è XML ben formato")
    public void assertWsdlWellFormed() {
        wsdlVerifier.assertWellFormed(instanceInterfaceBytes());
    }

    @Then("non è disponibile un'interfaccia dell'istanza accettata come valida")
    public void assertNoValidInstanceInterface() {
        Assertions.assertThat(entityStore.getLastOrThrow(EService.class).getLastDraftDescriptor().getInterfaceDocument())
                .isNull();
    }

    @And("l'interfaccia del template è rimasta invariata")
    public void assertTemplateInterfaceUnchanged() {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        EServiceTemplateInterface original = templateInterface();
        byte[] current = templateUseCase.downloadInterfaceBytes(template, original.getId());
        Assertions.assertThat(current)
                .as("Interfaccia originale del template %s, documento %s", template.getId(), original.getId())
                .containsExactly(original.getContent());
    }

    @And("la specifica OpenAPI contiene i dati configurati")
    public void assertRestOpenApiValues() {
        log.info("PIN-10024 OpenAPI comparison: fixture={}, templateId={}, documentId={}, eServiceId={}, configuration={}",
                interfaceFixture, entityStore.getLastOrThrow(EServiceTemplate.class).getId(),
                templateInterface().getId(), entityStore.getLastOrThrow(EService.class).getId(),
                expectedRestConfiguration);
        restVerifier.assertConfiguredValues(instanceInterfaceBytes(), expectedRestConfiguration);
    }

    @And("la specifica OpenAPI conserva schemi e riferimenti senza espansioni o alias introdotti dalla validazione")
    public void assertRestOpenApiSchemasAndReferences() {
        log.info("PIN-10024 OpenAPI structure comparison: fixture={}, templateId={}, documentId={}, eServiceId={}",
                interfaceFixture, entityStore.getLastOrThrow(EServiceTemplate.class).getId(),
                templateInterface().getId(), entityStore.getLastOrThrow(EService.class).getId());
        restVerifier.assertStructurePreserved(
                templateInterface().getContent(),
                instanceInterfaceBytes(),
                isYamlTemplate()
        );
    }

    @And("la specifica OpenAPI è valida")
    public void assertRestOpenApiValid() {
        restVerifier.assertValid(instanceInterfaceBytes());
    }

    private EService createTemplateInstance() {
        EService eService = eServiceUseCase.createEServiceInstanceFromTemplate(
                entityStore.getLastOrThrow(EServiceTemplate.class)
        );
        createdEService = eService;
        log.info("PIN-10024 instance created: fixture={}, templateId={}, eServiceId={}, descriptorId={}",
                interfaceFixture, entityStore.getLastOrThrow(EServiceTemplate.class).getId(),
                eService.getId(), eService.getLastDraftDescriptor().getId());
        return eService;
    }

    private byte[] instanceInterfaceBytes() {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return descriptorUseCase.downloadInterfaceBytes(eService, eService.getLastDraftDescriptor());
    }

    private EServiceTemplateInterface templateInterface() {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        UUID documentId = template.getVersions().get(0).getInterfaceDocument().id();
        return entityStore.getById(documentId, EServiceTemplateInterface.class).orElseThrow();
    }

    private boolean isYamlTemplate() {
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        EServiceTemplateInterface original = templateInterface();
        return original.getName().toLowerCase().endsWith(".yaml")
                || original.getName().toLowerCase().endsWith(".yml");
    }

    private static EServiceTemplateInterfaceFailureReason failureReason(String reason) {
        return reason.contains("URL")
                ? EServiceTemplateInterfaceFailureReason.URL_NOT_ALLOWED
                : EServiceTemplateInterfaceFailureReason.URL_COUNT_MISMATCH;
    }
}
