package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateHooks {
    private final EntityStore entities;
    private final EServiceTemplateUseCase templates;
    private final EServiceTemplateVersionUseCase versions;
    private final CurrentUserSession session;
    private final CurrentChannel<Channel> channel;

    // Published templates cannot be deleted/archived by BFF; suspension is the supported retirement operation.
    @After(value = "@private-catalog", order = 90)
    public void cleanUpTemplates() {
        var created = entities.getAll(EServiceTemplate.class);
        if (created.isEmpty()) return;
        var user = session.getUser();
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
            session.set(user, tenant);
            channel.setCurrentChannel(previousChannel);
        }
        if (failure.getSuppressed().length > 0) throw failure;
    }
}
