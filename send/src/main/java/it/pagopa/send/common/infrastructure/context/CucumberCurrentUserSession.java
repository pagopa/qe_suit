package it.pagopa.send.common.infrastructure.context;

import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;

import java.util.List;

public class CucumberCurrentUserSession implements CurrentUserSession {
    private User sender;
    private List<User> recipients;

    @Override
    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public User getSender() {
        if (sender == null) throw new IllegalStateException("Current sender is not set");
        return sender;
    }

    @Override
    public void setRecipients(List<User> recipients) {
        this.recipients = recipients;
    }

    @Override
    public List<User> getRecipients() {
        if (recipients == null) throw new IllegalStateException("Current recipients are not set");
        return recipients;
    }

    @Override
    public User getCurrentActor() {
        if (sender != null) return sender;
        if (recipients != null && !recipients.isEmpty()) return recipients.get(0);
        return null;
    }

    public void reset() {
        this.sender = null;
        this.recipients = null;
    }
}
