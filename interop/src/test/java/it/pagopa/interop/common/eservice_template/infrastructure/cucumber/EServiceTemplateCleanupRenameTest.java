package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EServiceTemplateCleanupRenameTest {
    @Test
    void cleanupRenamesTemplateWithPartialCommandAndStillRetiresVersions() {
        var store = mock(EntityStore.class);
        var useCase = mock(EServiceTemplateUseCase.class);
        var factory = mock(EServiceTemplateRequestFactory.class);
        var versions = mock(EServiceTemplateVersionUseCase.class);
        var session = mock(AuthSession.class);
        @SuppressWarnings("unchecked")
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        var template = EServiceTemplate.builder().id(UUID.randomUUID())
                .creatorId(Tenant.COMUNE_DI_MILANO.getOrganizationId()).build();
        when(store.getAll(EServiceTemplate.class)).thenReturn(List.of(template));
        when(useCase.getEServiceTemplate(template)).thenReturn(template);
        var update = mock(UpdateEServiceTemplateCommand.class);
        when(factory.defaultUpdateEServiceTemplateCommand(true)).thenReturn(update);
        when(update.name(anyString())).thenReturn(update);
        var hooks = new EServiceTemplateHooks(store, useCase, factory, versions, session, channel);

        hooks.renameTemplatesForCleanup();
        hooks.cleanUpTemplates();

        verify(update).name(matches("cleanup-template-[A-Za-z0-9]{20}"));
        var sequence = inOrder(useCase, versions);
        sequence.verify(useCase).updateEServiceTemplateMetadata(template, update);
        sequence.verify(versions).retireVersions(template);
    }

    @Test
    void renameExecutesBeforeInstanceCleanup() throws Exception {
        var rename = EServiceTemplateHooks.class.getMethod("renameTemplatesForCleanup")
                .getAnnotation(After.class);
        var instances = it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceHooks.class
                .getMethod("cleanUpEServices").getAnnotation(After.class);
        assertThat(rename.order()).isGreaterThan(instances.order());
    }

    @Test
    void cleanUpTagIsSupportedByTemplateHook() throws Exception {
        Method method = EServiceTemplateHooks.class.getMethod("cleanUpTemplates");
        assertThat(method.getAnnotation(After.class).value()).contains("@clean-up");
    }
}
