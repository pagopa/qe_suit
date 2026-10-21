package it.pagopa.send.common.infrastructure.context;

import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.kernel.context.CurrentUserSession;

import java.util.List;

public class InMemoryCurrentUserSession implements CurrentUserSession {
    private final ThreadLocal<List<User>> currentRecipients = new ThreadLocal<>();
    private final ThreadLocal<User> currentSender = new ThreadLocal<>();

    @Override
    public void setSender(User sender) {
        currentSender.set(sender);
    }

    @Override
    public User getSender() {
        User sender = currentSender.get();
        if (sender == null) throw new IllegalStateException("Current sender is not set");
        return sender;
    }

    @Override
    public void setRecipients(List<User> recipients) {
        currentRecipients.set(recipients);
    }

    @Override
    public List<User> getRecipients() {
        List<User> recipients = currentRecipients.get();
        if (recipients == null) throw new IllegalStateException("Current recipients are not set");
        return recipients;
    }

    @Override
    public User getCurrentActor() {
        User sender = currentSender.get();
        if (sender != null) return sender;

        List<User> recipients = currentRecipients.get();
        if (recipients != null && !recipients.isEmpty()) return recipients.get(0);

        return null;
    }
}
