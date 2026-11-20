package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.utils.RandomUtils;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateHooks {
    private final EntityStore entities;
    private final EServiceTemplateUseCase templates;
    private final EServiceTemplateRequestFactory requestFactory;
    private final EServiceTemplateVersionUseCase versions;
    private final AuthSession session;
    private final CurrentChannel<Channel> channel;

    /** Rinomina il padre prima del cleanup delle istanze (After order 100). */
    @After(value = "@private-catalog or @clean-up", order = 120)
    public void renameTemplatesForCleanup() {
        var created = entities.getAll(EServiceTemplate.class);
        if (created.isEmpty()) return;
        var user = session.getActor();
        var tenant = session.getTenant();
        var previousChannel = channel.getCurrentChannel();
        var failure = new IllegalStateException("Rinomina dei template e-service non completata");
        try {
            channel.setCurrentChannel(Channel.BFF);
            for (var template : created) {
                try {
                    var creator = Tenant.fromOrganizationId(template.getCreatorId());
                    session.set(User.getTenantAdmin(creator), creator);
                    var current = templates.getEServiceTemplate(template);
                    var command = requestFactory.defaultUpdateEServiceTemplateCommand(true)
                            .name(RandomUtils.randomAlphanumericName("cleanup-template", 20));
                    templates.updateEServiceTemplateMetadata(current, command);
                } catch (RuntimeException | AssertionError exception) {
                    failure.addSuppressed(exception);
                }
            }
        } finally {
            channel.setCurrentChannel(previousChannel);
            session.set(user, tenant);
        }
        if (failure.getSuppressed().length > 0) throw failure;
    }

    // Published templates cannot be deleted/archived by BFF; suspension is supported.
    @After(value = "@private-catalog or @clean-up", order = 90)
    public void cleanUpTemplates() {
        var created = entities.getAll(EServiceTemplate.class);
        if (created.isEmpty()) return;
        var user = session.getActor();
        var tenant = session.getTenant();
        var previousChannel = channel.getCurrentChannel();
        var failure = new IllegalStateException("Cleanup dei template e-service non completato");
        try {
            channel.setCurrentChannel(Channel.BFF);
            for (var template : created) {
                try {
                    var creator = Tenant.fromOrganizationId(template.getCreatorId());
                    session.set(User.getTenantAdmin(creator), creator);
                    versions.retireVersions(templates.getEServiceTemplate(template));
                } catch (RuntimeException | AssertionError exception) {
                    failure.addSuppressed(exception);
                }
            }
        } finally {
            channel.setCurrentChannel(previousChannel);
            session.set(user, tenant);
        }
        if (failure.getSuppressed().length > 0) throw failure;
    }
}
