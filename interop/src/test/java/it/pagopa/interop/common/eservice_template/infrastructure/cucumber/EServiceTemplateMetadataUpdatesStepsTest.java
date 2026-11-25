package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInstance;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateMetadataUpdateConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceTemplateMetadataUpdatesStepsTest {
    private final InMemoryEntityStore store = new InMemoryEntityStore();
    private final InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
    private final EServiceTemplateMetadataUpdateConfigurationMapper configurations =
            mock(EServiceTemplateMetadataUpdateConfigurationMapper.class);
    private final EServiceTemplateMetadataUpdatesSteps steps = new EServiceTemplateMetadataUpdatesSteps(
            journey, new BffEServiceTemplateRequestFactory(), configurations, store);

    @Test
    void propagationUsesEachRecordedSuffixRatherThanThePreviousInstanceName() {
        var first = storeService("first");
        var second = storeService("second");
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).name("previous template")
                .instance(new EServiceTemplateInstance("generated-first", first.getRef()))
                .instance(new EServiceTemplateInstance("generated-second", second.getRef()))
                .build();
        store.upsert("template", template);
        store.upsert("other", EServiceTemplate.builder().id(UUID.randomUUID()).build());
        when(configurations.apply(eq(List.of()), any()))
                .thenReturn(Map.of("nome", "new template", "descrizione", "new description"));

        steps.updateMetadata(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, "template", List.of());
        verify(journey).updateEServiceTemplateMetadata(argThat(command ->
                "template".equals(command.getAlias())));

        var updated = template.toBuilder().name("new template").description("new description").build();
        store.upsert(updated);
        stubPropagation("first", first, "new template - generated-first");
        stubPropagation("second", second, "new template - generated-second");

        steps.checkMetadata("template");
        steps.checkInstanceMetadataPropagation("template", List.of("first", "second"));

        verify(journey).waitUntilEService(eq("first"), any());
        verify(journey).waitUntilEService(eq("second"), any());
    }

    @Test
    void rejectsInstanceAssociatedOnlyWithADifferentTemplate() {
        var unrelated = storeService("unrelated");
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).name("template").build();
        store.upsert("template", template);
        store.upsert("other", EServiceTemplate.builder().id(UUID.randomUUID()).name("template")
                .instance(new EServiceTemplateInstance("other-label", unrelated.getRef())).build());

        steps.updateMetadata(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO, "template", List.of());

        assertThatThrownBy(() -> steps.checkInstanceMetadataPropagation("template", List.of("unrelated")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unrelated")
                .hasMessageContaining(unrelated.getId().toString())
                .hasMessageContaining("non associata al template template");
        verify(journey, never()).waitUntilEService(anyString(), any());
    }

    private EService storeService(String alias) {
        var service = EService.builder().id(UUID.randomUUID()).name("old name without suffix").build();
        store.upsert(alias, service);
        return service;
    }

    private void stubPropagation(String alias, EService instance, String expectedName) {
        when(journey.waitUntilEService(eq(alias), any())).thenAnswer(invocation -> {
            Predicate<EService> condition = invocation.getArgument(1);
            var refreshed = instance.toBuilder().name(expectedName).description("new description").build();
            assertThat(condition.test(instance)).isFalse();
            assertThat(condition.test(refreshed.toBuilder().description("old description").build())).isFalse();
            assertThat(condition.test(refreshed)).isTrue();
            store.upsert(refreshed);
            return journey;
        });
    }
}
