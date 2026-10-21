package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateInstanceConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplatePublicationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import io.cucumber.cucumberexpressions.ExpressionFactory;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceTemplatePublicationStepsTest {
    private final InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
    private final InMemoryEntityStore entities = new InMemoryEntityStore();
    private final EServiceTemplatePublicationSteps steps = new EServiceTemplatePublicationSteps(
            journey, new BffEServiceTemplateRequestFactory(),
            Mappers.getMapper(EServiceTemplateConfigurationMapper.class), entities);

    @Test
    void aliasesAreRegisteredAndCreatorIsExplicitForEveryTemplate() {
        var first = configuration("TPL-1", "Template Stampo Alfa", Tenant.COMUNE_DI_MILANO);
        var second = configuration("TPL-2", "Template Stampo Beta", Tenant.SOGECAP);
        when(journey.createEServiceTemplate(any(), eq(EServiceTemplateVersionState.PUBLISHED))).thenAnswer(invocation -> {
            var command = (BffEServiceTemplateCreationCommand) invocation.getArgument(0);
            entities.upsert(EServiceTemplate.builder().id(UUID.randomUUID())
                    .name(command.getBffCreationPayload().getName()).build());
            return journey;
        });

        steps.publishTemplates(List.of(first, second));

        assertThat(entities.getByAliasOrThrow("TPL-1", EServiceTemplate.class).getName()).contains("Template Stampo Alfa");
        assertThat(entities.getByAliasOrThrow("TPL-2", EServiceTemplate.class).getName()).contains("Template Stampo Beta");
        var order = inOrder(journey);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        order.verify(journey).createEServiceTemplate(any(EServiceTemplateCreationCommand.class), eq(EServiceTemplateVersionState.PUBLISHED));
        order.verify(journey).withProducer(Tenant.SOGECAP, UserRole.ADMIN);
        order.verify(journey).createEServiceTemplate(any(EServiceTemplateCreationCommand.class), eq(EServiceTemplateVersionState.PUBLISHED));
    }

    @Test
    void instanceUsesReferencedTemplatesCreatorRatherThanLastScenarioSessionAndKeepsSuffix() {
        var template = EServiceTemplate.builder().id(UUID.randomUUID())
                .creatorId(Tenant.COMUNE_DI_MILANO.getOrganizationId()).build();
        entities.upsert("TPL-1", template);
        var configuration = new EServiceTemplateInstanceConfiguration();
        configuration.setAlias("ES-1");
        configuration.setTemplate("TPL-1");
        configuration.setInstanceLabel("eservice-1");

        steps.publishInstances(List.of(configuration));

        var command = ArgumentCaptor.forClass(EServiceTemplateInstanceCreationCommand.class);
        var order = inOrder(journey);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        order.verify(journey).createPublishedEServiceTemplateInstance(eq(template), command.capture(), eq("ES-1"));
        assertThat(((BffEServiceTemplateInstanceCreationCommand) command.getValue()).getBffPayload().getInstanceLabel())
                .contains("eservice-1");
    }

    @Test
    void unknownTemplateAliasFailsBeforeAnyJourneyOperation() {
        var configuration = new EServiceTemplateInstanceConfiguration();
        configuration.setAlias("ES-1");
        configuration.setTemplate("missing");

        assertThatThrownBy(() -> steps.publishInstances(List.of(configuration)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing");
        verifyNoInteractions(journey);
    }

    @Test
    void tableSynonymsShareTheSameBindingImplementations() throws NoSuchMethodException {
        var expressionFactory = new ExpressionFactory(new ParameterTypeRegistry(Locale.ITALIAN));
        var templateExpression = Arrays.stream(EServiceTemplatePublicationSteps.class
                        .getMethod("publishTemplates", List.class).getAnnotationsByType(Given.class))
                .map(Given::value)
                .findFirst()
                .orElseThrow();
        var expression = expressionFactory.createExpression(templateExpression);
        assertThat(expression.match("i seguenti template e-service pubblicati:")).isNotNull();
        assertThat(expression.match("i seguenti template di e-service pubblicati:")).isNotNull();
        assertThat(Arrays.stream(EServiceTemplatePublicationSteps.class.getMethod("publishInstances", List.class)
                .getAnnotationsByType(Given.class)).map(Given::value))
                .containsExactly("le seguenti istanze di template pubblicate:", "le seguenti istanze di template e-service pubblicate:");
    }

    private static EServiceTemplatePublicationConfiguration configuration(String alias, String name, Tenant creator) {
        var configuration = new EServiceTemplatePublicationConfiguration();
        configuration.setAlias(alias);
        configuration.setName(name);
        configuration.setProducer(creator);
        configuration.setTechnology(it.pagopa.interop.common.eservice.domain.EServiceTechnology.SOAP);
        return configuration;
    }
}
