package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateMetadataUpdateConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateMetadataUpdateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class EServiceTemplateMetadataUpdatesSteps {
    private final InteropJourney interopJourney;
    private final EServiceTemplateRequestFactory requestFactory;
    private final ObjectMapper objectMapper;
    private final EServiceTemplateMetadataUpdateConfigurationMapper configurationMapper;
    private final EntityStore entityStore;
    private Map<String, String> requestedMetadata = Map.of();
    private EServiceTemplate initialEServiceTemplate;

    @Given("^un template e-service con versione (assenti|in bozza|pubblicata|sospesa|pubblicata e nuova bozza)$")
    public void createTemplateWithVersions(String version) {

        interopJourney.createEServiceTemplate();

        switch (version) {
            // case "assenti" -> {}
            case "in bozza" -> interopJourney.publishVersion(EServiceTemplateVersionState.DRAFT);
            case "pubblicata" -> interopJourney.publishVersion(EServiceTemplateVersionState.PUBLISHED);
            case "sospesa" -> {
                interopJourney
                        .publishVersion(EServiceTemplateVersionState.SUSPENDED)
                        .waitUntilEServiceTemplate(template -> template.getLatestVersion().getState() == EServiceTemplateVersionState.SUSPENDED);
            }
            // case "soltanto deprecate" -> {}
            case "pubblicata e nuova bozza" -> {
                interopJourney
                        .publishVersion(EServiceTemplateVersionState.PUBLISHED)
                        .waitUntilEServiceTemplate(template -> template.getLatestVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                        .addVersion(EServiceTemplateVersionState.DRAFT);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported version: " + version);
            }
        }
    }

    @Given("un {userRole} di/del {tenant} richiede la modifica dei metadati dell'{currentEServiceTemplate}:")
    @Given("un {userRole} di/del {tenant} richiede la modifica dei metadati del {currentEServiceTemplate}:")
    public void updateMetadata(UserRole role, Tenant producer, EServiceTemplate eServiceTemplate, List<EServiceTemplateMetadataUpdateConfiguration> metadata) {
        initialEServiceTemplate = eServiceTemplate.toBuilder()
                // .versions(eServiceTemplate.getVersions())
                .build();
        UpdateEServiceTemplateCommand command = requestFactory.defaultUpdateEServiceTemplateCommand(true);
        requestedMetadata = configurationMapper.apply(metadata, command);
        interopJourney
                .withProducer(producer, role)
                .patchEServiceTemplate(command);
    }

    @Then("il {currentEServiceTemplate} presenta i metadati richiesti e conserva quelli non modificati")
    public void checkMetadata(EServiceTemplate actualEServiceTemplate) {
        EServiceTemplate expectedEServiceTemplate = initialEServiceTemplate.toBuilder()
                .name(requestedMetadata.getOrDefault("nome", initialEServiceTemplate.getName()))
                .description(requestedMetadata.getOrDefault("descrizione", initialEServiceTemplate.getDescription()))
                .intendedTarget(requestedMetadata.getOrDefault("intendedTarget", initialEServiceTemplate.getIntendedTarget()))
                .technology(requestedEnum("technology", EServiceTechnology.class, initialEServiceTemplate.getTechnology()))
                .mode(requestedEnum("mode", EServiceMode.class, initialEServiceTemplate.getMode()))
                .isSignalHubEnabled(requestedBoolean("signalHubEnabled", initialEServiceTemplate.getIsSignalHubEnabled()))
                .personalData(requestedBoolean("handlePersonalData", initialEServiceTemplate.getPersonalData()))
                .asyncExchange(requestedBoolean("asyncExchange", initialEServiceTemplate.getAsyncExchange()))
                .build();

        assertThat(actualEServiceTemplate)
                .as("E-service %s dopo la modifica dei metadati")
                .isEqualTo(expectedEServiceTemplate);
    }

    private Boolean requestedBoolean(String field, Boolean initialValue) {
        return requestedMetadata.containsKey(field)
                ? requestedMetadata.get(field) == null ? null : Boolean.valueOf(requestedMetadata.get(field))
                : initialValue;
    }

    private <T extends Enum<T>> T requestedEnum(String field, Class<T> enumType, T initialValue) {
        if (!requestedMetadata.containsKey(field)) {
            return initialValue;
        }
        String value = requestedMetadata.get(field);
        return value == null ? null : Enum.valueOf(enumType, value);
    }
}
