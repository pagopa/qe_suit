package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.*;
import it.pagopa.interop.common.eservice_template.domain.*;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceTemplateHooksTest {
    private final InMemoryEntityStore entities = new InMemoryEntityStore();
    private final EServiceTemplateUseCase templates = mock(EServiceTemplateUseCase.class);
    private final EServiceTemplateVersionGateway gateway = mock(EServiceTemplateVersionGateway.class);
    private final EServiceTemplateVersionUseCase versions = new EServiceTemplateVersionUseCase(
            gateway, new BffEServiceTemplateRequestFactory());
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    @SuppressWarnings("unchecked")
    private final CurrentChannel<Channel> channel = mock(CurrentChannel.class);
    private final EServiceTemplateHooks hook = new EServiceTemplateHooks(entities, templates, versions, session, channel);

    @Test
    void deletesDraftsSuspendsPublishedVersionsAndRestoresSessionAndChannel() {
        var draft = version(DRAFT);
        var published = version(PUBLISHED);
        var suspended = version(SUSPENDED);
        var template = EServiceTemplate.builder().id(UUID.randomUUID())
                .creatorId(Tenant.COMUNE_DI_MILANO.getOrganizationId())
                .version(draft).version(published).version(suspended).build();
        entities.upsert("TPL-1", template);
        when(templates.getEServiceTemplate(template)).thenReturn(template);
        when(session.getUser()).thenReturn(User.getTenantAdmin(Tenant.PAGO_PA));
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);

        hook.cleanUpTemplates();

        var order = inOrder(session, gateway, channel);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        order.verify(gateway).deleteDraftVersion(template.getRef(), draft.getRef());
        order.verify(gateway).suspendVersion(template.getRef(), published.getRef());
        order.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
        verifyNoMoreInteractions(gateway);
        assertThat(entities.getByAliasOrThrow("TPL-1", EServiceTemplate.class)).isSameAs(template);
    }

    @Test
    void aggregatesFailuresAndStillAttemptsOtherTemplatesAsTheirCreators() {
        var first = EServiceTemplate.builder().id(UUID.randomUUID())
                .creatorId(Tenant.COMUNE_DI_MILANO.getOrganizationId()).version(version(PUBLISHED)).build();
        var second = EServiceTemplate.builder().id(UUID.randomUUID())
                .creatorId(Tenant.SOGECAP.getOrganizationId()).version(version(PUBLISHED)).build();
        entities.upsert(first);
        entities.upsert(second);
        when(templates.getEServiceTemplate(first)).thenThrow(new IllegalStateException("refresh failed"));
        when(templates.getEServiceTemplate(second)).thenReturn(second);
        when(channel.getCurrentChannel()).thenReturn(Channel.BFF);

        assertThatThrownBy(hook::cleanUpTemplates).isInstanceOf(IllegalStateException.class)
                .satisfies(error -> assertThat(error.getSuppressed()).hasSize(1));

        verify(gateway).suspendVersion(second.getRef(), second.getLatestVersion().getRef());
        verify(session).set(User.getTenantAdmin(Tenant.SOGECAP), Tenant.SOGECAP);
        verify(session).set(null, null);
        verify(channel, times(2)).setCurrentChannel(Channel.BFF);
    }

    @Test
    void emptyStoreDoesNotSwitchSessionOrChannel() {
        hook.cleanUpTemplates();
        verifyNoInteractions(session, channel, templates, gateway);
    }

    private static EServiceTemplateVersion version(EServiceTemplateVersionState state) {
        return EServiceTemplateVersion.builder().id(UUID.randomUUID()).version("1").state(state).build();
    }
}
