package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
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

@RequiredArgsConstructor
public class EServiceTemplateMetadataUpdatesSteps {
    private final InteropJourney interopJourney;
    private final EServiceTemplateRequestFactory requestFactory;
    private final ObjectMapper objectMapper;
    private final EServiceTemplateMetadataUpdateConfigurationMapper configurationMapper;
    private final EntityStore entityStore;

    @Given("^un template e-service con versione (assenti|in bozza|pubblicata|sospesa|pubblicata e nuova bozza)$")
    public void createTemplateWithVersions(String version) {

        interopJourney.createEServiceTemplate();

        switch (version) {
            // case "assenti" -> {}
            case "in bozza" -> interopJourney.publishVersion(EServiceTemplateVersionState.DRAFT);
            case "pubblicata" -> interopJourney.publishVersion(EServiceTemplateVersionState.PUBLISHED);
            case "sospesa" -> interopJourney.publishVersion(EServiceTemplateVersionState.SUSPENDED);
            // case "soltanto deprecate" -> {}
            case "pubblicata e nuova bozza" -> {
                interopJourney.publishVersion(EServiceTemplateVersionState.PUBLISHED);
                interopJourney.addVersion(EServiceTemplateVersionState.DRAFT);
            }
            default -> {
                throw new IllegalArgumentException("Unsupported version: " + version);
            }
        }
    }

    @Given("un {userRole} di/del {tenant} richiede la modifica dei metadati del/dell'{currentEServiceTemplate}:")
    public void updateMetadata(UserRole role, Tenant producer, EServiceTemplate eServiceTemplate, List<EServiceTemplateMetadataUpdateConfiguration> metadata) {
        UpdateEServiceTemplateCommand command = requestFactory.defaultUpdateEServiceTemplateCommand();
        configurationMapper.apply(metadata, command);
        interopJourney
            .withProducer(producer, role)
            .patchEServiceTemplate(command);
    }
}
