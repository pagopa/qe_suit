package it.pagopa.send.common.infrastructure.context;

import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.kernel.context.CurrentUserSession;

import java.util.List;

public class InMemoryCurrentUserSession implements CurrentUserSession {
    private final ThreadLocal<List<Recipient>> currentRecipient = new ThreadLocal<>();
    private final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();

    @Override
    public void setSender(Tenant sender) {
        currentTenant.set(sender);
    }

    @Override
    public Tenant getSender() {
        Tenant tenant = currentTenant.get();
        if (tenant == null) throw new IllegalStateException("Current sender is not set");
        return tenant;
    }

    @Override
    public void setRecipients(List<Recipient> recipients) {
        currentRecipient.set(recipients);
    }

    @Override
    public List<Recipient> getRecipients() {
        List<Recipient> recipients = currentRecipient.get();
        if (recipients == null) throw new IllegalStateException("Current recipients are not set");
        return recipients;
    }

    @Override
    public User getCurrentActor() {
        Tenant tenant = currentTenant.get();
        if (tenant != null) return tenant;

        List<Recipient> recipients = currentRecipient.get();
        if (recipients != null && !recipients.isEmpty()) return recipients.get(0);

        return null;
    }
}
