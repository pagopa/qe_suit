package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplatePublicationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateInstanceConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class EServiceTemplatePublicationSteps {
    private final InteropJourney journey;
    private final EServiceTemplateRequestFactory factory;
    private final EServiceTemplateConfigurationMapper mapper;
    private final EntityStore entities;

    @Given("i seguenti template (di )e-service pubblicati:")
    public void publishTemplates(List<EServiceTemplatePublicationConfiguration> configurations) {
        for (var configuration : configurations) {
            var command = factory.defaultCreationEServiceTemplateCommand();
            mapper.apply(configuration, command);
            journey.withProducer(configuration.getProducer(), UserRole.ADMIN)
                    .createEServiceTemplate(command, EServiceTemplateVersionState.PUBLISHED);
            entities.upsert(configuration.getAlias(), entities.getLastOrThrow(EServiceTemplate.class));
        }
    }

    @Given("le seguenti istanze di template pubblicate:")
    @Given("le seguenti istanze di template e-service pubblicate:")
    public void publishInstances(List<EServiceTemplateInstanceConfiguration> configurations) {
        for (var configuration : configurations) {
            var template = entities.getByAliasOrThrow(configuration.getTemplate(), EServiceTemplate.class);
            var command = factory.defaultCreationEServiceTemplateInstanceCommand();
            mapper.apply(configuration, command);
            journey.withProducer(Tenant.fromOrganizationId(template.getCreatorId()), UserRole.ADMIN)
                    .createPublishedEServiceTemplateInstance(template, command, configuration.getAlias());
        }
    }
}
